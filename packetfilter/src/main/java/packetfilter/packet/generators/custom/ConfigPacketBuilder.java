package packetfilter.packet.generators.custom;

import packetfilter.packet.Config;
import packetfilter.packet.Packet;
import packetfilter.packet.Packet.MessageType;
import packetfilter.utils.AddressRange;

public class ConfigPacketBuilder implements PacketBuilder {
    class ModifiableConfig implements TemporarilyModifiable<Config> {
        int address;
        boolean personaNonGrata;
        boolean acceptingRange;
        int addressBegin;
        int addressEnd;

        public ModifiableConfig(Packet packet) {
            if (packet.type != MessageType.ConfigPacket) {
                throw new IllegalArgumentException();
            }
            Config config = packet.config;
            address = config.address;
            personaNonGrata = config.personaNonGrata;
            acceptingRange = config.acceptingRange;
            addressBegin = config.addressBegin;
            addressEnd = config.addressEnd;
        }

        @Override
        public Config freeze() {
            return new Config(address, personaNonGrata, acceptingRange, addressBegin, addressEnd);
        }

    }

    private ModifiableConfig config;

    public ConfigPacketBuilder(Packet packet) {
        config = new ModifiableConfig(packet);
    }

    public ConfigPacketBuilder withAddress(int address) {
        config.address = address;
        return this;
    }

    public ConfigPacketBuilder withPersonaNonGrata(boolean png) {
        config.personaNonGrata = png;
        return this;
    }

    public ConfigPacketBuilder withAcceptRange(AddressRange range, boolean accept) {
        return this.withAddressRange(range).withRangeAcceptanceSetting(accept);
    }

    public ConfigPacketBuilder withAddressRange(AddressRange range) {
        config.addressBegin = range.start;
        config.addressEnd = range.end;
        return this;
    }

    public ConfigPacketBuilder withAddressRangeBegin(int addressRangeStart) {
        config.addressBegin = addressRangeStart;
        return this;
    }

    public ConfigPacketBuilder withAddressRangeEnd(int addressRangeEnd) {
        config.addressEnd = addressRangeEnd;
        return this;
    }

    public ConfigPacketBuilder withRangeAcceptanceSetting(boolean acceptRange) {
        config.acceptingRange = acceptRange;
        return this;
    }

    @Override
    public Packet build() {
        return new Packet(config.freeze());
    }

}
