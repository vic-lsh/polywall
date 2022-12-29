package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.generators.ConfigFileMappingException;

public class PipelineConfigPacketWorkerTest extends PacketWorkerTestBase<PipelineConfigPacketWorker> {

    protected int numPackets = 10000;

    @Override
    protected PipelineConfigPacketWorker createDefaultTestWorker() {
        return new PipelineConfigPacketWorker(makeSerialService(), numPackets);
    }

    @Override
    protected boolean isSpecializedWorker() {
        return true;
    }

    @Test
    public void testConfigPacketIsProcessed() throws IOException, ConfigFileMappingException {
        worker.process(PacketTestHelpers.makeOneConfigPacket());
        assertEquals(1, worker.getProcessedCount());
    }

    @Test
    public void testDataPacketIsNotProcessed() throws IOException, ConfigFileMappingException {
        worker.process(PacketTestHelpers.makeOneDataPacket());
        assertEquals(0, worker.getProcessedCount());
    }

    @Test
    public void testConfigPacketIsNotFurtherProcessed() throws IOException, ConfigFileMappingException {
        assertFalse(worker.process(PacketTestHelpers.makeOneConfigPacket()));
    }

    @Test
    public void testDataPacketNeedFurtherProcessing() throws IOException, ConfigFileMappingException {
        assertTrue(worker.process(PacketTestHelpers.makeOneDataPacket()));
    }

}
