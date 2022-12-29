package packetfilter.packet.dispatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class BoundedPacketDispatcherTest {

    @Test
    public void testGetPacketQueues() throws IOException, ConfigFileMappingException {
        int nThreads = 10;
        int nPackets = 100_000;
        int queueCapacity = 30;
        PacketGeneratorConfig pktGenConfig = new ConfigReader().readConfig("preset1");
        BoundedPacketDispatcher dispatcher = new BoundedPacketDispatcher(nThreads, nPackets, queueCapacity,
                pktGenConfig);

        var queues = dispatcher.getPacketQueues();
        assertEquals(nThreads, queues.length);
        for (int i = 0; i < queues.length; i++) {
            assertNotNull(queues[i]);
        }
    }
}
