package slotexpansion.machine;

import java.util.Random;

/**
 * Generates slot symbols and owns the upgradeable chance values used to select them.
 */
final class SlotSymbolGenerator {
    private final Random random;

    private int additionalSevenSymbolChance;
    private int additionalTier4SymbolChance;
    private int additionalTier3SymbolChance;
    private int additionalTier2SymbolChance;
    private int tier1SymbolChance = SymbolChanceConfig.BASE_TIER1_CHANCE;
    private int additionalX2SymbolChance;

    SlotSymbolGenerator() {
        this(new Random());
    }

    SlotSymbolGenerator(Random random) {
        this.random = random;
    }

    /**
     * Rolls a symbol using this machine's current upgrade chances.
     *
     * <p>Chance bands are cumulative. The X2 symbol can only appear in an
     * interior column; other symbol outcomes retain their existing band order.</p>
     */
    int rollSymbol(
            int cellIndex,
            int columnCount,
            int symbolCount,
            boolean sevenSymbolsUnlocked,
            boolean tier4SymbolsUnlocked,
            boolean tier3SymbolsUnlocked,
            boolean tier2SymbolsUnlocked,
            boolean freeSpins) {
        int chance = random.nextInt(0, SymbolChanceConfig.CHANCE_RANGE);
        int tier1 = tier1SymbolChance;
        int tier2 = SymbolChanceConfig.BASE_TIER2_CHANCE + additionalTier2SymbolChance;
        int tier3 = SymbolChanceConfig.BASE_TIER3_CHANCE + additionalTier3SymbolChance;
        int tier4 = SymbolChanceConfig.BASE_TIER4_CHANCE + additionalTier4SymbolChance;
        int requestedBonus = freeSpins ? additionalX2SymbolChance * 2 : 0;
        int remainingBonus = requestedBonus;
        int taken = Math.min(tier1, remainingBonus);
        tier1 -= taken;
        remainingBonus -= taken;
        taken = Math.min(tier2, remainingBonus);
        tier2 -= taken;
        remainingBonus -= taken;
        taken = Math.min(tier3, remainingBonus);
        tier3 -= taken;
        remainingBonus -= taken;
        taken = Math.min(tier4, remainingBonus);
        tier4 -= taken;
        remainingBonus -= taken;
        int x2Chance = SymbolChanceConfig.BASE_X2_CHANCE + additionalX2SymbolChance
                + requestedBonus - remainingBonus;
        int column = cellIndex % columnCount;
        if (chance < x2Chance) {
            // Reserve the same band on edges, where x2 is replaced by a tier-one symbol.
            return column == 0 || column == columnCount - 1
                    ? random.nextInt(1, symbolCount - 9) : symbolCount - 1;
        }
        return rollRegularSymbol(chance, x2Chance, tier1, tier2, tier3, tier4, symbolCount, sevenSymbolsUnlocked,
                tier4SymbolsUnlocked, tier3SymbolsUnlocked, tier2SymbolsUnlocked);
    }

    private int rollRegularSymbol(
            int chance,
            int x2Chance,
            int tier1,
            int tier2,
            int tier3,
            int tier4,
            int symbolCount,
            boolean sevenSymbolsUnlocked,
            boolean tier4SymbolsUnlocked,
            boolean tier3SymbolsUnlocked,
            boolean tier2SymbolsUnlocked) {
        int boundary = x2Chance + SymbolChanceConfig.BASE_SEVEN_CHANCE + additionalSevenSymbolChance;
        if (chance < boundary && sevenSymbolsUnlocked) {
            return symbolCount - 2;
        }
        boundary += tier4;
        if (chance < boundary && tier4SymbolsUnlocked) {
            return random.nextInt(symbolCount - 4, symbolCount - 2);
        }
        boundary += tier3;
        if (chance < boundary && tier3SymbolsUnlocked) {
            return random.nextInt(symbolCount - 6, symbolCount - 4);
        }
        boundary += tier2;
        if (chance < boundary && tier2SymbolsUnlocked) {
            return random.nextInt(symbolCount - 9, symbolCount - 6);
        }
        boundary += tier1;
        if (chance < boundary) {
            return random.nextInt(1, symbolCount - 9);
        }
        return 0;
    }

    void resetForGridUpgrade() {
        additionalSevenSymbolChance = 0;
        additionalTier4SymbolChance = 0;
        additionalTier3SymbolChance = 0;
        additionalTier2SymbolChance = 0;
        tier1SymbolChance = SymbolChanceConfig.BASE_TIER1_CHANCE;
        additionalX2SymbolChance = 0;
    }

    int getAdditionalSevenSymbolChance() {
        return additionalSevenSymbolChance;
    }

    void setAdditionalSevenSymbolChance(int chance) {
        additionalSevenSymbolChance = chance;
    }

    int getAdditionalTier4SymbolChance() {
        return additionalTier4SymbolChance;
    }

    void setAdditionalTier4SymbolChance(int chance) {
        additionalTier4SymbolChance = chance;
    }

    int getAdditionalTier3SymbolChance() {
        return additionalTier3SymbolChance;
    }

    void setAdditionalTier3SymbolChance(int chance) {
        additionalTier3SymbolChance = chance;
    }

    int getAdditionalTier2SymbolChance() {
        return additionalTier2SymbolChance;
    }

    void setAdditionalTier2SymbolChance(int chance) {
        additionalTier2SymbolChance = chance;
    }

    int getTier1SymbolChance() {
        return tier1SymbolChance;
    }

    void setTier1SymbolChance(int chance) {
        tier1SymbolChance = chance;
    }

    int getAdditionalX2SymbolChance() {
        return additionalX2SymbolChance;
    }

    void setAdditionalX2SymbolChance(int chance) {
        additionalX2SymbolChance = chance;
    }
}
