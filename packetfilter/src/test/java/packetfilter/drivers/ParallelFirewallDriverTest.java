package packetfilter.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import packetfilter.firewall.SerialFirewall;
import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;

public class ParallelFirewallDriverTest {
    @Test
    public void testNumWorkersIsPositive() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ParallelFirewallDriverTestStub(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ParallelFirewallDriverTestStub(0);
        });
    }

    @Test
    public void testCanInstantiateSubClass() throws IOException, ConfigFileMappingException {
        assertNotNull(new ParallelFirewallDriverTestStub(1));
        assertNotNull(new ParallelFirewallDriverTestStub(15));
        assertNotNull(new ParallelFirewallDriverTestStub(33));
        assertNotNull(new ParallelFirewallDriverTestStub(ParallelFirewallDriverTestStub.MAX_WORKERS));
    }

    @Test
    public void testErrsIfExceedsMaxNumWorkers() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ParallelFirewallDriverTestStub(ParallelFirewallDriverTestStub.MAX_WORKERS + 1);
        });
    }

    @Test
    public void testCanAccessNumWorkers() throws IOException, ConfigFileMappingException {
        assertEquals(new ParallelFirewallDriverTestStub(33).nThreads, 33);
    }
}

class ParallelFirewallDriverTestStub extends AbstractParallelFirewallDriver<SerialFirewall> {
    public ParallelFirewallDriverTestStub(int numWorkers) throws IOException, ConfigFileMappingException {
        super(numWorkers, new ConfigReader().readConfig("preset1"));
    }

    @Override
    public void process(int nPackets) throws InterruptedException, ExecutionException {
        return;
    }

    @Override
    protected SerialFirewall createFirewall() {
        return null;
    }
}
