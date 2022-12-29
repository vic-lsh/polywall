package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class PipelinePacketControlWorker extends PipelinePacketWorker {

    public PipelinePacketControlWorker(FirewallService service, int numPackets, PacketGeneratorConfig pktGenConfig,
            PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, pktGenConfig, nextPhase);
    }

    public PipelinePacketControlWorker(FirewallService service, int numPackets, PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, nextPhase);
    }

    public PipelinePacketControlWorker(FirewallService service, int numPackets) {
        super(service, numPackets);
    }

    @Override
    public boolean process(Packet pkt) {
        inboundCount++;
        boolean shouldContinue = this.service.checkOrUpdateAccessControl(pkt);
        processedCount++;
        return shouldContinue;
    }

}
