package packetfilter.firewall.helpers;

import java.util.Iterator;

import packetfilter.packet.Packet;
import packetfilter.packet.generators.custom.CustomPacketGenerator;
import packetfilter.utils.AddressRange;
import packetfilter.utils.RangeRandom;

/**
 * Container for packets used for testing whether a range of source addresses
 * are accepted by a destination address.
 */
public class AcceptanceRangeTestData {

    private final int destAddr;
    private final AddressRange range;
    private final AcceptanceRangeTestConfig config;
    private final RangeRandom rand;
    private final CustomPacketGenerator pktGen;

    public AcceptanceRangeTestData(int destAddr, AddressRange srcRange,
            AcceptanceRangeTestConfig config) {
        this.destAddr = destAddr;
        this.range = srcRange;
        this.config = config;
        int SEED = 100;
        this.rand = new RangeRandom(SEED);
        this.pktGen = new CustomPacketGenerator(config.pktGenConfig());
    }

    public Iterator<Packet> inRangeTestPackets() {
        return makePackets(range, true, config.numInRangeTests());
    }

    public Iterator<Packet> outOfRangeTestPackets() {
        return makePackets(range, false, config.numOutOfRangeTests());
    }

    private Iterator<Packet> makePackets(AddressRange range, boolean inRange, int count) {
        Iterator<Packet> it = new Iterator<Packet>() {

            private int dispensedCount = 0;

            @Override
            public boolean hasNext() {
                return dispensedCount < count;
            }

            @Override
            public Packet next() {
                dispensedCount++;

                int src = inRange ? rand.nextIntInRange(range.start, range.end)
                        : rand.nextIntOutOfRange(range.start, range.end);

                return pktGen.buildDataPacket().withSource(src).withDest(destAddr).build();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
}