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
        assertEquals(BigInteger.valueOf(828), price.current());
        price.reset();
        assertEquals(BigInteger.valueOf(600), price.current());
        price.advance();
        assertEquals(BigInteger.valueOf(690), price.current());
    }

    @Test
    void regularGrowthAcceleratesAndMilestonesGrowSeparately() {
        UpgradePrice price = new UpgradePrice(BigInteger.valueOf(1000), 1);
        price.advance();
        assertEquals(BigInteger.valueOf(1150), price.current());
        price.advance();
        assertEquals(BigInteger.valueOf(1380), price.current());
        price.advance();
        assertEquals(BigInteger.valueOf(1725), price.current());
        for (int i = 3; i < 8; i++) price.advance();
        BigInteger eighthPrice = price.current();
        price.advance();
        assertEquals(eighthPrice.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE)
                .divide(BigInteger.TWO), price.current());
        UpgradePrice milestone = new UpgradePrice(BigInteger.valueOf(75000), 2,
                UpgradePrice.Growth.MILESTONE);
        assertEquals(BigInteger.valueOf(300000), milestone.current());
        milestone.advance();
        assertEquals(BigInteger.valueOf(1800000), milestone.current());
        milestone.reset();
        assertEquals(BigInteger.valueOf(300000), milestone.current());
    }
}
