package slotexpansion.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.math.BigInteger;

/** A stake text field that stores and displays its value as an integer amount. */
public class CasinoTextField extends JTextField {
    public BigInteger spinAmount;
    private boolean displayUnchanged;

    public CasinoTextField() {
        super();
        getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { displayUnchanged = false; }
            @Override public void removeUpdate(DocumentEvent e) { displayUnchanged = false; }
            @Override public void changedUpdate(DocumentEvent e) { displayUnchanged = false; }
        });
    }

    public void updateFormat(BigInteger amount) {
        spinAmount = amount;
        super.setText(IdleNumberFormatter.format(amount));
        displayUnchanged = true;
    }

    public BigInteger getAmount() {
        String value = getText();
        if (spinAmount != null && displayUnchanged) {
            return spinAmount;
        }
        return IdleNumberFormatter.parse(value);
    }

}
