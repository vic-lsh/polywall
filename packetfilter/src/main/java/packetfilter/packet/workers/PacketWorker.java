package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;

public class PacketWorker extends AbstractPacketWorker {

    public PacketWorker(FirewallService service) {
        super(service);
    }

}
