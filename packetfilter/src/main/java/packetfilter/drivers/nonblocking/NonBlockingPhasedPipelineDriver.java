package packetfilter.drivers.nonblocking;

import packetfilter.drivers.AbstractPhasedPipelineDriver;
import packetfilter.firewall.NonBlockingFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

public class NonBlockingPhasedPipelineDriver extends AbstractPhasedPipelineDriver<NonBlockingFirewall> {

    public NonBlockingPhasedPipelineDriver(PhasedPipelineWorkflow workflow,
            PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected NonBlockingFirewall createFirewall() {
        return new NonBlockingFirewall();
    }

}
