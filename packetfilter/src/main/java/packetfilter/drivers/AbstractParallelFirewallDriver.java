package packetfilter.drivers;

import packetfilter.firewall.Firewall;
import packetfilter.firewall.fingerprint.ConcurrentFPCollector;
import packetfilter.firewall.fingerprint.FPCollector;
import packetfilter.firewall.service.ConcurrentFirewallService;
import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.PacketGeneratorConfig;

public abstract class AbstractParallelFirewallDriver<F extends Firewall> extends AbstractFirewallDriver<F> {
    public static final int MAX_WORKERS = 256;

    public final int nThreads;

    public AbstractParallelFirewallDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(packetGenConfig);

        if (numWorkers <= 0 || numWorkers > MAX_WORKERS) {
            throw new IllegalArgumentException();
        }
        this.nThreads = numWorkers;
    }

    protected FirewallService createFirewallService(F firewall) {
        return new ConcurrentFirewallService<Firewall, FPCollector>(firewall, new ConcurrentFPCollector());
    }

}
