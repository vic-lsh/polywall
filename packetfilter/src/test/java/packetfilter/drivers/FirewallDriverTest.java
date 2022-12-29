package packetfilter.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packetfilter.firewall.SerialFirewall;
import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class FirewallDriverTest {

    FirewallDriverStub driver;

    @BeforeEach
    public void init() throws IOException, ConfigFileMappingException {
        this.driver = new FirewallDriverStub(new ConfigReader().readConfig("preset1"));
    }

    @Test
    public void testCanGetName() {
        assertEquals("FirewallDriverStub", driver.getName());
    }
}

class FirewallDriverStub extends AbstractFirewallDriver<SerialFirewall> {

    public FirewallDriverStub(PacketGeneratorConfig pktGenConfig) {
        super(pktGenConfig);
    }

    @Override
    public void process(int nPackets) throws InterruptedException, ExecutionException {
    }

    @Override
    protected SerialFirewall createFirewall() {
        return null;
    }

    @Override
    protected FirewallService createFirewallService(SerialFirewall firewall) {
        return null;
    }

}