package packetfilter.packet.generators.custom;

import packetfilter.packet.Packet;

interface PacketBuilder {
    public Packet build();
}