package packetfilter.firewall;

import packetfilter.firewall.internals.StripedLockPNGRMap;
import packetfilter.packet.Packet;

public class StripedLockPNGRFilrewall implements Firewall {

    private StripedLockPNGRMap PNGR;

    public StripedLockPNGRFilrewall(int numberOfLocks) {
        this.PNGR = new StripedLockPNGRMap(numberOfLocks);
    }

    @Override
    public boolean decide(Packet pkt) throws IllegalArgumentException {
        return this.PNGR.decide(pkt);
    }

    @Override
    public void update(Packet configPkt) throws IllegalArgumentException {
        this.PNGR.updateConfig(configPkt);
    }

}
