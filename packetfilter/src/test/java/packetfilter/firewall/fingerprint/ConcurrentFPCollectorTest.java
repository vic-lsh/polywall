package packetfilter.firewall.fingerprint;

public class ConcurrentFPCollectorTest extends ConcurrentFPCollectorTestBase<ConcurrentFPCollector> {

    @Override
    protected ConcurrentFPCollector createFPCollector() {
        return new ConcurrentFPCollector();
    }

}
