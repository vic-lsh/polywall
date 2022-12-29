package packetfilter.firewall.fingerprint;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentFPCollector implements FPCollector {

    private ConcurrentHashMap<Integer, Integer> fingerprints;

    public ConcurrentFPCollector() {
        this.fingerprints = new ConcurrentHashMap<>();
    }

    @Override
    public void collect(int fingerprint) {
        var prev = this.fingerprints.putIfAbsent(fingerprint, 1);
        if (prev != null) {
            this.fingerprints.put(fingerprint, prev + 1);
        }
    }

    @Override
    public int size() {
        return this.fingerprints.values().stream().reduce(0, (acc, cur) -> acc + cur);
    }

}
