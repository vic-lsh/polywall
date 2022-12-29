package packetfilter.firewall;

public class CoarseFirewallTest extends ConcurrentFirewallTestBase<CoarseFirewall> {

    @Override
    protected CoarseFirewall createFirewall() {
        return new CoarseFirewall();
    }

}
