package packetfilter.packet.workers.pools;

import java.util.concurrent.ExecutionException;

/**
 * Defines behavior of forking then joining a fixed number of threads.
 */
public interface StaticForkJoinable {
    /**
     * Start worker threads to begin consuming work.
     * 
     * Number of worker threads are defined via constructors / setters of
     * classes that implement this interface.
     */
    public StaticForkJoinable start();

    /**
     * Blocks until all worker threads finish.
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public StaticForkJoinable join() throws InterruptedException, ExecutionException;
}
