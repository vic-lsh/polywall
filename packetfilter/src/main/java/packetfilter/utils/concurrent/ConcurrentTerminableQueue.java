package packetfilter.utils.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An implementation of `TerminableQueue`. The internal queue is a
 * non-blocking, unbounded `ConcurrentLinkedQueue`.
 */
public class ConcurrentTerminableQueue<T> extends TerminableQueue<T, ConcurrentLinkedQueue<T>> {

    @Override
    protected ConcurrentLinkedQueue<T> initQueue() {
        return new ConcurrentLinkedQueue<>();
    }

}
