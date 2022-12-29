package packetfilter.drivers.stripedlock.pngr;

import packetfilter.firewall.StripedLockPNGRFilrewall;

interface FirewallFactory {

    default public StripedLockPNGRFilrewall createStripedLockPNGRFirewall() {
        return new StripedLockPNGRFilrewall(10_000);
    }
}
