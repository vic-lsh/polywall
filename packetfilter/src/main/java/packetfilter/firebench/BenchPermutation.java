package packetfilter.firebench;

import java.util.concurrent.ExecutionException;

import packetfilter.drivers.DriversManifest;
import packetfilter.drivers.FirewallDriver;
import packetfilter.firebench.config.ThreadingConfig;
import packetfilter.firebench.logger.BenchLogger;
import packetfilter.firebench.results.BenchmarkResult;
import packetfilter.firebench.results.SingleFirewallBenchmarkResult;
import packetfilter.packet.generators.PacketGeneratorConfig;

/**
 * In a benchmark that enumerates many packet generator configs and threading
 * configs, provide a mechanism to run benchmarking on a single packet generator
 * config and threading config.
 * 
 * In other words, it benchmarks a single permutation of all possible config
 * combinations.
 */
public class BenchPermutation {

    private final DriversManifest drivers;
    private final String pktGenConfigName;
    private final ThreadingConfig threadingConfig;

    public BenchPermutation(String pktGenConfigName, PacketGeneratorConfig pktGenConfig,
            ThreadingConfig threadingConfig) {

        this.pktGenConfigName = pktGenConfigName;
        this.threadingConfig = threadingConfig;
        drivers = new DriversManifest(threadingConfig, pktGenConfig);

    }

    public BenchPermutationRunner bench(int numPackets, int numTrials) {
        return new BenchPermutationRunner(numPackets, numTrials);
    }

    class BenchPermutationRunner {

        private final int numPackets;
        private final int numTrials;

        public BenchPermutationRunner(int numPackets, int numTrials) {
            this.numPackets = numPackets;
            this.numTrials = numTrials;
        }

        public void collect(BenchmarkResult resultCollector, long baseline)
                throws InterruptedException, ExecutionException {
            BenchLogger.log("---------------------------------------------------");
            BenchLogger.log("Benchmarking config", pktGenConfigName);
            BenchLogger.log("---------------------------------------------------");

            for (var firewallFactory : drivers) {
                FirewallDriver firewall = firewallFactory.get();
                FirewallBench firebench = new FirewallBench(firewall);
                String firewallName = firewall.getName();

                BenchLogger.log("*", firewallName);

                long dur = firebench.bench(numPackets, numTrials);

                resultCollector.collect(pktGenConfigName, threadingConfig, firewallName,
                        new SingleFirewallBenchmarkResult(dur, baseline));
            }

            var collected = resultCollector.query(pktGenConfigName, threadingConfig);
            if (collected == null) {
                throw new IllegalStateException("Collected results cannot be null.");
            }
            BenchLogger.log(collected.toString());
        }
    }
}
