package packetfilter.firewall;

public class NonBlockingFirewallTest extends ConcurrentFirewallTestBase<NonBlockingFirewall> {

    @Override
    protected NonBlockingFirewall createFirewall() {
        return new NonBlockingFirewall();
    }

}
