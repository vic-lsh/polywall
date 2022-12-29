package packetfilter.packet.generators.custom;

import packetfilter.packet.Body;
import packetfilter.packet.Header;
import packetfilter.packet.Packet;
import packetfilter.packet.Packet.MessageType;

public class DataPacketBuilder implements PacketBuilder {
    class ModifiableHeader implements TemporarilyModifiable<Header> {
        int source;
        int dest;
        int sequenceNumber;
        int trainSize;
        int tag;

        public ModifiableHeader(Packet packet) {
            Header header = packet.header;
            source = header.source;
            dest = header.dest;
            sequenceNumber = header.sequenceNumber;
            trainSize = header.trainSize;
            tag = header.tag;
        }

        @Override
        public Header freeze() {
            return new Header(source, dest, sequenceNumber, trainSize, tag);
        }
    }

    class ModifiableBody implements TemporarilyModifiable<Body> {
        long iterations;
        long seed;

        public ModifiableBody(Packet packet) {
            Body body = packet.body;
            iterations = body.iterations;
            seed = body.seed;
        }

        @Override
        public Body freeze() {
            return new Body(iterations, seed);
        }
    }

    private ModifiableBody body;
    private ModifiableHeader header;

    public DataPacketBuilder(Packet initial) {
        if (initial.type != MessageType.DataPacket) {
            throw new IllegalArgumentException();
        }

        header = new ModifiableHeader(initial);
        body = new ModifiableBody(initial);
    }

    public DataPacketBuilder withSource(int source) {
        header.source = source;
        return this;
    }

    public DataPacketBuilder withDest(int dest) {
        header.dest = dest;
        return this;
    }

    @Override
    public Packet build() {
        return new Packet(header.freeze(), body.freeze());
    }
}
