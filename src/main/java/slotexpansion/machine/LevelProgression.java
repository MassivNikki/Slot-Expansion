package slotexpansion.machine;

import java.math.BigInteger;

/** Level thresholds for the current XP curve. */
final class LevelProgression {
    private static final BigInteger BASE = BigInteger.valueOf(500);
    private static final BigInteger ELEVEN = BigInteger.valueOf(11);
    private static final BigInteger TEN = BigInteger.TEN;

    private LevelProgression() {}

    /** The cubic term slows leveling; the compounded 10% adds long-term growth. */
    static BigInteger requirement(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be positive");
        }
        return BASE.multiply(BigInteger.valueOf(level).pow(3))
                .multiply(ELEVEN.pow(level - 1)).divide(TEN.pow(level - 1));
    }
}
