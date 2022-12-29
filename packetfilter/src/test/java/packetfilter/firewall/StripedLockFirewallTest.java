package packetfilter.firewall;

public class StripedLockFirewallTest extends ConcurrentFirewallTestBase<StripedLockFirewall> {

    @Override
    protected StripedLockFirewall createFirewall() {
        int numLocks = 3200;
        return new StripedLockFirewall(numLocks);
    }

}
