package packetfilter.firewall;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packetfilter.firewall.helpers.AcceptanceRangeTestConfig;
import packetfilter.firewall.helpers.AcceptanceRangeTestData;
import packetfilter.firewall.helpers.FirewallOperator;
import packetfilter.packet.Packet;
import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.generators.custom.CustomPacketGenerator;
import packetfilter.utils.AddressRange;

/**
 * Implements common tests for every firewall implementation.
 */
public abstract class FirewallTestBase<F extends Firewall> {
    protected F firewall;
    protected PacketGeneratorConfig pktGenConfig;
    protected CustomPacketGenerator pktGen;
    protected FirewallOperator operator;

    protected final int NOOP_ADDR = 0;

    protected abstract F createFirewall();

    @BeforeEach
    public void init() throws IOException, ConfigFileMappingException {
        firewall = createFirewall();
        pktGenConfig = new ConfigReader().readConfig("preset1");
        operator = new FirewallOperator(firewall, pktGenConfig);
        pktGen = new CustomPacketGenerator(pktGenConfig);
    }

    @Test
    public void testCannotCallDecideWithConfigPkt() {
        assertThrows(IllegalArgumentException.class, () -> {
            firewall.decide(pktGen.getConfigPacket());
        });
    }

    @Test
    public void testCannotUpdateConfigWithDataPkt() {
        assertThrows(IllegalArgumentException.class, () -> {
            firewall.update(pktGen.getDataPacket());
        });
    }

    @Test
    public void testBanSourceAddress() {
        final int addr = 123;

        Packet pngTestPacket = pktGen.buildDataPacket().withSource(addr).build();
        assertTrue(firewall.decide(pngTestPacket));

        operator.banSourceAddress(addr);
        assertFalse(firewall.decide(pngTestPacket));
    }

    @Test
    public void testRemoveBanOnSourceAddress() {
        final int addr = 123;

        operator.banSourceAddress(addr);
        operator.permitSourceAddress(addr);

        Packet pngTestPacket = pktGen.buildDataPacket().withSource(addr).build();
        assertTrue(firewall.decide(pngTestPacket));
    }

    @Test
    public void testAcceptanceRange() {
        final int rangeBegin = 1000;
        final int rangeEnd = 1250;
        final boolean accept = true;
        final var range = new AddressRange(rangeBegin, rangeEnd);
        final int inRangeTestItr = 500;
        final int outOfRangeTestItr = 500;
        final var config = new AcceptanceRangeTestConfig(inRangeTestItr,
                outOfRangeTestItr, pktGenConfig);

        // First, test revoking access to packets
        testAcceptanceRangeUpdate(range, !accept, config);
        // Then, test reverting this change
        testAcceptanceRangeUpdate(range, accept, config);
    }

    /**
     * Test changing whether a firewall accepts packet going to a range of
     * addresses.
     */
    protected void testAcceptanceRangeUpdate(AddressRange range, boolean accept, AcceptanceRangeTestConfig config) {
        final int destAddr = 123;

        if (accept) {
            operator.permitAddressRangeToDest(range, destAddr);
        } else {
            operator.banAddressRangeFromDest(range, destAddr);
        }

        AcceptanceRangeTestData testData;
        testData = new AcceptanceRangeTestData(destAddr, range, config);
        testData.inRangeTestPackets().forEachRemaining((packet) -> {
            assert (firewall.decide(packet) == accept);
        });
        testData.outOfRangeTestPackets().forEachRemaining((packet) -> {
            assertTrue(firewall.decide(packet));
        });
    }
}
