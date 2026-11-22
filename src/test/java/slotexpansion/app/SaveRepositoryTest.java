package slotexpansion.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import slotexpansion.machine.MachineSave;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaveRepositoryTest {
    @TempDir Path directory;

    @Test
    void roundTripsLegacyRecordsAndUpgradedMachines() throws IOException {
        Path path = directory.resolve("SaveData");
        String legacy = "1234,4,10,1,2,3,4,40,1,2,3,5,800,400,true,2,3";
        MachineSave original = MachineSave.parse(legacy);
        Files.writeString(path, legacy + System.lineSeparator());
        SaveRepository repository = new SaveRepository(path);
        assertEquals(List.of(original), repository.load());
        repository.save(repository.load());
        assertEquals(List.of(original), repository.load());
    }

    @Test
    void extendedRecordsPreservePurchaseCountsAfterOddsAreReallocated() throws IOException {
        MachineSave original = new MachineSave(BigInteger.TEN, 1, BigInteger.ZERO,
                0, 0, 3, 1, 0, 0, 0, -1, 3, 1000, 500, false, 0, 0,
                0, 0, 3, 2, 0, 5);
        SaveRepository repository = new SaveRepository(directory.resolve("SaveData"));
        repository.save(List.of(original));
        assertEquals(List.of(original), repository.load());
    }

    @Test
    void oldRecordsStartWithSymbolsLocked() {
        MachineSave legacy = MachineSave.parse("1234,4,10,1,2,3,4,40,1,2,3,5,800,400,true,2,3");
        String oldRecord = "1234,4,10,1,2,3,4,40,1,2,3,5,800,400,true,2,3,1,2,3,4,1";
        MachineSave previous = MachineSave.parse(oldRecord);
        assertEquals(0, legacy.unlockedSymbols());
        assertEquals(0, previous.unlockedSymbols());
        assertThrows(IllegalArgumentException.class,
                () -> MachineSave.parse(oldRecord + ",16"));
    }

    @Test
    void currentLevelsAndXpRoundTripWithoutRecalculation() throws IOException {
        String record = "1234,4,10,1,2,3,4,40,1,2,3,5,800,400,true,2,3,1,2,3,4,1,5";
        Path path = directory.resolve("SaveData");
        Files.writeString(path, record);
        SaveRepository repository = new SaveRepository(path);
        MachineSave saved = repository.load().getFirst();
        assertEquals(4, saved.level());
        assertEquals(BigInteger.TEN, saved.xp());
        assertEquals(BigInteger.valueOf(1234), saved.balance());
        assertEquals(3, saved.petTier());
        assertEquals(5, saved.unlockedSymbols());
        assertEquals(4, saved.tier2Purchases());
        repository.save(List.of(saved));
        assertEquals(List.of(saved), repository.load());
        assertEquals(23, saved.toCsv().split(",").length);
    }

    @Test
    void missingAndEmptySavesStartANewGame() throws IOException {
        Path path = directory.resolve("SaveData");
        SaveRepository repository = new SaveRepository(path);
        assertEquals(List.of(MachineSave.newGame()), repository.load());
        Files.writeString(path, "# header\n");
        assertEquals(List.of(MachineSave.newGame()), repository.load());
    }

    @Test
    void malformedSaveFailsWithoutOverwritingIt() throws IOException {
        Path path = directory.resolve("SaveData");
        Files.writeString(path, "broken,data\n");
        SaveRepository repository = new SaveRepository(path);
        IOException error = assertThrows(IOException.class, repository::load);
        assertTrue(error.getMessage().contains("line 1"));
        assertEquals("broken,data\n", Files.readString(path));
    }

    @Test
    void invalidSaveWriteLeavesPreviousFileUntouched() throws IOException {
        Path path = directory.resolve("SaveData");
        Files.writeString(path, "previous");
        assertThrows(IllegalArgumentException.class, () -> new SaveRepository(path).save(List.of()));
        assertEquals("previous", Files.readString(path));
    }
}
