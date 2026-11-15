package slotexpansion.machine;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlotSymbolGeneratorTest {
    private static class FixedRandom extends Random {
        int chance;
        @Override public int nextInt(int origin, int bound) {
            return origin == 0 && bound == 100 ? chance : origin;
        }
    }

    @Test
    void sevensHaveExactlyThreePercentChanceInEveryColumn() {
        FixedRandom random = new FixedRandom();
        SlotSymbolGenerator generator = new SlotSymbolGenerator(random);
        for (int columns = 3; columns <= 5; columns++) {
            for (int column = 0; column < columns; column++) {
                int sevens = 0;
                for (int roll = 0; roll < 100; roll++) {
                    random.chance = roll;
                    if (generator.rollSymbol(column, columns, 13, true, true, true, true, false) == 11) {
                        sevens++;
                    }
                }
                assertEquals(3, sevens, "column " + column + " of " + columns);
            }
        }
    }

    @Test
    void freeSpinBonusDoesNotMutateThePurchasedX2ChanceOrSevenOdds() {
        FixedRandom random = new FixedRandom();
        SlotSymbolGenerator generator = new SlotSymbolGenerator(random);
        generator.setAdditionalX2SymbolChance(2);
        generator.setAdditionalSevenSymbolChance(1);
        generator.setTier1SymbolChance(48);
        for (boolean freeSpins : new boolean[]{false, true}) {
            for (int column = 0; column < 3; column++) {
                int sevens = 0;
                int x2 = 0;
                for (int roll = 0; roll < 100; roll++) {
                    random.chance = roll;
                    int symbol = generator.rollSymbol(column, 3, 13, true, true, true, true, freeSpins);
                    if (symbol == 11) sevens++;
                    if (symbol == 12) x2++;
                }
                assertEquals(4, sevens);
                assertEquals(column == 1 ? (freeSpins ? 8 : 4) : 0, x2);
            }
        }
        assertEquals(2, generator.getAdditionalX2SymbolChance());
    }

    @Test
    void tierFourRollsOnlyTierFourSymbols() {
        Random upperBound = new Random() {
            @Override public int nextInt(int origin, int bound) {
                return origin == 0 && bound == 100 ? 5 : bound - 1;
            }
        };
        SlotSymbolGenerator generator = new SlotSymbolGenerator(upperBound);
        assertEquals(10, generator.rollSymbol(1, 3, 13, true, true, true, true, false));
    }

    @Test
    void freeSpinBonusUsesLowerTiersWhenTierOneIsExhausted() {
        FixedRandom random = new FixedRandom();
        SlotSymbolGenerator generator = new SlotSymbolGenerator(random);
        generator.setTier1SymbolChance(0);
        generator.setAdditionalTier2SymbolChance(10);
        generator.setAdditionalTier3SymbolChance(18);
        generator.setAdditionalTier4SymbolChance(14);
        generator.setAdditionalSevenSymbolChance(7);
        generator.setAdditionalX2SymbolChance(2);
        int thunder = 0;
        int sevens = 0;
        for (int roll = 0; roll < 100; roll++) {
            random.chance = roll;
            int symbol = generator.rollSymbol(1, 3, 13, true, true, true, true, true);
            if (symbol == 0) thunder++;
            if (symbol == 11) sevens++;
        }
        assertEquals(6, thunder);
        assertEquals(10, sevens);
        assertEquals(2, generator.getAdditionalX2SymbolChance());
    }

    @Test
    void lockedBandsBecomeTierOneWithoutChangingOtherBands() {
        FixedRandom random = new FixedRandom();
        SlotSymbolGenerator generator = new SlotSymbolGenerator(random);
        for (int mask = 0; mask < 16; mask++) {
            int tier1 = 0, tier2 = 0, tier3 = 0, tier4 = 0, sevens = 0, thunder = 0;
            for (int roll = 0; roll < 100; roll++) {
                random.chance = roll;
                int symbol = generator.rollSymbol(1, 3, 13,
                        (mask & 8) != 0, (mask & 4) != 0, (mask & 2) != 0, (mask & 1) != 0, false);
                if (symbol >= 1 && symbol <= 3) tier1++;
                if (symbol >= 4 && symbol <= 6) tier2++;
                if (symbol >= 7 && symbol <= 8) tier3++;
                if (symbol >= 9 && symbol <= 10) tier4++;
                if (symbol == 11) sevens++;
                if (symbol == 0) thunder++;
            }
            assertEquals((mask & 1) != 0 ? 20 : 0, tier2);
            assertEquals((mask & 2) != 0 ? 12 : 0, tier3);
            assertEquals((mask & 4) != 0 ? 6 : 0, tier4);
            assertEquals((mask & 8) != 0 ? 3 : 0, sevens);
            assertEquals(92 - tier2 - tier3 - tier4 - sevens, tier1);
            assertEquals(6, thunder);
        }
    }
}
