package CasinoStuff;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Random;

public class PlayerManager {
    private static long coins;
    private static JLabel coinAmountLabel;
    private static JLabel petImageLabel;
    private static long maxSpinAmount;
    private static int playerLevel = 0;
    private static long playerXp = 0;
    private static JLabel playerLevelLabel;
    private static JLabel progressBarLabel;
    private static JLabel playerXpAmountLabel;
    private static JLabel xpAmountNeededLabel;
    private static long amountForLevelUp;
    private static int progressBarWidth = 230;
    private static JFrame mainFrame;
    private static Border normalBorder = BorderFactory.createLineBorder(Color.black);
    private static int[] chances = {1000,652,422,272,172,122,92,67,47,30,17,7,2,0};


    public static void setMainFrame(JFrame mainFrame) {
        PlayerManager.mainFrame = mainFrame;
        generateLabels();
    }

    private static void generateCoinStuff() {
        coinAmountLabel = new JLabel();
        coinAmountLabel.setForeground(Color.white);
        mainFrame.add(coinAmountLabel);
    }

    public static void rearrangeCoinLabel() {
        FontMetrics fontMetrics = coinAmountLabel.getFontMetrics(coinAmountLabel.getFont());
        int textWidth = fontMetrics.stringWidth(coinAmountLabel.getText());
        coinAmountLabel.setPreferredSize(new Dimension(textWidth, coinAmountLabel.getHeight()));
        coinAmountLabel.setBounds(mainFrame.getBounds().width / 2 - textWidth / 2, 60, textWidth, 20);
        petImageLabel.setBounds(coinAmountLabel.getX() + textWidth + 20, coinAmountLabel.getY() - 5, 70, 40);
    }

    public static int getMinimumCoinsPerLane() {
        if (playerLevel > 3) {
            return playerLevel / 2;
        } else {
            return 1;
        }
    }

    public static long getCoins() {
        return coins;
    }

    //einheitliche coin objekt, welches das label bei jeder änderung mitändert
    public static void setCoins(long coins) {
        int coinAmount = String.valueOf(PlayerManager.coins).length();
        PlayerManager.coins = coins;
        coinAmountLabel.setText(coins + " C");
        if (coinAmount < String.valueOf(PlayerManager.coins).length()) {
            rearrangeCoinLabel();
        }
    }

    public static void setPetImage(ImageIcon icon) {
        petImageLabel.setVisible(true);
        petImageLabel.setIcon(icon);
        petImageLabel.setText(Pet.getMulti() + "x");
        petImageLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        petImageLabel.setForeground(Color.orange);
    }

    public static int getPlayerLevel() {
        return playerLevel;
    }

    public static void setPlayerLevel(int givenPlayerLevel) {
        playerLevel = givenPlayerLevel;
        playerLevelLabel.setText(String.valueOf(givenPlayerLevel));
        calcMaxSpinAmount();
        calculateNextLevelXpAmount();
    }

    public static long getPlayerXp() {
        return playerXp;
    }

    public static void setPlayerXp(long givenPlayerXp) {
        int xpAmount = String.valueOf(playerXp).length();
        playerXp = givenPlayerXp;
        playerXpAmountLabel.setText(String.valueOf(givenPlayerXp));
        if (xpAmount < String.valueOf(playerXp).length()) {
            rearrangeXpAmountLabel();
        }
        checkForLevelUp();
    }

    private static void calcMaxSpinAmount() {
        maxSpinAmount = (long) Math.pow(3, playerLevel);
    }

    private static void rearrangeXpAmountLabel() {
        FontMetrics fontMetrics = playerXpAmountLabel.getFontMetrics(playerXpAmountLabel.getFont());
        int textWidth = fontMetrics.stringWidth(playerXpAmountLabel.getText());
        playerXpAmountLabel.setPreferredSize(new Dimension(textWidth, playerXpAmountLabel.getHeight()));
        playerXpAmountLabel.setBounds(mainFrame.getWidth() / 2 - progressBarWidth / 2, 43, textWidth, 15);
    }

    protected static void arrangeLabelsBasedOnSize() {
        progressBarWidth = mainFrame.getWidth() - 100;

        FontMetrics fontMetrics = xpAmountNeededLabel.getFontMetrics(xpAmountNeededLabel.getFont());
        int textWidth = fontMetrics.stringWidth(xpAmountNeededLabel.getText());
        xpAmountNeededLabel.setPreferredSize(new Dimension(textWidth, xpAmountNeededLabel.getHeight()));
        xpAmountNeededLabel.setBounds(mainFrame.getWidth() / 2 + progressBarWidth / 2 - textWidth, 43, textWidth, 15);

        fontMetrics = playerLevelLabel.getFontMetrics(playerLevelLabel.getFont());
        textWidth = fontMetrics.stringWidth(playerLevelLabel.getText());
        playerLevelLabel.setPreferredSize(new Dimension(textWidth, playerLevelLabel.getHeight()));
        playerLevelLabel.setBounds(mainFrame.getWidth() / 2 - textWidth / 2, 10, textWidth, 15);

        rearrangeXpAmountLabel();

        fontMetrics = progressBarLabel.getFontMetrics(progressBarLabel.getFont());
        textWidth = fontMetrics.stringWidth(progressBarLabel.getText());
        progressBarLabel.setPreferredSize(new Dimension(textWidth, progressBarLabel.getHeight()));
        progressBarLabel.setBounds(mainFrame.getWidth() / 2 - progressBarWidth / 2, 30, progressBarWidth, 15);
    }

    private static void choosePetForLevelUp() {
        int rand = new Random().nextInt(0, 1000);
        int tier = 0;
        for (int i = 0; i < chances.length-1; i++) {
            if(rand < chances[i] && rand > chances[i+1]) {
                tier = i;
            }
        }
        if (Pet.petMultis[tier] > Pet.getMulti()) {
            Pet.setPet(tier);
            setPetImage(Pet.getIcon());
        }
        System.out.println("Won a pet with multi of: " + Pet.petMultis[tier]);
    }

    protected static void checkForLevelUp() {
        //solange die xp des spieler die levelup grenze überschreitet wird ein levelup durchgeführt
        while (amountForLevelUp <= playerXp) {
            playerLevel++;
            calcMaxSpinAmount();
            playerXp -= amountForLevelUp;
            setCoins(getCoins() + playerLevel * 100L);
            calculateNextLevelXpAmount();
            arrangeLabelsBasedOnSize();
            choosePetForLevelUp();
        }
        playerLevelLabel.setText(String.valueOf(playerLevel));
        playerXpAmountLabel.setText(String.valueOf(playerXp));
        xpAmountNeededLabel.setText(String.valueOf(amountForLevelUp));
        progressBarLabel.setText("");
        calculateProgressBar();
    }

    protected static void calculateNextLevelXpAmount() {
        //berechnet die anzahl die zum nächsten level up zu erreichen ist
        amountForLevelUp = (long) (Math.pow(playerLevel, 6) / 3) + 200;
        xpAmountNeededLabel.setText(String.valueOf(amountForLevelUp));
    }

    protected static void calculateProgressBar() {
        //es wird die Anzeige aktualisiert, damit man weis wie weit die xp sind
        StringBuilder builder = new StringBuilder();
        double progress = (double) playerXp / amountForLevelUp;
        int bars = (int) (progress * progressBarWidth / 10);
        for (int i = 0; i < bars; i++) {
            builder.append("█");
        }
        progressBarLabel.setText(builder.toString());
    }

    private static void generateLabels() {


        playerLevelLabel = new JLabel();
        playerLevelLabel.setForeground(Color.ORANGE);
        playerLevelLabel.setBounds(mainFrame.getBounds().width / 2, 10, 50, 20);
        playerLevelLabel.setFont(new Font("Arial", Font.BOLD, 15));
        mainFrame.add(playerLevelLabel);

        progressBarLabel = new JLabel();
        progressBarLabel.setForeground(Color.GREEN);
        progressBarLabel.setBounds(mainFrame.getBounds().width / 2 - progressBarWidth / 2, 30, progressBarWidth, 10);
        progressBarLabel.setFont(new Font("Arial", Font.BOLD, 14));
        progressBarLabel.setBorder(normalBorder);
        mainFrame.add(progressBarLabel);

        xpAmountNeededLabel = new JLabel();
        xpAmountNeededLabel.setForeground(Color.white);
        xpAmountNeededLabel.setBounds(mainFrame.getBounds().width / 2 + progressBarWidth / 2, 43, 60, 15);
        mainFrame.add(xpAmountNeededLabel);

        playerXpAmountLabel = new JLabel(String.valueOf(playerXp));
        playerXpAmountLabel.setForeground(Color.white);
        playerXpAmountLabel.setBounds(mainFrame.getBounds().width / 2 - progressBarWidth / 2, 43, 60, 15);
        mainFrame.add(playerXpAmountLabel);
        generateCoinStuff();

        petImageLabel = new JLabel();
        petImageLabel.setBackground(Color.white);
        petImageLabel.setVisible(false);
        mainFrame.add(petImageLabel);
    }
}
