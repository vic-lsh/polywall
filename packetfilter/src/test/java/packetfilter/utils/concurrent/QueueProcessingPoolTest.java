package packetfilter.utils.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class QueueProcessingPoolTest {
    @Test
    public void testSumInts() throws InterruptedException, TimeoutException {
        final int MIN = 1, MAX = 100000;
        var queue = initQueueWithNumRange(MIN, MAX);

        // use a pool of workers to sum integers
        AtomicInteger sum = new AtomicInteger(0);
        var pool = new QueueProcessingPool<>(queue, 34, (n) -> {
            sum.addAndGet(n);
        });
        pool.start();
        pool.join();

        assertEquals(sum.get(), sumRange(MIN, MAX));
    }

    private Queue<Integer> initQueueWithNumRange(int min, int max) {
        Queue<Integer> q = new LinkedBlockingQueue<>();
        for (int i = min; i <= max; i++) {
            q.add(i);
        }
        return q;
    }

    private int sumRange(int min, int max) {
        int c = 0;
        for (int i = min; i <= max; i++) {
            c += i;
        }
        return c;
    }
}
