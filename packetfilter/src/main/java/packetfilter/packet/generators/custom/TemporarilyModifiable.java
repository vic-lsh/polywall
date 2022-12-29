package packetfilter.packet.generators.custom;

interface TemporarilyModifiable<T> {
    public T freeze();
}
