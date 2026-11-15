package slotexpansion.app;

import slotexpansion.machine.MachineSave;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Reads legacy save records and replaces the previous save only after a complete write. */
public final class SaveRepository {
    private static final String HEADER =
            "# balance,slotLevel,slotXp,additionalSevenSymbolChance,additionalTier4SymbolChance,"
                    + "additionalTier3SymbolChance,additionalTier2SymbolChance,tier1SymbolChance,"
                    + "additionalX2SymbolChance,addedPaylines,petTier,columnCount,autoCooldown,"
                    + "manualCooldown,autoSpinUnlocked,minStakeUpgrades,maxStakeUpgrades,"
                    + "sevenPurchases,tier4Purchases,tier3Purchases,tier2Purchases,x2Purchases,unlockedSymbols";
    private final Path path;

    /**
     * Creates a repository for one save file.
     * @param path path of the save file
     */
    public SaveRepository(Path path) {
        this.path = path.toAbsolutePath();
    }

    /**
     * Reads and validates saved machines, or returns a fresh game for a missing or empty file.
     * @return saved machines in display order
     * @throws IOException if the file cannot be read or contains invalid data
     */
    public List<MachineSave> load() throws IOException {
        List<MachineSave> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    records.add(MachineSave.parse(line));
                } catch (IllegalArgumentException ex) {
                    throw new IOException("Invalid save record at line " + lineNumber + ": " + ex.getMessage(), ex);
                }
                if (records.size() > 8) {
                    throw new IOException("A save cannot contain more than eight machines");
                }
            }
        } catch (NoSuchFileException ex) {
            return List.of(MachineSave.newGame());
        }
        return records.isEmpty() ? List.of(MachineSave.newGame()) : records;
    }

    /**
     * Atomically replaces the save after writing all machine records.
     * @param records validated machine states
     * @throws IOException if writing or atomic replacement fails
     */
    public void save(List<MachineSave> records) throws IOException {
        if (records.isEmpty() || records.size() > 8) {
            throw new IllegalArgumentException("A save must contain one to eight machines");
        }
        Path temporary = Files.createTempFile(path.getParent(), "slot-expansion-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                writer.write(HEADER);
                writer.newLine();
                for (MachineSave record : records) {
                    writer.write(record.toCsv());
                    writer.newLine();
                }
            }
            try {
                Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                throw new IOException("This filesystem cannot safely replace the save file", ex);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
