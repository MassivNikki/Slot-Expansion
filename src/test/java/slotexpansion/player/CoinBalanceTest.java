package slotexpansion.player;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class CoinBalanceTest {
    @Test
    void cannotOverspendOrUseNegativeAmounts() {
        CoinBalance balance = new CoinBalance();
        balance.set(BigInteger.TEN);
        assertFalse(balance.trySpend(BigInteger.valueOf(11)));
        assertEquals(BigInteger.TEN, balance.get());
        assertThrows(IllegalArgumentException.class, () -> balance.add(BigInteger.valueOf(-1)));
        assertThrows(IllegalArgumentException.class, () -> balance.trySpend(BigInteger.valueOf(-1)));
        assertTrue(balance.trySpend(BigInteger.ONE));
        assertEquals(BigInteger.valueOf(9), balance.get());
    }
}
