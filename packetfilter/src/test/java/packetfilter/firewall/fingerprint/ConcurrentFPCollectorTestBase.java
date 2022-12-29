package packetfilter.firewall.fingerprint;

public abstract class ConcurrentFPCollectorTestBase<T extends FPCollector> extends FPCollectorTestBase<T> {

    @Override
    protected boolean isMultiThreaded() {
        return true;
    }
}
