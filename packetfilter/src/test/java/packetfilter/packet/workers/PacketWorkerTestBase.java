package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packetfilter.firewall.SerialFirewall;
import packetfilter.firewall.service.SerialFirewallService;
import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;

public abstract class PacketWorkerTestBase<W extends AbstractPacketWorker> {

    protected static PacketGeneratorConfig pktGenConfig;
    protected static PacketGenerator pktGen;
    protected W worker;
    protected boolean isSpecializedWorker;

    abstract protected W createDefaultTestWorker();

    /** Whether the worker only selectively processes some packet. */
    abstract protected boolean isSpecializedWorker();

    protected SerialFirewallService<SerialFirewall> makeSerialService() {
        return new SerialFirewallService<SerialFirewall>(new SerialFirewall());
    }

    protected void startJoinRunnable(Runnable r) throws InterruptedException {
        var thread = new Thread(r);
        thread.start();
        thread.join();
    }

    @BeforeAll
    public static void initOnce() throws IOException, ConfigFileMappingException {
        pktGenConfig = new ConfigReader().readConfig("preset1");
        pktGen = pktGenConfig.toPacketGenerator();
    }

    @BeforeEach
    public void initBeforeEach() {
        this.worker = createDefaultTestWorker();
        this.isSpecializedWorker = isSpecializedWorker();
    }

    @Test
    public void testPacketsAreProcessedOnDemand() {
        assertEquals(0, worker.getProcessedCount());
    }

    @Test
    public void testProcessingOnePacket() {
        assumeFalse(isSpecializedWorker);

        worker.process(pktGen.getPacket());
        assertEquals(1, worker.getProcessedCount());
    }

}
