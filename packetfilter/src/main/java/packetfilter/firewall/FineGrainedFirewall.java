package packetfilter.firewall;

import packetfilter.firewall.internals.ConcurrentPNGRMap;
import packetfilter.packet.Packet;

public class FineGrainedFirewall implements Firewall {
    private ConcurrentPNGRMap PNGR;

    public FineGrainedFirewall() {
        this.PNGR = new ConcurrentPNGRMap();
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
