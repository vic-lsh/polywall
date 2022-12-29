package packetfilter.range;

/**
 * Specifies the interface for DisjointRange: a set of operations to transform
 * the unified integer range into smaller ranges each associated with different
 * values.
 * 
 * This is a binary realization of the idea of disjoint ranges: each range can
 * only be associated with 0 or 1.
 */
public interface DisjointRange {
    /**
     * Marks the bit associated to every integer in the range [min, max], inclusive.
     * 
     * @param min    the lower bound to be assigned to the `update`.
     * @param max    the upper bound to be assigned to the `update`.
     * @param update specifies the bit associated with a numeric range.
     * @throws IllegalArgumentException if min > max.
     */
    public void set(int min, int max, boolean update) throws IllegalArgumentException;

    /**
     * Gets the boolean value associated with a number.
     * 
     * @param x the number for which the boolean value will be returned.
     * @return the boolean value associated with this number.
     */
    public boolean get(int x);
}
