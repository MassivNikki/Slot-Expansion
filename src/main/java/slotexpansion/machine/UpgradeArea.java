package slotexpansion.machine;

import slotexpansion.app.Application;
import slotexpansion.ui.CasinoUpgradeButton;
import slotexpansion.ui.CustomTabbedPaneUI;

import javax.swing.*;
import java.awt.Color;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;

/** Displays the upgrades for one machine; their effects are handled by UpgradeManager. */
public class UpgradeArea extends JFrame {
    enum Upgrade { SEVEN, TIER4, TIER3, TIER2, TIER1, X2, PAYLINE }

    private final Map<Upgrade, CasinoUpgradeButton> buttons = new EnumMap<>(Upgrade.class);
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
        setSize(350, 300);
        setLocation(Application.mainFrame.getX() + 35,
                Application.mainFrame.getY() + machine.machinePanel.getHeight() / 2);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setResizable(false);
        tabs.setUI(new CustomTabbedPaneUI());
        getContentPane().setBackground(Color.darkGray);
        increasePanel.setBackground(Color.darkGray);
        unlockPanel.setBackground(Color.darkGray);
        setTitle("Upgrades for machine " + machine.getSlotTier());
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
        tabs.addTab("Increase", increasePanel);
        tabs.addTab("Unlock", unlockPanel);
        getContentPane().add(tabs);
        updatePurchaseAvailability();
    }

    private void add(Upgrade kind, String title, int basePrice, int purchased,
                     int baseline, int maximum, Runnable action) {
        CasinoUpgradeButton button = new CasinoUpgradeButton(title, BigInteger.valueOf(basePrice),
                machine.getSlotTier(), "+1", 10, 10 + row++ * 20, increasePanel);
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
        increasePanel.add(button);
    }

    CasinoUpgradeButton button(Upgrade upgrade) {
        return buttons.get(upgrade);
    }

    void setPurchasesEnabled(boolean enabled) {
        purchasesEnabled = enabled;
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
            boolean available = kind == Upgrade.PAYLINE || (kind != Upgrade.TIER1 && upgradeManager.canIncrease(kind));
            entry.getValue().setEnabled(purchasesEnabled && available);
        }
    }

    void resetForGridExpansion() {
        addedGrids = 0;
        purchases.clear();
        for (CasinoUpgradeButton button : buttons.values()) {
            button.resetPrice();
            button.enableUpgradable();
        }
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
