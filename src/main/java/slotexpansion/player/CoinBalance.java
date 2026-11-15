package slotexpansion.player;

import java.math.BigInteger;
import java.util.Objects;

/** Owns the nonnegative shared balance without depending on Swing. */
public final class CoinBalance {
    private BigInteger coins = BigInteger.ZERO;

    /** Creates an empty balance. */
    public CoinBalance() {}

    /**
     * Reads the current balance.
     * @return the current balance
     */
    public BigInteger get() {
        return coins;
    }

    /**
     * Replaces the balance.
     * @param amount the nonnegative new balance
     */
    public void set(BigInteger amount) {
        coins = requireNonnegative(amount);
    }

    /**
     * Adds coins to the balance.
     * @param amount nonnegative amount to add
     */
    public void add(BigInteger amount) {
        coins = coins.add(requireNonnegative(amount));
    }

    /**
     * Attempts to subtract an amount without allowing a negative balance.
     * @param amount nonnegative cost
     * @return true if the amount was spent
     */
    public boolean trySpend(BigInteger amount) {
        requireNonnegative(amount);
        if (amount.compareTo(coins) > 0) {
            return false;
        }
        coins = coins.subtract(amount);
        return true;
    }

    private static BigInteger requireNonnegative(BigInteger amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Coin amount must not be negative");
        }
        return amount;
    }
}
