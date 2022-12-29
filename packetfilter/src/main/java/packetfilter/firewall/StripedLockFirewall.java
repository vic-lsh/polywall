package packetfilter.firewall;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import packetfilter.firewall.internals.ConcurrentPNGMap;
import packetfilter.firewall.internals.ConcurrentRMap;
import packetfilter.packet.Packet;
import packetfilter.utils.ArgumentValidator;

public class StripedLockFirewall implements Firewall {

    private ReentrantReadWriteLock[] locks;
    private ConcurrentPNGMap PNG;
    private ConcurrentRMap R;

    /**
     * Instantiate a firewall implemented using striped locks.
     * 
     * @param numberOfLocks the number of locks used in the firewall. The
     *                      number of locks created is static.
     */
    public StripedLockFirewall(int numberOfLocks) {
        this.PNG = new ConcurrentPNGMap();
        this.R = new ConcurrentRMap();

        this.locks = new ReentrantReadWriteLock[numberOfLocks];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    @Override
    public boolean decide(Packet pkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsDataPacket(pkt);

        int src = pkt.header.source, dst = pkt.header.dest;

        var srcEntryLock = findLock(src).readLock();
        var dstEntryLock = findLock(dst).readLock();

        boolean lockSrcFirst = src <= dst;
        var firstLock = lockSrcFirst ? srcEntryLock : dstEntryLock;
        var secondLock = lockSrcFirst ? dstEntryLock : srcEntryLock;

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                return this.PNG.decide(src) && this.R.decide(src, dst);
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    @Override
    public void update(Packet configPkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsConfigPacket(configPkt);

        int addr = configPkt.config.address;

        var lock = findLock(addr);
        lock.writeLock().lock();
        try {
            this.PNG.put(addr, configPkt.config.personaNonGrata);
            this.R.getOrCreate(addr).set(
                    configPkt.config.addressBegin, configPkt.config.addressEnd,
                    configPkt.config.acceptingRange);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private ReentrantReadWriteLock findLock(int address) {
        return locks[Math.abs(address) % locks.length];
    }

}
