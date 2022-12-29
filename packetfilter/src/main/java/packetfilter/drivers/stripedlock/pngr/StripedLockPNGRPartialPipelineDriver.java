package packetfilter.drivers.stripedlock.pngr;

import packetfilter.drivers.AbstractPartialPipelineDriver;
import packetfilter.firewall.StripedLockPNGRFilrewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

public class StripedLockPNGRPartialPipelineDriver extends AbstractPartialPipelineDriver<StripedLockPNGRFilrewall>
        implements FirewallFactory {

    public StripedLockPNGRPartialPipelineDriver(PartialPipelineWorkflow workflow,
            PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected StripedLockPNGRFilrewall createFirewall() {
        return this.createStripedLockPNGRFirewall();
    }

}
