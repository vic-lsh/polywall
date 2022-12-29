package packetfilter.drivers.serial;

import packetfilter.drivers.AbstractFirewallDriver;
import packetfilter.firewall.Firewall;
import packetfilter.firewall.SerialFirewall;
import packetfilter.firewall.service.FirewallService;
import packetfilter.firewall.service.SerialFirewallService;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.PacketWorker;

public class SerialFirewallDriver extends AbstractFirewallDriver<SerialFirewall> {

    private PacketWorker worker;

    public SerialFirewallDriver(PacketGeneratorConfig packetGenConfg) {
        super(packetGenConfg);
        this.worker = new PacketWorker(this.firewallService);
    }

    @Override
    public void process(int nPackets) throws InterruptedException {
        int processed = 0;
        while (processed < nPackets) {
            Packet pkt = pktGen.getPacket();
            worker.process(pkt);
            processed++;
        }
    }

    @Override
    protected SerialFirewall createFirewall() {
        return new SerialFirewall();
    }

    @Override
    protected FirewallService createFirewallService(SerialFirewall firewall) {
        return new SerialFirewallService<Firewall>(firewall);
    }

}
