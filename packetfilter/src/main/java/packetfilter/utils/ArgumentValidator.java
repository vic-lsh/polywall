package packetfilter.utils;

import java.util.function.Predicate;

import packetfilter.packet.Packet;
import packetfilter.packet.Packet.MessageType;

public class ArgumentValidator {
    public static <T> void validate(T value, Predicate<T> validator) throws IllegalArgumentException {
        if (!validator.test(value)) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> void notNull(T maybeNull) {
        validate(maybeNull, v -> v != null);
    }

    public static void validatePercentage(double x) {
        validate(x, v -> v >= 0 && x <= 1);
    }

    public static <T> void validateArrayNotEmpty(T[] arr) {
        validate(arr, a -> a.length > 0);
    }

    public static void validateIntArrayNotEmpty(int[] intArr) {
        validate(intArr, a -> a.length > 0);
    }

    public static void validateGreaterThanZero(int intValue) throws IllegalArgumentException {
        validate(intValue, (n) -> n > 0);
    }

    public static void validateGreaterEqualZero(int intValue) throws IllegalArgumentException {
        validate(intValue, (n) -> n >= 0);
    }

    public static void assertIsDataPacket(Packet packet) throws IllegalArgumentException {
        validate(packet, (p) -> p.type == MessageType.DataPacket);
    }

    public static void assertIsConfigPacket(Packet packet) throws IllegalArgumentException {
        validate(packet, (p) -> p.type == MessageType.ConfigPacket);
    }
}
