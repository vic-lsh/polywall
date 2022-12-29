package packetfilter.packet.workers.pools;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.PacketGeneratorWorker;
import packetfilter.packet.workers.PacketWorker;

public class PacketWorkerPool extends PacketProcssingPool {

    private FirewallService firewallService;
    private ExecutorService pool;
    private boolean didBeginExecution = false;
    private int nPackets;
    private PacketGeneratorConfig pktGenConfig;
    private final Future<?>[] joinHandles;

    /**
     * Create and use a pool of workers to independently process packets.
     * 
     * Workers only synchronize via the supplied firewall instance. The
     * firewall instance must be thread-safe.
     * 
     * @param nThreads number of threads used to process packets.
     */
    public PacketWorkerPool(int nThreads) {
        super(nThreads);
        this.pool = Executors.newFixedThreadPool(nThreads);
        this.joinHandles = new Future<?>[nThreads];
    }

    /**
     * Specifies dependencies and configurations before processing packets.
     * 
     * @param firewallService the firewall service to process all packets.
     * @param pktGenConfig    specification of how packets are to be generated.
     * @param nPackets        number of packets to be processed overall. Each worker
     *                        will process approximately `nPackets / nThreads`
     *                        number
     *                        of packets.
     * @return
     */
    public PacketWorkerPool configure(FirewallService firewallService, int nPackets,
            PacketGeneratorConfig pktGenConfig) {
        this.firewallService = firewallService;
        this.nPackets = nPackets;
        this.pktGenConfig = pktGenConfig;
        return this;
    }

    @Override
    public StaticForkJoinable start() {
        if (this.didBeginExecution) {
            return this;
        }

        int leftoverPktCount = nPackets % nThreads;
        PacketGenerator pktGen = this.pktGenConfig.toPacketGenerator();
        var leftoverWorker = new PacketWorker(this.firewallService);
        for (int i = 0; i < leftoverPktCount; i++) {
            leftoverWorker.process(pktGen.getPacket());
        }

        int pktPerThread = nPackets / nThreads;
        for (int i = 0; i < nThreads; i++) {
            var worker = new PacketGeneratorWorker(this.firewallService, pktPerThread, this.pktGenConfig);
            this.joinHandles[i] = this.pool.submit(worker);
        }

        this.didBeginExecution = true;

        return this;
    }

    @Override
    public StaticForkJoinable join() throws InterruptedException, ExecutionException {
        for (var handle : this.joinHandles) {
            handle.get();
        }

        this.pool.shutdown();

        return this;
    }

}
