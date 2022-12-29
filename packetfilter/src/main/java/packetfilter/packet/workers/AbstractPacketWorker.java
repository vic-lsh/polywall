package packetfilter.packet.workers;

import packetfilter.firewall.service.FirewallService;
import packetfilter.packet.Packet;

public abstract class AbstractPacketWorker {

    /** The service to process packets with. */
    protected FirewallService service;

    /** Number of packets processed. */
    protected int processedCount;

    /**
     * Number of `process()` invocations.
     * This number is strictly greater or equal to `processedCount`.
     */
    protected int inboundCount;

    public AbstractPacketWorker(FirewallService service) {
        this.service = service;
        this.processedCount = 0;
        this.inboundCount = 0;
    }

    /**
     * Performs some processing on a packet.
     * 
     * @param pkt the packet to process.
     * @return `true` if this packet may requires further processing. The worker may
     *         not have full knowledge about what this packet has gone through, so
     *         the caller must take care to reason about work that has been done on
     *         a packet.
     *         `false` if this worker knows this packet is fully processed.
     */
    public boolean process(Packet pkt) {
        this.inboundCount++;
        this.service.process(pkt);
        this.processedCount++;
        return false;
    }

    /**
     * Get the number of `process()` invocations that this worker has received.
     * 
     * This counts packets irrespective of whether work is actually performed
     * on a packet.
     * 
     * @return the number of packets that have flown through this worker.
     */
    public int getInboundCount() {
        return this.inboundCount;
    }

    /**
     * Get the number of packets that this worker has done work on.
     * 
     * This number omits packets that this worker has decided not to operate
     * on, i.e. they're not processed.
     * 
     * This method is not thread-safe. It is not intended to be used to poll
     * the progress of a worker.
     * 
     * @return the number of packets that have been processed.
     */
    public int getProcessedCount() {
        return this.processedCount;
    }
}
