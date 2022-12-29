package packetfilter.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import packetfilter.packet.generators.ConfigFileMappingException;
import packetfilter.packet.generators.ConfigReader;
import packetfilter.packet.generators.PacketGenerator;

public class ArgumentValidatorTest {
    @Test
    public void testValidateFn() {
        assertThrowsIllegalArgumentExp(() -> ArgumentValidator.validate("", (s) -> s.length() > 12));
    }

    @Test
    public void testValidateNonNullValues() {
        ArgumentValidator.notNull(1);
        assertThrowsIllegalArgumentExp(() -> ArgumentValidator.notNull(null));
    }

    @RepeatedTest(6)
    public void testNumGreaterThanZero() {
        var rand = new Random();

        assertDoesNotThrow(() -> {
            int posNum = Math.abs(rand.nextInt()) + 1;
            ArgumentValidator.validateGreaterThanZero(posNum);
        });

        assertThrowsIllegalArgumentExp(() -> ArgumentValidator.validateGreaterThanZero(0));

        assertThrowsIllegalArgumentExp(() -> {
            int negNum = -Math.abs(rand.nextInt());
            ArgumentValidator.validateGreaterThanZero(negNum);
        });
    }

    @RepeatedTest(6)
    public void testNumGreaterEqualZero() {
        var rand = new Random();

        assertDoesNotThrow(() -> {
            int nonNegNum = Math.abs(rand.nextInt());
            ArgumentValidator.validateGreaterEqualZero(nonNegNum);
        });

        assertDoesNotThrow(() -> ArgumentValidator.validateGreaterEqualZero(0));

        assertThrowsIllegalArgumentExp(() -> {
            int negNum = -Math.abs(rand.nextInt()) - 1;
            ArgumentValidator.validateGreaterEqualZero(negNum);
        });
    }

    private void assertThrowsIllegalArgumentExp(org.junit.jupiter.api.function.Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }

    @RepeatedTest(6)
    public void testValidatePercentage() {
        assertDoesNotThrow(() -> {
            double numBetweenZeroAndOne = Math.random();
            ArgumentValidator.validatePercentage(numBetweenZeroAndOne);
            ArgumentValidator.validatePercentage(0);
            ArgumentValidator.validatePercentage(1);
        });

        assertThrowsIllegalArgumentExp(() -> {
            ArgumentValidator.validatePercentage(Math.random() + 10);
        });

        assertThrowsIllegalArgumentExp(() -> {
            ArgumentValidator.validatePercentage(Math.random() - -10);
        });
    }

    @Test
    public void testArrayNotEmptyValidator() {
        assertThrowsIllegalArgumentExp(() -> {
            ArgumentValidator.validateArrayNotEmpty(new Integer[] {});
        });

        assertDoesNotThrow(() -> {
            ArgumentValidator.validateArrayNotEmpty(new Integer[] { 1, 2, 3 });
        });
    }

    @Nested
    class PacketValidationTest {
        private PacketGenerator pktGen;

        @BeforeEach
        public void init() throws IOException, ConfigFileMappingException {
            this.pktGen = new ConfigReader().readConfig("preset1").toPacketGenerator();
        }

        @Test
        public void testValidateDataPacket() {
            assertDoesNotThrow(() -> {
                ArgumentValidator.assertIsDataPacket(pktGen.getDataPacket());
            });

            assertThrowsIllegalArgumentExp(() -> {
                ArgumentValidator.assertIsDataPacket(pktGen.getConfigPacket());
            });
        }

        @Test
        public void testValidateConfigPacket() {
            assertDoesNotThrow(() -> {
                ArgumentValidator.assertIsConfigPacket(pktGen.getConfigPacket());
            });

            assertThrowsIllegalArgumentExp(() -> {
                ArgumentValidator.assertIsConfigPacket(pktGen.getDataPacket());
            });
        }
    }
}
