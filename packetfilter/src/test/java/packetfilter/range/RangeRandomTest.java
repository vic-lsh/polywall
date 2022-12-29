package packetfilter.range;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packetfilter.utils.RangeRandom;

public class RangeRandomTest {
    private RangeRandom rand;

    private final int TRIALS = 100000;

    @BeforeEach
    public void init() {
        this.rand = new RangeRandom(100);
    }

    @Test
    public void testIllegalArgInRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            rand.nextIntInRange(100, -100);
        });
    }

    @Test
    public void testIllegalArgInOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            rand.nextIntOutOfRange(100, -100);
        });
    }

    @Test
    public void testGenInPositiveRange() {
        testInRange(100, 100000);
    }

    @Test
    public void testGenOutOfPositiveRange() {
        testOutOfRange(1000, 100000);
    }

    @Test
    public void testGenInNegativeRange() {
        testInRange(-100, -41);
    }

    @Test
    public void testGenOutOfNegativeRange() {
        testOutOfRange(-100024, -334);
    }

    private void testInRange(int min, int max) {
        repeat(TRIALS, () -> {
            int out = rand.nextIntInRange(min, max);
            assertTrue(out >= min);
            assertTrue(out <= max);
        });
    }

    private void testOutOfRange(int min, int max) {
        repeat(TRIALS, () -> {
            int out = rand.nextIntOutOfRange(min, max);
            assertTrue(out < min || out > max);
        });
    }

    private void repeat(int N, Runnable func) {
        for (int i = 0; i < N; i++) {
            func.run();
        }
    }
}
