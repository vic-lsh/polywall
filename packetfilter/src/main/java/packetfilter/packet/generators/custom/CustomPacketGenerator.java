package packetfilter.packet.generators.custom;

import packetfilter.packet.Packet;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class CustomPacketGenerator {
    private PacketGenerator pktGen;

    public CustomPacketGenerator(PacketGeneratorConfig config) {
        this.pktGen = config.toPacketGenerator();
    }

    /** Constructs a data packet with specific parameters. */
    public DataPacketBuilder buildDataPacket() {
        return new DataPacketBuilder(pktGen.getDataPacket());
    }

    /** Constructs a config packet with specific parameters. */
    public ConfigPacketBuilder buildConfigPacket() {
        return new ConfigPacketBuilder(pktGen.getConfigPacket());
    }

    public Packet getDataPacket() {
        return this.pktGen.getDataPacket();
    }

    public Packet getConfigPacket() {
        return this.pktGen.getConfigPacket();
    }

    public Packet getPacket() {
        return this.pktGen.getPacket();
    }
}
