package slotexpansion.player;

import slotexpansion.ui.PriceLabel;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

/**
 * Holds the shared coin balance and keeps its display synchronized with changes.
 */
public class PlayerManager {
    private static final CoinBalance BALANCE = new CoinBalance();
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
        return BALANCE.get();
    }

    /** Replaces the balance and refreshes the display. */
    public static void setCoins(BigInteger coins) {
        BALANCE.set(coins);
        coinAmountLabel.setCoins(coins);
    }
    public static void decreaseCoins(BigInteger amount) {
        if (!trySpend(amount)) {
            throw new IllegalStateException("Insufficient coins");
        }
    }

    public static void increaseCoins(BigInteger amount) {
        BALANCE.add(amount);
        coinAmountLabel.setCoins(BALANCE.get());
    }

    /** Spends an amount only when affordable, then refreshes the balance display. */
    public static boolean trySpend(BigInteger amount) {
        if (!BALANCE.trySpend(amount)) {
            return false;
        }
        coinAmountLabel.setCoins(BALANCE.get());
        return true;
    }
}
