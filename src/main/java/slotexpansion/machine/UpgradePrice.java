package slotexpansion.machine;

import java.math.BigInteger;

/** Tracks the escalating cost of one upgrade independently of its display. */
public final class UpgradePrice {
    private final BigInteger initialPrice;
    private BigInteger price;

    /**
     * Creates a price scaled for a machine tier.
     * @param basePrice cost on a tier-one machine
     * @param machineTier machine number used for price scaling
     */
    public UpgradePrice(BigInteger basePrice, int machineTier) {
        initialPrice = basePrice.multiply(BigInteger.valueOf(machineTier).pow(2));
        price = initialPrice;
    }

    /**
     * Reads the current cost.
     * @return the current cost
     */
    public BigInteger current() {
        return price;
    }

    /** Doubles the cost after a purchase. */
    public void advance() {
        price = price.multiply(BigInteger.TWO);
    }

    /** Restores the initial tier-scaled cost. */
    public void reset() {
        price = initialPrice;
    }
}
