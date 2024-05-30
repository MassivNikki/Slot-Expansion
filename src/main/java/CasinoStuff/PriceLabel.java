package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PriceLabel extends JLabel {
    private boolean freeUpgrades = false;
    private static DecimalFormat scientificFormat = new DecimalFormat("0.##E0");
    private BigInteger price = BigInteger.valueOf(0);
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
    public static DecimalFormat getFormat(){
        DecimalFormatSymbols dfs = scientificFormat.getDecimalFormatSymbols();
        dfs.setExponentSeparator("e");
        dfs.setDecimalSeparator('.');
        scientificFormat.setDecimalFormatSymbols(dfs);
        return scientificFormat;
    }
    public void updateFormat(BigInteger amount){
        if(amount.compareTo(BigInteger.valueOf(10000)) >= 0){
            super.setText(getFormat().format(amount));
        }else super.setText(String.valueOf(amount));
    }

    //es sollte noch die länge des feldes anpassen -> geht ja mit setbounds x,y textwidth
    public void setPrice(BigInteger price, int slotTier){
        if(initialPriceSet){
            this.price = price;
        }else{
            this.price = price.multiply(BigInteger.valueOf((long) Math.pow(slotTier, 20)));
            initialPriceSet = true;
        }
        if(freeUpgrades){
            this.price = BigInteger.valueOf(1);
        }
        if(this.price.compareTo(BigInteger.valueOf(10000)) >= 0){
            super.setText(getFormat().format(this.price));
        }else super.setText(String.valueOf(this.price));
        resizeLabel();
    }
    public void setCoins(BigInteger coins){
        if(coins.compareTo(BigInteger.valueOf(10000)) >= 0){
            super.setText(getFormat().format(coins));
        }else super.setText(String.valueOf(coins));
    }

    public BigInteger increasePrice() {
        price = price.multiply(BigInteger.valueOf(20));
        setPrice(price,tierOfSlot);
        return price;
    }

    public BigInteger getPrice(){
        return price;
    }
    public void resizeLabel() {
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int textWidth = fontMetrics.stringWidth(getText());
        if(textWidth > getWidth()){
            setPreferredSize(new Dimension(textWidth, getHeight()));
            setBounds(getX(), getY(), textWidth, getHeight());
        }
    }

}
