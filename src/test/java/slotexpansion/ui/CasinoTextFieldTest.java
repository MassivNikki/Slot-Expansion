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
        assertEquals(BigInteger.valueOf(12345), field.getAmount());
        field.setText("2.5e4");
        assertEquals(BigInteger.valueOf(25000), field.getAmount());
        field.setText("1.2e0");
        assertThrows(ArithmeticException.class, field::getAmount);
    }
}
