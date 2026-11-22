package slotexpansion.machine;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelProgressionTest {
    @Test
    void thresholdsGrowFasterAsLevelsIncrease() {
        assertEquals(BigInteger.valueOf(500), LevelProgression.requirement(1));
        assertEquals(BigInteger.valueOf(4400), LevelProgression.requirement(2));
        assertEquals(BigInteger.valueOf(16335), LevelProgression.requirement(3));
        assertEquals(new BigInteger("1178973"), LevelProgression.requirement(10));
    }

    @Test
    void levelMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> LevelProgression.requirement(0));
    }
}
