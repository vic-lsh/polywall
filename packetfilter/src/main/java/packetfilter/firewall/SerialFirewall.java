package packetfilter.firewall;

import java.util.AbstractMap;
import java.util.HashMap;

import packetfilter.firewall.internals.PNGMap;
import packetfilter.firewall.internals.RMap;
import packetfilter.packet.Packet;
import packetfilter.range.DisjointRange;
import packetfilter.range.SerialDisjointRange;
import packetfilter.utils.ArgumentValidator;

public class SerialFirewall implements Firewall {

    private PNGMap PNG;
    private RMap R;

    public SerialFirewall() {
        this.PNG = new SerialPNGMap();
        this.R = new SerialRMap();
    }

    @Override
    public boolean decide(Packet pkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsDataPacket(pkt);

        int src = pkt.header.source, dst = pkt.header.dest;
        return PNG.decide(src) && R.decide(src, dst);
    }

    @Override
    public void update(Packet configPkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsConfigPacket(configPkt);

        var cfg = configPkt.config;

        PNG.put(cfg.address, cfg.personaNonGrata);

        R.getOrCreate(cfg.address).set(cfg.addressBegin, cfg.addressEnd, cfg.acceptingRange);
    }

}

class SerialPNGMap extends PNGMap {

    @Override
    protected AbstractMap<Integer, Boolean> createMap() {
        return new HashMap<>();
    }

}

class SerialRMap extends RMap {

    @Override
    protected AbstractMap<Integer, DisjointRange> createMap() {
        return new HashMap<>();
    }

    @Override
    protected DisjointRange createDisjointRange() {
        return new SerialDisjointRange();
    }

}