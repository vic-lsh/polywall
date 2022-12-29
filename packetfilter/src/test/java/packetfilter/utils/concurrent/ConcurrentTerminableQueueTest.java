package packetfilter.utils.concurrent;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.Packet;

public class ConcurrentTerminableQueueTest extends TerminableQueueTestBase<ConcurrentTerminableQueue<Packet>> {

    @Override
    protected ConcurrentTerminableQueue<Packet> createDefaultQueue() {
        return new ConcurrentTerminableQueue<>();
    }

    @Test
    public void testReturnsNullWhenPollingEmptyQueue() throws QueueTerminatedException {
        assertNull(queue.poll());
    }

    @Test
    public void testConcurrentQueueTermination() throws InterruptedException {
        var enqueuer = new Thread(() -> this.enqueueThenTerminate(255));
        var dequeuer = new Thread(this::dequeueUntilTerminated);

        int maxExecTimeMillis = 1000;
        enqueuer.start();
        dequeuer.start();
        dequeuer.join(maxExecTimeMillis);
        enqueuer.join();
    }

    private void enqueueThenTerminate(int enqueueCount) {
        for (int i = 0; i < enqueueCount; i++) {
            try {
                queue.add(PacketTestHelpers.makeOnePacket());
            } catch (Exception e) {
                System.exit(1);
            }
        }
        queue.terminate();
    }

    private void dequeueUntilTerminated() {
        while (true) {
            try {
                queue.poll();
            } catch (QueueTerminatedException e) {
                return;
            }
        }
    }

}
