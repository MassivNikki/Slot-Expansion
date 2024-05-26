package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class PriceLabel extends JLabel {
    DecimalFormat scientificFormat = new DecimalFormat("0.###E0");
    private BigInteger price;
    private int tierOfSlot = 1;
    private boolean initialPriceSet = false;
    public PriceLabel(BigInteger price, int slotTier) {
        super();
        tierOfSlot = slotTier;
        setPrice(price,tierOfSlot);
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.yellow);
    }

    public PriceLabel() {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.yellow);
    }
    public void updateFormat(BigInteger xp){
        if(xp.compareTo(BigInteger.valueOf(10000)) >= 0){
            super.setText(scientificFormat.format(xp));
        }else super.setText(String.valueOf(xp));
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    //es sollte noch die länge des feldes anpassen -> geht ja mit setbounds x,y textwidth
    public void setPrice(BigInteger price, int slotTier){
        if(initialPriceSet){
            this.price = price;
        }else{
            this.price = price.multiply(BigInteger.valueOf((long) Math.pow(slotTier, 20)));
            initialPriceSet = true;
        }
        if(this.price.compareTo(BigInteger.valueOf(10000)) >= 0){
            super.setText(scientificFormat.format(this.price));
        }else super.setText(String.valueOf(this.price));

        resizeLabel();
    }
    public void setCoins(BigInteger coins){
        if(coins.compareTo(BigInteger.valueOf(10000)) >= 0){
            super.setText(scientificFormat.format(coins));
        }else super.setText(String.valueOf(coins));
    }

    public BigInteger increasePrice() {
        price = price.multiply(price.divide(BigInteger.valueOf(1000)));
        setPrice(price,tierOfSlot);
        return price;
    }

    public BigInteger getPrice(){
        return price;
    }
    public void resizeLabel() {
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int textWidth = fontMetrics.stringWidth(getText());
        setPreferredSize(new Dimension(textWidth, getHeight()));
        setBounds(getX(), getY(), textWidth, getHeight());
    }

}
