package packetfilter.firebench.config;

import packetfilter.utils.ArgumentValidator;

/**
 * Builds a `ThreadingStrategy`.
 */
public class ThreadingStrategyBuilder {

    private int[] threadCount;
    private Double percentageDataWorkers;
    private Double percentageControlWorkers;

    /** Specifies the number of worker threads. */
    public ThreadingStrategyBuilder withNumberOfThreads(int threadCount) {
        return withNumberOfThreads(new int[] { threadCount });
    }

    /**
     * Specifies the number of worker threads.
     * 
     * If multiple arguments are provided, the ThreadingStrategy instance
     * can iterate through threads as specified in the arguments.
     */
    public ThreadingStrategyBuilder withNumberOfThreads(int... threadCount) {
        ArgumentValidator.validateIntArrayNotEmpty(threadCount);
        for (int c : threadCount) {
            ArgumentValidator.validateGreaterThanZero(c);
        }

        this.threadCount = threadCount;
        return this;
    }

    /**
     * Specifies the percentage of threads that are data workers in a partial
     * pipeline.
     */
    public ThreadingStrategyBuilder withPercentageDataWorkers(double percentage) {
        ArgumentValidator.validatePercentage(percentage);
        this.percentageDataWorkers = percentage;
        return this;
    }

    /**
     * Specifies the percentage of threads that are config workers in a phased
     * pipeline.
     */
    public ThreadingStrategyBuilder withPercentageControlWorkers(double percentage) {
        ArgumentValidator.validatePercentage(percentage);
        this.percentageControlWorkers = percentage;
        return this;
    }

    public ThreadingStrategy build() throws IncompleteThreadingStrategyBuildException {
        try {
            ArgumentValidator.notNull(threadCount);
            ArgumentValidator.notNull(percentageDataWorkers);
            ArgumentValidator.notNull(percentageControlWorkers);
        } catch (IllegalArgumentException e) {
            throw new IncompleteThreadingStrategyBuildException();
        }

        return new ThreadingStrategy(threadCount, percentageDataWorkers, percentageControlWorkers);
    }

}
