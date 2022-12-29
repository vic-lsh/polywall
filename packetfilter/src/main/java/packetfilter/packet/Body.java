package packetfilter.packet;

public class Body {
    public final long iterations;
    public final long seed;

    public Body() {
        iterations = 0;
        seed = 0;
    }

    public Body(long iterations, long seed) {
        this.iterations = iterations;
        this.seed = seed;
    }
}
