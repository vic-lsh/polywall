package packetfilter.firewall.internals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import packetfilter.packet.Packet;
import packetfilter.range.ConcurrentDisjointRange;
import packetfilter.utils.ArgumentValidator;

public class StripedLockPNGRMap {

    private ConcurrentHashMap<Integer, StrippedLockMapValue> map;
    private ReentrantReadWriteLock[] locks;

    public StripedLockPNGRMap(int numLocks) {
        this.map = new ConcurrentHashMap<>();

        this.locks = new ReentrantReadWriteLock[numLocks];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    public boolean decide(Packet pkt) {
        ArgumentValidator.assertIsDataPacket(pkt);

        int src = pkt.header.source, dst = pkt.header.dest;

        var srcEntry = this.map.get(src);
        var dstEntry = this.map.get(dst);

        if (srcEntry == null && dstEntry == null) {
            return true;
        }

        if (srcEntry == null) {
            var lock = findLock(dst);
            lock.readLock().lock();
            try {
                return dstEntry.hasRecvPermission(src);
            } finally {
                lock.readLock().unlock();
            }
        }

        if (dstEntry == null) {
            var lock = findLock(src);
            lock.readLock().lock();
            try {
                return !srcEntry.isPersonaNonGrata();
            } finally {
                lock.readLock().unlock();
            }
        }

        // both source and dest entries are defined
        var srcEntryLock = findLock(src).readLock();
        var dstEntryLock = findLock(dst).readLock();

        boolean lockSrcFirst = src <= dst;
        var firstLock = lockSrcFirst ? srcEntryLock : dstEntryLock;
        var secondLock = lockSrcFirst ? dstEntryLock : srcEntryLock;

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

    public void updateConfig(Packet pkt) {
        ArgumentValidator.assertIsConfigPacket(pkt);

        var entry = this.getOrCreate(pkt.config.address);
        var lock = findLock(pkt.config.address);

        lock.writeLock().lock();
        try {
            entry.updateConfig(pkt);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private StrippedLockMapValue getOrCreate(int address) {
        return this.map.computeIfAbsent(address, addr -> new StrippedLockMapValue());
    }

    private ReentrantReadWriteLock findLock(int address) {
        return locks[address % locks.length];
    }

}

/**
 * Value entry for `StripedLockPNGRMap`.
 * 
 * Assumes access to this class is protected by an external lock that provides
 * read-write mutual exclusion.
 */
class StrippedLockMapValue extends AbstractPNGRMapValue {
    private volatile boolean personaNonGrata;
    private volatile ConcurrentDisjointRange recvPermission;

    public StrippedLockMapValue() {
        this.personaNonGrata = false;
        this.recvPermission = new ConcurrentDisjointRange();
    }

    @Override
    public void updateConfig(Packet pkt) {
        ArgumentValidator.assertIsConfigPacket(pkt);

        this.personaNonGrata = pkt.config.personaNonGrata;
        this.recvPermission.set(pkt.config.addressBegin, pkt.config.addressEnd, pkt.config.acceptingRange);
    }

    @Override
    public boolean isPersonaNonGrata() {
        return this.personaNonGrata;
    }

    @Override
    public boolean hasRecvPermission(int sourceAddr) {
        return this.recvPermission.get(sourceAddr);
    }

}