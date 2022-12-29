package packetfilter.firewall.internals;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentPNGMap extends PNGMap {

    @Override
    protected AbstractMap<Integer, Boolean> createMap() {
        return new ConcurrentHashMap<>();
    }

}