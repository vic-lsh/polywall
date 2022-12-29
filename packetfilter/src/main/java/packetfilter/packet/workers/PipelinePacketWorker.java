package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.utils.concurrent.QueueTerminatedException;
import packetfilter.utils.concurrent.ConcurrentTerminableQueue;

/**
 * Process packets as they are pushed to to a queue.
 * 
 * The worker terminates when it has reached its maximum number of packets to
 * process (configurable via the constructor). Alternatively, it can
 * be terminated by invoking `signalTermination()`, after which the worker
 * stops when remaining work in the queue is processed.
 */
public class PipelinePacketWorker extends AbstractPacketWorker implements Runnable {

    private ConcurrentTerminableQueue<Packet> queue;
    private int numPackets;
    private boolean hasNextPhase;
    PipelinePacketWorker[] nextPhase;

    private PacketGeneratorConfig pktGenConfig;

    public PipelinePacketWorker(FirewallService service, int numPackets) {
        super(service);
        this.numPackets = numPackets;
        this.queue = new ConcurrentTerminableQueue<Packet>();
        this.hasNextPhase = false;
    }

    public PipelinePacketWorker(FirewallService service, int numPackets, ConcurrentTerminableQueue<Packet> queue) {
        super(service);
        this.numPackets = numPackets;
        this.queue = queue;
        this.hasNextPhase = false;
    }

    public PipelinePacketWorker(FirewallService service, int numPackets, PipelinePacketWorker[] nextPhase) {
        this(service, numPackets);
        hasNextPhase = true;
        this.nextPhase = nextPhase;
        this.queue = new ConcurrentTerminableQueue<Packet>();
    }

    public PipelinePacketWorker(FirewallService service, int numPackets, PacketGeneratorConfig pktGenConfig,
            PipelinePacketWorker[] nextPhase) {
        super(service);
        this.numPackets = numPackets;
        this.pktGenConfig = pktGenConfig;
        this.hasNextPhase = true;
        this.nextPhase = nextPhase;
    }

    public void placeWork(Packet packet) {
        this.queue.add(packet);
    }

    public void signalTermination() {
        this.queue.terminate();
    }

    @Override
    public void run() {
        if (this.pktGenConfig != null) {
            processWithPktGenerator();
        } else {
            processWithQueue();
        }
    }

    private void processWithQueue() {
        while (inboundCount < numPackets) {
            Packet next;

            try {
                next = this.queue.poll();
            } catch (QueueTerminatedException e) {
                return;
            }

            if (next != null) {
                var shouldProceed = this.process(next);
                if (shouldProceed && hasNextPhase) {
                    nextPhase[inboundCount % nextPhase.length].placeWork(next);
                }
            }
        }
    }

    private void processWithPktGenerator() {
        PacketGenerator pktgen = this.pktGenConfig.toPacketGenerator();

        while (inboundCount < numPackets) {
            var pkt = pktgen.getPacket();
            boolean shouldProceed = this.process(pkt);
            if (shouldProceed && hasNextPhase) {
                nextPhase[inboundCount % nextPhase.length].placeWork(pkt);
            }
        }
    }

}
