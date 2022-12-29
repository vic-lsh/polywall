package packetfilter.firebench.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ThreadingStrategyBuilderTest {

    @Test
    public void testBuildThreadingStrategy() throws IncompleteThreadingStrategyBuildException {
        var strategy = new ThreadingStrategyBuilder()
                .withNumberOfThreads(new int[] { 34, 78, 100 })
                .withPercentageDataWorkers(0.5)
                .withPercentageControlWorkers(0.8).build();

        assertNotNull(strategy);
    }

    @Test
    public void testBuilderSupportsVariadicNumThreadArgs() throws IncompleteThreadingStrategyBuildException {
        var strategy = new ThreadingStrategyBuilder()
                .withNumberOfThreads(1, 2, 3, 4, 10, 21, 111, 1209, 3000)
                .withPercentageDataWorkers(0.5)
                .withPercentageControlWorkers(0.8).build();

        assertNotNull(strategy);
    }

    @Test
    public void testCanBuildWithOneThreadVariant() throws IncompleteThreadingStrategyBuildException {
        var strategy = new ThreadingStrategyBuilder()
                .withNumberOfThreads(3)
                .withPercentageDataWorkers(0.5)
                .withPercentageControlWorkers(0.8).build();

        assertNotNull(strategy);
    }

    @Test
    public void testCannotBuildWithNonPositiveThreadCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder().withNumberOfThreads(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder().withNumberOfThreads(-3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder()
                    .withNumberOfThreads(new int[] { 3, 8, -1 }); // all elements must be positive
        });
    }

    @Test
    public void testDataWorkerPercentageMustBeLegal() {
        assertDoesNotThrow(() -> new ThreadingStrategyBuilder().withPercentageDataWorkers(0));
        assertDoesNotThrow(() -> new ThreadingStrategyBuilder().withPercentageDataWorkers(1));

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder().withPercentageDataWorkers(-1.3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder().withPercentageDataWorkers(3.8);
        });
    }

    @Test
    public void testControlWorkerPercentageMustBeLegal() {
        assertDoesNotThrow(() -> new ThreadingStrategyBuilder().withPercentageControlWorkers(0));
        assertDoesNotThrow(() -> new ThreadingStrategyBuilder().withPercentageControlWorkers(1));

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder().withPercentageControlWorkers(-1.3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadingStrategyBuilder().withPercentageControlWorkers(3.8);
        });
    }

    @Test
    public void testIncompleteBuildErrs() {
        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder().build();
        });

        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder().withNumberOfThreads(1).build();
        });

        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder().withPercentageControlWorkers(0.1).build();
        });

        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder().withPercentageDataWorkers(0.3).build();
        });

        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder()
                    .withNumberOfThreads(1).withPercentageControlWorkers(0.1).build();
        });

        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder()
                    .withNumberOfThreads(1).withPercentageDataWorkers(0.1).build();
        });

        assertThrows(IncompleteThreadingStrategyBuildException.class, () -> {
            new ThreadingStrategyBuilder()
                    .withPercentageDataWorkers(0.8).withPercentageControlWorkers(0.1).build();
        });
    }
}
