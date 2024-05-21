package CasinoStuff;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Pet {

    private static int multi = 1;
    private static ImageIcon icon;
    public static int autoCooldownBonus = 0;
    public static int[] petMultis = {2, 3, 4, 5, 7, 9, 11, 14, 17, 25, 30, 40};
    //ghost ameise wurm käfer frosch salamander spinne scorpion schlange crocodile basilisk hydra dragon
    private static final ArrayList<String> imageNames = new ArrayList<>(
            Arrays.asList("ghost", "ant", "worm", "beetle", "frog", "salamander", "spider", "scorpion", "snake", "crocodile", "basilisk", "hydra", "dragon"));
    private static final ArrayList<ImageIcon> icons = new ArrayList<>();
    public static int petTier;

    public static void setPet(int petTier) {
        if(petTier == -1){
            Pet.petTier = -1;
            return;
        }
        Pet.multi = petMultis[petTier];
        if(multi == petMultis[petMultis.length-1]){
            autoCooldownBonus = 100;
        }
        Pet.petTier = petTier;
        if (icons.isEmpty()) {
            Application.generateIconsFromFolder(imageNames, icons, "pets", 40);
        }
        icon = icons.get(petTier);
        PlayerManager.setPetImage(icon);
    }


    public static ImageIcon getIcon() {
        return icon;
    }

    public static int getMulti() {
        return multi;
    }
}
