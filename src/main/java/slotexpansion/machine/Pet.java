package slotexpansion.machine;

import slotexpansion.app.Application;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents the pet assigned to a slot machine and its payout multiplier.
 */
public class Pet {

    private static final int[] MULTIPLIERS = {2, 3, 4, 5, 7, 9, 11, 14, 17, 20, 25, 30};
    private static final int[] CHANCE_BOUNDS = {1000, 652, 422, 272, 149, 114, 84, 59, 39, 24, 14, 7, 0};
    private int multi = 1;
    private ImageIcon icon;
    private int autoCooldownBonus = 0;
    // Icon order matches the tier order in petMultis.
    private final ArrayList<String> imageNames = new ArrayList<>(
            Arrays.asList("ghost", "ant", "worm", "beetle", "frog", "salamander", "spider", "scorpion", "snake", "crocodile", "basilisk", "hydra", "dragon"));
    private final ArrayList<ImageIcon> icons = new ArrayList<>();
    private int petTier;

    public Pet(int petTier) {
        Application.generateIconsFromFolder(imageNames, icons, "pets", 40);
        setTier(petTier);
    }

    /**
     * Selects a pet tier, or clears the pet when the tier is {@code -1}.
     */
    public void setTier(int tier) {
        autoCooldownBonus = 0;
        if (tier == -1) {
            this.petTier = -1;
            multi = 1;
            icon = null;
            return;
        }
        multi = MULTIPLIERS[tier];
        if (multi == MULTIPLIERS[MULTIPLIERS.length - 1]) {
            autoCooldownBonus = 100;
        }
        this.petTier = tier;
        icon = icons.get(tier);
    }


    public ImageIcon getIcon() {
        return icon;
    }

    public int getMulti() {
        return multi;
    }

    int getTier() {
        return petTier;
    }

    int getAutoCooldownBonus() {
        return autoCooldownBonus;
    }

    static int multiplierForTier(int tier) {
        return MULTIPLIERS[tier];
    }

    static int chooseTier(Random random) {
        int roll = random.nextInt(0, 1000);
        for (int i = 0; i < MULTIPLIERS.length; i++) {
            if (roll < CHANCE_BOUNDS[i] && roll >= CHANCE_BOUNDS[i + 1]) {
                return i;
            }
        }
        throw new IllegalStateException("Pet roll is outside the configured ranges");
    }
}
