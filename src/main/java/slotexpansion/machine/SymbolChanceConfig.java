package slotexpansion.machine;

/**
 * Central chance tuning values shared by symbol rolls and upgrade controls.
 */
public final class SymbolChanceConfig {
    /** Number of equally likely values used for a symbol roll. */
    public static final int CHANCE_RANGE = 100;

    /** Default X2 symbol chance before upgrades. */
    public static final int BASE_X2_CHANCE = 2;
    /** Default seven-symbol chance before upgrades. */
    public static final int BASE_SEVEN_CHANCE = 3;
    /** Default tier-four symbol chance before upgrades. */
    public static final int BASE_TIER4_CHANCE = 6;
    /** Default tier-three symbol chance before upgrades. */
    public static final int BASE_TIER3_CHANCE = 12;
    /** Default tier-two symbol chance before upgrades. */
    public static final int BASE_TIER2_CHANCE = 20;
    /** Default tier-one symbol chance before upgrades. */
    public static final int BASE_TIER1_CHANCE = 51;

    /** Maximum displayed seven-symbol chance, including its baseline. */
    public static final int MAX_SEVEN_CHANCE = 10;
    /** Maximum displayed tier-four symbol chance, including its baseline. */
    public static final int MAX_TIER4_CHANCE = 20;
    /** Maximum displayed tier-three symbol chance, including its baseline. */
    public static final int MAX_TIER3_CHANCE = 30;
    /** Maximum displayed tier-two symbol chance, including its baseline. */
    public static final int MAX_TIER2_CHANCE = 35;
    /** Maximum displayed X2 symbol chance, including its baseline. */
    public static final int MAX_X2_CHANCE = 5;

    // Preserve the existing value assigned when the grid expands.
    static final int GRID_UPGRADE_TIER1_CHANCE = 53;
    static final int SEVEN_CHANCE_BOUNDARY = BASE_X2_CHANCE + BASE_SEVEN_CHANCE;
    static final int TIER4_CHANCE_BOUNDARY =
            SEVEN_CHANCE_BOUNDARY + BASE_TIER4_CHANCE;
    static final int TIER3_CHANCE_BOUNDARY =
            TIER4_CHANCE_BOUNDARY + BASE_TIER3_CHANCE;
    static final int TIER2_CHANCE_BOUNDARY =
            TIER3_CHANCE_BOUNDARY + BASE_TIER2_CHANCE;
    static final int TIER1_CHANCE_BOUNDARY =
            TIER2_CHANCE_BOUNDARY + BASE_TIER1_CHANCE;

    private SymbolChanceConfig() {
    }

    static int[] baseUpgradeChanceLabels() {
        return new int[]{
                BASE_SEVEN_CHANCE,
                BASE_TIER4_CHANCE,
                BASE_TIER3_CHANCE,
                BASE_TIER2_CHANCE,
                BASE_TIER1_CHANCE,
                BASE_X2_CHANCE
        };
    }
}
