package packetfilter.firewall.internals;

import java.util.AbstractMap;

public abstract class PNGMap {
    private AbstractMap<Integer, Boolean> map;

    protected abstract AbstractMap<Integer, Boolean> createMap();

    public PNGMap() {
        this.map = this.createMap();
    }

    public boolean decide(int address) {
        Boolean pngStatus = this.map.get(address);
        if (pngStatus == null) {
            return true;
        }
        return pngStatus != true;
    }

    public void put(int address, boolean personaNonGrata) {
        this.map.put(address, personaNonGrata);
    }
}