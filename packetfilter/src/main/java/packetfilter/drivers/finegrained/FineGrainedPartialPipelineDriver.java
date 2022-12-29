package packetfilter.drivers.finegrained;

import packetfilter.drivers.AbstractPartialPipelineDriver;
import packetfilter.firewall.FineGrainedFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

public class FineGrainedPartialPipelineDriver extends AbstractPartialPipelineDriver<FineGrainedFirewall> {

    public FineGrainedPartialPipelineDriver(PartialPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected FineGrainedFirewall createFirewall() {
        return new FineGrainedFirewall();
    }

}
