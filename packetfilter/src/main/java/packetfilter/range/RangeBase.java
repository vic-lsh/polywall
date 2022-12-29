package packetfilter.range;

public abstract class RangeBase implements Comparable<RangeBase> {
    protected int min;
    protected int max;

    public RangeBase(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException();
        }

        this.min = min;
        this.max = max;
    }

    @Override
    public int compareTo(RangeBase o) {
        if (this.min < o.min()) {
            return -1;
        } else if (this.min == o.min()) {
            if (this.max < o.max()) {
                return -1;
            } else if (this.max == o.max()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    public RangeComparison compareRange(RangeBase other) {
        if (other.min == this.min && other.max == this.max) {
            return RangeComparison.Equal;
        }

        boolean strictContainedByOther = other.min < this.min && other.max > this.max;
        boolean containedByOtherTouchesLower = other.min == this.min && other.max > this.max;
        boolean containedByOtherTouchesUpper = other.min < this.min && other.max == this.max;
        if (strictContainedByOther || containedByOtherTouchesLower || containedByOtherTouchesUpper) {
            return RangeComparison.Contained; // this is contained by other
        }

        boolean strictContainsOther = other.min > this.min && other.max < this.max;
        boolean containsOtherTouchesUpper = other.min > this.min && other.max == this.max;
        boolean containsOtherTouchesLower = other.min == this.min && other.max < this.max;
        if (strictContainsOther || containsOtherTouchesLower || containsOtherTouchesUpper) {
            return RangeComparison.Contains; // this contains other
        }

        if (other.min < this.min && other.max >= this.min) {
            return RangeComparison.OverlapLower;
        }

        if (other.min > this.min && other.min <= this.max) {
            return RangeComparison.OverlapUpper;
        }

        if (this.min > other.max) {
            return RangeComparison.StrictlyHigher; // self strictly higher than other
        }

        if (this.max < other.min) {
            return RangeComparison.StrictlyLower; // self strictly lower than other
        }

        return null;
    }

    public int max() {
        return max;
    }

    public int min() {
        return min;
    }

    public boolean inRange(int x) {
        return x >= min && x <= max;
    }

    @Override
    public String toString() {
        return String.format("Range min: '%d', max: '%d'", this.min, this.max);
    }
}
