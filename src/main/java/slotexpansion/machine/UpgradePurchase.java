package slotexpansion.machine;

import slotexpansion.player.PlayerManager;
import java.math.BigInteger;

/** Performs an affordable upgrade purchase and advances its next price. */
public final class UpgradePurchase {
    private final UpgradePrice price;

    /**
     * Creates a purchase using the specified base cost and machine tier.
     * @param basePrice cost on a tier-one machine
     * @param machineTier machine number used for price scaling
     */
    public UpgradePurchase(BigInteger basePrice, int machineTier) {
        price = new UpgradePrice(basePrice, machineTier);
    }

    /**
     * Charges the balance, advances the price, and applies the effect if affordable.
     * @param effect upgrade action
     * @return true when the purchase was affordable
     */
    public boolean purchase(Runnable effect) {
        if (!PlayerManager.trySpend(price.current())) {
            return false;
        }
        price.advance();
        effect.run();
        return true;
    }

    /**
     * Reads the price charged by the next purchase.
     * @return the cost of the next purchase
     */
    public BigInteger currentPrice() {
        return price.current();
    }

    /** Advances the price when reconstructing saved purchases. */
    public void advancePrice() {
        price.advance();
    }

    /** Resets the price after a grid expansion. */
    public void resetPrice() {
        price.reset();
    }
}
