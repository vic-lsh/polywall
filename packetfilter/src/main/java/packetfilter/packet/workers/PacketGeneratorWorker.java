package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class PacketGeneratorWorker extends AbstractPacketWorker implements Runnable {

    private PacketGenerator pktGen;
    private int nPackets;

    /**
     * Create a Runnable task that processes N packets.
     * 
     * @param service      the firewall service instance to process packets with.
     * @param nPackets     number of packets to process.
     * @param pktGenConfig config defining parameters of the packets to generate.
     */
    public PacketGeneratorWorker(
            FirewallService service,
            int nPackets,
            PacketGeneratorConfig pktGenConfig) {
        super(service);
        this.pktGen = pktGenConfig.toPacketGenerator();
        this.nPackets = nPackets;
    }

    @Override
    public void run() {
        while (getProcessedCount() < this.nPackets) {
            this.process(this.pktGen.getPacket());
        }
    }

}
