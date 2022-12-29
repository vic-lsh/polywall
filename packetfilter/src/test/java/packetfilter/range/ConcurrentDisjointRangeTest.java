package packetfilter.range;

public class ConcurrentDisjointRangeTest extends DisjointRangeTestBase<ConcurrentDisjointRange> {

    @Override
    protected ConcurrentDisjointRange createBinaryDisjointRange() {
        return new ConcurrentDisjointRange(INITIAL_BIT);
    }

}
