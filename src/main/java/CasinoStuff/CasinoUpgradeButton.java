package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.function.Consumer;

public class CasinoUpgradeButton extends CasinoButton {
    private PriceLabel priceLabel = new PriceLabel();
    private JLabel chanceLabel = new JLabel();
    private Timer backToOrgColor = new Timer(500, e ->  {
        super.setOriginalColor();
    });

    public CasinoUpgradeButton(String description, BigInteger price, int slotTier, String increment, int startX, int startY, JFrame frame) {
        super(increment);
        colorBackground();
        setBounds(startX + 210, startY, 30, 20);
        if (!description.isEmpty()) {
            JLabel upgradeInfoLabel = new JLabel(description);
            upgradeInfoLabel.setForeground(Color.white);
            upgradeInfoLabel.setBounds(startX, startY, 150, 20);
            frame.add(upgradeInfoLabel);
        }
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
        backToOrgColor.setRepeats(false);
    }

    public CasinoUpgradeButton(BigInteger price, int slotTier, String increment, int startX, int startY, JFrame frame, int width, int height, int priceLabelPosition) {
        super(increment);
        colorBackground();
        setBounds(startX, startY, width, height);
        priceLabel = new PriceLabel(price, slotTier);
        priceLabel.updateFormat(priceLabel.getPrice());
        switch (priceLabelPosition) {
            case -1:
                priceLabel.setBounds(startX, startY - 20, width, 20);
                priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                break;
            case 0:
                priceLabel.setBounds(startX + width, startY, width, 20);
                break;
            case 1:
                priceLabel.setBounds(startX, startY + height, width, 20);
                priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                break;
        }
        frame.add(priceLabel);
        if (price.compareTo(BigInteger.valueOf(0)) == 0) {
            disableUpgradable();
        }
        setMargin(new Insets(0, 0, 0, 0));
        backToOrgColor.setRepeats(false);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        priceLabel.setBounds(x, y +height, width, 20);
    }

    public void removeFromMainframe(JFrame frame) {
        frame.remove(priceLabel);
        frame.remove(this);
    }
    public void addCustomActionListener(Consumer<CasinoUpgradeButton> customAction) {
        this.addActionListener(e -> {
            // Bedingung prüfen
            if (enoughCoins()) {
                PlayerManager.decreaseCoins(getPrice());
                increasePrice();
                // Spezifischen Code ausführen
                customAction.accept(CasinoUpgradeButton.this);
            }else{
                notEnoughCoins();
            }
        });
    }

    public void notEnoughCoins(){
        setBackground(new Color(0xD52941));
        backToOrgColor.start();
    }

    private void colorBackground() {
        setBackground(new Color(0x2dce98));

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
    public boolean enoughCoins(){
        return priceLabel.getPrice().compareTo(PlayerManager.getCoins()) <= 0;
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
