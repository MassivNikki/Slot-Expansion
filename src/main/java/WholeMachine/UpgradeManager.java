package WholeMachine;

import CasinoStuff.Application;
import CasinoStuff.CasinoUpgradeButton;

/**
 * Applies purchases made in an {@link UpgradeArea} to its machine's slot grid.
 */
public class UpgradeManager {

    private final UpgradeArea upgradeArea;
    private final SlotGrid slotGrid;

    public UpgradeManager(SlotGrid slotGrid, UpgradeArea upgradeArea) {
        this.slotGrid = slotGrid;
        this.upgradeArea = upgradeArea;
    }

    private void updateTierLabel() {
        // Higher-tier symbol chances are taken from lower-tier bands to keep the total fixed.
        if (slotGrid.getTier1SymbolChance() == 0) {
            if (slotGrid.getAdditionalTier2SymbolChance() == -20) {
                if (slotGrid.getAdditionalTier3SymbolChance() == -15) {
                    if (slotGrid.getAdditionalTier4SymbolChance() > -10) {
                        slotGrid.setAdditionalTier4SymbolChance(slotGrid.getAdditionalTier4SymbolChance() - 1);
                        upgradeArea.upgradeIncreaseButtons.get(1).setChanceLabelText(slotGrid.getAdditionalTier4SymbolChance() + upgradeArea.symbolOrgChances[1] + "%");
                    }
                } else {
                    slotGrid.setAdditionalTier3SymbolChance(slotGrid.getAdditionalTier3SymbolChance() - 1);
                    upgradeArea.upgradeIncreaseButtons.get(2).setChanceLabelText(slotGrid.getAdditionalTier3SymbolChance() + upgradeArea.symbolOrgChances[2] + "%");
                }
            } else {
                slotGrid.setAdditionalTier2SymbolChance(slotGrid.getAdditionalTier2SymbolChance() - 1);
                upgradeArea.upgradeIncreaseButtons.get(3).setChanceLabelText(slotGrid.getAdditionalTier2SymbolChance() + upgradeArea.symbolOrgChances[3] + "%");
            }

        } else {
            slotGrid.setTier1SymbolChance(slotGrid.getTier1SymbolChance() - 1);
            upgradeArea.upgradeIncreaseButtons.get(4).setChanceLabelText(slotGrid.getTier1SymbolChance() + "%");
        }
    }

    /** Advances the grid size and resets the chance upgrades for the new grid. */
    public void upgradeGrid() {
        slotGrid.upgradeGrid();
        resetAllUpgrades();
    }

    private void resetAllUpgrades() {
        for (int i = 0; i < upgradeArea.upgradeIncreaseButtons.size()-1; i++) {
            upgradeArea.upgradeIncreaseButtons.get(i).setChanceLabelText(upgradeArea.symbolOrgChances[i]+"%");
        }
        upgradeArea.upgradeIncreaseButtons.getLast().setChanceLabelText(slotGrid.getWinningGrids().size() + "/" + (slotGrid.getWinningGrids().size() + slotGrid.getCurrentAdditionalWinningGrid().size()));
        upgradeArea.addedGrids = 0;
        enableAllLabelsAndButtons();
    }

    private void enableAllLabelsAndButtons() {
        upgradeArea.upgradeIncreaseButtons.forEach(CasinoUpgradeButton::enableUpgradable);
    }

    /** Activates a purchased optional payline and updates its remaining count. */
    public void addNewWinningGrid(int grid) {
        String labelText = upgradeArea.upgradeIncreaseButtons.get(6).getChanceLabelText();
        int delimiterIndex = labelText.indexOf('/');
        upgradeArea.addedGrids++;
        slotGrid.addNewWinningGrid(grid);
        upgradeArea.upgradeIncreaseButtons.get(6).setChanceLabelText(
                slotGrid.getWinningGrids().size() + labelText.substring(delimiterIndex));
        if (upgradeArea.addedGrids == slotGrid.getCurrentAdditionalWinningGrid().size()) {
            upgradeArea.upgradeIncreaseButtons.get(6).disableUpgradable();
        }
    }

    private void increaseChance(int chance, CasinoUpgradeButton chanceButton, int symbol, int originalChance, int maxChance) {
        chance++;
        switch (symbol) {
            case 7:
                slotGrid.setAdditionalSevenSymbolChance(chance);
                break;
            case 4:
                slotGrid.setAdditionalTier4SymbolChance(chance);
                break;
            case 3:
                slotGrid.setAdditionalTier3SymbolChance(chance);
                break;
            case 2:
                slotGrid.setAdditionalTier2SymbolChance(chance);
                break;
            case 1:
                slotGrid.setAdditionalX2SymbolChance(chance);
                break;
        }
        chanceButton.setChanceLabelText(chance + originalChance + "/" + maxChance + "%");
        updateTierLabel();
        if (chance >= maxChance - originalChance) {
            chanceButton.disableUpgradable();
        }
    }

    /** Increases the chance of rolling a seven symbol by one step. */
    public void increaseAdditionalSevenSymbolChance() {
        increaseChance(slotGrid.getAdditionalSevenSymbolChance(),
                upgradeArea.upgradeIncreaseButtons.getFirst(), 7, upgradeArea.symbolOrgChances[0], Application.maxSevenChance);
    }

    /** Increases the chance of rolling a tier-four symbol by one step. */
    public void increaseAdditionalTier4SymbolChance() {
        increaseChance(slotGrid.getAdditionalTier4SymbolChance(),
                upgradeArea.upgradeIncreaseButtons.get(1), 4, upgradeArea.symbolOrgChances[1], Application.maxTier4Chance);
    }

    /** Increases the chance of rolling a tier-three symbol by one step. */
    public void increaseAdditionalTier3SymbolChance() {
        increaseChance(slotGrid.getAdditionalTier3SymbolChance(),
                upgradeArea.upgradeIncreaseButtons.get(2), 3, upgradeArea.symbolOrgChances[2], Application.maxTier3Chance);
    }

    /** Increases the chance of rolling a tier-two symbol by one step. */
    public void increaseAdditionalTier2SymbolChance() {
        increaseChance(slotGrid.getAdditionalTier2SymbolChance(),
                upgradeArea.upgradeIncreaseButtons.get(3), 2, upgradeArea.symbolOrgChances[3], Application.maxTier2Chance);
    }

    /** Increases the chance of rolling an x2 symbol by one step. */
    public void increaseAdditionalX2SymbolChance() {
        increaseChance(slotGrid.getAdditionalX2SymbolChance(),
                upgradeArea.upgradeIncreaseButtons.get(5), 1, upgradeArea.symbolOrgChances[5], Application.maxX2Chance);
    }

}
