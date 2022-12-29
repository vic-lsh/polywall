package packetfilter.firewall;

public class SerialFirewallTest extends FirewallTestBase<SerialFirewall> {

    @Override
    protected SerialFirewall createFirewall() {
        return new SerialFirewall();
    }

}
