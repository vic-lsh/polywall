package packetfilter.drivers.stripedlock.pngr;

import packetfilter.drivers.AbstractWorkerPoolDriver;
import packetfilter.firewall.StripedLockPNGRFilrewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class StripedLockPNGRWorkerPoolDriver extends AbstractWorkerPoolDriver<StripedLockPNGRFilrewall>
        implements FirewallFactory {

    public StripedLockPNGRWorkerPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected StripedLockPNGRFilrewall createFirewall() {
        return this.createStripedLockPNGRFirewall();
    }

}
