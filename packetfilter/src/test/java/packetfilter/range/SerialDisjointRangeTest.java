package packetfilter.range;

public class SerialDisjointRangeTest extends DisjointRangeTestBase<SerialDisjointRange> {

    @Override
    protected SerialDisjointRange createBinaryDisjointRange() {
        return new SerialDisjointRange(INITIAL_BIT);
    }

}
