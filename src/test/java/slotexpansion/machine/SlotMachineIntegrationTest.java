package slotexpansion.machine;

import org.junit.jupiter.api.Test;
import slotexpansion.app.Application;
import slotexpansion.player.PlayerManager;
import slotexpansion.ui.CasinoTextField;
import slotexpansion.ui.CasinoUpgradeButton;

import javax.swing.*;
import java.awt.Component;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SlotMachineIntegrationTest {
    private static class ControlledRandom extends Random {
        boolean awardFreeSpins;
        int cell;

        @Override public int nextInt(int origin, int bound) {
            if (origin == 0 && bound == 100) {
                if (awardFreeSpins) return 99;
                return new int[]{5, 11, 23}[cell++ % 3];
            }
            return origin;
        }
    }

    @Test
    void restoredGridKeepsUpgradesAndFreeSpinsBlockOnlyItsControls() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            Application.mainFrame = frame;
            PlayerManager.setMainFrame(frame);
            PlayerManager.setCoins(BigInteger.valueOf(100_000));
            ControlledRandom random = new ControlledRandom();
            JPanel panel = new JPanel(null);
            panel.setSize(400, 450);
            SlotMachine machine = new SlotMachine(panel, 1, new SlotSymbolGenerator(random));
            JPanel otherPanel = new JPanel(null);
            otherPanel.setSize(400, 450);
            SlotMachine otherMachine = new SlotMachine(otherPanel, 2);
            MachineSave saved = MachineSave.parse("100000,1,0,2,1,0,0,48,0,1,-1,4,800,400,true,0,0");
            try {
                machine.restore(saved);
                otherMachine.restore(MachineSave.newGame());
                assertEquals(saved, machine.snapshot(saved.balance()));
                CasinoUpgradeButton upgrade = machine.upgradeArea.button(UpgradeArea.Upgrade.SEVEN);
                BigInteger balanceBefore = PlayerManager.getCoins();
                random.awardFreeSpins = true;
                machine.slotGrid.spinSlotMachine(BigInteger.valueOf(5));
                assertTrue(machine.slotGrid.isFreeSpinsActivated());
                assertFalse(upgrade.isEnabled());
                assertFalse(machine.addColumnUpgradeButton.isEnabled());
                assertTrue(otherMachine.upgradeArea.button(UpgradeArea.Upgrade.SEVEN).isEnabled());
                assertFalse(machine.upgradeArea.button(UpgradeArea.Upgrade.PAYLINE).isEnabled());
                for (Component component : panel.getComponents()) {
                    if (component instanceof CasinoTextField field) assertFalse(field.isEnabled());
                    if (component instanceof JCheckBox checkbox) assertFalse(checkbox.isEnabled());
                    if (component instanceof JButton button && !button.getText().contains("Spin(")) {
                        assertFalse(button.isEnabled(), button.getText());
                    }
                }
                upgrade.doClick();
                assertEquals(2, machine.slotGrid.getAdditionalSevenSymbolChance());
                assertEquals(balanceBefore, PlayerManager.getCoins());

                random.awardFreeSpins = false;
                while (machine.slotGrid.isFreeSpinsActivated()) {
                    machine.slotGrid.spinSlotMachine(BigInteger.valueOf(5));
                }
                assertTrue(upgrade.isEnabled());
                assertTrue(machine.addColumnUpgradeButton.isEnabled());
            } finally {
                machine.upgradeArea.dispose();
                otherMachine.upgradeArea.dispose();
                frame.dispose();
            }
        });
    }

    @Test
    void lowerTierReallocationKeepsPurchasePriceAcrossReloadAndGridReset() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            Application.mainFrame = frame;
            PlayerManager.setMainFrame(frame);
            PlayerManager.setCoins(BigInteger.valueOf(100_000));
            JPanel panel = new JPanel(null);
            panel.setSize(400, 450);
            SlotMachine machine = new SlotMachine(panel, 1);
            JPanel secondPanel = new JPanel(null);
            secondPanel.setSize(400, 450);
            SlotMachine restored = new SlotMachine(secondPanel, 1);
            try {
                MachineSave state = MachineSave.parse("100000,1,0,0,0,3,2,0,0,0,-1,3,1000,500,false,0,0");
                machine.restore(state);
                assertFalse(machine.upgradeArea.button(UpgradeArea.Upgrade.TIER2).isEnabled());
                CasinoUpgradeButton tier2 = machine.upgradeArea.button(UpgradeArea.Upgrade.TIER2);
                assertEquals(BigInteger.valueOf(600), tier2.getPrice());
                machine.upgradeArea.button(UpgradeArea.Upgrade.TIER3).doClick();
                assertEquals(1, machine.slotGrid.getAdditionalTier2SymbolChance());
                assertEquals(BigInteger.valueOf(600), tier2.getPrice());
                restored.restore(machine.snapshot(PlayerManager.getCoins()));
                assertEquals(BigInteger.valueOf(600), restored.upgradeArea.button(UpgradeArea.Upgrade.TIER2).getPrice());
                restored.upgradeArea.upgradeManager.upgradeGrid();
                assertEquals(BigInteger.valueOf(150), restored.upgradeArea.button(UpgradeArea.Upgrade.TIER2).getPrice());
            } finally {
                machine.upgradeArea.dispose();
                restored.upgradeArea.dispose();
                frame.dispose();
            }
        });
    }
}
