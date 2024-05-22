package CasinoStuff;

import WholeMachine.Pet;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Random;

public class PlayerManager {
    private static long coins;
    private static JLabel coinAmountLabel;
    private static JFrame mainFrame;
    private static Border normalBorder = BorderFactory.createLineBorder(Color.black);


    public static void setMainFrame(JFrame mainFrame) {
        PlayerManager.mainFrame = mainFrame;
        generateCoinStuff();
        //generateLabels();
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
        coinAmountLabel.setBounds(mainFrame.getBounds().width / 2 - textWidth / 2, 20, textWidth, 20);
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

}
