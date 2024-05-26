package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

public class PlayerManager {
    private static BigInteger coins;
    private static PriceLabel coinAmountLabel;
    private static JFrame mainFrame;


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
    public static void setSizeToMainFrameSize(){
        coinAmountLabel.setBounds(0,20,mainFrame.getSize().width-16,20);
    }

    public static BigInteger getCoins() {
        return coins;
    }

    //einheitliche coin objekt, welches das label bei jeder änderung mitändert
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
