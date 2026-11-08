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
        if (value.contains("e")) {
            // Split scientific notation into its base and exponent.
            String[] parts = value.split("e");
            BigDecimal base = new BigDecimal(parts[0]);
            int exponent = Integer.parseInt(parts[1]);
            // Convert the formatted value back to an integer amount.
            amount = base.multiply(BigDecimal.TEN.pow(exponent)).toBigInteger();
        } else {
            amount = new BigInteger(getText());
        }
        return amount;
    }

}
