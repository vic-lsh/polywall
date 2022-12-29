package packetfilter.packet;

public class Packet {
    public enum MessageType {
        ConfigPacket, DataPacket
    }

    public final Config config;
    public final Header header;
    public final Body body;
    public final MessageType type;

    public Packet(Config config) {
        this.config = config;
        this.header = null;
        this.body = null;
        this.type = MessageType.ConfigPacket;
    }

    public Packet(Header header, Body body) {
        this.config = null;
        this.header = header;
        this.body = body;
        this.type = MessageType.DataPacket;
    }

    public void printPacket() {
        if (type == MessageType.ConfigPacket) {
            System.out.println("CONFIG: " + config.address + " <" + config.personaNonGrata + "," + config.acceptingRange
                    + ">" + " [" + config.addressBegin + "," + config.addressEnd + ")");
        } else {
            System.out.println("data:   " + "<" + header.source + "," + header.dest + ">" + " " + header.sequenceNumber
                    + "/" + header.trainSize + " (" + header.tag + ")");
        }
    }

    public int fingerprint() {
        // Note: this is not a function provided with the stencil code.
        // The fingerprint() method is supposed to be part of the stencil code,
        // but it is not. For lack of a better version, we use Java's
        // hashCode() function as a substitute.
        return this.hashCode();
    }
}
