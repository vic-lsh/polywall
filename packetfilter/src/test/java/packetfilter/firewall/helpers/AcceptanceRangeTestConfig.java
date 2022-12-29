package packetfilter.firewall.helpers;

import packetfilter.packet.generators.PacketGeneratorConfig;

/**
 * Config holder for `AcceptanceRangeTestData`.
 */
public record AcceptanceRangeTestConfig(int numInRangeTests, int numOutOfRangeTests,
        PacketGeneratorConfig pktGenConfig) {
}