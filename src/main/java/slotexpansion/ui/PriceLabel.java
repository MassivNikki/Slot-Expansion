package slotexpansion.ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/** Displays coin amounts and upgrade prices, abbreviating large values. */
public class PriceLabel extends JLabel {
    private static final DecimalFormat scientificFormat = new DecimalFormat("0.##E0");

    public PriceLabel() {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.yellow);
    }
    /** Returns the shared scientific-notation formatter used for large amounts. */
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

    public void setCoins(BigInteger coins){
        updateFormat(coins);
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
