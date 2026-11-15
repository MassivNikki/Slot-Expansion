package slotexpansion.machine;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaylineEvaluatorTest {
    @Test
    void levelOnePaysAndWildcardsAndPetApplyToTheFinalAmount() {
        int[] symbols = {4, 12, 4, 0, 0, 0, 0, 0, 0};
        int[] line = {1, 1, 1, 0, 0, 0, 0, 0, 0};
        var result = PaylineEvaluator.evaluate(symbols, line, 3, 12, BigInteger.valueOf(5), 3, 1);
        assertEquals(3, result.matchingSymbols());
        assertEquals(BigInteger.valueOf(180), result.payout());
    }

    @Test
    void levelMultiplierAndWinningPrefixAreAppliedOnce() {
        int[] symbols = {4, 4, 4, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] line = {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        var result = PaylineEvaluator.evaluate(symbols, line, 5, 12, BigInteger.TEN, 1, 6);
        assertEquals(3, result.matchingSymbols());
        assertEquals(BigInteger.valueOf(120), result.payout());
    }

    @Test
    void fewerThanThreeMatchesDoNotPay() {
        int[] symbols = {4, 4, 1, 0, 0, 0, 0, 0, 0};
        int[] line = {1, 1, 1, 0, 0, 0, 0, 0, 0};
        assertEquals(BigInteger.ZERO,
                PaylineEvaluator.evaluate(symbols, line, 3, 12, BigInteger.TEN, 1, 1).payout());
    }
}
