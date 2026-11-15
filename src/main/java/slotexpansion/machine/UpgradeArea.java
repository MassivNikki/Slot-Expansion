package slotexpansion.machine;

import slotexpansion.machine.MachineState.SymbolTier;
import slotexpansion.ui.CasinoUpgradeButton;
import slotexpansion.ui.CustomTabbedPaneUI;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;

/** Displays the upgrades for one machine; their effects are handled by UpgradeManager. */
public class UpgradeArea extends JPanel {
    enum Upgrade { SEVEN, TIER4, TIER3, TIER2, TIER1, X2, PAYLINE }

    private final Map<Upgrade, CasinoUpgradeButton> buttons = new EnumMap<>(Upgrade.class);
    private final Map<SymbolTier, CasinoUpgradeButton> unlockButtons = new EnumMap<>(SymbolTier.class);
    private final Map<Upgrade, Integer> purchases = new EnumMap<>(Upgrade.class);
    private final SlotGrid grid;
    private final SlotMachine machine;
    final UpgradeManager upgradeManager;
    int addedGrids;
    private final JPanel increasePanel = new JPanel(null);
    private final JPanel unlockPanel = new JPanel(null);
    private final JTabbedPane tabs = new JTabbedPane();
    private int row;
    private boolean purchasesEnabled = true;

    public UpgradeArea(SlotGrid grid, SlotMachine machine) {
        this.grid = grid;
        this.machine = machine;
        upgradeManager = new UpgradeManager(grid, this);
        setLayout(new BorderLayout());
        tabs.setUI(new CustomTabbedPaneUI());
        setBackground(Color.decode("#04295E"));
        increasePanel.setBackground(Color.decode("#04296E"));
        unlockPanel.setBackground(Color.decode("#04296E"));
    }

    /** Creates controls from restored state after paylines and chances are loaded. */
    public void initiate() {
        for (int i = 0; i < addedGrids; i++) {
            grid.addNewWinningGrid(i);
        }
        add(Upgrade.SEVEN, "7 Symbol chance:", 800, grid.getAdditionalSevenSymbolChance(),
                SymbolChanceConfig.BASE_SEVEN_CHANCE, SymbolChanceConfig.MAX_SEVEN_CHANCE,
                upgradeManager::increaseAdditionalSevenSymbolChance);
        add(Upgrade.TIER4, "Tier 4 Symbol chance:", 600, grid.getAdditionalTier4SymbolChance(),
                SymbolChanceConfig.BASE_TIER4_CHANCE, SymbolChanceConfig.MAX_TIER4_CHANCE,
                upgradeManager::increaseAdditionalTier4SymbolChance);
        add(Upgrade.TIER3, "Tier 3 Symbol chance:", 300, grid.getAdditionalTier3SymbolChance(),
                SymbolChanceConfig.BASE_TIER3_CHANCE, SymbolChanceConfig.MAX_TIER3_CHANCE,
                upgradeManager::increaseAdditionalTier3SymbolChance);
        add(Upgrade.TIER2, "Tier 2 Symbol chance:", 150, grid.getAdditionalTier2SymbolChance(),
                SymbolChanceConfig.BASE_TIER2_CHANCE, SymbolChanceConfig.MAX_TIER2_CHANCE,
                upgradeManager::increaseAdditionalTier2SymbolChance);
        add(Upgrade.TIER1, "Tier 1 Symbol chance:", 0, 0,
                grid.getTier1SymbolChance(), 0, null);
        add(Upgrade.X2, "X2 Symbol chance:", 1000, grid.getAdditionalX2SymbolChance(),
                SymbolChanceConfig.BASE_X2_CHANCE, SymbolChanceConfig.MAX_X2_CHANCE,
                upgradeManager::increaseAdditionalX2SymbolChance);
        add(Upgrade.PAYLINE, "New winning grid:", 600, addedGrids,
                grid.getWinLanes() - addedGrids,
                grid.getWinLanes() - addedGrids + grid.getAdditionalPaylineCount(),
                () -> upgradeManager.addNewWinningGrid(addedGrids));
        addUnlock(SymbolTier.TIER2, "Tier 2 symbols:", 150, 0);
        addUnlock(SymbolTier.TIER3, "Tier 3 symbols:", 300, 1);
        addUnlock(SymbolTier.TIER4, "Tier 4 symbols:", 600, 2);
        addUnlock(SymbolTier.SEVEN, "7 symbols:", 800, 3);
        tabs.addTab("Increase", increasePanel);
        tabs.addTab("Unlock", unlockPanel);
        add(tabs, BorderLayout.CENTER);
        updatePurchaseAvailability();
    }

    private void addUnlock(SymbolTier tier, String title, int price, int row) {
        CasinoUpgradeButton button = new CasinoUpgradeButton(title, BigInteger.valueOf(price),
                machine.getSlotTier(), "Buy", 10, 10 + row * 20, unlockPanel);
        unlockButtons.put(tier, button);
        button.addCustomActionListener(ignored -> {
            machine.state.unlock(tier);
            refreshUnlock(tier);
            updatePurchaseAvailability();
        });
        unlockPanel.add(button);
        refreshUnlock(tier);
    }

    private void refreshUnlock(SymbolTier tier) {
        CasinoUpgradeButton button = unlockButtons.get(tier);
        boolean unlocked = machine.state.isUnlocked(tier);
        button.setChanceLabelText(unlocked ? "Owned" : "Locked");
        if (unlocked) {
            button.disableUpgradable();
        } else {
            button.enableUpgradable();
        }
    }

    private void add(Upgrade kind, String title, int basePrice, int purchased,
                     int baseline, int maximum, Runnable action) {
        JPanel panel = kind == Upgrade.PAYLINE ? unlockPanel : increasePanel;
        int position = kind == Upgrade.PAYLINE ? 4 : row++;
        CasinoUpgradeButton button = new CasinoUpgradeButton(title, BigInteger.valueOf(basePrice),
                machine.getSlotTier(), "+1", 10, 10 + position * 20, panel);
        buttons.put(kind, button);
        int priceSteps = kind == Upgrade.PAYLINE ? purchased : purchaseCount(kind);
        for (int i = 0; i < priceSteps; i++) {
            button.increasePrice();
        }
        String suffix = kind == Upgrade.PAYLINE ? "" : "%";
        button.setChanceLabelText(kind == Upgrade.TIER1 ? baseline + "%"
                : (baseline + purchased) + "/" + maximum + suffix);
        if (purchased >= maximum - baseline) {
            button.disableUpgradable();
        }
        if (action != null) {
            button.addCustomActionListener(ignored -> {
                action.run();
                if (kind != Upgrade.PAYLINE) {
                    purchases.merge(kind, 1, Integer::sum);
                }
                updatePurchaseAvailability();
            });
        }
        panel.add(button);
    }

    CasinoUpgradeButton button(Upgrade upgrade) {
        return buttons.get(upgrade);
    }

    CasinoUpgradeButton unlockButton(SymbolTier tier) {
        return unlockButtons.get(tier);
    }

    void setPurchasesEnabled(boolean enabled) {
        purchasesEnabled = enabled;
        tabs.setEnabled(enabled);
        updatePurchaseAvailability();
    }

    int purchaseCount(Upgrade upgrade) {
        return purchases.getOrDefault(upgrade, 0);
    }

    void restorePurchaseCounts(MachineSave state) {
        purchases.put(Upgrade.SEVEN, state.sevenPurchases());
        purchases.put(Upgrade.TIER4, state.tier4Purchases());
        purchases.put(Upgrade.TIER3, state.tier3Purchases());
        purchases.put(Upgrade.TIER2, state.tier2Purchases());
        purchases.put(Upgrade.X2, state.x2Purchases());
    }

    void updatePurchaseAvailability() {
        for (Map.Entry<Upgrade, CasinoUpgradeButton> entry : buttons.entrySet()) {
            Upgrade kind = entry.getKey();
            SymbolTier tier = switch (kind) {
                case SEVEN -> SymbolTier.SEVEN;
                case TIER4 -> SymbolTier.TIER4;
                case TIER3 -> SymbolTier.TIER3;
                case TIER2 -> SymbolTier.TIER2;
                default -> null;
            };
            boolean available = (tier == null || machine.state.isUnlocked(tier))
                    && (kind == Upgrade.PAYLINE || (kind != Upgrade.TIER1 && upgradeManager.canIncrease(kind)));
            entry.getValue().setEnabled(purchasesEnabled && available);
        }
        unlockButtons.forEach((tier, button) ->
                button.setEnabled(purchasesEnabled && !machine.state.isUnlocked(tier)));
    }

    void resetForGridExpansion() {
        addedGrids = 0;
        purchases.clear();
        for (CasinoUpgradeButton button : buttons.values()) {
            button.resetPrice();
            button.enableUpgradable();
        }
        unlockButtons.forEach((tier, button) -> {
            button.resetPrice();
            refreshUnlock(tier);
        });
        button(Upgrade.SEVEN).setChanceLabelText(SymbolChanceConfig.BASE_SEVEN_CHANCE + "/" + SymbolChanceConfig.MAX_SEVEN_CHANCE + "%");
        button(Upgrade.TIER4).setChanceLabelText(SymbolChanceConfig.BASE_TIER4_CHANCE + "/" + SymbolChanceConfig.MAX_TIER4_CHANCE + "%");
        button(Upgrade.TIER3).setChanceLabelText(SymbolChanceConfig.BASE_TIER3_CHANCE + "/" + SymbolChanceConfig.MAX_TIER3_CHANCE + "%");
        button(Upgrade.TIER2).setChanceLabelText(SymbolChanceConfig.BASE_TIER2_CHANCE + "/" + SymbolChanceConfig.MAX_TIER2_CHANCE + "%");
        button(Upgrade.TIER1).setChanceLabelText(grid.getTier1SymbolChance() + "%");
        button(Upgrade.X2).setChanceLabelText(SymbolChanceConfig.BASE_X2_CHANCE + "/" + SymbolChanceConfig.MAX_X2_CHANCE + "%");
        updatePaylineLabel();
        updatePurchaseAvailability();
    }

    void updatePaylineLabel() {
        int base = grid.getWinLanes() - addedGrids;
        button(Upgrade.PAYLINE).setChanceLabelText(grid.getWinLanes() + "/"
                + (base + grid.getAdditionalPaylineCount()));
        if (addedGrids == grid.getAdditionalPaylineCount()) {
            button(Upgrade.PAYLINE).disableUpgradable();
        }
    }
}
