package packetfilter.drivers.stripedlock.pngr;

import packetfilter.drivers.AbstractTaskPoolDriver;
import packetfilter.firewall.StripedLockPNGRFilrewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class StripedLockPNGRTaskPoolDriver extends AbstractTaskPoolDriver<StripedLockPNGRFilrewall>
        implements FirewallFactory {

    public StripedLockPNGRTaskPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected StripedLockPNGRFilrewall createFirewall() {
        return this.createStripedLockPNGRFirewall();
    }

}
