package slotexpansion.machine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PetTest {
    @Test
    void clearingTheFastestPetClearsItsCooldownBonus() {
        Pet pet = new Pet(11);
        assertEquals(100, pet.getAutoCooldownBonus());
        pet.setTier(-1);
        assertEquals(0, pet.getAutoCooldownBonus());
        assertEquals(1, pet.getMulti());
    }
}
