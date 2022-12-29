package packetfilter.firewall;

import packetfilter.firewall.internals.NonBlockingPNGRMap;
import packetfilter.packet.Packet;

public class NonBlockingFirewall implements Firewall {

    private NonBlockingPNGRMap PNGR;

    public NonBlockingFirewall() {
        this.PNGR = new NonBlockingPNGRMap();
    }

    @Override
    public boolean decide(Packet pkt) throws IllegalArgumentException {
        return this.PNGR.decide(pkt);
    }

    @Override
    public void update(Packet configPkt) throws IllegalArgumentException {
        this.PNGR.update(configPkt);
    }

}
