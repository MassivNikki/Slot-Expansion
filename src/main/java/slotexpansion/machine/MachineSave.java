package slotexpansion.machine;

import java.math.BigInteger;

/**
 * Persisted state of one machine. The balance is repeated for compatibility
 * with the original one-record-per-machine save format.
 *
 * @param balance shared coin balance
 * @param level machine level
 * @param xp experience toward the next level
 * @param sevenChance purchased change to seven probability
 * @param tier4Chance change to tier-four probability
 * @param tier3Chance change to tier-three probability
 * @param tier2Chance change to tier-two probability
 * @param tier1Chance remaining tier-one probability
 * @param x2Chance purchased change to x2 probability
 * @param addedPaylines number of optional paylines purchased
 * @param petTier pet index, or -1 without a pet
 * @param columns grid width
 * @param autoCooldown automatic-spin delay in milliseconds
 * @param manualCooldown manual-spin delay in milliseconds
 * @param autoUnlocked whether auto-spin is unlocked
 * @param minUpgrades minimum-stake purchase count
 * @param maxUpgrades maximum-stake purchase count
 * @param sevenPurchases seven-chance purchase count
 * @param tier4Purchases tier-four purchase count
 * @param tier3Purchases tier-three purchase count
 * @param tier2Purchases tier-two purchase count
 * @param x2Purchases x2 purchase count
 * @param unlockedSymbols bit mask of purchased symbol tiers
 */
public record MachineSave(BigInteger balance, int level, BigInteger xp,
                          int sevenChance, int tier4Chance, int tier3Chance, int tier2Chance,
                          int tier1Chance, int x2Chance, int addedPaylines, int petTier,
                          int columns, int autoCooldown, int manualCooldown,
                          boolean autoUnlocked, int minUpgrades, int maxUpgrades,
                          int sevenPurchases, int tier4Purchases, int tier3Purchases,
                          int tier2Purchases, int x2Purchases, int unlockedSymbols) {
    /** Validates the snapshot before it is saved or applied. */
    public MachineSave {
        int maxPaylines = switch (columns) {
            case 3 -> 4;
            case 4 -> 10;
            case 5 -> 18;
            default -> throw new IllegalArgumentException("Grid width must be 3, 4, or 5");
        };
        if (balance.signum() < 0 || xp.signum() < 0 || level < 1
                || petTier < -1 || petTier >= 12 || addedPaylines < 0 || addedPaylines > maxPaylines
                || autoCooldown < 200 || autoCooldown > 1000 || autoCooldown % 50 != 0
                || manualCooldown < 200 || manualCooldown > 500 || manualCooldown % 50 != 0
                || minUpgrades < 0 || minUpgrades > 10000 || maxUpgrades < 0 || maxUpgrades > 10000
                || sevenChance < 0 || sevenChance > 7 || tier4Chance < -6 || tier4Chance > 14
                || tier3Chance < -12 || tier3Chance > 18 || tier2Chance < -20 || tier2Chance > 15
                || tier1Chance < 0 || tier1Chance > 53 || x2Chance < 0 || x2Chance > 9
                || sevenPurchases < sevenChance || tier4Purchases < Math.max(0, tier4Chance)
                || tier3Purchases < Math.max(0, tier3Chance) || tier2Purchases < Math.max(0, tier2Chance)
                || x2Purchases < x2Chance || sevenPurchases > 10000 || tier4Purchases > 10000
                || tier3Purchases > 10000 || tier2Purchases > 10000 || x2Purchases > 10000
                || unlockedSymbols < 0 || unlockedSymbols > 15) {
            throw new IllegalArgumentException("Machine state contains values outside supported ranges");
        }
    }

    // Legacy records lack purchase counters; remaining upgrades are the best available estimate.
    private MachineSave(BigInteger balance, int level, BigInteger xp,
                       int sevenChance, int tier4Chance, int tier3Chance, int tier2Chance,
                       int tier1Chance, int x2Chance, int addedPaylines, int petTier,
                       int columns, int autoCooldown, int manualCooldown,
                       boolean autoUnlocked, int minUpgrades, int maxUpgrades) {
        this(balance, level, xp, sevenChance, tier4Chance, tier3Chance, tier2Chance,
                tier1Chance, x2Chance, addedPaylines, petTier, columns, autoCooldown, manualCooldown,
                autoUnlocked, minUpgrades, maxUpgrades, sevenChance, Math.max(0, tier4Chance),
                Math.max(0, tier3Chance), Math.max(0, tier2Chance), x2Chance, 0);
    }

    /**
     * Creates the initial state for a new game.
     * @return a fresh machine state
     */
    public static MachineSave newGame() {
        return new MachineSave(BigInteger.valueOf(1000), 1, BigInteger.ZERO,
                0, 0, 0, 0, 51, 0, 0, -1, 3, 1000, 500, false, 0, 0);
    }

    /**
     * Parses legacy 17- and 22-field records and the current format.
     * @param line one comma-separated machine record
     * @return the validated machine state
     * @throws IllegalArgumentException if the record is malformed or out of range
     */
    public static MachineSave parse(String line) {
        String[] v = line.split(",", -1);
        if ((v.length != 17 && v.length != 22 && v.length != 23)
                || !(v[14].equals("true") || v[14].equals("false"))) {
            throw new IllegalArgumentException("Expected 17, 22, or 23 save fields and a boolean auto-spin flag");
        }
        MachineSave legacy = new MachineSave(new BigInteger(v[0]), Integer.parseInt(v[1]), new BigInteger(v[2]),
                Integer.parseInt(v[3]), Integer.parseInt(v[4]), Integer.parseInt(v[5]),
                Integer.parseInt(v[6]), Integer.parseInt(v[7]), Integer.parseInt(v[8]),
                Integer.parseInt(v[9]), Integer.parseInt(v[10]), Integer.parseInt(v[11]),
                Integer.parseInt(v[12]), Integer.parseInt(v[13]), Boolean.parseBoolean(v[14]),
                Integer.parseInt(v[15]), Integer.parseInt(v[16]));
        if (v.length == 17) {
            return legacy;
        }
        return new MachineSave(legacy.balance, legacy.level, legacy.xp, legacy.sevenChance,
                legacy.tier4Chance, legacy.tier3Chance, legacy.tier2Chance, legacy.tier1Chance,
                legacy.x2Chance, legacy.addedPaylines, legacy.petTier, legacy.columns,
                legacy.autoCooldown, legacy.manualCooldown, legacy.autoUnlocked, legacy.minUpgrades,
                legacy.maxUpgrades, Integer.parseInt(v[17]), Integer.parseInt(v[18]),
                Integer.parseInt(v[19]), Integer.parseInt(v[20]), Integer.parseInt(v[21]),
                v.length == 23 ? Integer.parseInt(v[22]) : 0);
    }

    /**
     * Serializes this state for persistence.
     * @return the current comma-separated save format
     */
    public String toCsv() {
        return String.join(",", balance.toString(), Integer.toString(level), xp.toString(),
                Integer.toString(sevenChance), Integer.toString(tier4Chance), Integer.toString(tier3Chance),
                Integer.toString(tier2Chance), Integer.toString(tier1Chance), Integer.toString(x2Chance),
                Integer.toString(addedPaylines), Integer.toString(petTier), Integer.toString(columns),
                Integer.toString(autoCooldown), Integer.toString(manualCooldown), Boolean.toString(autoUnlocked),
                Integer.toString(minUpgrades), Integer.toString(maxUpgrades),
                Integer.toString(sevenPurchases), Integer.toString(tier4Purchases),
                Integer.toString(tier3Purchases), Integer.toString(tier2Purchases), Integer.toString(x2Purchases),
                Integer.toString(unlockedSymbols));
    }
}
