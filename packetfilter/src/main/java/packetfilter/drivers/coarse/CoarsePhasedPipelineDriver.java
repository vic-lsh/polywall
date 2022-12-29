package packetfilter.drivers.coarse;

import packetfilter.drivers.AbstractPhasedPipelineDriver;
import packetfilter.firewall.CoarseFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

public class CoarsePhasedPipelineDriver extends AbstractPhasedPipelineDriver<CoarseFirewall> {

    public CoarsePhasedPipelineDriver(PhasedPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected CoarseFirewall createFirewall() {
        return new CoarseFirewall();
    }

}
