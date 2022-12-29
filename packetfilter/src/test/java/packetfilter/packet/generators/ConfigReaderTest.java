package packetfilter.packet.generators;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigReaderTest {

    @Test
    public void testReadSampleCfgWithCfgFolder() throws IOException, ConfigFileMappingException {
        ConfigReader reader = new ConfigReader("./configs");
        assert (reader.readConfig("preset1") != null);

    }

    @Test
    public void testThrowsIoExceptionIfDirNotFound() throws IOException, ConfigFileMappingException {
        Assertions.assertThrows(NoSuchFileException.class, () -> {
            var reader = new ConfigReader("../unknown/path");
            reader.readConfig("cfg");
        });
    }

    @Test
    public void testReadSampleCfgWithPresetDir() throws IOException, ConfigFileMappingException {
        ConfigReader reader = new ConfigReader();
        assert (reader.readConfig("preset1") != null);
    }

}
