package WholeMachine;

import CasinoStuff.Application;
import CasinoStuff.CasinoUpgradeButton;
import CasinoStuff.PlayerManager;
import CasinoStuff.PriceLabel;

import javax.swing.*;

public class UpgradeManager {

    private final UpgradeArea upgradeArea;
    private final SlotGrid slotGrid;

    public UpgradeManager(SlotGrid slotGrid, UpgradeArea upgradeArea) {
        this.slotGrid = slotGrid;
        this.upgradeArea = upgradeArea;
    }

    private void updateTierLabel() {
        if (slotGrid.getTier1SymbolChance() == 0) {
            if (slotGrid.getAdditionalTier2SymbolChance() == -20) {
                if (slotGrid.getAdditionalTier3SymbolChance() == -15) {
                    if (slotGrid.getAdditionalTier4SymbolChance() > -10) {
                        slotGrid.setAdditionalTier4SymbolChance(slotGrid.getAdditionalTier4SymbolChance() - 1);
                        upgradeArea.upgradeButtons.get(1).setChanceLabelText(slotGrid.getAdditionalTier4SymbolChance() + upgradeArea.buttonChances[1] + "%");
                    }
                } else {
                    slotGrid.setAdditionalTier3SymbolChance(slotGrid.getAdditionalTier3SymbolChance() - 1);
                    upgradeArea.upgradeButtons.get(2).setChanceLabelText(slotGrid.getAdditionalTier3SymbolChance() + upgradeArea.buttonChances[2] + "%");
                }
            } else {
                slotGrid.setAdditionalTier2SymbolChance(slotGrid.getAdditionalTier2SymbolChance() - 1);
                upgradeArea.upgradeButtons.get(3).setChanceLabelText(slotGrid.getAdditionalTier2SymbolChance() + upgradeArea.buttonChances[3] + "%");
            }

        } else {
            slotGrid.setTier1SymbolChance(slotGrid.getTier1SymbolChance() - 1);
            upgradeArea.upgradeButtons.get(4).setChanceLabelText(slotGrid.getTier1SymbolChance() + "%");
        }
    }

    public void upgradeGrid() {
        slotGrid.upgradeGrid();
        resetAllUpgrades();
    }

    private void resetAllUpgrades() {
        for (int i = 0; i < upgradeArea.upgradeButtons.size()-1; i++) {
            upgradeArea.upgradeButtons.get(i).setChanceLabelText(upgradeArea.buttonChances[i]+"%");
        }
        upgradeArea.upgradeButtons.getLast().setChanceLabelText(slotGrid.getWinningGrids().size() + "/" + (slotGrid.getWinningGrids().size() + slotGrid.getCurrentAdditionalWinningGrid().size()));
        upgradeArea.addedGrids = 0;
        enableAllLabelsAndButtons();
    }

    private void enableAllLabelsAndButtons() {
        upgradeArea.upgradeButtons.forEach(CasinoUpgradeButton::enableUpgradable);
    }

    public void addNewWinningGrid(int grid) {
        if (PlayerManager.getCoins().compareTo(upgradeArea.upgradeButtons.get(6).getPrice()) > 0) {
            String labelText = upgradeArea.upgradeButtons.get(6).getChanceLabelText();
            int delimiterIndex = labelText.indexOf('/');
            PlayerManager.decreaseCoins(upgradeArea.upgradeButtons.get(6).getPrice());
            upgradeArea.upgradeButtons.get(6).increasePrice();
            upgradeArea.addedGrids++;
            slotGrid.addNewWinningGrid(grid);
            upgradeArea.upgradeButtons.get(6).setChanceLabelText(slotGrid.getWinningGrids().size() + labelText.substring(delimiterIndex));
            if (upgradeArea.addedGrids == slotGrid.getCurrentAdditionalWinningGrid().size()) {
                upgradeArea.upgradeButtons.get(6).disableUpgradable();
            }
        }
    }

    private void increaseChance(int chance,CasinoUpgradeButton chanceButton, int symbol, int originalChance, int maxChance) {
        //System.out.println(symbol + " chance got upgraded!");
        if (chanceButton.getPrice().compareTo(PlayerManager.getCoins()) <= 0) {
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
            updateTierLabel();
            chanceButton.setChanceLabelText(chance + originalChance + "/" + maxChance + "%");
            PlayerManager.decreaseCoins(chanceButton.getPrice());
            if (chance >= maxChance - originalChance) {
                chanceButton.disableUpgradable();
//                chanceButton.setVisible(false);
//                priceLabel.setVisible(false);
            }
            chanceButton.increasePrice();
            //priceLabel.increasePrice();
        }
    }

    public void increaseAdditionalSevenSymbolChance() {
         increaseChance( slotGrid.getAdditionalSevenSymbolChance(),
                upgradeArea.upgradeButtons.getFirst(), 7, 3, Application.maxSevenChance);
    }

    public void increaseAdditionalTier4SymbolChance() {
         increaseChance(slotGrid.getAdditionalTier4SymbolChance(),
                 upgradeArea.upgradeButtons.get(1), 4, 10, Application.maxTier4Chance);

    }

    public void increaseAdditionalTier3SymbolChance() {
         increaseChance( slotGrid.getAdditionalTier3SymbolChance(),
                 upgradeArea.upgradeButtons.get(2), 3, 15, Application.maxTier3Chance);
    }

    public void increaseAdditionalTier2SymbolChance() {
         increaseChance( slotGrid.getAdditionalTier2SymbolChance(),
                 upgradeArea.upgradeButtons.get(3), 2, 20, Application.maxTier2Chance);

    }

    public void increaseAdditionalX2SymbolChance() {
         increaseChance( slotGrid.getAdditionalX2SymbolChance(),
                 upgradeArea.upgradeButtons.get(5), 1, 2, Application.maxX2Chance);
    }

}
