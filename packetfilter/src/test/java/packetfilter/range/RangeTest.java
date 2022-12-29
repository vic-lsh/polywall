package packetfilter.range;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RangeTest {
    @Nested
    class InitializationTest {
        @Test
        public void testMaxCannotBeLowerThanMin() {
            assertThrows(IllegalArgumentException.class, () -> {
                new MutableRange(100, 99);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new MutableRange(-300, -10000);
            });
        }

        @Test
        public void testRangeCanBeInitialized() {
            var range = new MutableRange(100, 200);
            assertEquals(range.min(), 100);
            assertEquals(range.max(), 200);

            range = new MutableRange(-300, -120);
            assertEquals(range.min(), -300);
            assertEquals(range.max(), -120);
        }
    }

    @Nested
    class MinMaxValueUpdateTest {
        @Test
        public void testUpdateRangeMin() {
            var range = new MutableRange(100, 200);
            range.setMin(188);
            assertEquals(range.min(), 188);
        }

        @Test
        public void testRangeMinUpdateCannotBreakInvariant() {
            var range = new MutableRange(100, 200);
            assertThrows(IllegalArgumentException.class, () -> {
                range.setMin(300);
            });
        }

        @Test
        public void testUpdateRangeMax() {
            var range = new MutableRange(100, 200);
            range.setMax(300);
            assertEquals(range.max(), 300);
        }

        @Test
        public void testRangeMaxUpdateCannotBreakInvariant() {
            var range = new MutableRange(100, 200);
            assertThrows(IllegalArgumentException.class, () -> {
                range.setMax(-12);
            });
        }
    }

    @Nested
    class CheckInRangeTest {
        @Test
        public void testCheckValusIsInRange() {
            var range = new MutableRange(-3, 3);
            assertTrue(range.inRange(1));
            assertFalse(range.inRange(100));
        }

        @Test
        public void testInRangeCheckIsInclusive() {
            var range = new MutableRange(-3, 3);
            assertTrue(range.inRange(-3));
            assertTrue(range.inRange(3));
        }
    }

    @Nested
    class DefaultComparisonTest {
        @Test
        public void testRangeEquality() {
            int min = 30, max = 40;
            var n1 = new MutableRange(min, max);
            var n2 = new MutableRange(min, max);
            assertTrue(n1.compareTo(n2) == 0);
        }

        @Test
        public void testRangeMinComparison() {
            // when 1 min is less than another, max is irrelevant

            MutableRange r1, r2;

            r1 = new MutableRange(10, 30);
            r2 = new MutableRange(20, 30);
            assertTrue(r1.compareTo(r2) == -1);
            assertTrue(r2.compareTo(r1) == 1);

            r1 = new MutableRange(10, 11);
            r2 = new MutableRange(20, 30);
            assertTrue(r1.compareTo(r2) == -1);
            assertTrue(r2.compareTo(r1) == 1);

            r1 = new MutableRange(10, 30);
            r2 = new MutableRange(20, 21);
            assertTrue(r1.compareTo(r2) == -1);
            assertTrue(r2.compareTo(r1) == 1);
        }

        @Test
        public void testRangeMaxComparson() {
            // only consider the max field if min is equal

            MutableRange r1, r2;

            r1 = new MutableRange(10, 20);
            r2 = new MutableRange(10, 30);
            assertTrue(r1.compareTo(r2) == -1);
            assertTrue(r2.compareTo(r1) == 1);
        }

        @Test
        public void testComparisonIsImplementationAgnostic() {
            int min = 30, max = 40;
            // 2 concrete classes should reuse the same abstract class compareTo.
            var n1 = new MutableRange(min, max);
            var n2 = new ImmutableRange(min, max);
            assertTrue(n1.compareTo(n2) == 0);
        }
    }

    @Nested
    class RangeComparisonTest {
        @Test
        public void testRangeEqualityComparison() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(100, 200);
            assertEquals(range.compareRange(other), RangeComparison.Equal);
        }

        @Test
        public void testRangeCompareContains() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(144, 145);
            assertEquals(range.compareRange(other), RangeComparison.Contains);
        }

        @Test
        public void testRangeContainsWhenOneBoundEquals() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(188, 200);
            assertEquals(range.compareRange(other), RangeComparison.Contains);

            range = new MutableRange(100, 200);
            other = new MutableRange(100, 123);
            assertEquals(range.compareRange(other), RangeComparison.Contains);
        }

        @Test
        public void testRangeCompareContained() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(-10, 222);
            assertEquals(range.compareRange(other), RangeComparison.Contained);
        }

        @Test
        public void testRangeContainedWhenOneBoundEquals() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(-10, 200);
            assertEquals(range.compareRange(other), RangeComparison.Contained);

            range = new MutableRange(100, 200);
            other = new MutableRange(100, 299);
            assertEquals(range.compareRange(other), RangeComparison.Contained);
        }

        @Test
        public void testRangeLowerOverlap() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(-10, 135);
            assertEquals(range.compareRange(other), RangeComparison.OverlapLower);
        }

        @Test
        public void testLowerOverlapWhenLowerBoundEquals() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(-10, 100);
            assertEquals(range.compareRange(other), RangeComparison.OverlapLower);
        }

        @Test
        public void testRangeUpperOverlap() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(134, 288);
            assertEquals(range.compareRange(other), RangeComparison.OverlapUpper);
        }

        @Test
        public void testUpperOverlapWhenUpperBoundEquals() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(200, 299);
            assertEquals(range.compareRange(other), RangeComparison.OverlapUpper);
        }

        @Test
        public void testStrictlyLower() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(300, 400);
            assertEquals(range.compareRange(other), RangeComparison.StrictlyLower);
        }

        @Test
        public void testStrictlyHigher() {
            var range = new MutableRange(100, 200);
            var other = new MutableRange(-123, -45);
            assertEquals(range.compareRange(other), RangeComparison.StrictlyHigher);
        }
    }

    @Nested
    class MiscRangeTest {
        @Test
        public void testRangeToString() {
            var range = new MutableRange(100, 200);
            assertEquals(range.toString(), "Range min: '100', max: '200'");
        }
    }

}
