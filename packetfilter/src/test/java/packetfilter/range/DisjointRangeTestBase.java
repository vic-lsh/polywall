package packetfilter.range;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packetfilter.utils.RangeRandom;

/**
 * Implements common tests for the binary disjoint range implementations.
 */
public abstract class DisjointRangeTestBase<R extends DisjointRange> {

    R disjointRange;

    protected final boolean INITIAL_BIT = true;
    protected final int TRIALS = 10;

    private Random rand;
    private RangeRandom rangeRand;

    protected abstract R createBinaryDisjointRange();

    @BeforeEach
    public void init() {
        rand = new Random(100);
        rangeRand = new RangeRandom(100);
        disjointRange = createBinaryDisjointRange();
    }

    @Test
    public void testThrowsWhenMinGreaterThanMax() {
        assertThrows(IllegalArgumentException.class, () -> {
            disjointRange.set(1, -1, true);
        });
    }

    @Test
    public void testDisjointRangeInitializedToDefaultValue() {
        // initially, every number is set to initial bit
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rand.nextInt()), INITIAL_BIT);
        });

        // noop
        int min = 10024;
        int max = 11101120;
        disjointRange.set(min, max, INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), INITIAL_BIT);
        });
    }

    @Test
    public void testFlipAllBits() {
        disjointRange.set(Integer.MIN_VALUE, Integer.MAX_VALUE, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rand.nextInt()), !INITIAL_BIT);
        });
    }

    @Test
    public void testFlipSection() {
        int min, max;
        min = 390303;
        max = 10000000;
        disjointRange.set(min, max, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntOutOfRange(min, max)), INITIAL_BIT);
        });
    }

    @Test
    public void testFlipOverlappedSections() {
        int min = 30003;
        int max = 1011022;
        disjointRange.set(min, max, !INITIAL_BIT);
        int min2 = min - 10000;
        int max2 = max - 10000;
        disjointRange.set(min2, max2, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            int next = rangeRand.nextIntInRange(min2, max); // the union of both ranges should have !initialbit.
            assertSame(disjointRange.get(next), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntOutOfRange(min2, max)), INITIAL_BIT);
        });
    }

    @Test
    public void testNegativeRange() {
        int negMin = -100000;
        int negMax = -3300;
        disjointRange.set(negMin, negMax, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(negMin, negMax)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(negMax + 1, 0)), INITIAL_BIT);
        });
    }

    @Test
    public void testMultipleDisjointRanges() {
        int negMin = -100000;
        int negMax = -3300;
        int min = -negMax;
        int max = -negMin;
        disjointRange.set(negMin, negMax, !INITIAL_BIT);
        disjointRange.set(min, max, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(negMin, negMax)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(negMax + 1, 0)), INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(0, min - 1)), INITIAL_BIT);
        });
    }

    @Test
    public void testRangeFromNegativeToPositive() {
        int crossBoundsMin = -300;
        int crossBoundsMax = 277;
        disjointRange.set(crossBoundsMin, crossBoundsMax, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(crossBoundsMin, crossBoundsMax)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntOutOfRange(crossBoundsMin, crossBoundsMax)), INITIAL_BIT);
        });
    }

    @Test
    public void testInnerPartialUnsetBits() {
        int min = -100024;
        int max = 3333;
        disjointRange.set(min, max, !INITIAL_BIT);
        int unsetMin = -100;
        int unsetMax = 100;
        disjointRange.set(unsetMin, unsetMax, INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(Integer.MIN_VALUE, min - 1)), INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, unsetMin - 1)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(unsetMin, unsetMax)), INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(unsetMax + 1, max)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(max + 1, Integer.MAX_VALUE)), INITIAL_BIT);
        });
    }

    @Test
    public void testOverlappedPartialUnsetBits() {
        int min = -100024;
        int max = 3333;
        disjointRange.set(min, max, !INITIAL_BIT);
        int unsetMin = -100;
        int unsetMax = max + 100;
        disjointRange.set(unsetMin, unsetMax, INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(Integer.MIN_VALUE, min - 1)), INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, unsetMin - 1)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(unsetMin, unsetMax)), INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(max + 1, Integer.MAX_VALUE)), INITIAL_BIT);
        });
    }

    @Test
    public void testSyntheticWorkload() {
        int min, max;

        min = 390303;
        max = 10000000;

        // Setting range to initial-bit is a no-op
        disjointRange.set(min, max, INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), INITIAL_BIT);
            assertSame(disjointRange.get(rand.nextInt()), INITIAL_BIT);
        });

        // Test setting opposite for some range.
        disjointRange.set(min, max, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntOutOfRange(min, max)), INITIAL_BIT);
        });

        // Test setting min and max to a range overlapping to the previous range
        int min2 = min - 10000;
        int max2 = max - 20000;
        disjointRange.set(min2, max2, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            int next = rangeRand.nextIntInRange(min2, max); // the union of both ranges should have !initialbit.
            assertSame(disjointRange.get(next), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntOutOfRange(min2, max)), INITIAL_BIT);
        });

        // Test setting a negative number range
        int negMin = -max;
        int negMax = -min;
        disjointRange.set(negMin, negMax, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(negMin, negMax)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(negMax + 1, 0)), INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(0, min - 1)), INITIAL_BIT);
        });

        // Test setting a range that goes from negative to positive
        int crossBoundsMin = -300;
        int crossBoundsMax = 277;
        disjointRange.set(crossBoundsMin, crossBoundsMax, !INITIAL_BIT);
        repeat(TRIALS, () -> {
            assertSame(disjointRange.get(rangeRand.nextIntInRange(crossBoundsMin, crossBoundsMax)), !INITIAL_BIT);
            assertSame(disjointRange.get(rangeRand.nextIntInRange(min, max)), !INITIAL_BIT);
        });

        // Test flipping back some bits
    }

    private void repeat(int N, Runnable func) {
        for (int i = 0; i < N; i++) {
            func.run();
        }
    }
}
