package packetfilter.firewall;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import packetfilter.firewall.internals.ConcurrentPNGMap;
import packetfilter.firewall.internals.ConcurrentRMap;
import packetfilter.firewall.internals.PNGMap;
import packetfilter.firewall.internals.RMap;
import packetfilter.packet.Packet;
import packetfilter.utils.ArgumentValidator;

public class CoarseFirewall implements Firewall {

    private PNGMap PNG;
    private RMap R;

    private ReentrantReadWriteLock rwlock;

    public CoarseFirewall() {
        this.PNG = new ConcurrentPNGMap();
        this.R = new ConcurrentRMap();
        this.rwlock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean decide(Packet pkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsDataPacket(pkt);

        this.rwlock.readLock().lock();
        try {
            int src = pkt.header.source, dst = pkt.header.dest;
            return PNG.decide(src) && R.decide(src, dst);
        } finally {
            this.rwlock.readLock().unlock();
        }
    }

    @Override
    public void update(Packet configPkt) throws IllegalArgumentException {
        ArgumentValidator.assertIsConfigPacket(configPkt);

        this.rwlock.writeLock().lock();
        try {
            var cfg = configPkt.config;

            PNG.put(cfg.address, cfg.personaNonGrata);

            R.getOrCreate(cfg.address).set(cfg.addressBegin, cfg.addressEnd, cfg.acceptingRange);
        } finally {
            this.rwlock.writeLock().unlock();
        }
    }

}
