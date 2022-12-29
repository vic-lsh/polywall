package packetfilter.firebench.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import packetfilter.packet.workers.pools.workflows.IncompleteWorkflowBuildException;
import packetfilter.packet.workers.pools.workflows.Workflows;

public class ThreadingConifgTest {

    @Test
    public void testThreadCountsAreCorrect() throws IncompleteWorkflowBuildException {
        var cfg = new ThreadingConfig(
                32,
                Workflows.buildPartialPipeline()
                        .numConfigWorkers(30).numDataWorkers(12).build(),
                Workflows.buildPhasedPipeline()
                        .controlPhase(12).collectionPhase(3).build());

        assertEquals(32, cfg.defaultNumThreads);
        assertEquals(30, cfg.partialPipelineWorkflow.numConfigWorkers());
        assertEquals(12, cfg.partialPipelineWorkflow.numDataWorkers());
        assertEquals(12, cfg.phasedPipelineWorkflow.numCtrlPhaseWorkers());
        assertEquals(3, cfg.phasedPipelineWorkflow.numCollectionPhaseWorkers());
    }
}
