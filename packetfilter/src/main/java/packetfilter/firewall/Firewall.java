package packetfilter.firewall;

import packetfilter.packet.Packet;

public interface Firewall {
    /**
     * Determines whether a packet passes access control.
     * 
     * @param pkt a data packet to check whether it is allowed.
     * 
     * @return `true` if a packet passes access control, else `false`.
     * 
     * @throws IllegalArgumentException if the provided packet is a config packet
     *                                  instead of a data packet.
     */
    public boolean decide(Packet pkt) throws IllegalArgumentException;

    /**
     * Performs a blocking update to access control constraints.
     * 
     * Access control queries after termination of a `updateConfig()` call are
     * guaranteed to reflect constraint changes imposed by the input config packet.
     * 
     * @param configPkt a config packet with access control constraints.
     * 
     * @throws IllegalArgumentsException if the provided packet is a data packet
     *                                   instead of a config packet.
     */
    public void update(Packet configPkt) throws IllegalArgumentException;
}
