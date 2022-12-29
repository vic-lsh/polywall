package packetfilter.packet.generators;

/**
 * A set of parameters for packet generator.
 */
public record PacketGeneratorConfig(int numAddressesLog, int numTrainsLog, int meanTrainSize, int meanTrainsPerComm,
        int meanWindow, int meanCommsPerAddress, int meanWork, float configFraction, float pngFraction,
        float acceptingFraction) {

    public PacketGenerator toPacketGenerator() {
        return new PacketGenerator(numAddressesLog, numTrainsLog, meanTrainSize, meanTrainsPerComm, meanWindow,
                meanCommsPerAddress, meanWork, configFraction, pngFraction, acceptingFraction);
    }
}