package packetfilter.packet.generators;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigReader {
    String cfgDir = "./configs";

    public ConfigReader() {
    }

    public ConfigReader(String configFolder) {
        this.cfgDir = configFolder;
    }

    public PacketGeneratorConfig readConfig(String configName) throws IOException, ConfigFileMappingException {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path cfgPath = Paths.get(currentPath.toString(), this.cfgDir, ensureFileSuffix(configName, "json")).normalize();

        String cfgContent = Files.readString(cfgPath, StandardCharsets.US_ASCII);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PacketGeneratorConfig cfg = objectMapper.readValue(cfgContent, PacketGeneratorConfig.class);
            return cfg;
        } catch (Exception e) {
            throw new ConfigFileMappingException();
        }
    }

    private String ensureFileSuffix(String fstring, String suffix) {
        if (fstring.endsWith(suffix)) {
            return fstring;
        } else {
            return fstring + "." + suffix;
        }
    }

}
