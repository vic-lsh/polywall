package packetfilter.firewall.fingerprint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class FPCollectorTestBase<P extends FPCollector> {

    protected P collector;

    protected boolean multiThreaded = false;

    protected abstract P createFPCollector();

    protected abstract boolean isMultiThreaded();

    @BeforeEach
    public void init() {
        collector = createFPCollector();
        multiThreaded = isMultiThreaded();
    }

    @Test
    public void testNewCollectorContainsNoFingerprint() {
        assertEquals(0, collector.size());
    }

    @Test
    public void testAddOneFingerprint() {
        collector.collect(333);
        assertEquals(1, collector.size());
    }

    @Test
    public void testConcurrentFingerprintCollection() throws InterruptedException {
        assumeTrue(multiThreaded);

        final int numInserts = 1000;
        final int numThrs = 10;
        Thread[] threads = new Thread[numThrs];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < numInserts; j++) {
                    collector.collect(ThreadLocalRandom.current().nextInt());
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(numThrs * numInserts, collector.size());
    }
}
