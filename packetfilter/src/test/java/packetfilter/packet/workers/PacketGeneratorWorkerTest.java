package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import packetfilter.firewall.SerialFirewall;
import packetfilter.firewall.service.SerialFirewallService;

public class PacketGeneratorWorkerTest extends PacketWorkerTestBase<PacketGeneratorWorker> {

    protected int nPackets = 1000;

    @Override
    protected PacketGeneratorWorker createDefaultTestWorker() {
        var service = new SerialFirewallService<>(new SerialFirewall());
        return new PacketGeneratorWorker(service, nPackets, pktGenConfig);
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
}
