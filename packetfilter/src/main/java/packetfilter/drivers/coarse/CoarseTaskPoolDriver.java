package packetfilter.drivers.coarse;

import packetfilter.drivers.AbstractTaskPoolDriver;
import packetfilter.firewall.CoarseFirewall;
import packetfilter.packet.generators.PacketGeneratorConfig;

/**
 * Implement firewall with a single-producer, multi-consumer model.
 * 
 * - A single producer generates packets. All workers poll from this producer.
 * - Each worker consumes one packet at a time. A worker does not specialize
 * in processing data or configuration packet.
 * - The firewall itself uses a RWLock to provide config-and-data packet mutural
 * exclusion.
 */
public class CoarseTaskPoolDriver extends AbstractTaskPoolDriver<CoarseFirewall> {

    public CoarseTaskPoolDriver(int numWorkers, PacketGeneratorConfig packetGenConfig) {
        super(numWorkers, packetGenConfig);
    }

    @Override
    protected CoarseFirewall createFirewall() {
        return new CoarseFirewall();
    }

}
