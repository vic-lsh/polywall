package packetfilter.utils.concurrent;

import java.util.AbstractQueue;

/**
 * A queue that is terminable by a thread or process other than the consumer.
 * 
 * Provides a mechanism for another thread to stop the consumer(s) of this queue
 * from further consumption. This is achieved via the `terminate()` method.
 * 
 * Users of this abstract class need to specify the queue instance they want to
 * use. The specified queue must be thread-safe.
 */
public abstract class TerminableQueue<T, Q extends AbstractQueue<T>> {

    private volatile boolean terminated = false;

    protected Q queue;

    /**
     * @return A thread-safe queue.
     */
    abstract protected Q initQueue();

    public TerminableQueue() {
        this.queue = initQueue();
    }

    /**
     * Gets the next element in the queue, or errs if the queue is terminated.
     * 
     * @return the next value in the queue. This is possibly `null`, if the
     *         underlying queue implementation chooses to return `null`.
     * @throws QueueTerminatedException if the queue is terminated.
     */
    public T poll() throws QueueTerminatedException {
        if (terminated) {
            throw new QueueTerminatedException();
        }
        return this.queue.poll();
    }

    public void add(T item) {
        this.queue.add(item);
    }

    public int size() {
        return this.queue.size();
    }

    /**
     * Halts further consumption of this queue.
     */
    public void terminate() {
        terminated = true;
    }
}
