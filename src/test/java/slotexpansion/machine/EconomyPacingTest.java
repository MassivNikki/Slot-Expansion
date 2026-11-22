package slotexpansion.machine;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EconomyPacingTest {
    @Test
    void typicalFirstMachineProgressTakesHundredsOfSpins() {
        int[] machineSpins = new int[50];
        int[] gridSpins = new int[50];
        for (int seed = 0; seed < machineSpins.length; seed++) {
            SlotSymbolGenerator generator = new SlotSymbolGenerator(new Random(seed));
            Random petRandom = new Random(seed + 500);
            UpgradePrice stakePrice = new UpgradePrice(BigInteger.valueOf(500), 1);
            BigInteger coins = BigInteger.valueOf(1000);
            BigInteger stake = BigInteger.valueOf(5);
            BigInteger xp = BigInteger.ZERO;
            int level = 1, petMultiplier = 1, freeSpins = 0, spins = 0;
            boolean tier2 = false, auto = false;
            while (coins.compareTo(UpgradePrice.FIRST_MACHINE_UNLOCK) < 0 && spins < 3000) {
                spins++;
                if (freeSpins == 0) {
                    if (!tier2 && coins.compareTo(BigInteger.valueOf(150)) >= 0) {
                        coins = coins.subtract(BigInteger.valueOf(150));
                        tier2 = true;
                    }
                    if (coins.compareTo(stakePrice.current().multiply(BigInteger.TWO)) >= 0) {
                        coins = coins.subtract(stakePrice.current());
                        stake = stake.compareTo(BigInteger.valueOf(20)) > 0
                                ? stake.add(stake.divide(BigInteger.valueOf(5)))
                                : stake.add(BigInteger.valueOf(4));
                        stakePrice.advance();
                    }
                    if (!auto && coins.compareTo(BigInteger.valueOf(20_000)) >= 0) {
                        coins = coins.subtract(BigInteger.valueOf(10_000));
                        auto = true;
                    }
                }
                SpinCalculator.Result result = SpinCalculator.spin(generator, 3, 13,
                        SlotPaylineLayouts.baseForColumnCount(3), stake, petMultiplier, level,
                        freeSpins > 0, false, false, false, tier2);
                if (freeSpins > 0) freeSpins--;
                freeSpins += result.freeSpinsAwarded();
                coins = coins.add(result.totalPayout());
                xp = xp.add(result.totalPayout());
                while (xp.compareTo(LevelProgression.requirement(level)) >= 0) {
                    xp = xp.subtract(LevelProgression.requirement(level));
                    level++;
                    petMultiplier = Math.max(petMultiplier, Pet.multiplierForTier(Pet.chooseTier(petRandom)));
                }
                if (gridSpins[seed] == 0 && coins.compareTo(UpgradePrice.FIRST_GRID_EXPANSION) >= 0) {
                    gridSpins[seed] = spins;
                }
            }
            machineSpins[seed] = spins;
        }
        Arrays.sort(machineSpins);
        Arrays.sort(gridSpins);
        int medianMachine = machineSpins[machineSpins.length / 2];
        int medianGrid = gridSpins[gridSpins.length / 2];
        // Approximate 10–20 minutes at one spin per second; real play varies with luck and spending.
        assertTrue(medianMachine >= 600 && medianMachine <= 1200, "median spins: " + medianMachine);
        assertTrue(medianGrid >= 200 && medianGrid < medianMachine, "grid median: " + medianGrid);
    }
}
