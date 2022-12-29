package packetfilter.firewall.fingerprint;

public class SerialFPCollectorTest extends FPCollectorTestBase<SerialFPCollector> {

    @Override
    protected SerialFPCollector createFPCollector() {
        return new SerialFPCollector();
    }

    @Override
    protected boolean isMultiThreaded() {
        return false;
    }

}
