package packetfilter.firewall;

public class StripedLockPNGRFirewallTest extends ConcurrentFirewallTestBase<StripedLockPNGRFilrewall> {

    @Override
    protected StripedLockPNGRFilrewall createFirewall() {
        return new StripedLockPNGRFilrewall(4300);
    }

}
