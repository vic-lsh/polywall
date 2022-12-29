package packetfilter.firewall.internals;

import packetfilter.packet.Packet;

/**
 * Specify a minimal set of common functionalities provided by a PNGR map entry.
 */
abstract class AbstractPNGRMapValue {

    /**
     * Performs required updates specified by a configuration packet.
     * 
     * @param pkt a config packet whose address is the one mapped to this entry.
     */
    abstract public void updateConfig(Packet pkt);

    /**
     * Whether the address associated with this map entry is marked as PNG.
     * 
     * @return whether a packet with the source address mapped to this entry
     *         is marked as PNG.
     */
    abstract public boolean isPersonaNonGrata();

    /**
     * Checks whether packet from a source address can be sent to the
     * destination address associated with this map entry.
     * 
     * @param sourceAddr the source address to check.
     * @return whether the source-destination pair is permitted.
     */
    abstract public boolean hasRecvPermission(int sourceAddr);

}
