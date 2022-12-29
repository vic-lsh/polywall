package packetfilter.packet.generators.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;

import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.utils.ArgumentValidator;

public class PacketProducer {

    private ConcurrentLinkedQueue<Packet> queue;
    private volatile boolean produceCompleted;
    private Thread dispatcher;

    public PacketProducer(int nPackets, PacketGeneratorConfig packetGenConfig) {
        PacketGenerator pktGen = packetGenConfig.toPacketGenerator();
        queue = new ConcurrentLinkedQueue<>();
        produceCompleted = false;

        ArgumentValidator.validateGreaterEqualZero(nPackets);

        dispatcher = new Thread(() -> {
            for (int i = 0; i < nPackets; i++) {
                queue.add(pktGen.getPacket());
            }
            produceCompleted = true;
        });
        dispatcher.start();
    }

    /**
     * Provides a new packet if not all packets have been polled.
     * 
     * This method is thread-safe.
     * 
     * @return a new packet to process, or `null` if the producer is slower than
     *         consumer(s).
     * @throws EmptyProducerException if the number of polled packets have
     *                                reached `nPackets` specified in the producer
     *                                configuration.
     */
    public Packet poll() throws EmptyProducerException {
        var next = queue.poll();
        if (next == null && produceCompleted) {
            throw new EmptyProducerException();
        }
        return next;
    }
}
