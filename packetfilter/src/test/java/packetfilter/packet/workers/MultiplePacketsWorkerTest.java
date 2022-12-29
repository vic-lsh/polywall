package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import packetfilter.firewall.SerialFirewall;
import packetfilter.firewall.service.SerialFirewallService;
import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.Packet;

public class MultiplePacketsWorkerTest extends PacketWorkerTestBase<MultiplePacketsWorker> {

    protected int nPackets = 100;

    @Override
    protected MultiplePacketsWorker createDefaultTestWorker() {
        var service = new SerialFirewallService<>(new SerialFirewall());
        return new MultiplePacketsWorker(service, PacketTestHelpers.makeNPackets(nPackets, pktGen));
    }

    @Override
    protected boolean isSpecializedWorker() {
        return false;
    }

    @Test
    public void testRunWorker() throws InterruptedException {
        startJoinRunnable(worker);
        assertEquals(nPackets, worker.getProcessedCount());
    }

    @Test
    public void testNullValuesCauseEarlyTermination() throws InterruptedException {
        Packet[] packets = new Packet[] {
                pktGen.getPacket(),
                pktGen.getPacket(),
                null, // should terminate here
                pktGen.getPacket()
        };

        var worker = new MultiplePacketsWorker(makeSerialService(), packets);
        startJoinRunnable(worker);

        assertEquals(2, worker.getProcessedCount());
        assertNotEquals(packets.length, worker.getProcessedCount());
    }

}
