package packetfilter.firewall.internals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import packetfilter.packet.Packet;
import packetfilter.packet.Packet.MessageType;
import packetfilter.range.ConcurrentDisjointRange;
import packetfilter.utils.ArgumentValidator;

public class ConcurrentPNGRMap {
    private ConcurrentHashMap<Integer, PNGRMapValue> map;

    public ConcurrentPNGRMap() {
        this.map = new ConcurrentHashMap<>();
    }

    public boolean decide(Packet pkt) {
        ArgumentValidator.assertIsDataPacket(pkt);

        int src = pkt.header.source, dst = pkt.header.dest;

        while (true) {
            var srcEntry = this.map.get(src);
            var dstEntry = this.map.get(dst);

            if (srcEntry == null && dstEntry == null) {
                return true;
            } else if (srcEntry == null) {
                if (this.map.containsKey(src)) { // srcEntry has been modified
                    continue;
                }
                return dstEntry.hasRecvPermission(src);
            } else if (dstEntry == null) {
                if (this.map.containsKey(dst)) { // dstEntry has been modified
                    continue;
                }
                return !srcEntry.isPersonaNonGrata();
            } else {
                boolean lockSrcFirst = src <= dst;
                Lock firstLock = lockSrcFirst ? srcEntry.readLock() : dstEntry.readLock();
                Lock secondLock = lockSrcFirst ? dstEntry.readLock() : srcEntry.readLock();

                firstLock.lock();
                try {
                    secondLock.lock();
                    try {
                        return !srcEntry.isPersonaNonGrata() && dstEntry.hasRecvPermission(src);
                    } finally {
                        secondLock.unlock();
                    }
                } finally {
                    firstLock.unlock();
                }
            }
        }
    }

    public void update(Packet pkt) {
        ArgumentValidator.assertIsConfigPacket(pkt);

        this.getOrCreate(pkt.config.address).updateConfig(pkt);
    }

    private PNGRMapValue getOrCreate(int address) {
        return this.map.computeIfAbsent(address, addr -> new PNGRMapValue(addr));
    }
}

class PNGRMapValue extends AbstractPNGRMapValue {
    private final int addr;
    private ReentrantReadWriteLock rwlock;
    private volatile boolean personaNonGrata;
    private ConcurrentDisjointRange recvPermission;

    public PNGRMapValue(int address) {
        this.addr = address;
        this.rwlock = new ReentrantReadWriteLock();
        this.personaNonGrata = false;
        this.recvPermission = new ConcurrentDisjointRange();
    }

    public int associatedAddress() {
        return this.addr;
    }

    public void updateConfig(Packet pkt) {
        if (pkt.type == MessageType.DataPacket) {
            throw new IllegalArgumentException();
        }

        this.rwlock.writeLock().lock();
        try {
            this.unsafeSetPersonaNonGrata(pkt.config.personaNonGrata);
            this.unsafeUpdateRecvPermission(pkt.config.addressBegin, pkt.config.addressEnd, pkt.config.acceptingRange);
        } finally {
            this.rwlock.writeLock().unlock();
        }
    }

    public Lock readLock() {
        return this.rwlock.readLock();
    }

    public boolean isPersonaNonGrata() {
        this.readLock().lock();
        try {
            return this.personaNonGrata;
        } finally {
            this.readLock().unlock();
        }
    }

    public boolean hasRecvPermission(int sourceAddr) {
        this.readLock().lock();
        try {
            return this.recvPermission.get(sourceAddr);
        } finally {
            this.readLock().unlock();
        }
    }

    private void unsafeSetPersonaNonGrata(boolean personaNonGrata) {
        this.personaNonGrata = personaNonGrata;
    }

    private void unsafeUpdateRecvPermission(int addressBegin, int addressEnd, boolean shouldAccept) {
        this.recvPermission.set(addressBegin, addressEnd, shouldAccept);
    }
}
