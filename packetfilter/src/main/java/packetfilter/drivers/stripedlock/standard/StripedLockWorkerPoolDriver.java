package packetfilter.drivers.stripedlock.standard;

import packetfilter.drivers.AbstractWorkerPoolDriver;
import packetfilter.firewall.StripedLockFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class StripedLockWorkerPoolDriver extends AbstractWorkerPoolDriver<StripedLockFirewall>
        implements FirewallFactory {

    public StripedLockWorkerPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected StripedLockFirewall createFirewall() {
        return this.createStripedLockFirewall();
    }

}
