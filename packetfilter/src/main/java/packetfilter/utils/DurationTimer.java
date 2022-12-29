package packetfilter.utils;

/**
 * Convenience class for measuring a duration.
 */
public class DurationTimer {

    private long start;

    /**
     * Starts timing a duration.
     */
    public void start() {
        start = System.nanoTime();
    }

    /**
     * Ends timing a duration.
     * 
     * @return the duration recorded in nanoseconds.
     */
    public long end() {
        long end = System.nanoTime();
        return end - start;
    }
}
