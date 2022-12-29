package packetfilter.firewall.service;

import packetfilter.firewall.Firewall;
import packetfilter.firewall.fingerprint.FPCollector;

public class ConcurrentFirewallService<F extends Firewall, P extends FPCollector> extends AbstractFirewallService<F, P> {

    public ConcurrentFirewallService(F firewall, P fingerprints) {
        super(firewall, fingerprints);
    }

}
