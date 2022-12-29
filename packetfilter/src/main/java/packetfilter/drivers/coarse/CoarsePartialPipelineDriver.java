package packetfilter.drivers.coarse;

import packetfilter.drivers.AbstractPartialPipelineDriver;
import packetfilter.firewall.CoarseFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

public class CoarsePartialPipelineDriver extends AbstractPartialPipelineDriver<CoarseFirewall> {

    public CoarsePartialPipelineDriver(PartialPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected CoarseFirewall createFirewall() {
        return new CoarseFirewall();
    }

}
