package packetfilter.drivers;

import java.util.concurrent.ExecutionException;

import packetfilter.firewall.Firewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.generators.concurrent.PacketProducer;
import packetfilter.packet.workers.pools.PacketTaskPool;

public abstract class AbstractTaskPoolDriver<F extends Firewall> extends AbstractPoolDriver<F, PacketTaskPool> {

    public AbstractTaskPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    public void process(int nPackets) throws InterruptedException, ExecutionException {
        var packetsPerTask = 123;
        this.processWithPool(nPackets, packetsPerTask);
    }

    @Override
    protected PacketTaskPool createPool() {
        return new PacketTaskPool(nThreads);
    }

    protected void processWithPool(int nPackets, int packetsPerTask) throws InterruptedException, ExecutionException {
        var producer = new PacketProducer(nPackets, pktGenConfig);
        var pool = createPool();
        pool.configure(firewallService, producer, packetsPerTask).start().join();
    }

}
