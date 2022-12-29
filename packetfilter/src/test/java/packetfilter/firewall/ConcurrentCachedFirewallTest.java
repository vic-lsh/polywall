package packetfilter.firewall;

import org.junit.jupiter.api.Disabled;

@Disabled("Firewall with caching has not reached performance goals.")
public class ConcurrentCachedFirewallTest extends ConcurrentFirewallTestBase<ConcurrentCachedFirewall> {

    @Override
    protected ConcurrentCachedFirewall createFirewall() {
        return new ConcurrentCachedFirewall();
    }

}
