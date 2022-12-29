package packetfilter.range;

public class MutableRange extends RangeBase {

    public MutableRange(int min, int max) {
        super(min, max);
    }

    public void setMin(int min) {
        if (min > this.max) {
            throw new IllegalArgumentException();
        }
        this.min = min;
    }

    public void setMax(int max) {
        if (max < this.min) {
            throw new IllegalArgumentException();
        }
        this.max = max;
    }

}
