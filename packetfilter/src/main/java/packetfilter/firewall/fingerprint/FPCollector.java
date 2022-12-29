package packetfilter.firewall.fingerprint;

/**
 * An interface that specifies the behavior of a class that performs packet
 * fingerprint collection.
 */
public interface FPCollector {

    /**
     * Collects the fingerprint of a packet.
     */
    public void collect(int fingerprint);

    /**
     * Obtains the number of packets whose finterprints are collected.
     * 
     * @return the number of fingerprints collected.
     */
    public int size();
}
