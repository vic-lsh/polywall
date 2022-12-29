package packetfilter.firebench.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGeneratorConfig;

/**
 * Describes and obtains the packet generator configs to use in a benchmark.
 */
public class PacketGenStrategy implements Iterable<Map.Entry<String, PacketGeneratorConfig>> {

    public final String[] pktGenConfigs;
    public final HashMap<String, PacketGeneratorConfig> configs;

    public PacketGenStrategy(String pktGenConfig) throws IOException, ConfigFileMappingException {
        this(new String[] { pktGenConfig });
    }

    public PacketGenStrategy(String[] pktGenConfigs) throws IOException, ConfigFileMappingException {
        this.pktGenConfigs = pktGenConfigs;
        this.configs = readAllConfigs(pktGenConfigs);
    }

    public static PacketGenStrategy using(String... pktGenConfigNames) throws IOException, ConfigFileMappingException {
        return new PacketGenStrategy(pktGenConfigNames);
    }

    public static PacketGenStrategy useAll() throws IOException, ConfigFileMappingException {
        return new PacketGenStrategy(new String[] {
                "preset1",
                "preset2",
                "preset3",
                "preset4",
                "preset5",
                "preset6",
                "preset7",
                "preset8"
        });
    }

    private static HashMap<String, PacketGeneratorConfig> readAllConfigs(String[] configNames)
            throws IOException, ConfigFileMappingException {
        var configs = new LinkedHashMap<String, PacketGeneratorConfig>();
        var reader = new ConfigReader();

        for (String configName : configNames) {
            PacketGeneratorConfig config = reader.readConfig(configName);
            configs.put(configName, config);
        }

        return configs;
    }

    @Override
    public Iterator<Entry<String, PacketGeneratorConfig>> iterator() {
        return this.configs.entrySet().iterator();
    }

}
