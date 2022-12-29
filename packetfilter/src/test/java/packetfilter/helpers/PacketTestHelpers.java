package packetfilter.helpers;

import java.io.IOException;
import java.util.HashMap;

import packetfilter.packet.Packet;
import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGenerator;

public class PacketTestHelpers {

    private static HashMap<String, PacketGenerator> pktGens = new HashMap<>();

    private static String defaultPktGenConfigName = "preset1";

    public static Packet makeOnePacket(String configName) throws IOException, ConfigFileMappingException {
        return getOrCreatePktGen(configName).getPacket();
    }

    public static Packet makeOnePacket() throws IOException, ConfigFileMappingException {
        return makeOnePacket(defaultPktGenConfigName);
    }

    public static Packet makeOneConfigPacket(String configName) throws IOException, ConfigFileMappingException {
        return getOrCreatePktGen(configName).getConfigPacket();
    }

    public static Packet makeOneConfigPacket() throws IOException, ConfigFileMappingException {
        return getOrCreatePktGen(defaultPktGenConfigName).getConfigPacket();
    }

    public static Packet makeOneDataPacket(String configName) throws IOException, ConfigFileMappingException {
        return getOrCreatePktGen(configName).getDataPacket();
    }

    public static Packet makeOneDataPacket() throws IOException, ConfigFileMappingException {
        return getOrCreatePktGen(defaultPktGenConfigName).getDataPacket();
    }

    public static Packet[] makeNPackets(int nPackets, PacketGenerator pktGen) {
        Packet[] pkts = new Packet[nPackets];

        for (int i = 0; i < nPackets; i++) {
            pkts[i] = pktGen.getPacket();
        }

        return pkts;
    }

    private static PacketGenerator getOrCreatePktGen(String configName) throws IOException, ConfigFileMappingException {
        PacketGenerator pktGen = pktGens.get(configName);

        if (pktGen == null) {
            var newPktGen = new ConfigReader().readConfig(configName).toPacketGenerator();
            pktGens.put(configName, newPktGen);
            pktGen = newPktGen;
        }

        return pktGen;
    }
}
