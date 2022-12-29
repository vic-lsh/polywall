package packetfilter.firebench.config;

/**
 * Config for firewall benchmark executions.
 * 
 * @param numPackets        number of packets to process for each firewall.
 * @param numTrials         number of trials to execute for each firewall.
 * @param packetGenStrategy what packet generator configs to use in
 *                          benchmarking.
 * @param threadingStrategy how many threads to use during benchmarking.
 */
public record BenchConfig(
        int numPackets,
        int numTrials,
        PacketGenStrategy packetGenStrategy,
        ThreadingStrategy threadingStrategy) {
}
