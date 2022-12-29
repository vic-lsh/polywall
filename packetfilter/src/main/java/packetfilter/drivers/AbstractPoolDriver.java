package packetfilter.drivers;

import java.util.concurrent.ExecutionException;

import packetfilter.firewall.Firewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.StaticForkJoinable;

public abstract class AbstractPoolDriver<F extends Firewall, P extends StaticForkJoinable> extends AbstractParallelFirewallDriver<F> {

    public AbstractPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    protected abstract P createPool();

    protected static void executePool(StaticForkJoinable pool) throws InterruptedException, ExecutionException {
        pool.start();
        pool.join();
    }
}
