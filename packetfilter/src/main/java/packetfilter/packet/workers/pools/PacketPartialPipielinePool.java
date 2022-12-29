package packetfilter.packet.workers.pools;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.PacketGeneratorWorker;
import packetfilter.packet.workers.PipelineConfigPacketWorker;
import packetfilter.packet.workers.PipelineDataPacketWorker;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

/**
 * Implements a two-pipeline processing architecture, where the first phase
 * processes config packets, and the second phase processes data packets.
 * 
 * Architecture:
 * 
 * There is one dispatcher thread that sends work to the first phase (config
 * workers). The config worker hands work to workers in the next phase (data
 * workers), in a round-robin fashion.
 * 
 * Each worker contains an internal queue to lower contention. Either the
 * dispatcher thread or a worker from the previuos phase can enqueue work
 * to a worker.
 * 
 * Work is only delivered from one phase to the next if the packet needs to
 * be further processed.
 */
public class PacketPartialPipielinePool extends PacketProcssingPool {

    private int numDataWorkers;
    private int numConfigWorkers;
    private ExecutorService dataWorkerPool;
    private ExecutorService configWorkerPool;
    private PipelineDataPacketWorker[] dataWorkers;
    private PipelineConfigPacketWorker[] configWorkers;
    private Future<?>[] dataFutures;
    private Future<?>[] configFutures;

    private int nPackets;
    private FirewallService firewallService;
    private PacketGeneratorConfig pktGenConfig;
    private boolean started = false;

    public PacketPartialPipielinePool(PartialPipelineWorkflow workflow) {
        super(workflow.numTotalWorkers());
        this.numDataWorkers = workflow.numConfigWorkers();
        this.numConfigWorkers = workflow.numDataWorkers();
        this.dataWorkerPool = Executors.newFixedThreadPool(numDataWorkers);
        this.configWorkerPool = Executors.newFixedThreadPool(numConfigWorkers);
        this.dataFutures = new Future<?>[numDataWorkers];
        this.configFutures = new Future<?>[numConfigWorkers];
    }

    public StaticForkJoinable configure(FirewallService firewallService, int nPackets,
            PacketGeneratorConfig pktGenConfig) {
        this.firewallService = firewallService;
        this.nPackets = nPackets;
        this.pktGenConfig = pktGenConfig;
        this.dataWorkers = newDataPacketWorkers(numDataWorkers);
        this.configWorkers = newConfigPacketWorkers(numConfigWorkers);
        return this;
    }

    @Override
    public StaticForkJoinable start() {
        if (started) {
            return this;
        }

        // define phase 1
        for (int i = 0; i < this.configWorkers.length; i++) {
            var handle = this.configWorkerPool.submit(configWorkers[i]);
            configFutures[i] = handle;
        }

        // define phase 2
        for (int i = 0; i < this.dataWorkers.length; i++) {
            var handle = this.dataWorkerPool.submit(dataWorkers[i]);
            dataFutures[i] = handle;
        }

        return this;
    }

    @Override
    public StaticForkJoinable join() throws InterruptedException, ExecutionException {
        for (var handle : configFutures) {
            handle.get();
        }
        this.configWorkerPool.shutdown();

        for (var dataWorker : dataWorkers) {
            dataWorker.signalTermination();
        }

        for (var handle : dataFutures) {
            handle.get();
        }
        this.dataWorkerPool.shutdown();

        int firstPhaseWorkerCnt = numConfigWorkers;
        int leftover = nPackets % firstPhaseWorkerCnt;
        if (leftover > 0) {
            var leftoverWorker = new PacketGeneratorWorker(firewallService, leftover, pktGenConfig);
            leftoverWorker.run();
        }

        started = false;

        return this;
    }

    public PipelineDataPacketWorker[] newDataPacketWorkers(int nWorkers) {
        var arr = new PipelineDataPacketWorker[nWorkers];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new PipelineDataPacketWorker(firewallService, nPackets / nWorkers);
        }
        return arr;
    }

    public PipelineConfigPacketWorker[] newConfigPacketWorkers(int nWorkers) {
        var arr = new PipelineConfigPacketWorker[nWorkers];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new PipelineConfigPacketWorker(firewallService, nPackets / nWorkers, pktGenConfig, dataWorkers);
        }
        return arr;
    }

}
