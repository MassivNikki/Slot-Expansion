package slotexpansion.ui;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Formats amounts without changing their exact values, and parses typed stakes. */
final class IdleNumberFormatter {
    private static final String[] NAMED_SUFFIXES =
            {"", "t", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc"};
    private static final int LETTER_SUFFIX_COUNT = 26 * 26;
    private static final int LAST_SUFFIX_GROUP = NAMED_SUFFIXES.length - 1 + LETTER_SUFFIX_COUNT;
    private static final Pattern SUFFIXED_AMOUNT =
            Pattern.compile("([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+))([A-Za-z]{1,2})");

    private IdleNumberFormatter() {}

    static String format(BigInteger amount) {
        String digits = amount.abs().toString();
        int exponent = digits.length() - 1;
        int group = exponent / 3;
        boolean scientific = group > LAST_SUFFIX_GROUP;
        int integerDigits = scientific ? 1 : exponent % 3 + 1;
        String significant = digits.length() < 4 ? (digits + "0000").substring(0, 4) : digits.substring(0, 4);
        String suffix = scientific ? "e" + exponent : suffixFor(group);
        return (amount.signum() < 0 ? "-" : "")
                + significant.substring(0, integerDigits) + "."
                + significant.substring(integerDigits) + suffix;
    }

    static BigInteger parse(String text) {
        Matcher matcher = SUFFIXED_AMOUNT.matcher(text);
        if (matcher.matches()) {
            int group = groupFor(matcher.group(2));
            return new BigDecimal(matcher.group(1)).movePointRight(group * 3).toBigIntegerExact();
        }
        return new BigDecimal(text).toBigIntegerExact();
    }

    private static String suffixFor(int group) {
        if (group < NAMED_SUFFIXES.length) {
            return NAMED_SUFFIXES[group];
        }
        int index = group - NAMED_SUFFIXES.length;
        return new String(new char[]{(char) ('a' + index / 26), (char) ('a' + index % 26)});
    }

    private static int groupFor(String suffix) {
        for (int group = 1; group < NAMED_SUFFIXES.length; group++) {
            if (NAMED_SUFFIXES[group].equals(suffix)) {
                return group;
            }
        }
        if (suffix.length() == 2 && suffix.charAt(0) >= 'a' && suffix.charAt(0) <= 'z'
                && suffix.charAt(1) >= 'a' && suffix.charAt(1) <= 'z') {
            return NAMED_SUFFIXES.length + (suffix.charAt(0) - 'a') * 26 + suffix.charAt(1) - 'a';
        }
        throw new NumberFormatException("Unknown amount suffix: " + suffix);
    }
}
