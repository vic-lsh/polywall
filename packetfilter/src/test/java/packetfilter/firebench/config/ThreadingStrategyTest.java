package packetfilter.firebench.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ThreadingStrategyTest {

    @Test
    public void testEnumeration() throws IncompleteThreadingStrategyBuildException {
        int[] numThreads = new int[] { 10, 13, 14, 20 };
        double percentageDataWorkers = 0.5;
        double percentageControlWorkers = 0.8;

        var strategy = new ThreadingStrategyBuilder()
                .withNumberOfThreads(numThreads)
                .withPercentageDataWorkers(percentageDataWorkers)
                .withPercentageControlWorkers(percentageControlWorkers).build();

        int index = 0;
        for (ThreadingConfig cfg : strategy) {
            assertEquals(numThreads[index], cfg.defaultNumThreads);
            index++;
        }

        assertEquals(index, numThreads.length);

        ThreadingConfig cfg;
        var itr = strategy.iterator();
        cfg = itr.next();
        assertEquals(5, cfg.partialPipelineWorkflow.numDataWorkers());
        assertEquals(5, cfg.partialPipelineWorkflow.numConfigWorkers());
        assertEquals(8, cfg.phasedPipelineWorkflow.numCtrlPhaseWorkers());
        assertEquals(2, cfg.phasedPipelineWorkflow.numCollectionPhaseWorkers());

        cfg = itr.next(); // numthreads = 13. Test thread allocation for odd numbers.
        assertEquals(6, cfg.partialPipelineWorkflow.numDataWorkers());
        assertEquals(7, cfg.partialPipelineWorkflow.numConfigWorkers());
        assertEquals(10, cfg.phasedPipelineWorkflow.numCtrlPhaseWorkers());
        assertEquals(3, cfg.phasedPipelineWorkflow.numCollectionPhaseWorkers());
    }
}
