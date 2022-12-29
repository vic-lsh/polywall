package packetfilter.firewall;

public class FineGrainedFirewallTest extends ConcurrentFirewallTestBase<FineGrainedFirewall> {

    @Override
    protected FineGrainedFirewall createFirewall() {
        return new FineGrainedFirewall();
    }

}
