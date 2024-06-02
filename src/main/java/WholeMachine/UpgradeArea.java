package WholeMachine;

import CasinoStuff.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class UpgradeArea extends JFrame {

    protected final ArrayList<CasinoUpgradeButton> upgradeIncreaseButtons = new ArrayList<>();
    protected final int[] symbolOrgChances = {3, 6, 12, 20, 53, 2};
    protected BigInteger sevenSymbolPrice = BigInteger.valueOf(200000);
    protected BigInteger tier4SymbolPrice = BigInteger.valueOf(100000);
    protected BigInteger tier3SymbolPrice = BigInteger.valueOf(50000);
    protected BigInteger tier2SymbolPrice = BigInteger.valueOf(10000);
    protected BigInteger x2SymbolPrice = BigInteger.valueOf(200000);
    protected BigInteger newGridPrice = BigInteger.valueOf(200000);
    public int addedGrids = 0;
    private final SlotGrid slotGrid;
    protected final UpgradeManager upgradeManager;
    private int totalUpgradeAmount = 0;
    private SlotMachine machine;
    private JPanel increaseUpgradesPanel = new JPanel(null);
    private JPanel unlockPanel = new JPanel(null);
    private JTabbedPane tabs = new JTabbedPane();

    public UpgradeArea(SlotGrid slotGrid, SlotMachine machine) {
        this.slotGrid = slotGrid;
        this.machine = machine;
        upgradeManager = new UpgradeManager(this.slotGrid, this);
        setSize(350, 300);
        setLocation(Application.mainFrame.getX() + 35, Application.mainFrame.getY() + machine.machinePanel.getHeight() / 2);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setResizable(false);
        tabs.setUI(new CustomTabbedPaneUI());
        getContentPane().setBackground(Color.darkGray);
        unlockPanel.setBackground(Color.darkGray);
        increaseUpgradesPanel.setBackground(Color.darkGray);
        setTitle("Upgrades for machine " + machine.slotTier);
    }
    private void addTabs(){
        tabs.addTab("Increase",increaseUpgradesPanel);
        tabs.addTab("Unlock",unlockPanel);
        tabs.setBounds(10,10,300,20);
        getContentPane().add(tabs);
    }

    public void initiate() {
        for (int i = 0; i < addedGrids; i++) {
            slotGrid.addNewWinningGrid(i);
        }
        addNewIncreaseUpgrade("7 Symbol chance:"
                , slotGrid.getAdditionalSevenSymbolChance(), symbolOrgChances[0], Application.maxSevenChance, 7, sevenSymbolPrice, "%");
        addNewIncreaseUpgrade("Tier 4 Symbol chance:"
                , slotGrid.getAdditionalTier4SymbolChance(), symbolOrgChances[1], Application.maxTier4Chance, 4, tier4SymbolPrice, "%");
        addNewIncreaseUpgrade("Tier 3 Symbol chance:"
                , slotGrid.getAdditionalTier3SymbolChance(), symbolOrgChances[2], Application.maxTier3Chance, 3, tier3SymbolPrice, "%");
        addNewIncreaseUpgrade("Tier 2 Symbol chance:"
                , slotGrid.getAdditionalTier2SymbolChance(), symbolOrgChances[3], Application.maxTier2Chance, 2, tier2SymbolPrice, "%");
        addNewIncreaseUpgrade("Tier 1 Symbol chance:"
                , 0, slotGrid.getTier1SymbolChance(), 0, -1, BigInteger.valueOf(0), "%");
        addNewIncreaseUpgrade("X2 Symbol chance:"
                , slotGrid.getAdditionalX2SymbolChance(), symbolOrgChances[5], Application.maxX2Chance, 1, x2SymbolPrice, "%");
        addNewIncreaseUpgrade("New winning grid:"
                , addedGrids, slotGrid.getWinningGrids().size() - addedGrids, slotGrid.getWinningGrids().size() - addedGrids + slotGrid.getCurrentAdditionalWinningGrid().size(), 0, newGridPrice, "");
        totalUpgradeAmount = 0;
        addTabs();
    }

    private void addNewUnlockButton(){

            CasinoUpgradeButton unlockUpgradeBtt = new CasinoUpgradeButton("Unlock tier 2 symbols:", BigInteger.valueOf(10000),machine.slotTier,"unlock",10,10+ totalUpgradeAmount * 20,unlockPanel);
    }

    private void addNewIncreaseUpgrade(String upgradeInfo, int symbolAdditionalChance, int originalChance, int maxChance, int winSymbol, BigInteger price, String kind) {
        upgradeIncreaseButtons.add(new CasinoUpgradeButton(upgradeInfo, price, machine.slotTier, "+1", 10, 10 + totalUpgradeAmount * 20, increaseUpgradesPanel));
        CasinoUpgradeButton currentButton = upgradeIncreaseButtons.getLast();
        for (int i = 0; i < symbolAdditionalChance; i++) {
            currentButton.increasePrice();
        }
        currentButton.setChanceLabelText(symbolAdditionalChance + originalChance + "/" + maxChance + kind);
        if (symbolAdditionalChance == maxChance - originalChance) {
            currentButton.disableUpgradable();
        }
        determineUpgradeMethod(currentButton, winSymbol);
        increaseUpgradesPanel.add(currentButton);
        totalUpgradeAmount++;
    }

    private void determineUpgradeMethod(CasinoUpgradeButton btt, int symbol) {
        switch (symbol) {
            case 10:
                btt.addCustomActionListener(e -> {
                    upgradeManager.increaseAdditionalSevenSymbolChance();
                });
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


}
