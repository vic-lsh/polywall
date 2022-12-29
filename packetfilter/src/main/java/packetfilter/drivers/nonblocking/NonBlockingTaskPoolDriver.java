package packetfilter.drivers.nonblocking;

import packetfilter.drivers.AbstractTaskPoolDriver;
import packetfilter.firewall.NonBlockingFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class NonBlockingTaskPoolDriver extends AbstractTaskPoolDriver<NonBlockingFirewall> {

    public NonBlockingTaskPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected NonBlockingFirewall createFirewall() {
        return new NonBlockingFirewall();
    }

}
