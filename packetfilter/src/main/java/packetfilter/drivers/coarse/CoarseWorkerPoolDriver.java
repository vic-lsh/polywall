package packetfilter.drivers.coarse;

import packetfilter.drivers.AbstractWorkerPoolDriver;
import packetfilter.firewall.CoarseFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class CoarseWorkerPoolDriver extends AbstractWorkerPoolDriver<CoarseFirewall> {

    public CoarseWorkerPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected CoarseFirewall createFirewall() {
        return new CoarseFirewall();
    }

}
