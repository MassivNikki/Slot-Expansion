package slotexpansion.machine;

import java.math.BigInteger;

/**
 * Calculates one payline without changing the balance or any Swing components.
 * The paytable columns correspond to the symbol indices in the grid.
 */
final class PaylineEvaluator {
    private static final int[][] THREE_MATCH = {
            {0, 1, 1, 1, 3, 3, 3, 4, 4, 6, 6, 10},
            {0, 1, 1, 1, 2, 2, 2, 3, 3, 5, 5, 7},
            {0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5}
    };
    private static final int[][] FOUR_MATCH = {
            {0, 3, 3, 3, 4, 4, 4, 6, 6, 10, 10, 15},
            {0, 2, 2, 2, 3, 3, 3, 5, 5, 7, 7, 10}
    };
    private static final int[] FIVE_MATCH = {0, 5, 5, 5, 10, 10, 10, 20, 20, 30, 30, 100};
    private static final int PAYOUT_SCALE = 2;

    record Result(int matchingSymbols, BigInteger payout) {}

    private PaylineEvaluator() {}

    /**
     * Evaluates the consecutive matching prefix of a row-major payline.
     * Wildcards after the first symbol each double the final payout.
     */
    static Result evaluate(int[] symbols, int[] line, int columns, int wildSymbol,
                           BigInteger stake, int petMultiplier, int level) {
        int target = -1;
        int matches = 0;
        int wilds = 0;
        for (int column = 0; column < columns; column++) {
            int symbol = -1;
            for (int row = 0; row < 3; row++) {
                int index = row * columns + column;
                if (line[index] == 1) {
                    symbol = symbols[index];
                    break;
                }
            }
            if (symbol < 0) {
                throw new IllegalArgumentException("Payline has no cell in column " + column);
            }
            if (column == 0) {
                target = symbol;
            } else if (symbol == wildSymbol) {
                wilds++;
            } else if (symbol != target) {
                break;
            }
            matches++;
        }

        if (matches < 3) {
            return new Result(matches, BigInteger.ZERO);
        }
        int multiplier = switch (matches) {
            case 3 -> THREE_MATCH[columns - 3][target];
            case 4 -> FOUR_MATCH[columns - 4][target];
            case 5 -> FIVE_MATCH[target];
            default -> throw new IllegalArgumentException("Unsupported payline length: " + matches);
        };
        BigInteger payout = stake.multiply(BigInteger.valueOf(multiplier))
                .multiply(BigInteger.valueOf(PAYOUT_SCALE))
                .multiply(BigInteger.TWO.pow(wilds))
                .multiply(BigInteger.valueOf(petMultiplier))
                .multiply(BigInteger.valueOf(Math.max(1, level / 2)));
        return new Result(matches, payout);
    }
}
