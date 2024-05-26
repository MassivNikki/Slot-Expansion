package WholeMachine;

import CasinoStuff.Application;
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
                        upgradeArea.additionalTier4SymbolChanceLabel.setText(slotGrid.getAdditionalTier4SymbolChance() + 10 + "%");
                    }
                } else {
                    slotGrid.setAdditionalTier3SymbolChance(slotGrid.getAdditionalTier3SymbolChance() - 1);
                    upgradeArea.additionalTier3SymbolChanceLabel.setText(slotGrid.getAdditionalTier3SymbolChance() + 15 + "%");
                }
            } else {
                slotGrid.setAdditionalTier2SymbolChance(slotGrid.getAdditionalTier2SymbolChance() - 1);
                upgradeArea.additionalTier2SymbolChanceLabel.setText(slotGrid.getAdditionalTier2SymbolChance() + 20 + "%");
            }

        } else {
            slotGrid.setTier1SymbolChance(slotGrid.getTier1SymbolChance() - 1);
            upgradeArea.additionalTier1SymbolChanceLabel.setText(slotGrid.getTier1SymbolChance() + "%");
        }
    }

    public void upgradeGrid() {
        slotGrid.upgradeGrid();
        resetAllUpgrades();
    }

    private void resetAllUpgrades() {
        upgradeArea.additionalTier1SymbolChanceLabel.setText(46 + "%");
        upgradeArea.additionalTier2SymbolChanceLabel.setText(20 + "%");
        upgradeArea.additionalTier3SymbolChanceLabel.setText(15 + "%");
        upgradeArea.additionalTier4SymbolChanceLabel.setText(10 + "%");
        upgradeArea.additionalSevenSymbolChanceLabel.setText(3 + "%");
        upgradeArea.additionalX2SymbolChanceLabel.setText(2 + "%");
        upgradeArea.additionalGridsLabel.setText(slotGrid.getWinningGrids().size() + "/" + (slotGrid.getWinningGrids().size() + slotGrid.getCurrentAdditionalWinningGrid().size()));
        upgradeArea.addedGrids = 0;
        enableAllLabelsAndButtons();
    }

    private void enableAllLabelsAndButtons() {
        upgradeArea.additionalSevenSymbolChancePriceLabel.setVisible(true);
        upgradeArea.additionalX2SymbolChancePriceLabel.setVisible(true);
        upgradeArea.additionalTier4SymbolChancePriceLabel.setVisible(true);
        upgradeArea.additionalTier3SymbolChancePriceLabel.setVisible(true);
        upgradeArea.additionalTier2SymbolChancePriceLabel.setVisible(true);
        upgradeArea.additionalGridsPriceLabel.setVisible(true);

        upgradeArea.additionalSevenSymbolChanceBtt.setVisible(true);
        upgradeArea.additionalX2SymbolChanceBtt.setVisible(true);
        upgradeArea.additionalTier3SymbolChanceBtt.setVisible(true);
        upgradeArea.additionalTier2SymbolChanceBtt.setVisible(true);
        upgradeArea.additionalTier4SymbolChanceBtt.setVisible(true);
        upgradeArea.additionalWinningGridsBtt.setVisible(true);
    }

    public void addNewWinningGrid(int grid) {
        if (PlayerManager.getCoins().compareTo(upgradeArea.additionalGridsPriceLabel.getPrice()) > 0) {
            String labelText = upgradeArea.additionalGridsLabel.getText();
            int delimiterIndex = labelText.indexOf('/');
            PlayerManager.decreaseCoins(upgradeArea.additionalGridsPriceLabel.getPrice());
            upgradeArea.additionalGridsPriceLabel.increasePrice();
            upgradeArea.addedGrids++;
            slotGrid.addNewWinningGrid(grid);
            upgradeArea.additionalGridsLabel.setText(slotGrid.getWinningGrids().size() + labelText.substring(delimiterIndex));
            if (upgradeArea.addedGrids == slotGrid.getCurrentAdditionalWinningGrid().size()) {
                upgradeArea.additionalWinningGridsBtt.setVisible(false);
                upgradeArea.additionalGridsPriceLabel.setVisible(false);
            }
        }
    }

    private void increaseChance(PriceLabel priceLabel, int chance, JLabel chanceLabel, JButton chanceButton, int symbol, int originalChance, int maxChance) {
        //System.out.println(symbol + " chance got upgraded!");
        if (priceLabel.getPrice().compareTo(PlayerManager.getCoins()) <= 0) {
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
            chanceLabel.setText(chance + originalChance + "/" + maxChance + "%");
            PlayerManager.decreaseCoins(priceLabel.getPrice());
            if (chance >= maxChance - originalChance) {
                chanceButton.setVisible(false);
                priceLabel.setVisible(false);
            }
            priceLabel.increasePrice();
        }
    }

    public void increaseAdditionalSevenSymbolChance() {
         increaseChance(upgradeArea.additionalSevenSymbolChancePriceLabel, slotGrid.getAdditionalSevenSymbolChance(),
                upgradeArea.additionalSevenSymbolChanceLabel, upgradeArea.additionalSevenSymbolChanceBtt, 7, 3, Application.maxSevenChance);
    }

    public void increaseAdditionalTier4SymbolChance() {
         increaseChance(upgradeArea.additionalTier4SymbolChancePriceLabel, slotGrid.getAdditionalTier4SymbolChance(),
                upgradeArea.additionalTier4SymbolChanceLabel, upgradeArea.additionalTier4SymbolChanceBtt, 4, 10, Application.maxTier4Chance);

    }

    public void increaseAdditionalTier3SymbolChance() {
         increaseChance(upgradeArea.additionalTier3SymbolChancePriceLabel, slotGrid.getAdditionalTier3SymbolChance(),
                upgradeArea.additionalTier3SymbolChanceLabel, upgradeArea.additionalTier3SymbolChanceBtt, 3, 15, Application.maxTier3Chance);
    }

    public void increaseAdditionalTier2SymbolChance() {
         increaseChance(upgradeArea.additionalTier2SymbolChancePriceLabel, slotGrid.getAdditionalTier2SymbolChance(),
                upgradeArea.additionalTier2SymbolChanceLabel, upgradeArea.additionalTier2SymbolChanceBtt, 2, 20, Application.maxTier2Chance);

    }

    public void increaseAdditionalX2SymbolChance() {
         increaseChance(upgradeArea.additionalX2SymbolChancePriceLabel, slotGrid.getAdditionalX2SymbolChance(),
                upgradeArea.additionalX2SymbolChanceLabel, upgradeArea.additionalX2SymbolChanceBtt, 1, 2, Application.maxX2Chance);
    }

}
