package packetfilter.firewall;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import packetfilter.firewall.helpers.AcceptanceRangeTestConfig;
import packetfilter.firewall.helpers.AcceptanceRangeTestData;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.custom.CustomPacketGenerator;
import packetfilter.utils.AddressRange;
import packetfilter.utils.RangeRandom;

/**
 * Provide concurrent versions of the tests specified in `FirewallTestBase`.
 */
public abstract class ConcurrentFirewallTestBase<F extends Firewall> extends FirewallTestBase<F> {

    protected ThreadLocal<CustomPacketGenerator> threadLocalPktGen = new ThreadLocal<>() {
        protected CustomPacketGenerator initialValue() {
            return new CustomPacketGenerator(pktGenConfig);
        };
    };

    /**
     * The number of threads to test under.
     * 
     * For each of the values specified in this array, run the concurrent test
     * suite.
     */
    protected int[] NUM_THRS = { 2, 7, 20, 50, 83, 100 };

    /**
     * In repeated test iterations, the number of repeats to perform.
     */
    protected int TRIALS = 50000;

    @Nested
    class ConcurrentTests {

        private static RangeRandom rangeRand = new RangeRandom(0);

        @BeforeEach
        void concurrentTestsInit() throws IOException, ConfigFileMappingException {
            init();
        }

        @Test
        public void testBanSourceAddress() throws InterruptedException, ExecutionException {
            concurrentlyTest((barr) -> {
                final int seed = 111;

                // simulate many source addresses to ban
                for (int i = 0; i < TRIALS; i++) {
                    int sourceAddr = i * seed;

                    operator.banSourceAddress(sourceAddr);

                    Packet testPkt = threadLocalPktGen.get()
                            .buildDataPacket().withSource(sourceAddr).build();

                    assertFalse(firewall.decide(testPkt));

                    // test previously banned addresses, are banned
                    int testSrcAddr = seed * rangeRand.nextIntInRange(0, i);

                    testPkt = threadLocalPktGen.get()
                            .buildDataPacket().withSource(testSrcAddr).build();

                    assertFalse(firewall.decide(testPkt));

                }
            });
        }

        @Test
        public void testRemoveBanOnSrcAddress() throws InterruptedException, ExecutionException {
            final int seed = 111;

            concurrentlyTest((barr) -> {
                // ban some addresses
                for (int i = 0; i < TRIALS; i++) {
                    operator.banSourceAddress(seed * i);
                }

                barr.await();

                // permit banned address one by one
                for (int i = 0; i < TRIALS; i++) {
                    int srcToPermit = seed * i;
                    operator.permitSourceAddress(srcToPermit);

                    // verify address is indeed permitted
                    Packet pktWithSrc = threadLocalPktGen.get().buildDataPacket().withSource(srcToPermit).build();
                    assertTrue(firewall.decide(pktWithSrc));

                    // verify that address permitted before remains permitted
                    int randomPermittedSrc = seed * rangeRand.nextIntInRange(0, i);
                    Packet testPkt = threadLocalPktGen.get()
                            .buildDataPacket().withSource(randomPermittedSrc).build();

                    assertTrue(firewall.decide(testPkt));
                }
            });
        }

        @Test
        public void testBanAddressRange() throws InterruptedException, ExecutionException {
            final int numReps = 20;
            final int seed = 119;
            final int testPacketCount = 10;

            concurrentlyTest((barr) -> {
                final var srcRange = new AddressRange(2222, 5758);

                for (int i = 0; i < numReps; i++) {
                    final var dataGenCfg = new AcceptanceRangeTestConfig(
                            testPacketCount, testPacketCount, pktGenConfig);
                    final int destAddr = i * seed;

                    var testData = new AcceptanceRangeTestData(destAddr, srcRange, dataGenCfg);

                    operator.banAddressRangeFromDest(srcRange, destAddr);

                    testData.inRangeTestPackets().forEachRemaining((packet) -> {
                        assertFalse(firewall.decide(packet));
                    });

                    testData.outOfRangeTestPackets().forEachRemaining((packet) -> {
                        assertTrue(firewall.decide(packet));
                    });
                }
            });
        }

        @Test
        public void testRemoveBanOnAddressRange() throws InterruptedException, ExecutionException {
            final int numReps = 20;
            final int seed = 119;
            final int testPacketCount = 10;

            concurrentlyTest((barr) -> {
                final var srcRange = new AddressRange(2222, 5758);

                // first, ban some source address ranges
                for (int i = 0; i < numReps; i++) {
                    final int destAddr = i * seed;
                    operator.banAddressRangeFromDest(srcRange, destAddr);
                }

                barr.await();

                // then, disable ban on address ranges iteratively
                for (int i = 0; i < numReps; i++) {
                    final var dataGenCfg = new AcceptanceRangeTestConfig(
                            testPacketCount, testPacketCount, pktGenConfig);
                    final int destAddr = i * seed;

                    var testData = new AcceptanceRangeTestData(destAddr, srcRange, dataGenCfg);

                    operator.permitAddressRangeToDest(srcRange, destAddr);

                    testData.inRangeTestPackets().forEachRemaining((packet) -> {
                        assertTrue(firewall.decide(packet));
                    });

                    testData.outOfRangeTestPackets().forEachRemaining((packet) -> {
                        assertTrue(firewall.decide(packet));
                    });
                }
            });
        }

        /**
         * Convenience method for executing a test body over the array of threads
         * as specified in this class.
         * 
         * @param testBody the test body to execute.
         * @throws InterruptedException
         * @throws ExecutionException
         */
        private void concurrentlyTest(ThrowableConsumer<CyclicBarrier> testBody)
                throws InterruptedException, ExecutionException {
            var runner = new ForkJoinTestRunner(NUM_THRS, testBody, () -> {
                try {
                    init(); // reinit firewall and related variables.
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });

            runner.run();
        }
    }
}

class ForkJoinTestRunner {

    private Runner[] testRunners;
    protected Runnable cleanup;

    public ForkJoinTestRunner(int[] threadCounts, ThrowableConsumer<CyclicBarrier> testBody) {
        this.testRunners = new Runner[threadCounts.length];
        for (int i = 0; i < threadCounts.length; i++) {
            testRunners[i] = new Runner(threadCounts[i], testBody);
        }
    }

    public ForkJoinTestRunner(int[] threadCounts, ThrowableConsumer<CyclicBarrier> testBody, Runnable cleanup) {
        this.testRunners = new Runner[threadCounts.length];
        for (int i = 0; i < threadCounts.length; i++) {
            testRunners[i] = new Runner(threadCounts[i], testBody);
        }
        this.cleanup = cleanup;
    }

    public ForkJoinTestRunner(int numThreads, ThrowableConsumer<CyclicBarrier> testBody) {
        this((new int[] { numThreads }), testBody);
    }

    public ForkJoinTestRunner(int numThreads, ThrowableConsumer<CyclicBarrier> testBody, Runnable cleanup) {
        this((new int[] { numThreads }), testBody, cleanup);
    }

    public void run() throws InterruptedException, ExecutionException {
        for (Runner runner : testRunners) {
            runner.start();
            runner.join();
        }
    }

    class Runner {
        private ExecutorService pool;
        private Future<?>[] handles;
        private final ThrowableConsumer<CyclicBarrier> testBody;
        private final CyclicBarrier barr;

        public Runner(int numThreads, ThrowableConsumer<CyclicBarrier> testBody) {
            this.pool = Executors.newFixedThreadPool(numThreads);
            this.handles = new Future<?>[numThreads];
            this.testBody = testBody;
            this.barr = new CyclicBarrier(numThreads);
        }

        public void start() {
            if (cleanup != null) {
                cleanup.run();
            }

            for (int i = 0; i < handles.length; i++) {
                this.handles[i] = this.pool.submit(() -> {
                    try {
                        testBody.accept(barr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                });
            }
        }

        public void join() throws InterruptedException, ExecutionException {
            for (int i = 0; i < handles.length; i++) {
                handles[i].get();
            }
            this.pool.shutdown();
        }
    }
}

/**
 * Convenience interface to make test bodies throwable.
 */
@FunctionalInterface
interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}
