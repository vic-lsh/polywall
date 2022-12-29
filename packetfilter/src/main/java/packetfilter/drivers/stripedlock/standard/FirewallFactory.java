package packetfilter.drivers.stripedlock.standard;

import packetfilter.firewall.StripedLockFirewall;

interface FirewallFactory {

    default StripedLockFirewall createStripedLockFirewall() {
        int numberOfLocks = 10_000;
        return new StripedLockFirewall(numberOfLocks);
    }

}
