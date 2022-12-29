package packetfilter.packet.generators.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class PacketProducerTest {

    private PacketGeneratorConfig pktGenConfig;

    @BeforeEach
    public void readConfig() throws IOException, ConfigFileMappingException {
        this.pktGenConfig = new ConfigReader().readConfig("preset1");
    }

    @Test
    public void testInitialization() {
        final int numPackets = 20;
        new PacketProducer(numPackets, this.pktGenConfig);
    }

    @Test
    public void testNumPacketsMustBeNonNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PacketProducer(-1, this.pktGenConfig);
        });
    }

    @Test
    public void testCanPollNPackets() {
        int pktCount = 1_000;
        var producer = new PacketProducer(pktCount, this.pktGenConfig);

        int count = 0;
        while (true) {
            try {
                if (producer.poll() != null) {
                    count++;
                }
            } catch (EmptyProducerException e) {
                break;
            }
        }

        assertEquals(pktCount, count);
    }

    @Nested
    class MultiThreadingTest {
        class PacketConsumer implements Runnable {

            int count = 0;
            private PacketProducer producer;

            public PacketConsumer(PacketProducer producer) {
                this.producer = producer;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        if (producer.poll() != null) {
                            count++;
                        }
                    }
                } catch (EmptyProducerException e) {
                    return;
                }
            }
        }

        @Test
        public void testSupportsMultiThreadedPolling() throws InterruptedException {
            final int NUM_THREADS = 64;
            final int NUM_PKTS = 100000;

            var producer = new PacketProducer(NUM_PKTS, pktGenConfig);

            ArrayList<PacketConsumer> consumers = new ArrayList<>();
            Thread[] threads = new Thread[NUM_THREADS];

            for (int i = 0; i < NUM_THREADS; i++) {
                var consumer = new PacketConsumer(producer);
                threads[i] = new Thread(consumer);
                consumers.add(consumer);
            }

            for (var thread : threads) {
                thread.start();
            }
            for (var thread : threads) {
                thread.join();
            }

            int sum = consumers.stream().map(c -> c.count).reduce(0, (a, b) -> a + b);
            assertEquals(NUM_PKTS, sum);
        }
    }

}
