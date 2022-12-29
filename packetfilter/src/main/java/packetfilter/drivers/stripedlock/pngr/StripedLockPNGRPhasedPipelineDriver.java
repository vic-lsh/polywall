package packetfilter.drivers.stripedlock.pngr;

import packetfilter.drivers.AbstractPhasedPipelineDriver;
import packetfilter.firewall.StripedLockPNGRFilrewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

public class StripedLockPNGRPhasedPipelineDriver extends AbstractPhasedPipelineDriver<StripedLockPNGRFilrewall>
        implements FirewallFactory {

    public StripedLockPNGRPhasedPipelineDriver(PhasedPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow, packetGenConfig);
    }

    @Override
    protected StripedLockPNGRFilrewall createFirewall() {
        return this.createStripedLockPNGRFirewall();
    }

}
