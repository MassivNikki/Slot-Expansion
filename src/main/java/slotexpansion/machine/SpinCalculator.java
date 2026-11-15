package slotexpansion.machine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates and evaluates a spin without updating the wallet or the interface.
 * The caller applies the returned winnings and free-spin award.
 */
final class SpinCalculator {
    record WinningLine(int[] payline, int matches, BigInteger payout) {}
    record Result(int[] symbols, List<WinningLine> wins, BigInteger totalPayout, int freeSpinsAwarded) {}

    private SpinCalculator() {}

    static Result spin(SlotSymbolGenerator generator, int columns, int symbolCount,
                       List<int[]> paylines, BigInteger stake, int petMultiplier, int level,
                       boolean freeSpins, boolean sevenUnlocked, boolean tier4Unlocked,
                       boolean tier3Unlocked, boolean tier2Unlocked) {
        int[] symbols = new int[columns * 3];
        int thunderCount = 0;
        for (int i = 0; i < symbols.length; i++) {
            symbols[i] = generator.rollSymbol(i, columns, symbolCount, sevenUnlocked,
                    tier4Unlocked, tier3Unlocked, tier2Unlocked, freeSpins);
            if (symbols[i] == 0) {
                thunderCount++;
            }
        }
        List<WinningLine> wins = new ArrayList<>();
        BigInteger total = BigInteger.ZERO;
        for (int[] payline : paylines) {
            PaylineEvaluator.Result result = PaylineEvaluator.evaluate(symbols, payline, columns,
                    symbolCount - 1, stake, petMultiplier, level);
            if (result.payout().signum() > 0) {
                wins.add(new WinningLine(payline, result.matchingSymbols(), result.payout()));
                total = total.add(result.payout());
            }
        }
        int freeSpinsAwarded = thunderCount >= 3 ? (thunderCount - 2) * 10 : 0;
        return new Result(symbols, List.copyOf(wins), total, freeSpinsAwarded);
    }
}
