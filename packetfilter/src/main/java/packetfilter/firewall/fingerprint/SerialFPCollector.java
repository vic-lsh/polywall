package packetfilter.firewall.fingerprint;

import java.util.HashMap;

public class SerialFPCollector implements FPCollector {

    private HashMap<Integer, Integer> fingerprints;

    public SerialFPCollector() {
        this.fingerprints = new HashMap<>();
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
