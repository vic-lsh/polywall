package packetfilter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import packetfilter.firebench.FirewallBenchRunner;
import packetfilter.firebench.config.BenchConfig;
import packetfilter.firebench.config.IncompleteThreadingStrategyBuildException;
import packetfilter.firebench.config.PacketGenStrategy;
import packetfilter.firebench.config.ThreadingStrategyBuilder;
import packetfilter.packet.generators.ConfigFileMappingException;

/**
 * The packet filter application.
 */
public class App {

    /** Number of packets processed by each driver during benchmarking. */
    private final static int NUM_PACKETS = 10_000_000;

    /** Number of repeated trials performed during benchmarking. */
    private final static int NUM_TRIALS = 3;

    /**
     * In a partial pipeline, the percentage of the total number of worker threads
     * that is data workers (the rest are config workers).
     */
    private final static double PERCENTAGE_DATA_WORKERS = 0.5;
    /**
     * In a phased pipeline, the percentage of total worker thread count that is
     * control workers (the rest are collection workers).
     */
    private final static double PERCENTAGE_CONTROL_WORKERS = 0.8;

    /** Whether to show printouts during benchmarking. */
    private final static boolean DEBUG = true;

    public static void main(String[] args)
            throws IOException, ConfigFileMappingException, InterruptedException, ExecutionException,
            IncompleteThreadingStrategyBuildException {
        runBenchmarks();
    }

    private static void runBenchmarks()
            throws IOException, ConfigFileMappingException, InterruptedException, ExecutionException,
            IncompleteThreadingStrategyBuildException {

        FirewallBenchRunner benchmarkDriver = new FirewallBenchRunner(
                new BenchConfig(
                        NUM_PACKETS,
                        NUM_TRIALS,
                        PacketGenStrategy.useAll(),
                        new ThreadingStrategyBuilder()
                                .withNumberOfThreads(3, 6, 10)
                                .withPercentageDataWorkers(PERCENTAGE_DATA_WORKERS)
                                .withPercentageControlWorkers(PERCENTAGE_CONTROL_WORKERS).build()));

        benchmarkDriver.enableDebugMesages(DEBUG);

        benchmarkDriver.bench().print();
    }
}
