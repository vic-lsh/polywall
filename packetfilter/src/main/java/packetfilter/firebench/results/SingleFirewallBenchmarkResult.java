package packetfilter.firebench.results;

import java.util.concurrent.TimeUnit;

public class SingleFirewallBenchmarkResult {

    public final long durationNanos;
    public final long durationMillis;
    public final double speedup;

    public SingleFirewallBenchmarkResult(long durationNanos, long baselineNanos) {
        this.durationNanos = durationNanos;
        this.durationMillis = TimeUnit.MILLISECONDS.convert(durationNanos, TimeUnit.NANOSECONDS);
        this.speedup = (double) baselineNanos / durationNanos;
    }

}
