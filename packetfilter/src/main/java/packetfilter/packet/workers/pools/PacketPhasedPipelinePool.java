package packetfilter.packet.workers.pools;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.PacketGeneratorWorker;
import packetfilter.packet.workers.PipelinePacketControlWorker;
import packetfilter.packet.workers.PipelinePacketFingerprintWorker;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

/**
 * Implements a two-phased pipeline, where the first phase performs
 * control-related tasks, while the second phase performs fingerprinting.
 * 
 * Workers in the first phase do not specialize in packet types. If a worker
 * receives a config packet, it updates the permission table. Otherwise the
 * worker checks if this data packet is allowed to proceed. If so, the worker
 * passes this packet to the next phase.
 * 
 * The next phase only receives data packets. Upon receipt, a worker calculates
 * the fingerprint of this data packet and stores it.
 */
public class PacketPhasedPipelinePool extends PacketProcssingPool {

    private PhasedPipelineWorkflow workflow;
    private FirewallService firewallService;
    private int nPackets;
    // private PacketGenerator pktGen;
    private PacketGeneratorConfig pktGenConfig;

    private PipelinePacketControlWorker[] controlWorkers;
    private ExecutorService controlWorkerPool;
    private Future<?>[] controlWorkerHandles;

    private PipelinePacketFingerprintWorker[] fpWorkers;
    private ExecutorService fpWorkerPool;
    private Future<?>[] fpWorkerHandles;

    public PacketPhasedPipelinePool(PhasedPipelineWorkflow workflow) {
        super(workflow.numTotalWorkers());
        this.workflow = workflow;
    }

    public PacketPhasedPipelinePool configure(FirewallService firewallService, int nPackets,
            PacketGeneratorConfig pktGenConfig) {
        this.firewallService = firewallService;
        this.nPackets = nPackets;
        this.pktGenConfig = pktGenConfig;

        this.controlWorkerPool = Executors.newFixedThreadPool(workflow.numCtrlPhaseWorkers());
        this.controlWorkerHandles = new Future<?>[workflow.numCtrlPhaseWorkers()];
        controlWorkers = new PipelinePacketControlWorker[workflow.numCtrlPhaseWorkers()];

        this.fpWorkerPool = Executors.newFixedThreadPool(workflow.numCollectionPhaseWorkers());
        this.fpWorkerHandles = new Future<?>[workflow.numCollectionPhaseWorkers()];
        fpWorkers = new PipelinePacketFingerprintWorker[workflow.numCollectionPhaseWorkers()];

        return this;
    }

    @Override
    public StaticForkJoinable start() {

        // second phase
        int pktsPerFPWorker = nPackets / workflow.numCollectionPhaseWorkers();
        for (int i = 0; i < fpWorkers.length; i++) {
            fpWorkers[i] = new PipelinePacketFingerprintWorker(firewallService, pktsPerFPWorker);
            var handle = this.fpWorkerPool.submit(fpWorkers[i]);
            this.fpWorkerHandles[i] = handle;
        }

        // first phase
        int pktsPerCtrlWorker = nPackets / workflow.numCtrlPhaseWorkers();
        for (int i = 0; i < workflow.numCtrlPhaseWorkers(); i++) {
            controlWorkers[i] = new PipelinePacketControlWorker(
                    firewallService, pktsPerCtrlWorker, pktGenConfig, fpWorkers);
            var handle = this.controlWorkerPool.submit(controlWorkers[i]);
            this.controlWorkerHandles[i] = handle;
        }

        return this;
    }

    @Override
    public StaticForkJoinable join() throws InterruptedException, ExecutionException {
        for (var handle : controlWorkerHandles) {
            handle.get();
        }
        this.controlWorkerPool.shutdown();

        for (var fpWorker : fpWorkers) {
            fpWorker.signalTermination();
        }

        for (var handle : fpWorkerHandles) {
            handle.get();
        }
        this.fpWorkerPool.shutdown();

        int leftovers = nPackets % workflow.numCtrlPhaseWorkers();
        if (leftovers > 0) {
            var leftoverWorker = new PacketGeneratorWorker(firewallService, leftovers, pktGenConfig);
            leftoverWorker.run();
        }

        return this;
    }

}
