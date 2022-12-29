package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.utils.ArgumentValidator;

public class PipelinePacketFingerprintWorker extends PipelinePacketWorker {

    public PipelinePacketFingerprintWorker(FirewallService service, int numPackets, PacketGeneratorConfig pktGenConfig,
            PipelinePacketWorker[] nextPhase) {
        super(service, numPackets, pktGenConfig, nextPhase);
    }

    public PipelinePacketFingerprintWorker(FirewallService service, int numPackets) {
        super(service, numPackets);
    }

    @Override
    public boolean process(Packet pkt) {
        inboundCount++;

        ArgumentValidator.assertIsDataPacket(pkt);

        this.service.collectFingerprint(pkt);
        processedCount++;

        return false;
    }

}
