package packetfilter.firewall.internals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PermissionCacheTest {

    PermissionCache cache;

    @BeforeEach
    public void init() {
        cache = new PermissionCache();
    }

    @Test
    public void testMapKeyGeneration() {
        int source = 3339;
        int dest = 10121222;

        long expectedKey = Integer.MAX_VALUE * source + dest;
        long actualKey = cache.put(source, dest, true);

        assertEquals(expectedKey, actualKey);
    }

    @Test
    public void testCacheRetrieval() {
        int src = 333;
        int dst = 111;

        assertEquals(null, cache.get(src, dst));

        cache.put(src, dst, false);
        assertFalse(cache.get(src, dst));

        cache.put(src, dst, true);
        assertTrue(cache.get(src, dst));
    }

    @Test
    public void testRevocation() {
        int srcSeed = 100;
        int dstSeed = 2323;
        int entriesCount = 10;
        ConcurrentSkipListSet<Long> keys = new ConcurrentSkipListSet<>();

        for (int i = 0; i < entriesCount; i++) {
            keys.add(cache.put(srcSeed * i, dstSeed * i, true));
        }

        cache.revoke(keys);

        for (int i = 0; i < entriesCount; i++) {
            assertEquals(null, cache.get(srcSeed * i, dstSeed * i));
        }
    }
}
