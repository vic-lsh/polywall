package packetfilter.drivers.finegrained;

import packetfilter.drivers.AbstractWorkerPoolDriver;
import packetfilter.firewall.FineGrainedFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class FineGrainedWorkerPoolDriver extends AbstractWorkerPoolDriver<FineGrainedFirewall> {

    public FineGrainedWorkerPoolDriver(int numWorkers, PacketGeneratorConfig packetGenCfg) {
        super(numWorkers, packetGenCfg);
    }

    @Override
    protected FineGrainedFirewall createFirewall() {
        return new FineGrainedFirewall();
    }

}
