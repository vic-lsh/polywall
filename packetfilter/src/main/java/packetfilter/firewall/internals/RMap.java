package packetfilter.firewall.internals;

import java.util.AbstractMap;

import packetfilter.range.DisjointRange;

public abstract class RMap {
    private AbstractMap<Integer, DisjointRange> map;

    protected abstract AbstractMap<Integer, DisjointRange> createMap();

    protected abstract DisjointRange createDisjointRange();

    public RMap() {
        this.map = this.createMap();
    }

    public boolean decide(int src, int dst) {
        DisjointRange permission = this.map.get(dst);
        if (permission == null) {
            return true;
        }
        return permission.get(src);
    }

    public DisjointRange getOrCreate(int address) {
        return this.map.computeIfAbsent(address, v -> this.createDisjointRange());
    }

    public void put(int address, DisjointRange disjointRange) {
        this.map.put(address, disjointRange);
    }

}
