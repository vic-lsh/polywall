package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.generators.ConfigFileMappingException;

public class PipelineDataPacketWorkerTest extends PacketWorkerTestBase<PipelineDataPacketWorker> {

    protected int numPackets = 10000;

    @Override
    protected PipelineDataPacketWorker createDefaultTestWorker() {
        return new PipelineDataPacketWorker(makeSerialService(), numPackets);
    }

    @Override
    protected boolean isSpecializedWorker() {
        return true;
    }

    @Test
    public void testDataPacketIsProcessed() throws IOException, ConfigFileMappingException {
        worker.process(PacketTestHelpers.makeOneDataPacket());
        assertEquals(1, worker.getProcessedCount());
    }

    @Test
    public void testConfigPacketIsNotProcessed() throws IOException, ConfigFileMappingException {
        worker.process(PacketTestHelpers.makeOneConfigPacket());
        assertEquals(0, worker.getProcessedCount());
    }

    @Test
    public void testDataPacketNeedNoFurtherProcessing() throws IOException, ConfigFileMappingException {
        assertFalse(worker.process(PacketTestHelpers.makeOneDataPacket()));
    }

    @Test
    public void testConfigPacketNeedFurtherProcessing() throws IOException, ConfigFileMappingException {
        assertTrue(worker.process(PacketTestHelpers.makeOneConfigPacket()));
    }

}
