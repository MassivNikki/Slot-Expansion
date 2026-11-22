package slotexpansion.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import slotexpansion.machine.MachineSave;
import slotexpansion.player.PlayerManager;
import slotexpansion.ui.CasinoUpgradeButton;

import javax.swing.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MachinePriceIntegrationTest {
    @TempDir Path directory;

    @Test
    void nextMachinePriceIsTheSameAfterBuyingAndReloading() throws Exception {
        SaveRepository repository = new SaveRepository(directory.resolve("SaveData"));
        repository.save(List.of(MachineSave.newGame()));
        SwingUtilities.invokeAndWait(() -> {
            try {
                new Application(repository).run();
                PlayerManager.setCoins(BigInteger.valueOf(1_000_000));
                CasinoUpgradeButton button = nextMachineButton();
                assertEquals(BigInteger.valueOf(150_000), button.getPrice());
                button.doClick();
                assertEquals(BigInteger.valueOf(850_000), PlayerManager.getCoins());
                assertEquals(BigInteger.valueOf(900_000), nextMachineButton().getPrice());
            } finally {
                Application.mainFrame.dispose();
            }
        });
        repository.save(List.of(MachineSave.newGame(), MachineSave.newGame()));
        SwingUtilities.invokeAndWait(() -> {
            try {
                new Application(repository).run();
                assertEquals(BigInteger.valueOf(900_000), nextMachineButton().getPrice());
            } finally {
                Application.mainFrame.dispose();
            }
        });
    }

    private static CasinoUpgradeButton nextMachineButton() {
        JPanel canvas = (JPanel) Arrays.stream(Application.mainFrame.getContentPane().getComponents())
                .filter(component -> component instanceof JPanel).findFirst().orElseThrow();
        return (CasinoUpgradeButton) Arrays.stream(canvas.getComponents())
                .filter(component -> component instanceof CasinoUpgradeButton).findFirst().orElseThrow();
    }
}
