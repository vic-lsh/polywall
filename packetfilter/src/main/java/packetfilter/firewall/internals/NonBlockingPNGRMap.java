package packetfilter.firewall.internals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;

import packetfilter.packet.Packet;
import packetfilter.range.ConcurrentDisjointRange;
import packetfilter.utils.ArgumentValidator;

public class NonBlockingPNGRMap {
    private ConcurrentHashMap<Integer, AtomicStampedReference<LockFreePNGRMapValue>> map;

    public NonBlockingPNGRMap() {
        this.map = new ConcurrentHashMap<>();
    }

    public boolean decide(Packet pkt) {
        ArgumentValidator.assertIsDataPacket(pkt);

        int src = pkt.header.source, dst = pkt.header.dest;

        while (true) {
            var srcRef = this.map.get(src);
            var dstRef = this.map.get(dst);

            if (srcRef == null && dstRef == null) {
                return true; // no restrictions this source-destination pair
            } else if (srcRef == null) {
                boolean permitted = dstRef.getReference().hasRecvPermission(src);
                if (this.map.containsKey(src)) { // state changed, discard work
                    continue;
                }
                return permitted;
            } else if (dstRef == null) {
                boolean permitted = !srcRef.getReference().isPersonaNonGrata();
                if (this.map.containsKey(dst)) { // state changed, discard work
                    continue;
                }
                return permitted;
            } else {
                int[] srcStamp = new int[] { -1 };
                int[] dstStamp = new int[] { -1 };

                while (true) {
                    var srcEntry = srcRef.get(srcStamp);
                    var dstEntry = dstRef.get(dstStamp);

                    boolean permitted = !srcEntry.isPersonaNonGrata() && dstEntry.hasRecvPermission(src);

                    boolean snapshotSucceeded = srcRef.getStamp() == srcStamp[0] && dstRef.getStamp() == dstStamp[0];

                    if (snapshotSucceeded) {
                        // there's an interval where srcEntry and dstEntry did not change.
                        return permitted;
                    }
                }
            }
        }
    }

    public void update(Packet pkt) {
        ArgumentValidator.assertIsConfigPacket(pkt);

        while (true) {
            int[] stamp = { -1 };
            var ref = this.getOrCreate(pkt.config.address);
            var before = ref.get(stamp);
            var update = before.copyUpdateConfig(pkt);
            if (ref.compareAndSet(before, update, stamp[0], stamp[0] + 1)) {
                return;
            }
        }
    }

    private AtomicStampedReference<LockFreePNGRMapValue> getOrCreate(int address) {
        return this.map.computeIfAbsent(
                address,
                addr -> new AtomicStampedReference<>(new LockFreePNGRMapValue(), 0));
    }
}

class LockFreePNGRMapValue extends AbstractPNGRMapValue {
    private boolean personaNonGrata;
    private ConcurrentDisjointRange recvPermission;

    public LockFreePNGRMapValue() {
        this.personaNonGrata = false;
        this.recvPermission = new ConcurrentDisjointRange();
    }

    LockFreePNGRMapValue(LockFreePNGRMapValue cloneTarget) {
        this.personaNonGrata = cloneTarget.personaNonGrata;
        this.recvPermission = cloneTarget.recvPermission.clone();
    }

    public boolean isPersonaNonGrata() {
        return this.personaNonGrata;
    }

    public boolean hasRecvPermission(int sourceAddr) {
        return this.recvPermission.get(sourceAddr);
    }

    public LockFreePNGRMapValue copyUpdateConfig(Packet pkt) {
        ArgumentValidator.assertIsConfigPacket(pkt);

        var newEntry = this.clone();

        newEntry.personaNonGrata = pkt.config.personaNonGrata;
        newEntry.recvPermission.set(pkt.config.addressBegin, pkt.config.addressEnd, pkt.config.acceptingRange);

        return newEntry;
    }

    @Override
    protected LockFreePNGRMapValue clone() {
        return new LockFreePNGRMapValue(this);
    }

    @Override
    public void updateConfig(Packet pkt) {
        // Updating config by mutating this instance is not supported.
        // See `copyUpdateConfig()`.
        throw new UnsupportedOperationException();
    }
}