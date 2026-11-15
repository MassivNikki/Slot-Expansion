package slotexpansion.machine;

import org.junit.jupiter.api.Test;
import slotexpansion.app.Application;
import slotexpansion.player.PlayerManager;
import slotexpansion.ui.CasinoTextField;
import slotexpansion.ui.CasinoUpgradeButton;
import slotexpansion.machine.MachineState.SymbolTier;

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
            panel.setSize(400, 540);
            SlotMachine machine = new SlotMachine(panel, 1, new SlotSymbolGenerator(random));
            JPanel otherPanel = new JPanel(null);
            otherPanel.setSize(400, 540);
            SlotMachine otherMachine = new SlotMachine(otherPanel, 2);
            MachineSave saved = MachineSave.parse("100000,1,0,2,1,0,0,48,0,1,-1,4,800,400,true,0,0,2,1,0,0,0,8");
            try {
                machine.restore(saved);
                otherMachine.restore(MachineSave.newGame());
                otherMachine.upgradeArea.unlockButton(SymbolTier.SEVEN).doClick();
                assertEquals(saved, machine.snapshot(saved.balance()));
                CasinoUpgradeButton upgrade = machine.upgradeArea.button(UpgradeArea.Upgrade.SEVEN);
                BigInteger balanceBefore = PlayerManager.getCoins();
                random.awardFreeSpins = true;
                machine.slotGrid.spinSlotMachine(BigInteger.valueOf(5));
                assertTrue(machine.slotGrid.isFreeSpinsActivated());
                assertFalse(upgrade.isEnabled());
                assertFalse(machine.upgradeArea.unlockButton(SymbolTier.TIER2).isEnabled());
                assertTrue(otherMachine.upgradeArea.unlockButton(SymbolTier.TIER2).isEnabled());
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
                machine.upgradeArea.unlockButton(SymbolTier.TIER2).doClick();
                assertEquals(2, machine.slotGrid.getAdditionalSevenSymbolChance());
                assertFalse(machine.state.isUnlocked(SymbolTier.TIER2));
                assertEquals(balanceBefore, PlayerManager.getCoins());

                random.awardFreeSpins = false;
                while (machine.slotGrid.isFreeSpinsActivated()) {
                    machine.slotGrid.spinSlotMachine(BigInteger.valueOf(5));
                }
                assertTrue(upgrade.isEnabled());
                assertTrue(machine.upgradeArea.unlockButton(SymbolTier.TIER2).isEnabled());
                assertTrue(machine.addColumnUpgradeButton.isEnabled());
            } finally {
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
            panel.setSize(400, 540);
            SlotMachine machine = new SlotMachine(panel, 1);
            JPanel secondPanel = new JPanel(null);
            secondPanel.setSize(400, 540);
            SlotMachine restored = new SlotMachine(secondPanel, 1);
            try {
                MachineSave state = MachineSave.parse("100000,1,0,0,0,3,2,0,0,0,-1,3,1000,500,false,0,0,0,0,3,2,0,15");
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
                frame.dispose();
            }
        });
    }

    @Test
    void symbolUnlocksArePerMachineSavedAndResetWithGridExpansion() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            Application.mainFrame = frame;
            PlayerManager.setMainFrame(frame);
            PlayerManager.setCoins(BigInteger.valueOf(10_000));
            JPanel panel = new JPanel(null);
            panel.setSize(400, 540);
            SlotMachine machine = new SlotMachine(panel, 2);
            JPanel otherPanel = new JPanel(null);
            otherPanel.setSize(400, 540);
            SlotMachine restored = new SlotMachine(otherPanel, 2);
            try {
                machine.restore(MachineSave.newGame());
                assertSame(panel, machine.upgradeArea.getParent());
                assertTrue(machine.upgradeArea.getY() >= 320);
                CasinoUpgradeButton unlock = machine.upgradeArea.unlockButton(SymbolTier.TIER2);
                assertEquals(BigInteger.valueOf(600), unlock.getPrice());
                assertFalse(machine.upgradeArea.button(UpgradeArea.Upgrade.TIER2).isEnabled());
                PlayerManager.setCoins(BigInteger.valueOf(599));
                unlock.doClick();
                assertFalse(machine.state.isUnlocked(SymbolTier.TIER2));
                assertEquals(BigInteger.valueOf(599), PlayerManager.getCoins());
                PlayerManager.setCoins(BigInteger.valueOf(10_000));
                unlock.doClick();
                assertEquals(BigInteger.valueOf(9400), PlayerManager.getCoins());
                assertTrue(machine.state.isUnlocked(SymbolTier.TIER2));
                assertFalse(restored.state.isUnlocked(SymbolTier.TIER2));
                assertTrue(machine.upgradeArea.button(UpgradeArea.Upgrade.TIER2).isEnabled());
                assertFalse(unlock.isVisible());

                restored.restore(machine.snapshot(PlayerManager.getCoins()));
                assertTrue(restored.state.isUnlocked(SymbolTier.TIER2));
                assertFalse(restored.upgradeArea.unlockButton(SymbolTier.TIER2).isVisible());
                restored.upgradeArea.upgradeManager.upgradeGrid();
                assertFalse(restored.state.isUnlocked(SymbolTier.TIER2));
                assertTrue(restored.upgradeArea.unlockButton(SymbolTier.TIER2).isVisible());
                assertEquals(BigInteger.valueOf(600), restored.upgradeArea.unlockButton(SymbolTier.TIER2).getPrice());
                assertFalse(restored.upgradeArea.button(UpgradeArea.Upgrade.TIER2).isEnabled());
            } finally {
                frame.dispose();
            }
        });
    }

    @Test
    void paylinePurchaseAppearsInUnlockTabAndStillAddsALine() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            Application.mainFrame = frame;
            PlayerManager.setMainFrame(frame);
            PlayerManager.setCoins(BigInteger.valueOf(1000));
            JPanel panel = new JPanel(null);
            panel.setSize(400, 540);
            SlotMachine machine = new SlotMachine(panel, 1);
            try {
                machine.restore(MachineSave.newGame());
                JTabbedPane tabs = (JTabbedPane) machine.upgradeArea.getComponent(0);
                CasinoUpgradeButton payline = machine.upgradeArea.button(UpgradeArea.Upgrade.PAYLINE);
                assertSame(tabs.getComponentAt(1), payline.getParent());
                assertEquals("Unlock", tabs.getTitleAt(1));
                assertEquals(5, machine.slotGrid.getWinLanes());
                payline.doClick();
                assertEquals(6, machine.slotGrid.getWinLanes());
                assertEquals(BigInteger.valueOf(400), PlayerManager.getCoins());
                assertEquals(BigInteger.valueOf(1200), payline.getPrice());
            } finally {
                frame.dispose();
            }
        });
    }
}
