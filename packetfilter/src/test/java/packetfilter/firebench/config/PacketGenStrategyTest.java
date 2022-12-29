package packetfilter.firebench.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import packetfilter.packet.generators.ConfigFileMappingException;

public class PacketGenStrategyTest {

    @Test
    public void testCanCreateStrategyWithAllConfigs() throws IOException, ConfigFileMappingException {
        PacketGenStrategy.useAll();
    }

    @Test
    public void testThrowsIfBadConfigNameProvided() {
        assertThrows(IOException.class, () -> {
            new PacketGenStrategy("non-existent-file");
        });
    }

    @Test
    public void testPktGenConfigNames() throws IOException, ConfigFileMappingException {
        String[] configs = new String[] { "preset1", "preset3" };

        var strategy = new PacketGenStrategy(configs);

        assertTrue(testStringArrayContentEquality(configs, strategy.pktGenConfigs));
    }

    @Test
    public void testReadConfigs() throws IOException, ConfigFileMappingException {
        String[] configs = new String[] { "preset1", "preset3" };

        var strategy = new PacketGenStrategy(configs);

        assertTrue(strategy.configs.containsKey("preset1"));
        assertTrue(strategy.configs.containsKey("preset3"));

        assertEquals(configs.length, strategy.configs.size());
    }

    private boolean testStringArrayContentEquality(String[] arr1, String[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }

        for (int i = 0; i < arr2.length; i++) {
            if (arr2[i] != arr1[i]) {
                return false;
            }
        }

        return true;
    }
}
