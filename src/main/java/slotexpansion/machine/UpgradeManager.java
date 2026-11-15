package slotexpansion.machine;

import slotexpansion.machine.UpgradeArea.Upgrade;
import slotexpansion.ui.CasinoUpgradeButton;

/** Applies upgrade effects and updates the corresponding controls. */
public class UpgradeManager {
    private final UpgradeArea area;
    private final SlotGrid grid;

    public UpgradeManager(SlotGrid grid, UpgradeArea area) {
        this.grid = grid;
        this.area = area;
    }

    /** A higher chance replaces one point of the lowest available symbol band. */
    private void takeFromLowerBand(Upgrade upgraded) {
        if (grid.getTier1SymbolChance() > 0) {
            grid.setTier1SymbolChance(grid.getTier1SymbolChance() - 1);
            area.button(Upgrade.TIER1).setChanceLabelText(grid.getTier1SymbolChance() + "%");
        } else if (upgraded != Upgrade.TIER2
                && grid.getAdditionalTier2SymbolChance() > -SymbolChanceConfig.BASE_TIER2_CHANCE) {
            grid.setAdditionalTier2SymbolChance(grid.getAdditionalTier2SymbolChance() - 1);
            updateLabel(Upgrade.TIER2, grid.getAdditionalTier2SymbolChance(),
                    SymbolChanceConfig.BASE_TIER2_CHANCE, SymbolChanceConfig.MAX_TIER2_CHANCE);
        } else if (upgraded != Upgrade.TIER2 && upgraded != Upgrade.TIER3
                && grid.getAdditionalTier3SymbolChance() > -SymbolChanceConfig.BASE_TIER3_CHANCE) {
            grid.setAdditionalTier3SymbolChance(grid.getAdditionalTier3SymbolChance() - 1);
            updateLabel(Upgrade.TIER3, grid.getAdditionalTier3SymbolChance(),
                    SymbolChanceConfig.BASE_TIER3_CHANCE, SymbolChanceConfig.MAX_TIER3_CHANCE);
        } else if ((upgraded == Upgrade.SEVEN || upgraded == Upgrade.X2)
                && grid.getAdditionalTier4SymbolChance() > -SymbolChanceConfig.BASE_TIER4_CHANCE) {
            grid.setAdditionalTier4SymbolChance(grid.getAdditionalTier4SymbolChance() - 1);
            updateLabel(Upgrade.TIER4, grid.getAdditionalTier4SymbolChance(),
                    SymbolChanceConfig.BASE_TIER4_CHANCE, SymbolChanceConfig.MAX_TIER4_CHANCE);
        } else {
            throw new IllegalStateException("No symbol probability remains to upgrade");
        }

    }

    boolean canIncrease(Upgrade upgraded) {
        if (grid.getTier1SymbolChance() > 0) return true;
        if (upgraded != Upgrade.TIER2
                && grid.getAdditionalTier2SymbolChance() > -SymbolChanceConfig.BASE_TIER2_CHANCE) return true;
        if (upgraded != Upgrade.TIER2 && upgraded != Upgrade.TIER3
                && grid.getAdditionalTier3SymbolChance() > -SymbolChanceConfig.BASE_TIER3_CHANCE) return true;
        return (upgraded == Upgrade.SEVEN || upgraded == Upgrade.X2)
                && grid.getAdditionalTier4SymbolChance() > -SymbolChanceConfig.BASE_TIER4_CHANCE;
    }

    public void upgradeGrid() {
        grid.upgradeGrid();
        area.resetForGridExpansion();
    }

    public void addNewWinningGrid(int index) {
        grid.addNewWinningGrid(index);
        area.addedGrids++;
        area.updatePaylineLabel();
    }

    private void updateLabel(Upgrade kind, int additional, int baseline, int maximum) {
        CasinoUpgradeButton button = area.button(kind);
        button.setChanceLabelText((additional + baseline) + "/" + maximum + "%");
        if (additional >= maximum - baseline) {
            button.disableUpgradable();
        } else {
            button.enableUpgradable();
        }
    }

    public void increaseAdditionalSevenSymbolChance() {
        grid.setAdditionalSevenSymbolChance(grid.getAdditionalSevenSymbolChance() + 1);
        takeFromLowerBand(Upgrade.SEVEN);
        updateLabel(Upgrade.SEVEN, grid.getAdditionalSevenSymbolChance(),
                SymbolChanceConfig.BASE_SEVEN_CHANCE, SymbolChanceConfig.MAX_SEVEN_CHANCE);
    }

    public void increaseAdditionalTier4SymbolChance() {
        grid.setAdditionalTier4SymbolChance(grid.getAdditionalTier4SymbolChance() + 1);
        takeFromLowerBand(Upgrade.TIER4);
        updateLabel(Upgrade.TIER4, grid.getAdditionalTier4SymbolChance(),
                SymbolChanceConfig.BASE_TIER4_CHANCE, SymbolChanceConfig.MAX_TIER4_CHANCE);
    }

    public void increaseAdditionalTier3SymbolChance() {
        grid.setAdditionalTier3SymbolChance(grid.getAdditionalTier3SymbolChance() + 1);
        takeFromLowerBand(Upgrade.TIER3);
        updateLabel(Upgrade.TIER3, grid.getAdditionalTier3SymbolChance(),
                SymbolChanceConfig.BASE_TIER3_CHANCE, SymbolChanceConfig.MAX_TIER3_CHANCE);
    }

    public void increaseAdditionalTier2SymbolChance() {
        grid.setAdditionalTier2SymbolChance(grid.getAdditionalTier2SymbolChance() + 1);
        takeFromLowerBand(Upgrade.TIER2);
        updateLabel(Upgrade.TIER2, grid.getAdditionalTier2SymbolChance(),
                SymbolChanceConfig.BASE_TIER2_CHANCE, SymbolChanceConfig.MAX_TIER2_CHANCE);
    }

    public void increaseAdditionalX2SymbolChance() {
        grid.setAdditionalX2SymbolChance(grid.getAdditionalX2SymbolChance() + 1);
        takeFromLowerBand(Upgrade.X2);
        updateLabel(Upgrade.X2, grid.getAdditionalX2SymbolChance(),
                SymbolChanceConfig.BASE_X2_CHANCE, SymbolChanceConfig.MAX_X2_CHANCE);
    }
}
