package packetfilter.drivers;

import packetfilter.firewall.Firewall;
import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.generators.PacketGenerator;
import packetfilter.packet.generators.PacketGeneratorConfig;

public abstract class AbstractFirewallDriver<F extends Firewall> implements FirewallDriver {

    protected F firewall;
    protected FirewallService firewallService;
    protected final PacketGeneratorConfig pktGenConfig;
    protected PacketGenerator pktGen;

    public AbstractFirewallDriver(PacketGeneratorConfig pktGenConfig) {
        this.pktGenConfig = pktGenConfig;
        this.initState();
    }

    protected abstract F createFirewall();

    protected abstract FirewallService createFirewallService(F firewall);

    @Override
    public void restart() {
        this.initState();
    }

    @Override
    public FirewallWarmupWorker warmup() {
        return new FirewallWarmupWorker(firewall, pktGenConfig);
    }

    @Override
    public String getName() {
        var classNamePath = this.getClass().getName().split("\\.");
        return classNamePath[classNamePath.length - 1];
    }

    private void initState() {
        this.firewall = this.createFirewall();
        this.firewallService = this.createFirewallService(firewall);
        this.pktGen = pktGenConfig.toPacketGenerator();
    }

}
