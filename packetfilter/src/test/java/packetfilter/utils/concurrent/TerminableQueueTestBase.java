package packetfilter.utils.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.ConfigFileMappingException;

public abstract class TerminableQueueTestBase<Q extends TerminableQueue<Packet, ?>> {

    protected Q queue;

    protected abstract Q createDefaultQueue();

    @BeforeEach
    public void init() {
        this.queue = createDefaultQueue();
    }

    @Test
    public void testEnqueueDequeue() throws IOException, ConfigFileMappingException, QueueTerminatedException {
        var enqueued = PacketTestHelpers.makeOnePacket();
        queue.add(enqueued);
        var dequeued = queue.poll();
        assertEquals(enqueued, dequeued);
    }

    @Test
    public void testThrowsWhenDequeueAfterTermination() {
        queue.terminate();
        assertThrows(QueueTerminatedException.class, () -> queue.poll());
    }
}
