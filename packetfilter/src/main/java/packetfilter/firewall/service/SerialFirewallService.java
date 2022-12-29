package packetfilter.firewall.service;

import packetfilter.firewall.Firewall;
import packetfilter.firewall.fingerprint.SerialFPCollector;

public class SerialFirewallService<F extends Firewall> extends AbstractFirewallService<F, SerialFPCollector> {

    public SerialFirewallService(F firewall) {
        super(firewall, new SerialFPCollector());
    }

}
