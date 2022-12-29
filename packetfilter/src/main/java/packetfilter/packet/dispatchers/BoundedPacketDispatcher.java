package packetfilter.packet.dispatchers;

import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.utils.concurrent.ConcurrentTerminableQueue;
import packetfilter.utils.concurrent.TerminableQueue;

public class BoundedPacketDispatcher extends Thread {

    private final int nPackets;
    private final int queueCapacity;
    private final PacketGeneratorConfig pktGenConfig;
    private ConcurrentTerminableQueue<Packet>[] queues;

    public BoundedPacketDispatcher(int nThreads, int nPackets, int capacity, PacketGeneratorConfig pktGenConfig) {
        this.nPackets = nPackets;
        this.queueCapacity = capacity;
        this.pktGenConfig = pktGenConfig;
        this.queues = new ConcurrentTerminableQueue[nThreads];
        for (int i = 0; i < queues.length; i++) {
            queues[i] = new ConcurrentTerminableQueue<>();
        }
    }

    @Override
    public void run() {
        PacketGenerator pktgen = pktGenConfig.toPacketGenerator();

        int dispatched = 0;

        while (dispatched < nPackets) {
            for (var queue : queues) {
                if (queue.size() < queueCapacity) {
                    queue.add(pktgen.getPacket());
                    dispatched++;
                }
            }
        }

        for (var queue : queues) {
            queue.terminate();
        }
    }

    public TerminableQueue<Packet, ?>[] getPacketQueues() {
        return this.queues;
    }
}
