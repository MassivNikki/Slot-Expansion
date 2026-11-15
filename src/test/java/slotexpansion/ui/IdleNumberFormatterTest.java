package slotexpansion.ui;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdleNumberFormatterTest {
    @Test
    void displaysFourDigitsAndTruncatesAtEachMagnitude() {
        assertFormat("0", "0.000");
        assertFormat("5", "5.000");
        assertFormat("55", "55.00");
        assertFormat("999", "999.0");
        assertFormat("1000", "1.000t");
        assertFormat("999999", "999.9t");
        assertFormat("1000000", "1.000M");
        assertFormat("999200000000", "999.2B");
        assertFormat("1002000000000", "1.002T");
        assertFormat("9999000000000", "9.999T");
        assertFormat("10000000000000", "10.00T");
        assertFormat("99999999999999", "99.99T");
        assertFormat("100000000000000", "100.0T");
        assertFormat("-1234567", "-1.234M");
    }

    @Test
    void supportsEveryNamedAndAlphabeticalSuffixAndScientificFallback() {
        String[] named = {"t", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc"};
        for (int i = 0; i < named.length; i++) {
            BigInteger amount = BigInteger.TEN.pow((i + 1) * 3);
            assertEquals("1.000" + named[i], IdleNumberFormatter.format(amount));
            assertEquals(amount, IdleNumberFormatter.parse("1.000" + named[i]));
        }
        for (int index = 0; index < 26 * 26; index++) {
            String suffix = "" + (char) ('a' + index / 26) + (char) ('a' + index % 26);
            BigInteger amount = BigInteger.TEN.pow((index + 12) * 3);
            assertEquals("1.000" + suffix, IdleNumberFormatter.format(amount));
            assertEquals(amount, IdleNumberFormatter.parse("1.000" + suffix));
        }
        BigInteger afterZz = BigInteger.TEN.pow(2064);
        assertEquals("999.9zz", IdleNumberFormatter.format(afterZz.subtract(BigInteger.ONE)));
        assertEquals("1.000e2064", IdleNumberFormatter.format(afterZz));
        assertEquals(afterZz, IdleNumberFormatter.parse("1.000e2064"));
    }

    @Test
    void priceLabelUsesTheSameFormat() {
        PriceLabel label = new PriceLabel();
        label.setCoins(BigInteger.valueOf(12345));
        assertEquals("12.34t", label.getText());
    }

    private static void assertFormat(String value, String expected) {
        assertEquals(expected, IdleNumberFormatter.format(new BigInteger(value)));
    }
}
