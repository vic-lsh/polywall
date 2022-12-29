package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.generators.ConfigFileMappingException;

public class PipelinePacketControlWorkerTest extends PacketWorkerTestBase<PipelinePacketControlWorker> {

    protected int numPackets = 1000;

    @Override
    protected PipelinePacketControlWorker createDefaultTestWorker() {
        return new PipelinePacketControlWorker(makeSerialService(), numPackets);
    }

    @Override
    protected boolean isSpecializedWorker() {
        // While this worker is a pipelined worker, it processes both data and
        // config packets. Thus it is not specialized.
        return false;
    }

    @Test
    public void testConfigPacketIsFurtherProcessed() throws IOException, ConfigFileMappingException {
        assertFalse(worker.process(PacketTestHelpers.makeOneConfigPacket()));
    }

    @Test
    public void testDataPacketIsFurtherProcessed() throws IOException, ConfigFileMappingException {
        assertTrue(worker.process(PacketTestHelpers.makeOneDataPacket()));
    }

}
