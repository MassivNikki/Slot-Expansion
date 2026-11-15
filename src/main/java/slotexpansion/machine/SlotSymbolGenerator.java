package slotexpansion.machine;

import java.util.Random;

/**
 * Generates slot symbols and owns the upgradeable chance values used to select them.
 */
final class SlotSymbolGenerator {
    private final Random random = new Random();

    private int additionalSevenSymbolChance;
    private int additionalTier4SymbolChance;
    private int additionalTier3SymbolChance;
    private int additionalTier2SymbolChance;
    // Kept for the existing save and upgrade UI; rolls use the configured tier-one boundary.
    private int tier1SymbolChance = SymbolChanceConfig.BASE_TIER1_CHANCE;
    private int additionalX2SymbolChance;

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
            boolean tier2SymbolsUnlocked) {
        int chance = random.nextInt(0, SymbolChanceConfig.CHANCE_RANGE);
        if (isX2Symbol(cellIndex, columnCount, chance)) {
            return symbolCount - 1;
        }

        return rollRegularSymbol(chance, symbolCount, sevenSymbolsUnlocked,
                tier4SymbolsUnlocked, tier3SymbolsUnlocked, tier2SymbolsUnlocked);
    }

    private int rollRegularSymbol(
            int chance,
            int symbolCount,
            boolean sevenSymbolsUnlocked,
            boolean tier4SymbolsUnlocked,
            boolean tier3SymbolsUnlocked,
            boolean tier2SymbolsUnlocked) {
        // Each upgrade shifts the later cumulative bands.
        int cumulativeUpgradeChance = additionalX2SymbolChance;
        if (chance < SymbolChanceConfig.SEVEN_CHANCE_BOUNDARY
                + cumulativeUpgradeChance
                + additionalSevenSymbolChance
                && sevenSymbolsUnlocked) {
            return symbolCount - 2;
        }

        cumulativeUpgradeChance += additionalSevenSymbolChance;
        cumulativeUpgradeChance += additionalTier4SymbolChance;
        if (chance < SymbolChanceConfig.TIER4_CHANCE_BOUNDARY
                + cumulativeUpgradeChance
                && tier4SymbolsUnlocked) {
            return random.nextInt(symbolCount - 4, symbolCount) - 2;
        }

        cumulativeUpgradeChance += additionalTier3SymbolChance;
        if (chance < SymbolChanceConfig.TIER3_CHANCE_BOUNDARY
                + cumulativeUpgradeChance
                && tier3SymbolsUnlocked) {
            return random.nextInt(symbolCount - 6, symbolCount - 4);
        }

        cumulativeUpgradeChance += additionalTier2SymbolChance;
        if (chance < SymbolChanceConfig.TIER2_CHANCE_BOUNDARY
                + cumulativeUpgradeChance
                && tier2SymbolsUnlocked) {
            return random.nextInt(symbolCount - 9, symbolCount - 6);
        }
        if (chance < SymbolChanceConfig.TIER1_CHANCE_BOUNDARY) {
            return random.nextInt(1, symbolCount - 9);
        }
        return 0;
    }

    private boolean isX2Symbol(int cellIndex, int columnCount, int chance) {
        int column = cellIndex % columnCount;
        return chance < SymbolChanceConfig.BASE_X2_CHANCE + additionalX2SymbolChance
                && column != 0
                && column != columnCount - 1;
    }

    void resetForGridUpgrade() {
        additionalSevenSymbolChance = 0;
        additionalTier4SymbolChance = 0;
        additionalTier3SymbolChance = 0;
        additionalTier2SymbolChance = 0;
        tier1SymbolChance = SymbolChanceConfig.GRID_UPGRADE_TIER1_CHANCE;
        additionalX2SymbolChance = 0;
    }

    void applyFreeSpinX2ChanceMultiplier() {
        additionalX2SymbolChance *= 3;
    }

    void removeFreeSpinX2ChanceMultiplier() {
        additionalX2SymbolChance /= 3;
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
