package packetfilter.firewall.service;

import packetfilter.packet.Packet;

/**
 * Provides a uniform interface over work that can be done over a packet.
 */
public interface FirewallService {
    /**
     * Performs necessary processing over a packet.
     * 
     * If this is a data packet, the service performs fingerprint collection if
     * the packet passes access control. If this is a config packet, the service
     * updates access control permissions.
     * 
     * @param packet the packet to process.
     */
    public void process(Packet packet);

    /**
     * Performs packet processing only if the packet is a data packet.
     * 
     * @param packet the packet to process, if it is a data packet.
     * @return whether the packet is processed.
     */
    public boolean processIfDataPacket(Packet packet);

    /**
     * Performs packet processing only if the packet is a config packet.
     * 
     * @param packet the packet to process, if it is a config packet.
     * @return whether the packet is processed.
     */
    public boolean processIfConfigPacket(Packet packet);

    /**
     * Collects fingerprint of a packet, if it is a data packet.
     * 
     * @param dataPacket the data packet to collect fingerprint.
     * @throws IllegalArgumentException if the provided packet is a config packet
     */
    public void collectFingerprint(Packet dataPacket);

    /**
     * Checks permission for a data packet, or update permission for a config
     * packet.
     * 
     * This is useful for phased pipelines, where one phase may perform
     * control-related task and defer fingerprint collection to a future phase.
     * 
     * @param packet the packet to perform control-related task.
     * @return whether the packet passes access control, if the packet is a
     *         data packet. `false` if the packet is a config packet.
     */
    public boolean checkOrUpdateAccessControl(Packet packet);
}
