package packetfilter.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DelimitedNumberFormatterTest {

    DelimitedNumberFormatter formatter;

    @BeforeEach
    public void init() {
        this.formatter = new DelimitedNumberFormatter();
    }

    @Test
    public void testFormatsNaturalNumbers() {
        assertEquals("0", formatter.format(0));
        assertEquals("1,000", formatter.format(1000));
        assertEquals("9,232,199", formatter.format(9232199));
    }

    @Test
    public void testFormatNegativeNumber() {
        assertEquals("-3", formatter.format(-3));
        assertEquals("-23,111", formatter.format(-23111));
    }

}
