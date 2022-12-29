package packetfilter.drivers.finegrained;

import packetfilter.drivers.AbstractTaskPoolDriver;
import packetfilter.firewall.FineGrainedFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class FineGrainedTaskPoolDriver extends AbstractTaskPoolDriver<FineGrainedFirewall> {

    public FineGrainedTaskPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected FineGrainedFirewall createFirewall() {
        return new FineGrainedFirewall();
    }

}
