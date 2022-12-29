package packetfilter.packet.workers;

import packetfilter.firewall.SerialFirewall;
import packetfilter.firewall.service.SerialFirewallService;

public class PackertWorkerTest extends PacketWorkerTestBase<PacketWorker> {

    @Override
    protected PacketWorker createDefaultTestWorker() {
        var service = new SerialFirewallService<>(new SerialFirewall());
        return new PacketWorker(service);
    }

    @Override
    protected boolean isSpecializedWorker() {
        return false;
    }

}
