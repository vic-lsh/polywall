package packetfilter.drivers;

import java.util.concurrent.ExecutionException;

import packetfilter.firewall.Firewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.PacketWorkerPool;

public abstract class AbstractWorkerPoolDriver<F extends Firewall> extends AbstractPoolDriver<F, PacketWorkerPool> {

    public AbstractWorkerPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    public void process(int nPackets) throws InterruptedException, ExecutionException {
        this.processWithPool(nPackets);
    }

    @Override
    protected PacketWorkerPool createPool() {
        return new PacketWorkerPool(nThreads);
    }

    protected void processWithPool(int nPackets) throws InterruptedException, ExecutionException {
        createPool()
                .configure(firewallService, nPackets, pktGenConfig)
                .start().join();
    }

}
