package packetfilter.utils;

import java.util.Random;

/**
 * A utility class for generating random number in or outside of integer ranges.
 */
public class RangeRandom {
    private Random rand;

    public RangeRandom(int seed) {
        rand = new Random(seed);
    }

    /**
     * Return a random integer in the range [min, max] (inclusive on both ends).
     * 
     * @param min the minimum bound of the random number, inclusive.
     * @param max the maximum bound of the random number, inclusive.
     * @return a random number in the inclusive bound of `[min, max]`.
     */
    public int nextIntInRange(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException();
        }

        try {
            int bound = Math.addExact(Math.subtractExact(max, min), 1); // max-min+1
            return rand.nextInt(bound) + min;
        } catch (ArithmeticException e) { // previous bound calculation overflowed
            int bound = min - max + 1; // the reverse bound must not overflow
            return -rand.nextInt(bound) + max;
        }
    }

    /**
     * Returns an int in the disjoint ranges outside of `[min, max]`.
     * 
     * @param min the minimum bound of the invalid range of a random number.
     * @param max the maximum bound of the invalid range of a random number.
     * @return a number either in `[Integer.MIN_VALUE..min)` or
     *         `(max..Integer.MAX_VALUE]`.
     */
    public int nextIntOutOfRange(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException();
        }

        if (rand.nextInt() > 0) {
            return nextIntInRange(Integer.MIN_VALUE, min - 1);
        } else {
            return nextIntInRange(max + 1, Integer.MAX_VALUE);
        }
    }
}
