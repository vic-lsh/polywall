package packetfilter.utils.concurrent;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class QueueProcessingPool<W> {

    private final Queue<W> workQueue;
    private final ExecutorService pool;
    private final Consumer<W> task;
    private Thread workDispatcherThread;

    /**
     * Instantiates a pool of workers to consume items in a queue. Each worker
     * continues consuming items in the queue until the queue is empty.
     * 
     * The class assumes it is the sole user of the provided queue. Specifically,
     * new items should not be added after this queue is passed to this processing
     * pool.
     * 
     * @param finalizedWorkQueue a queue of work to be consumed by threads
     * @param nThreads           number of worker threads
     * @param task               the task to be performed by each worker
     */
    public QueueProcessingPool(Queue<W> finalizedWorkQueue, int nThreads, Consumer<W> task) {
        this.workQueue = finalizedWorkQueue;
        this.pool = Executors.newFixedThreadPool(nThreads);
        this.task = task;
    }

    /**
     * Start worker threads to begin consuming work.
     */
    public void start() {
        if (workDispatcherThread != null) {
            throw new IllegalStateException("Cannot invoke 'start' multiple times");
        }

        workDispatcherThread = new Thread(() -> {
            while (true) {
                var next = workQueue.poll();
                if (next == null) {
                    return;
                }
                this.pool.submit(() -> {
                    this.task.accept(next);
                });
            }
        });

        workDispatcherThread.start();
    }

    /**
     * Blocks until all work in the provided work queue has been consumed
     * by some thread.
     * 
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void join() throws InterruptedException, TimeoutException {
        if (workDispatcherThread == null) {
            throw new IllegalStateException("Cannot invoke 'join' before 'start'");
        }

        workDispatcherThread.join();
        pool.shutdown();
        var terminated = pool.awaitTermination(3, TimeUnit.SECONDS);
        if (!terminated) {
            throw new TimeoutException();
        }
    }
}
