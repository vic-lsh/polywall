package packetfilter.drivers.finegrained;

import packetfilter.drivers.AbstractPhasedPipelineDriver;
import packetfilter.firewall.FineGrainedFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

public class FineGrainedPhasedPipelineDriver extends AbstractPhasedPipelineDriver<FineGrainedFirewall> {

    public FineGrainedPhasedPipelineDriver(PhasedPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected FineGrainedFirewall createFirewall() {
        return new FineGrainedFirewall();
    }
}
