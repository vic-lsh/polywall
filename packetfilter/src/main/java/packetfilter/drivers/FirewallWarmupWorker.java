package packetfilter.drivers;

import packetfilter.firewall.Firewall;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class FirewallWarmupWorker extends Thread {

    private final PacketGenerator pktGen;
    private final Firewall firewall;
    private volatile int processed = 0;
    private final int total;

    public FirewallWarmupWorker(Firewall firewall, PacketGeneratorConfig pktGenConfig) {
        this.firewall = firewall;
        this.pktGen = pktGenConfig.toPacketGenerator();
        this.total = computeWarmupPacketCount(pktGenConfig.numAddressesLog());
    }

    @Override
    public void run() {
        for (processed = 0; processed < total; processed++) {
            this.firewall.update(this.pktGen.getConfigPacket());
        }
    }

    public int getProcessedCount() {
        return this.processed;
    }

    public int getTotal() {
        return this.total;
    }

    public double getProgress() {
        return (double) this.processed / (double) this.total;
    }

    private int computeWarmupPacketCount(int numAddressLog) {
        // uses A^(3/2) config packets to let the permission table reach a stable
        // state. A is defined as 2^numAddressLog.
        return (int) Math.pow(Math.pow(2, numAddressLog), 1.5);
    }
}
