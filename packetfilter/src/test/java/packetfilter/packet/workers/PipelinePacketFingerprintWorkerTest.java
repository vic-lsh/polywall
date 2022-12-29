package packetfilter.packet.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import packetfilter.helpers.PacketTestHelpers;
import packetfilter.packet.generators.ConfigFileMappingException;

public class PipelinePacketFingerprintWorkerTest extends PacketWorkerTestBase<PipelinePacketFingerprintWorker> {

    protected int numPackets = 1000;

    @Override
    protected PipelinePacketFingerprintWorker createDefaultTestWorker() {
        return new PipelinePacketFingerprintWorker(makeSerialService(), numPackets);
    }

    @Override
    protected boolean isSpecializedWorker() {
        return true;
    }

    @Test
    public void testProcessesDataPacket() throws IOException, ConfigFileMappingException {
        worker.process(PacketTestHelpers.makeOneDataPacket());
        assertEquals(1, worker.getProcessedCount());
    }

    @Test
    public void testThrowsIfArgIsConfigPacket() {
        assertThrows(IllegalArgumentException.class, () -> worker.process(PacketTestHelpers.makeOneConfigPacket()));
    }

    @Test
    public void testDoesNotForwardPacketToNextPhase() throws IOException, ConfigFileMappingException {
        assertFalse(worker.process(PacketTestHelpers.makeOneDataPacket()));
    }

}
