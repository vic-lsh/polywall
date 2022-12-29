package packetfilter.drivers.stripedlock.standard;

import packetfilter.drivers.AbstractTaskPoolDriver;
import packetfilter.firewall.StripedLockFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class StripedLockTaskPoolDriver extends AbstractTaskPoolDriver<StripedLockFirewall>
        implements FirewallFactory {

    public StripedLockTaskPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected StripedLockFirewall createFirewall() {
        return this.createStripedLockFirewall();
    }

}
