package packetfilter.drivers.stripedlock.standard;

import packetfilter.drivers.AbstractPhasedPipelineDriver;
import packetfilter.firewall.StripedLockFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

public class StripedLockPhasedPipelineDriver extends AbstractPhasedPipelineDriver<StripedLockFirewall>
        implements FirewallFactory {

    public StripedLockPhasedPipelineDriver(PhasedPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected StripedLockFirewall createFirewall() {
        return this.createStripedLockFirewall();
    }

}
