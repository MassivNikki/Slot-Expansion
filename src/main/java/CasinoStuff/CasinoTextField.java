package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;

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
            // Den String in zwei Teile trennen: Basis und Exponent
            String[] parts = value.split("e");
            BigDecimal base = new BigDecimal(parts[0]);
            int exponent = Integer.parseInt(parts[1]);
            // Den Wert berechnen: base * 10^exponent
            amount = base.multiply(BigDecimal.TEN.pow(exponent)).toBigInteger();
            // Ausgabe zur Bestätigung
            System.out.println(amount);
        } else {
            amount = new BigInteger(getText());
        }
        return amount;
    }

}
