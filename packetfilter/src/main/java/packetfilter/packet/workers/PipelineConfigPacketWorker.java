package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class PipelineConfigPacketWorker extends PipelinePacketWorker {

    public PipelineConfigPacketWorker(FirewallService service, int numPackets, PacketGeneratorConfig pktGenConfig,
            PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, pktGenConfig, nextPhase);
    }

    public PipelineConfigPacketWorker(FirewallService service, int numPackets,
            PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, nextPhase);
    }

    public PipelineConfigPacketWorker(FirewallService service, int numPackets) {
        super(service, numPackets);
    }

    @Override
    public boolean process(Packet pkt) {
        inboundCount++;
        boolean processed = this.service.processIfConfigPacket(pkt);
        if (processed) {
            processedCount++;
        }
        boolean shouldProceed = !processed;
        return shouldProceed;
    }

}
