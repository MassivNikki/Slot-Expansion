package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

public class CasinoUpgradeButton extends CasinoButton {
    private PriceLabel priceLabel = new PriceLabel();
    private JLabel chanceLabel = new JLabel();

    public CasinoUpgradeButton(String description, BigInteger price, int slotTier, long increment, int startX, int startY, JFrame frame) {
        super("+" + increment);
        setBounds(startX + 210, startY, 30, 20);
        JLabel upgradeInfoLabel = new JLabel(description);
        upgradeInfoLabel.setForeground(Color.white);
        upgradeInfoLabel.setBounds(startX, startY, 150, 20);
        frame.add(upgradeInfoLabel);
        priceLabel = new PriceLabel(price, slotTier);
        priceLabel.updateFormat(priceLabel.getPrice());
        priceLabel.setBounds(startX + 250, startY, 100, 20);
        frame.add(priceLabel);
        if (price.compareTo(BigInteger.valueOf(0)) == 0) {
            disableUpgradable();
        }
        chanceLabel.setForeground(Color.white);
        chanceLabel.setBounds(startX + 160, startY, 40, 20);
        frame.add(chanceLabel);
        setMargin(new Insets(0, 0, 0, 0));
    }

    public CasinoUpgradeButton() {
        super("test");
    }

    public void disableUpgradable() {
        priceLabel.setVisible(false);
        setVisible(false);
    }

    public void enableUpgradable() {
        if (priceLabel.getPrice().compareTo(BigInteger.valueOf(0)) > 0) {
            priceLabel.setVisible(true);
            setVisible(true);
        }

    }

    public void increasePrice() {
        priceLabel.increasePrice();
    }

    public BigInteger getPrice() {
        return priceLabel.getPrice();
    }

    public void setChanceLabelText(String text) {
        chanceLabel.setText(text);
    }

    public String getChanceLabelText() {
        return chanceLabel.getText();
    }

    public void getChanceLabelText(String text) {
        chanceLabel.setText(text);
    }
}
