package packetfilter.firewall.internals;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PermissionCache {

    private Lock[] locks;
    private ConcurrentHashMap<Long, Boolean> cache;

    public PermissionCache() {
        this(10_000);
    }

    public PermissionCache(int lockStripSize) {
        this.cache = new ConcurrentHashMap<>();
        this.locks = new ReentrantLock[lockStripSize];
        for (int i = 0; i < locks.length; i++) {
            this.locks[i] = new ReentrantLock();
        }
    }

    /**
     * Cache the permission of a source-destination address pair.
     * 
     * @param src       the source address.
     * @param dest      the destination address.
     * @param permitted whether packets are allowed to go from source to
     *                  destination.
     * @return a cache entry key computed based on source and destination. This key
     *         needs to be retained for cache revocation.
     */
    public long put(int src, int dest, boolean permitted) {
        long key = genKey(src, dest);

        acquire(key);
        try {
            this.cache.put(key, permitted);
            return key;
        } finally {
            release(key);
        }
    }

    /**
     * Obtain the cached permission status of a source-destination address pair.
     * 
     * @param src  the source address to look up.
     * @param dest the destination address to look up.
     * @return a boolean if some permission status is cached, or null.
     */
    public Boolean get(int src, int dest) {
        long key = genKey(src, dest);
        return cache.get(key);
    }

    /**
     * Delete some cached entries, if they exist.
     * 
     * @param entries a sorted set of cache keys to delete.
     */
    public void revoke(ConcurrentSkipListSet<Long> entries) {
        var unlockOrder = acquireLocks(entries);
        try {
            for (var key : entries) {
                cache.remove(key);
            }
        } finally {
            releaseLocks(unlockOrder);
        }
    }

    public long genKey(int src, int dest) {
        return src * Integer.MAX_VALUE + dest;
    }

    private void acquire(Long key) {
        getLock(key).lock();
    }

    private void release(Long key) {
        getLock(key).unlock();
    }

    private Stack<Long> acquireLocks(ConcurrentSkipListSet<Long> entries) {
        Stack<Long> reverseOrder = new Stack<>();

        for (var key : entries) {
            getLock(key).lock();
            reverseOrder.push(key);
        }

        return reverseOrder;
    }

    private void releaseLocks(Stack<Long> entries) {
        while (true) {
            try {
                var key = entries.pop();
                getLock(key).unlock();
            } catch (EmptyStackException e) {
                break;
            }
        }
    }

    private Lock getLock(Long key) {
        return locks[Math.abs(key.intValue()) % locks.length];
    }

}
