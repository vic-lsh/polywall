package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;

public class MultiplePacketsWorker extends AbstractPacketWorker implements Runnable {

    private Packet[] packets;

    /**
     * Create a Runnable task that passes multiple packets through a firewall.
     * 
     * Packets are processed in asscending order according to he array.
     * Processing the array halts as soon as a `null` packet is encountered,
     * so the array can be "null-padded".
     * 
     * @param service the firewall service instance to process packets with.
     * @param packets packets to process.
     */
    public MultiplePacketsWorker(FirewallService service, Packet[] packets) {
        super(service);
        this.packets = packets;
    }

    @Override
    public void run() {
        for (int i = 0; i < packets.length; i++) {
            if (packets[i] == null) {
                return;
            }
            this.process(packets[i]);
        }
    }

}
