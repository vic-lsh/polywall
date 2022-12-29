package packetfilter.firebench.results;

import java.text.DecimalFormat;
import java.util.HashMap;

import packetfilter.utils.DelimitedNumberFormatter;

public record MinimalPrintableBenchOutput(HashMap<String, SingleFirewallBenchmarkResult> results) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        var numFormatter = new DelimitedNumberFormatter();
        var speedupFormatter = new DecimalFormat("#.###");

        sb.append("-------------------------- RESULTS BEGIN ------------------------------\n");

        for (var entry : this.results.entrySet()) {
            String firewallName = entry.getKey();
            var benchResult = entry.getValue();

            sb.append(
                    String.format("Firewall %-50s Duration (millis): '%s'\t(speedup: %s)\n",
                            String.format("'%s'", firewallName),
                            numFormatter.format(benchResult.durationMillis),
                            speedupFormatter.format(benchResult.speedup)));
        }

        sb.append("--------------------------- RESULTS END -------------------------------\n");

        return sb.toString();
    }

}
