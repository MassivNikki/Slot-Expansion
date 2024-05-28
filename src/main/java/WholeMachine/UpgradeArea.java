package WholeMachine;

import CasinoStuff.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class UpgradeArea {

    protected final ArrayList<CasinoUpgradeButton> upgradeButtons = new ArrayList<>();
    protected final int[] symbolOrgChances = {3, 6, 12, 20, 53, 2};
    protected BigInteger sevenSymbolPrice = BigInteger.valueOf(200000);
    protected BigInteger tier4SymbolPrice = BigInteger.valueOf(100000);
    protected BigInteger tier3SymbolPrice = BigInteger.valueOf(50000);
    protected BigInteger tier2SymbolPrice = BigInteger.valueOf(10000);
    protected BigInteger x2SymbolPrice = BigInteger.valueOf(200000);
    protected BigInteger rowPrice = BigInteger.valueOf(1000000000);
    protected BigInteger newGridPrice = BigInteger.valueOf(200000);
    protected int addedGrids = 0;
    private final JFrame mainFrame;
    private final SlotGrid slotGrid;
    private final UpgradeManager upgradeManager;
    private final int startYCord;
    private final int startXCord;
    private int totalUpgradeAmount = 0;
    private SlotMachine machine;

    public UpgradeArea(JFrame mainFrame, SlotGrid slotGrid, int startXCord, int startYCord, SlotMachine machine) {
        this.mainFrame = mainFrame;
        this.slotGrid = slotGrid;
        this.startXCord = startXCord;
        this.startYCord = startYCord;
        this.machine = machine;
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
        addNewUpgrade("7 Symbol chance:"
                , slotGrid.getAdditionalSevenSymbolChance(), symbolOrgChances[0], Application.maxSevenChance, 7, sevenSymbolPrice, "%");
        addNewUpgrade("Tier 4 Symbol chance:"
                , slotGrid.getAdditionalTier4SymbolChance(), symbolOrgChances[1], Application.maxTier4Chance, 4, tier4SymbolPrice, "%");
        addNewUpgrade("Tier 3 Symbol chance:"
                , slotGrid.getAdditionalTier3SymbolChance(), symbolOrgChances[2], Application.maxTier3Chance, 3, tier3SymbolPrice, "%");
        addNewUpgrade("Tier 2 Symbol chance:"
                , slotGrid.getAdditionalTier2SymbolChance(), symbolOrgChances[3], Application.maxTier2Chance, 2, tier2SymbolPrice, "%");
        addNewUpgrade("Tier 1 Symbol chance:"
                , 0, slotGrid.getTier1SymbolChance(), 0, -1, BigInteger.valueOf(0), "%");
        addNewUpgrade("X2 Symbol chance:"
                , slotGrid.getAdditionalX2SymbolChance(), symbolOrgChances[5], Application.maxX2Chance, 1, x2SymbolPrice, "%");
        addNewUpgrade("New winning grid:"
                , addedGrids, slotGrid.getWinningGrids().size() - addedGrids, slotGrid.getWinningGrids().size() - addedGrids + slotGrid.getCurrentAdditionalWinningGrid().size(), 0, newGridPrice, "");
    }

    private void addNewUpgrade(String upgradeInfo, int symbolAdditionalChance, int originalChance, int maxChance, int winSymbol, BigInteger price, String kind) {
        upgradeButtons.add(new CasinoUpgradeButton(upgradeInfo, price, machine.slotTier, "+1", startXCord, startYCord + 240 + totalUpgradeAmount * 20, mainFrame));
        CasinoUpgradeButton currentButton = upgradeButtons.getLast();
        for (int i = 0; i < symbolAdditionalChance; i++) {
            currentButton.increasePrice();
        }
        currentButton.setChanceLabelText(symbolAdditionalChance + originalChance + "/" + maxChance + kind);
        if (symbolAdditionalChance == maxChance - originalChance) {
            currentButton.disableUpgradable();
        }
        determineUpgradeMethod(currentButton, winSymbol);
        mainFrame.add(currentButton);
        totalUpgradeAmount++;
    }

    private void determineUpgradeMethod(CasinoUpgradeButton btt, int symbol) {
        switch (symbol) {
            case 7:
                btt.addCustomActionListener(e -> {
                    upgradeManager.increaseAdditionalSevenSymbolChance();
                });
                break;
            case 4:
                btt.addCustomActionListener(e -> {
                    upgradeManager.increaseAdditionalTier4SymbolChance();
                });
                break;
            case 3:
                btt.addCustomActionListener(e -> {
                    upgradeManager.increaseAdditionalTier3SymbolChance();
                });
                break;
            case 2:
                btt.addCustomActionListener(e -> {
                    upgradeManager.increaseAdditionalTier2SymbolChance();
                });
                break;
            case 1:
                btt.addCustomActionListener(e -> {
                    upgradeManager.increaseAdditionalX2SymbolChance();
                });
                break;
            case 0:
                btt.addCustomActionListener(e -> {
                    upgradeManager.addNewWinningGrid(addedGrids);
                });
                break;
        }
    }

    private void generateAddRowButtons() {

        CasinoUpgradeButton addNewRowUpgradeButton =
                new CasinoUpgradeButton(rowPrice, machine.slotTier, "+1 Row",
                        startXCord + 50 * slotGrid.getRowAmount(), startYCord + 60,
                        mainFrame, 50, 150, PriceLabelPosition.UNDER);
        addNewRowUpgradeButton.addCustomActionListener(e -> {
            if (slotGrid.getRowAmount() < 5) {
                upgradeManager.upgradeGrid();
                if (slotGrid.getRowAmount() == 5) {
                    addNewRowUpgradeButton.removeFromMainframe(mainFrame);
                    mainFrame.repaint();
                } else {
                    addNewRowUpgradeButton.setBounds(startXCord + 50 * slotGrid.getRowAmount(), startYCord + 60, 50, 150);
                }
            }

        });
        mainFrame.add(addNewRowUpgradeButton);

    }
}
