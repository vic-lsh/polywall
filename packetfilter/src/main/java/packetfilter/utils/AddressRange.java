package packetfilter.utils;

/**
 * Hosts a valid range of addresses.
 * 
 * The range start value is guaranteed to be lower or equal to the range's end.
 */
public class AddressRange {
    public final int start;
    public final int end;

    public AddressRange(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException();
        }
        this.start = start;
        this.end = end;
    }
}
