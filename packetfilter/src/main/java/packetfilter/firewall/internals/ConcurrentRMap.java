package packetfilter.firewall.internals;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

import packetfilter.range.ConcurrentDisjointRange;
import packetfilter.range.DisjointRange;

public class ConcurrentRMap extends RMap {

    @Override
    protected AbstractMap<Integer, DisjointRange> createMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    protected DisjointRange createDisjointRange() {
        return new ConcurrentDisjointRange();
    }
}