package packetfilter.drivers.nonblocking;

import packetfilter.drivers.AbstractPartialPipelineDriver;
import packetfilter.firewall.NonBlockingFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

public class NonBlockingPartialPipelineDriver extends AbstractPartialPipelineDriver<NonBlockingFirewall> {

    public NonBlockingPartialPipelineDriver(PartialPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected NonBlockingFirewall createFirewall() {
        return new NonBlockingFirewall();
    }

}
