package packetfilter.packet;

public class Config {
    public final int address;
    public final boolean personaNonGrata;
    public final boolean acceptingRange;
    public final int addressBegin;
    public final int addressEnd;

    public Config(int address, boolean personaNonGrata, boolean acceptingRange, int addressBegin, int addressEnd) {
        this.address = address;
        this.personaNonGrata = personaNonGrata;
        this.acceptingRange = acceptingRange;
        this.addressBegin = addressBegin;
        this.addressEnd = addressEnd;
    }
}