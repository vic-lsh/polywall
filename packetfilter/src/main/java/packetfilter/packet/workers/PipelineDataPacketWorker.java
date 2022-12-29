package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class PipelineDataPacketWorker extends PipelinePacketWorker {

    public PipelineDataPacketWorker(FirewallService service, int numPackets, PacketGeneratorConfig pktGenConfig,
            PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, pktGenConfig, nextPhase);
    }

    public PipelineDataPacketWorker(FirewallService service, int numPackets,
            PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, nextPhase);
    }

    public PipelineDataPacketWorker(FirewallService service, int numPackets) {
        super(service, numPackets);
    }

    @Override
    public boolean process(Packet pkt) {
        inboundCount++;
        boolean processed = this.service.processIfDataPacket(pkt);
        if (processed) {
            processedCount++;
        }
        boolean shouldProceed = !processed;
        return shouldProceed;
    }

}
