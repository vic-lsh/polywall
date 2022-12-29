package packetfilter.firebench;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import packetfilter.drivers.serial.SerialFirewallDriver;
import packetfilter.firebench.config.BenchConfig;
import packetfilter.firebench.logger.BenchLogger;
import packetfilter.firebench.results.BenchmarkResult;
import packetfilter.firebench.results.SingleFirewallBenchmarkResult;
import packetfilter.utils.DelimitedNumberFormatter;
import packetfilter.utils.DurationTimer;

/**
 * Measures the performance of numerous firewall implementations.
 */
public class FirewallBenchRunner {

    private final BenchConfig config;

    public FirewallBenchRunner(BenchConfig config) {
        this.config = config;
    }

    public BenchmarkResult bench() throws InterruptedException, ExecutionException {
        BenchmarkResult results = new BenchmarkResult();

        DurationTimer timer = new DurationTimer();
        timer.start();

        for (var pktGenConfigEntry : config.packetGenStrategy()) {

            var pktGenConfigName = pktGenConfigEntry.getKey();
            var pktGenConfig = pktGenConfigEntry.getValue();

            var serialDriver = new SerialFirewallDriver(pktGenConfig);
            BenchLogger.log("*", serialDriver.getName(), "(baseline)");
            long baseline = new FirewallBench(serialDriver).bench(config.numPackets(), 1);
            var baselineResult = new SingleFirewallBenchmarkResult(baseline, baseline);

            for (var threadingConfig : config.threadingStrategy()) {
                // collect baseline result
                results.collect(pktGenConfigName, threadingConfig, serialDriver.getName(), baselineResult);

                var runner = new BenchPermutation(pktGenConfigName, pktGenConfig, threadingConfig);

                // compute and store results for multithreaded firewalls
                runner.bench(config.numPackets(), config.numTrials()).collect(results, baseline);
            }
        }

        var dur = timer.end();
        var durMins = TimeUnit.MINUTES.convert(dur, TimeUnit.NANOSECONDS);
        BenchLogger.log(String.format("Benchmark took %s minutes.", new DelimitedNumberFormatter().format(durMins)));

        return results;
    }

    public void enableDebugMesages(boolean shouldEnable) {
        if (shouldEnable) {
            BenchLogger.enable();
        } else {
            BenchLogger.disable();
        }
    }

}
