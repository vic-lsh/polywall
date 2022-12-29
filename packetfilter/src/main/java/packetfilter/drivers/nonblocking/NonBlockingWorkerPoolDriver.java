package packetfilter.drivers.nonblocking;

import packetfilter.drivers.AbstractWorkerPoolDriver;
import packetfilter.firewall.NonBlockingFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class NonBlockingWorkerPoolDriver extends AbstractWorkerPoolDriver<NonBlockingFirewall> {

    public NonBlockingWorkerPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected NonBlockingFirewall createFirewall() {
        return new NonBlockingFirewall();
    }

}
