package packetfilter.drivers.stripedlock.standard;

import packetfilter.drivers.AbstractPartialPipelineDriver;
import packetfilter.firewall.StripedLockFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

public class StripedLockPartialPipelineDriver extends AbstractPartialPipelineDriver<StripedLockFirewall>
        implements FirewallFactory {

    public StripedLockPartialPipelineDriver(PartialPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected StripedLockFirewall createFirewall() {
        return this.createStripedLockFirewall();
    }

}
