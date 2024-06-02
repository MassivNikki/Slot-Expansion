package WholeMachine;

import CasinoStuff.Application;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Pet {

    protected int multi = 1;
    protected ImageIcon icon;
    public int autoCooldownBonus = 0;
    public int[] petMultis = {2, 3, 4, 5, 7, 9, 11, 14, 17, 20, 25, 30};
    //ghost ameise wurm käfer frosch salamander spinne scorpion schlange crocodile basilisk hydra dragon
    protected final ArrayList<String> imageNames = new ArrayList<>(
            Arrays.asList("ghost", "ant", "worm", "beetle", "frog", "salamander", "spider", "scorpion", "snake", "crocodile", "basilisk", "hydra", "dragon"));
    protected final ArrayList<ImageIcon> icons = new ArrayList<>();
    public int petTier;

    public Pet(int petTier) {
        Application.generateIconsFromFolder(imageNames, icons, "pets", 40);
        setTier(petTier);
    }
    public void setTier(int tier) {
        if (tier == -1) {
            this.petTier = -1;
            multi = 1;
            icon = null;
            return;
        }
        multi = petMultis[tier];
        if (multi == petMultis[petMultis.length - 1]) {
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
}
