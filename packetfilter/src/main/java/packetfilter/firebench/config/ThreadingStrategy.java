package packetfilter.firebench.config;

import java.util.Iterator;

import packetfilter.packet.workers.pools.workflows.IncompleteWorkflowBuildException;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;
import packetfilter.packet.workers.pools.workflows.Workflows;

/**
 * Configures the number of threads to use, in various benchmarking scenarios.
 */
public class ThreadingStrategy implements Iterable<ThreadingConfig> {
    private int[] numThreadVariants;
    private double percentageDataWorkers;
    private double percentageControlWorkers;

    public ThreadingStrategy(int[] numThreadVariants, double percentageDataWorkers, double percentageControlWorkers) {
        this.numThreadVariants = numThreadVariants;
        this.percentageDataWorkers = percentageDataWorkers;
        this.percentageControlWorkers = percentageControlWorkers;
    }

    public Iterator<ThreadingConfig> iterator() {
        return new Iterator<ThreadingConfig>() {

            private int curr = 0;

            @Override
            public boolean hasNext() {
                return curr < numThreadVariants.length;
            }

            @Override
            public ThreadingConfig next() {
                int numThreads = numThreadVariants[curr++];
                return new ThreadingConfig(
                        numThreads,
                        constructPartialPipelineWorkflow(numThreads),
                        constructPhasedPipelineWorkflow(numThreads));
            }

        };
    }

    private PhasedPipelineWorkflow constructPhasedPipelineWorkflow(int numThreads) {
        int ctrlPhaseSize = (int) Math.floor(numThreads * percentageControlWorkers);
        int collectionPhaseSize = numThreads - ctrlPhaseSize;

        try {
            return Workflows.buildPhasedPipeline()
                    .controlPhase(ctrlPhaseSize)
                    .collectionPhase(collectionPhaseSize).build();
        } catch (IncompleteWorkflowBuildException e) {
            e.printStackTrace(); // impossible path.
            return null;
        }
    }

    private PartialPipelineWorkflow constructPartialPipelineWorkflow(int numThreads) {
        int dataWorkerCount = (int) Math.floor(numThreads * percentageDataWorkers);
        int configWorkerCount = numThreads - dataWorkerCount;

        try {
            return Workflows.buildPartialPipeline()
                    .numDataWorkers(dataWorkerCount)
                    .numConfigWorkers(configWorkerCount).build();
        } catch (IncompleteWorkflowBuildException e) {
            e.printStackTrace(); // impossible path.
            return null;
        }
    }

}
