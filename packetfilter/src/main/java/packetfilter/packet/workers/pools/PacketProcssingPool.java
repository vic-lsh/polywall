package packetfilter.packet.workers.pools;

public abstract class PacketProcssingPool implements StaticForkJoinable {

    protected final int nThreads;

    public PacketProcssingPool(int nThreads) {
        this.nThreads = nThreads;
    }
}
