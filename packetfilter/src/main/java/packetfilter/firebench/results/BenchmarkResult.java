package packetfilter.firebench.results;

import java.io.IOException;
import java.util.LinkedHashMap;

import packetfilter.firebench.config.ThreadingConfig;

public class BenchmarkResult {

    public final LinkedHashMap<String, PktConfigVariantResults> results;
    private BenchResultPersister persister;

    public BenchmarkResult() {
        this.results = new LinkedHashMap<>();
        this.persister = new BenchResultPersister(this, genUniqueOutputPath());
    }

    public void collect(
            String pktGenConfigName,
            ThreadingConfig threadingConfig,
            String firewallName,
            SingleFirewallBenchmarkResult result) {

        PktConfigVariantResults entry = this.results.computeIfAbsent(
                pktGenConfigName,
                name -> new PktConfigVariantResults());

        entry.add(threadingConfig, firewallName, result);

        this.persistResults();
    }

    public void print() {

    }

    public MinimalPrintableBenchOutput query(String pktGenConfigName, ThreadingConfig threadingConfig) {
        var pktConfigVariantResults = this.results.get(pktGenConfigName);
        if (pktConfigVariantResults == null) {
            return null;
        }

        var threadSpecificResults = pktConfigVariantResults.results.get(threadingConfig.defaultNumThreads);
        if (threadSpecificResults == null) {
            return null;
        }

        return threadSpecificResults.toPrintableResults();
    }

    private void persistResults() {
        try {
            this.persister.persist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String genUniqueOutputPath() {
        long currTime = System.nanoTime();
        return String.format("./bench/results_%d.json", currTime);
    }

}

class PktConfigVariantResults {

    public final String explainer = "Thread count to firewall benchmark results.";
    public final LinkedHashMap<Integer, ThreadConfigBenchmarkResult> results;

    public PktConfigVariantResults() {
        this.results = new LinkedHashMap<>();
    }

    public void add(ThreadingConfig threadingConfig, String firewallName, SingleFirewallBenchmarkResult result) {
        int numThreads = threadingConfig.defaultNumThreads;
        var entry = this.results.computeIfAbsent(numThreads, (tn) -> new ThreadConfigBenchmarkResult(threadingConfig));
        entry.add(firewallName, result);
    }
}

class ThreadConfigBenchmarkResult {

    public final ThreadingConfig threadingConfig;
    public final String explainer = "Firewall name to benchmark results.";
    public final LinkedHashMap<String, SingleFirewallBenchmarkResult> results;

    public ThreadConfigBenchmarkResult(ThreadingConfig threadingConfig) {
        this.threadingConfig = threadingConfig;
        this.results = new LinkedHashMap<>();
    }

    public void add(String firewallName, SingleFirewallBenchmarkResult result) {
        this.results.put(firewallName, result);
    }

    public MinimalPrintableBenchOutput toPrintableResults() {
        return new MinimalPrintableBenchOutput(this.results);
    }

}