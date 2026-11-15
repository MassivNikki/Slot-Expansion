package slotexpansion.ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/** A stake text field that stores and displays its value as an integer amount. */
public class CasinoTextField extends JTextField {
    public BigInteger spinAmount;

    public CasinoTextField() {
        super();
    }

    public void updateFormat(BigInteger amount) {
        spinAmount = amount;
        if (amount.compareTo(BigInteger.valueOf(10000)) >= 0) {
            super.setText(PriceLabel.getFormat().format(amount));
        } else super.setText(String.valueOf(amount));
    }

    public BigInteger getAmount() {
        String value = getText();
        BigInteger amount;
        if (spinAmount != null && value.equals(spinAmount.compareTo(BigInteger.valueOf(10000)) >= 0
                ? PriceLabel.getFormat().format(spinAmount) : spinAmount.toString())) {
            return spinAmount;
        }
        if (value.contains("e") || value.contains("E")) {
            // Split scientific notation into its base and exponent.
            amount = new BigDecimal(value).toBigIntegerExact();
        } else {
            amount = new BigInteger(getText());
        }
        return amount;
    }

}
