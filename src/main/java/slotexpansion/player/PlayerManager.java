package slotexpansion.player;

import slotexpansion.ui.PriceLabel;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

/**
 * Holds the shared coin balance and keeps its display synchronized with changes.
 */
public class PlayerManager {
    private static BigInteger coins;
    private static PriceLabel coinAmountLabel;
    private static JFrame mainFrame;


    /** Connects the manager to the game window and creates its balance display. */
    public static void setMainFrame(JFrame mainFrame) {
        PlayerManager.mainFrame = mainFrame;
        generateCoinStuff();
    }

    private static void generateCoinStuff() {
        coinAmountLabel = new PriceLabel();
        coinAmountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        setSizeToMainFrameSize();
        coinAmountLabel.setForeground(Color.ORANGE);
        mainFrame.add(coinAmountLabel);
    }
    /** Resizes the balance display to match the current window width. */
    public static void setSizeToMainFrameSize(){
        coinAmountLabel.setBounds(0,20,mainFrame.getSize().width-16,20);
    }

    /** Returns the current shared balance. */
    public static BigInteger getCoins() {
        return coins;
    }

    /** Replaces the balance and refreshes the display. */
    public static void setCoins(BigInteger coins) {
        PlayerManager.coins = coins;
        coinAmountLabel.setCoins(coins);
    }
    public static void decreaseCoins(BigInteger amount) {
        coins = coins.subtract(amount);
        coinAmountLabel.setCoins(coins);
    }

    public static void increaseCoins(BigInteger amount) {
        coins = coins.add(amount);
        coinAmountLabel.setCoins(coins);
    }
}
