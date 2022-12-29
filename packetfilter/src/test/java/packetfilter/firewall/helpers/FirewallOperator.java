package packetfilter.firewall.helpers;

import packetfilter.firewall.Firewall;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.generators.custom.CustomPacketGenerator;
import packetfilter.utils.AddressRange;

/**
 * Common application-level actions to be performed on a firewall.
 * 
 * Concurrency guarantees for FirewallTester is only limited by the supplied
 * Firewall instance. If a FirewallTester is instantiated with a lock-free
 * firewall, then the tester is lock-free as well.
 */
public class FirewallOperator {

    private Firewall firewall;
    private ThreadLocal<CustomPacketGenerator> threadLocalPktGen;

    public FirewallOperator(Firewall firewall, PacketGeneratorConfig pktGenConfig) {
        this.firewall = firewall;
        this.threadLocalPktGen = new ThreadLocal<>() {
            protected CustomPacketGenerator initialValue() {
                return new CustomPacketGenerator(pktGenConfig);
            };
        };
    }

    /**
     * Ban packets from a source address.
     * 
     * @param srcAddr the address to ban.
     */
    public void banSourceAddress(int srcAddr) {
        Packet banPacket = pktGen().buildConfigPacket()
                .withAddress(srcAddr)
                .withPersonaNonGrata(true)
                .build();

        firewall.update(banPacket);
    }

    /**
     * Allow the packet from a source address to pass the firewall.
     * 
     * @param srcAddr the address to permit access from.
     */
    public void permitSourceAddress(int srcAddr) {
        Packet permitPacket = pktGen().buildConfigPacket()
                .withAddress(srcAddr)
                .withPersonaNonGrata(false)
                .build();

        firewall.update(permitPacket);
    }

    /**
     * Prevent packets whose sources are within a range from reaching a destination
     * address.
     * 
     * @param range    the range of source addresses to prevent.
     * @param destAddr the destination address to prevents packets to go to.
     */
    public void banAddressRangeFromDest(AddressRange range, int destAddr) {
        Packet cfgPacket = pktGen().buildConfigPacket().withAddress(destAddr).withAcceptRange(range, false).build();
        firewall.update(cfgPacket);
    }

    /**
     * Allow packets whose sources are within a range to be sent to a destination
     * address.
     * 
     * @param range    the range of source addresses to allow.
     * @param destAddr the destination address to accept packets from the said
     *                 range.
     */
    public void permitAddressRangeToDest(AddressRange range, int destAddr) {
        Packet cfgPacket = pktGen().buildConfigPacket().withAddress(destAddr).withAcceptRange(range, true).build();
        firewall.update(cfgPacket);
    }

    private CustomPacketGenerator pktGen() {
        return this.threadLocalPktGen.get();
    }
}
