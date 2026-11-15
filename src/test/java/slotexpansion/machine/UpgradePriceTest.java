package slotexpansion.machine;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpgradePriceTest {
    @Test
    void resetsToTierScaledBasePrice() {
        UpgradePrice price = new UpgradePrice(BigInteger.valueOf(150), 2);
        price.advance();
        price.advance();
        assertEquals(BigInteger.valueOf(2400), price.current());
        price.reset();
        assertEquals(BigInteger.valueOf(600), price.current());
    }
}
