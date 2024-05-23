package WholeMachine;

import CasinoStuff.Application;
import CasinoStuff.CasinoButton;
import CasinoStuff.PlayerManager;

import javax.swing.*;
import java.awt.*;

public class UpgradeArea {

    protected CasinoButton additionalSevenSymbolChanceBtt = new CasinoButton();
    protected JLabel additionalSevenSymbolChancePriceLabel = new JLabel();
    protected JLabel additionalSevenSymbolChanceLabel = new JLabel();
    protected CasinoButton additionalTier4SymbolChanceBtt = new CasinoButton();
    protected JLabel additionalTier4SymbolChanceLabel = new JLabel();
    protected JLabel additionalTier4SymbolChancePriceLabel = new JLabel();
    protected CasinoButton additionalTier3SymbolChanceBtt = new CasinoButton();
    protected JLabel additionalTier3SymbolChanceLabel = new JLabel();
    protected JLabel additionalTier3SymbolChancePriceLabel = new JLabel();
    protected CasinoButton additionalTier2SymbolChanceBtt = new CasinoButton();
    protected JLabel additionalTier2SymbolChanceLabel = new JLabel();
    protected JLabel additionalTier2SymbolChancePriceLabel = new JLabel();
    protected JLabel additionalTier1SymbolChanceLabel = new JLabel();
    protected CasinoButton additionalX2SymbolChanceBtt = new CasinoButton();
    protected JLabel additionalX2SymbolChanceLabel = new JLabel();
    protected JLabel additionalX2SymbolChancePriceLabel = new JLabel();
    protected int addedGrids = 0;
    protected JLabel additionalGridsPriceLabel = new JLabel();
    protected JLabel additionalGridsLabel = new JLabel();
    protected CasinoButton additionalWinningGridsBtt = new CasinoButton();
    private final JFrame mainFrame;
    private final SlotGrid slotGrid;
    private final UpgradeManager upgradeManager;
    private final int startYCord;
    private final int startXCord;
    private int totalUpgradeAmount = 0;

    public UpgradeArea(JFrame mainFrame, SlotGrid slotGrid, int startXCord, int startYCord) {
        this.mainFrame = mainFrame;
        this.slotGrid = slotGrid;
        this.startXCord = startXCord;
        this.startYCord = startYCord;
        upgradeManager = new UpgradeManager(this.slotGrid, this);
        generateStaticLabels();
    }

    public int getAddedGrids() {
        return addedGrids;
    }

    public void setAddedGrids(int addedGrids) {
        this.addedGrids = addedGrids;
    }

    private void generateStaticLabels() {
        JLabel upgradeInfo = new JLabel("Upgrades");
        upgradeInfo.setForeground(Color.white);
        upgradeInfo.setBounds(startXCord, startYCord + 220, 300, 20);
        upgradeInfo.setFont(new Font("Arial", Font.BOLD, 17));
        mainFrame.add(upgradeInfo);
    }

    public void initiate() {
        generateAddRowButtons();
        for (int i = 0; i < addedGrids; i++) {
            slotGrid.addNewWinningGrid(i);
        }
        addNewUpgrade("7 Symbol chance:", additionalSevenSymbolChancePriceLabel
                , additionalSevenSymbolChanceBtt, additionalSevenSymbolChanceLabel
                , slotGrid.getAdditionalSevenSymbolChance(), 3, Application.maxSevenChance, 7, 20000, "%");
        addNewUpgrade("Tier 4 Symbol chance:", additionalTier4SymbolChancePriceLabel
                , additionalTier4SymbolChanceBtt, additionalTier4SymbolChanceLabel
                , slotGrid.getAdditionalTier4SymbolChance(), 10, Application.maxTier4Chance, 4, 10000, "%");
        addNewUpgrade("Tier 3 Symbol chance:", additionalTier3SymbolChancePriceLabel
                , additionalTier3SymbolChanceBtt, additionalTier3SymbolChanceLabel
                , slotGrid.getAdditionalTier3SymbolChance(), 15, Application.maxTier3Chance, 3, 5000, "%");
        addNewUpgrade("Tier 2 Symbol chance:", additionalTier2SymbolChancePriceLabel
                , additionalTier2SymbolChanceBtt, additionalTier2SymbolChanceLabel
                , slotGrid.getAdditionalTier2SymbolChance(), 20, Application.maxTier2Chance, 2, 1000, "%");
        addNewUpgrade("Tier 1 Symbol chance:", null
                , null, additionalTier1SymbolChanceLabel
                , slotGrid.getAdditionalTier2SymbolChance(), 46, 0, -1, 0, "%");
        addNewUpgrade("X2 Symbol chance:", additionalX2SymbolChancePriceLabel
                , additionalX2SymbolChanceBtt, additionalX2SymbolChanceLabel
                , slotGrid.getAdditionalX2SymbolChance(), 2, Application.maxX2Chance, 1, 20000, "%");
        addNewUpgrade("New winning grid:", additionalGridsPriceLabel
                , additionalWinningGridsBtt, additionalGridsLabel
                , slotGrid.getWinningGrids().size(), 0, slotGrid.getWinningGrids().size() - addedGrids + slotGrid.getCurrentAdditionalWinningGrid().size(), 0, 2000, "");

    }

    private void addNewUpgrade(String upgradeInfo, JLabel priceLabel, JButton increaseBtt, JLabel chanceLabel, int symbolAdditionalChance, int originalChance, int maxChance, int winSymbol, int price, String kind) {

        JLabel upgradeInfoLabel = new JLabel(upgradeInfo);
        upgradeInfoLabel.setForeground(Color.white);
        upgradeInfoLabel.setBounds(startXCord, startYCord + 240 + totalUpgradeAmount * 20, 200, 20);
        mainFrame.add(upgradeInfoLabel);
        if (priceLabel != null) {
            priceLabel.setText(String.valueOf(price * (symbolAdditionalChance > 0 ? (int)Math.pow(2,symbolAdditionalChance) : 1)));
            priceLabel.setForeground(Color.YELLOW);
            priceLabel.setBounds(startXCord + 220, startYCord + 240 + totalUpgradeAmount * 20, 50, 20);
            mainFrame.add(priceLabel);
        }
        chanceLabel.setText(symbolAdditionalChance + originalChance + "/" + maxChance + kind);
        chanceLabel.setForeground(Color.white);
        chanceLabel.setBounds(startXCord + 140, startYCord + 240 + totalUpgradeAmount * 20, 40, 20);
        mainFrame.add(chanceLabel);
        if (increaseBtt != null) {
            increaseBtt.setText("+1");
            increaseBtt.setMargin(new Insets(0, 0, 0, 0));
            increaseBtt.setBounds(startXCord + 180, startYCord + 240 + totalUpgradeAmount * 20, 30, 20);
            if (symbolAdditionalChance == maxChance - originalChance) {
                assert priceLabel != null;
                priceLabel.setVisible(false);
                increaseBtt.setVisible(false);
            }
            determineUpgradeMethod(increaseBtt, winSymbol);
            mainFrame.add(increaseBtt);
        }
        totalUpgradeAmount++;
    }

    private void determineUpgradeMethod(JButton btt, int symbol) {
        switch (symbol) {
            case 7:
                btt.addActionListener(e -> {
                    upgradeManager.increaseAdditionalSevenSymbolChance();
                });
                break;
            case 4:
                btt.addActionListener(e -> {
                    upgradeManager.increaseAdditionalTier4SymbolChance();
                });
                break;
            case 3:
                btt.addActionListener(e -> {
                    upgradeManager.increaseAdditionalTier3SymbolChance();
                });
                break;
            case 2:
                btt.addActionListener(e -> {
                    upgradeManager.increaseAdditionalTier2SymbolChance();
                });
                break;
            case 1:
                btt.addActionListener(e -> {
                    upgradeManager.increaseAdditionalX2SymbolChance();
                });
                break;
            case 0:
                btt.addActionListener(e -> {
                   upgradeManager.addNewWinningGrid(addedGrids);

                });
                break;
        }
    }

    private void generateAddRowButtons() {

        JLabel priceLabel = new JLabel("1000000");
        priceLabel.setForeground(Color.YELLOW);
        priceLabel.setBounds(startXCord + 50 * slotGrid.getRowAmount()+50, startYCord + 70, 60, 20);
        mainFrame.add(priceLabel);

        CasinoButton newSlotRowBtt = new CasinoButton("+1 Row");
        newSlotRowBtt.setMargin(new Insets(0, 0, 0, 0));
        newSlotRowBtt.setBounds(startXCord + 50 * slotGrid.getRowAmount(), startYCord + 60, 50, 150);
        newSlotRowBtt.addActionListener(e -> {
            if (slotGrid.getRowAmount() < 5) {
                if(PlayerManager.getCoins() >= Integer.parseInt(priceLabel.getText())){
                    upgradeManager.upgradeGrid();
                    if (slotGrid.getRowAmount() == 5) {
                        newSlotRowBtt.setVisible(false);
                        priceLabel.setVisible(false);
                    } else {
                        priceLabel.setText(String.valueOf(Integer.parseInt(priceLabel.getText())+10000));
                        newSlotRowBtt.setBounds(startXCord + 50 * slotGrid.getRowAmount(), startYCord + 60, 50, 150);
                        priceLabel.setBounds(startXCord + 50 * slotGrid.getRowAmount()+50, startYCord + 70, 50, 20);
                    }
                }
            }

        });
        mainFrame.add(newSlotRowBtt);

    }
}
