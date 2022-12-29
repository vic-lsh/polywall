package packetfilter.utils;

import java.text.DecimalFormat;

/**
 * Formats integers with thousand delimiters.
 */
public class DelimitedNumberFormatter {

    private DecimalFormat formatter = new DecimalFormat("#,###");

    public String format(long number) {
        return formatter.format(number);
    }
}
