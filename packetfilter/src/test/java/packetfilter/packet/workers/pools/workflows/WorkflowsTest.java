package packetfilter.packet.workers.pools.workflows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class WorkflowsTest {

    @Nested
    class BuildPartialPipeline {
        @Test
        public void testCreation() throws IncompleteWorkflowBuildException {
            int dataWorkersCount = 10, cfgWorkersCount = 5;

            var workflow = Workflows.buildPartialPipeline()
                    .numDataWorkers(dataWorkersCount)
                    .numConfigWorkers(cfgWorkersCount).build();

            assertEquals(dataWorkersCount, workflow.numDataWorkers());

            assertEquals(cfgWorkersCount, workflow.numConfigWorkers());
        }

        @Test
        public void testIncompleteBuildThrowsException() {
            assertThrows(IncompleteWorkflowBuildException.class, () -> {
                Workflows.buildPartialPipeline().build();
            });

            assertThrows(IncompleteWorkflowBuildException.class, () -> {
                Workflows.buildPartialPipeline().numDataWorkers(1).build();
            });

            assertThrows(IncompleteWorkflowBuildException.class, () -> {
                Workflows.buildPartialPipeline().numConfigWorkers(1).build();
            });
        }

        @Test
        public void testInvalidNumWorkers() {

            int[] testIllegalVals = new int[] { -3, 0 };

            for (int illegalWorkerCount : testIllegalVals) {
                assertThrows(IllegalArgumentException.class, () -> {
                    Workflows.buildPartialPipeline().numDataWorkers(illegalWorkerCount);
                });

                assertThrows(IllegalArgumentException.class, () -> {
                    Workflows.buildPartialPipeline().numConfigWorkers(illegalWorkerCount);
                });
            }
        }
    }

    @Nested
    class BuildPhasedPipeline {

        @Test
        public void testCreation() throws IncompleteWorkflowBuildException {
            int ctrlWorkersCount = 10, collectionWorkersCount = 5;

            var workflow = Workflows.buildPhasedPipeline()
                    .controlPhase(ctrlWorkersCount)
                    .collectionPhase(collectionWorkersCount).build();

            assertEquals(ctrlWorkersCount, workflow.numCtrlPhaseWorkers());

            assertEquals(collectionWorkersCount, workflow.numCollectionPhaseWorkers());
        }

        @Test
        public void testIncompleteBuildThrowsException() {
            assertThrows(IncompleteWorkflowBuildException.class, () -> {
                Workflows.buildPhasedPipeline().build();
            });

            assertThrows(IncompleteWorkflowBuildException.class, () -> {
                Workflows.buildPhasedPipeline().controlPhase(1).build();
            });

            assertThrows(IncompleteWorkflowBuildException.class, () -> {
                Workflows.buildPhasedPipeline().collectionPhase(1).build();
            });
        }

        @Test
        public void testInvalidNumWorkers() {

            int[] testIllegalVals = new int[] { -3, 0 };

            for (int illegalWorkerCount : testIllegalVals) {
                assertThrows(IllegalArgumentException.class, () -> {
                    Workflows.buildPhasedPipeline().controlPhase(illegalWorkerCount);
                });

                assertThrows(IllegalArgumentException.class, () -> {
                    Workflows.buildPhasedPipeline().collectionPhase(illegalWorkerCount);
                });
            }
        }
    }
}
