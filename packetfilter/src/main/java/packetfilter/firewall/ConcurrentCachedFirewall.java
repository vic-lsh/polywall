package packetfilter.firewall;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import packetfilter.firewall.internals.PermissionCache;
import packetfilter.packet.Packet;
import packetfilter.range.ConcurrentDisjointRange;
import packetfilter.utils.ArgumentValidator;

public class ConcurrentCachedFirewall implements Firewall {

    private PermissionCache cache;
    private ConcurrentHashMap<Integer, MapEntry> map;

    class MapEntry {
        public ReentrantLock lock;
        private volatile boolean personaNonGrata;
        private ConcurrentDisjointRange recvPermission;
        private ConcurrentSkipListSet<Long> affectedCaches;

        public MapEntry() {
            this.lock = new ReentrantLock();
            this.personaNonGrata = false;
            this.recvPermission = new ConcurrentDisjointRange();
            this.affectedCaches = new ConcurrentSkipListSet<>();
        }

        public boolean isPersonaNonGrata() {
            this.lock.lock();
            try {
                return this.personaNonGrata;
            } finally {
                this.lock.unlock();
            }
        }

        public boolean hasRecvPermission(int sourceAddr) {
            this.lock.lock();
            try {
                return this.recvPermission.get(sourceAddr);
            } finally {
                this.lock.unlock();
            }
        }

        public void addAffectedCache(long key) {
            affectedCaches.add(key);
        }

        private void unsafeSetPersonaNonGrata(boolean personaNonGrata) {
            this.personaNonGrata = personaNonGrata;
        }

        private void unsafeUpdateRecvPermission(int addressBegin, int addressEnd, boolean shouldAccept) {
            this.recvPermission.set(addressBegin, addressEnd, shouldAccept);
        }
    }

    public ConcurrentCachedFirewall() {
        this.cache = new PermissionCache();
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public boolean decide(Packet pkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsDataPacket(pkt);

        int src = pkt.header.source, dst = pkt.header.dest;

        Boolean cached = this.cache.get(src, dst);
        if (cached != null) {
            return cached;
        }

        var srcEntry = this.getOrCreate(src);
        var dstEntry = this.getOrCreate(dst);

        boolean lockSrcFirst = src <= dst;
        Lock firstLock = lockSrcFirst ? srcEntry.lock : dstEntry.lock;
        Lock secondLock = lockSrcFirst ? dstEntry.lock : srcEntry.lock;

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                boolean permitted = !srcEntry.isPersonaNonGrata() && dstEntry.hasRecvPermission(src);
                long key = cache.genKey(src, dst);
                srcEntry.addAffectedCache(key);
                dstEntry.addAffectedCache(key);
                cache.put(src, dst, permitted);
                return permitted;
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

        var entry = this.getOrCreate(configPkt.config.address);

        entry.lock.lock();
        try {
            if (entry.affectedCaches.size() > 0) {
                cache.revoke(entry.affectedCaches);
            }
            entry.unsafeSetPersonaNonGrata(configPkt.config.personaNonGrata);
            entry.unsafeUpdateRecvPermission(
                    configPkt.config.addressBegin,
                    configPkt.config.addressEnd,
                    configPkt.config.acceptingRange);
        } finally {
            entry.lock.unlock();
        }
    }

    private MapEntry getOrCreate(int address) {
        return this.map.computeIfAbsent(address, addr -> new MapEntry());
    }
}
