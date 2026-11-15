package slotexpansion.ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

/** Displays coin amounts and upgrade prices, abbreviating large values. */
public class PriceLabel extends JLabel {
    public PriceLabel() {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.yellow);
    }
    public void updateFormat(BigInteger amount){
        super.setText(IdleNumberFormatter.format(amount));
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
