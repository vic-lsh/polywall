package packetfilter.packet;

public class Header {
    public final int source;
    public final int dest;
    public final int sequenceNumber;
    public final int trainSize;
    public final int tag;

    public Header(int source, int dest, int seq, int trainSize, int tag) {
        this.source = source;
        this.dest = dest;
        this.sequenceNumber = seq;
        this.trainSize = trainSize;
        this.tag = tag;
    }
}