package slotexpansion.machine;

import java.math.BigInteger;

/** Tracks the escalating cost of one upgrade independently of its display. */
public final class UpgradePrice {
    /** Cost of unlocking the second machine. */
    public static final BigInteger FIRST_MACHINE_UNLOCK = BigInteger.valueOf(150_000);
    /** Base cost of a machine's first grid expansion. */
    public static final BigInteger FIRST_GRID_EXPANSION = BigInteger.valueOf(75_000);

    /** Growth rules for regular upgrades and major unlocks. */
    public enum Growth {
        /** Starts at 15% growth, increasing by five percentage points up to 50%. */
        REGULAR,
        /** Each subsequent milestone costs six times as much. */
        MILESTONE
    }

    private final BigInteger initialPrice;
    private final Growth growth;
    private BigInteger price;
    private int purchases;

    /**
     * Creates a price scaled for a machine tier.
     * @param basePrice cost on a tier-one machine
     * @param machineTier machine number used for price scaling
     */
    public UpgradePrice(BigInteger basePrice, int machineTier) {
        this(basePrice, machineTier, Growth.REGULAR);
    }

    /**
     * Creates a tier-scaled price with the specified growth rule.
     * @param basePrice cost on a tier-one machine
     * @param machineTier machine number used for price scaling
     * @param growth price growth rule
     */
    public UpgradePrice(BigInteger basePrice, int machineTier, Growth growth) {
        initialPrice = basePrice.multiply(BigInteger.valueOf(machineTier).pow(2));
        this.growth = growth;
        price = initialPrice;
    }

    /**
     * Reads the current cost.
     * @return the current cost
     */
    public BigInteger current() {
        return price;
    }

    /** Advances the price, rounding fractional coins up. */
    public void advance() {
        if (growth == Growth.MILESTONE) {
            price = price.multiply(BigInteger.valueOf(6));
        } else {
            BigInteger percentage = BigInteger.valueOf(115L + 5L * Math.min(purchases, 7));
            price = price.multiply(percentage).add(BigInteger.valueOf(99)).divide(BigInteger.valueOf(100));
        }
        purchases++;
    }

    /** Restores the initial tier-scaled cost. */
    public void reset() {
        price = initialPrice;
        purchases = 0;
    }
}
