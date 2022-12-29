package packetfilter.firewall.service;

import packetfilter.firewall.Firewall;

import packetfilter.firewall.fingerprint.FPCollector;
import packetfilter.packet.Packet;
import packetfilter.packet.Packet.MessageType;

abstract class AbstractFirewallService<F extends Firewall, P extends FPCollector> implements FirewallService {

    private F firewall;
    private P fingerprints;

    public AbstractFirewallService(F firewall, P fingerprints) {
        this.firewall = firewall;
        this.fingerprints = fingerprints;
    }

    @Override
    public void process(Packet packet) {
        if (packet.type == MessageType.ConfigPacket) {
            this.processConfigPacket(packet);
        } else {
            this.processDataPacket(packet);
        }
    }

    @Override
    public boolean processIfDataPacket(Packet packet) {
        if (packet.type == MessageType.DataPacket) {
            this.processDataPacket(packet);
            return true;
        }
        return false;
    }

    @Override
    public boolean processIfConfigPacket(Packet packet) {
        if (packet.type == MessageType.ConfigPacket) {
            this.processConfigPacket(packet);
            return true;
        }
        return false;
    }

    @Override
    public void collectFingerprint(Packet dataPacket) {
        this.fingerprints.collect(dataPacket.fingerprint());
    }

    @Override
    public boolean checkOrUpdateAccessControl(Packet packet) {
        if (packet.type == MessageType.DataPacket) {
            return this.firewall.decide(packet);
        } else {
            this.processConfigPacket(packet);
            return false;
        }
    }

    private void processDataPacket(Packet packet) {
        if (this.firewall.decide(packet)) {
            this.collectFingerprint(packet);
        }
    }

    private void processConfigPacket(Packet packet) {
        this.firewall.update(packet);
    }

}
