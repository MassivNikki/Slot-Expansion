package slotexpansion.ui;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CasinoTextFieldTest {
    @Test
    void unchangedAbbreviatedStakeKeepsItsExactValue() {
        CasinoTextField field = new CasinoTextField();
        field.updateFormat(BigInteger.valueOf(12345));
        assertEquals("12.34t", field.getText());
        assertEquals(BigInteger.valueOf(12345), field.getAmount());
        field.setText("12.34t");
        assertEquals(BigInteger.valueOf(12340), field.getAmount());
        field.setText("1.002T");
        assertEquals(new BigInteger("1002000000000"), field.getAmount());
        field.setText("1.000aa");
        assertEquals(BigInteger.TEN.pow(36), field.getAmount());
        field.setText("1.000ae");
        assertEquals(BigInteger.TEN.pow(48), field.getAmount());
        field.setText("5.000");
        assertEquals(BigInteger.valueOf(5), field.getAmount());
        field.setText("2.5e4");
        assertEquals(BigInteger.valueOf(25000), field.getAmount());
        field.setText("1.2e0");
        assertThrows(ArithmeticException.class, field::getAmount);
        field.setText("1.2M?");
        assertThrows(NumberFormatException.class, field::getAmount);
        field.setText("0.0001t");
        assertThrows(ArithmeticException.class, field::getAmount);
    }
}
