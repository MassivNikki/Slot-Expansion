package slotexpansion.machine;

import slotexpansion.player.PlayerManager;
import slotexpansion.ui.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigInteger;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coordinates the user interface and game state for one slot machine.
 */
public class SlotMachine {
    private static final Logger LOGGER = Logger.getLogger(SlotMachine.class.getName());

    // Machine state and its game systems.
    protected final JPanel machinePanel;
    final SlotLevelComponent slotLevelComponent;
    final SlotGrid slotGrid;
    final UpgradeArea upgradeArea;
    final MachineState state;
    protected int slotTier = 1;
    protected BigInteger columnUpgradePrice = UpgradePrice.FIRST_GRID_EXPANSION;
    private final Random petRandom = new Random();
    private int minIncreased = 0;
    private int maxIncreased = 0;
    private boolean autoSpinUnlocked = false;
    private int autoCooldown = 1000;
    private int manualCooldown = 500;

    // Swing components for the machine's controls and status.
    private final JLabel petImageLabel = new JLabel();
    private final JCheckBox changeAutoSpinCheckbox = new JCheckBox("Auto");
    private final CasinoTextField spinAmountLabel = new CasinoTextField();
    private final PriceLabel winLabel = new PriceLabel();
    private final CasinoButton stakeUpBtt = new CasinoButton("\uD83D\uDD3C");
    private final CasinoButton stakeDownBtt = new CasinoButton("\uD83D\uDD3D");
    private final CasinoButton maxStakeBtt = new CasinoButton("max");
    private final CasinoButton minStakeBtt = new CasinoButton("min");
    private CasinoUpgradeButton increaseMaxSpinAmountBtt;
    private CasinoUpgradeButton increaseMinSpinAmountBtt;
    private CasinoUpgradeButton manualCooldownUpgradeButton;
    private CasinoUpgradeButton autoCooldownUpgradeButton;
    private CasinoUpgradeButton autoSpinUnlockButton;
    protected CasinoUpgradeButton addColumnUpgradeButton;
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);

    // Manual spins use a one-shot timer; auto-spin uses a repeating timer.
    private CasinoButton spinBtt;
    private boolean spinCooldownActivated = false;
    private Timer timer;
    private int currentCooldown = 1000;

    public SlotMachine(JPanel machinePanel, int tier) {
        this(machinePanel, tier, new SlotSymbolGenerator());
    }

    SlotMachine(JPanel machinePanel, int tier, SlotSymbolGenerator symbolGenerator) {
        this.machinePanel = machinePanel;
        state = new MachineState(symbolGenerator);
        slotTier = tier;
        slotGrid = new SlotGrid(machinePanel, this, state);
        slotLevelComponent = new SlotLevelComponent(machinePanel, this);
        upgradeArea = new UpgradeArea(slotGrid, this);
        setPet(new Pet(-1));
        buildInitialComponents();
    }

    /** Returns the purchase tier used to scale this machine's upgrade prices. */
    public int getSlotTier() {
        return slotTier;
    }

    /** Reapplies saved upgrades after the machine has been constructed. */
    public void updateComponentsWithData() {
        int restoredMaxUpgrades = maxIncreased;
        for (int i = 0; i < restoredMaxUpgrades; i++) {
            increaseMaxSpinAmount();
            increaseMaxSpinAmountBtt.increasePrice();
            maxIncreased--;
        }
        int restoredMinUpgrades = minIncreased;
        for (int i = 0; i < restoredMinUpgrades; i++) {
            increaseMinSpinAmount();
            increaseMinSpinAmountBtt.increasePrice();
            minIncreased--;
        }
        restoreGridUpgradeState();
        restoreManualCooldownUpgradeState();
        updateSpinAmount(state.minimumStake());
        setupAutoSpinControls();
        updateTimer();
        spinBtt.setText("<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
        upgradeArea.initiate();
        arrangeAllComponents();
    }

    private void arrangeAllComponents() {
        spinAmountLabel.setBounds(machinePanel.getWidth() / 2 - 50, 35, 100, 20);
        stakeDownBtt.setBounds(spinAmountLabel.getX() - 25, 35, 20, 20);
        minStakeBtt.setBounds(stakeDownBtt.getX() - 35, 35, 30, 20);
        increaseMinSpinAmountBtt.setBounds(minStakeBtt.getX() - 55, 35, 50, 20);
        stakeUpBtt.setBounds(spinAmountLabel.getX() + spinAmountLabel.getWidth() + 5, 35, 20, 20);
        maxStakeBtt.setBounds(stakeUpBtt.getX() + stakeUpBtt.getWidth() + 5, 35, 30, 20);
        increaseMaxSpinAmountBtt.setBounds(maxStakeBtt.getX() + maxStakeBtt.getWidth() + 5, 35, 50, 20);
        spinBtt.setBounds(machinePanel.getWidth() / 2 - 50, 70, 100, 50);
        petImageLabel.setBounds(30, 75, 80, 40);
        changeAutoSpinCheckbox.setBounds(spinBtt.getX() + spinBtt.getWidth() + 10, 70, 50, 20);
        winLabel.setBounds(45, 125, 100, 30);
        if (manualCooldownUpgradeButton != null) {
            manualCooldownUpgradeButton.setBounds(spinBtt.getX(), spinBtt.getY() + spinBtt.getHeight() + 5, 40, 20);
        }
        if (autoCooldownUpgradeButton != null) {
            autoCooldownUpgradeButton.setBounds(changeAutoSpinCheckbox.getX(), changeAutoSpinCheckbox.getY() + changeAutoSpinCheckbox.getHeight(), changeAutoSpinCheckbox.getWidth(), 20);
        }
        if (autoSpinUnlockButton != null) {
            autoSpinUnlockButton.setBounds(changeAutoSpinCheckbox.getX(), changeAutoSpinCheckbox.getY(), changeAutoSpinCheckbox.getWidth() + 10, changeAutoSpinCheckbox.getHeight() + 50);
        }
    }

    /** Returns the machine's accumulated experience. */
    public BigInteger getSlotXp() {
        return state.xp();
    }

    /** Captures persistent state; temporary free-spin and animation state is not saved. */
    public MachineSave snapshot(BigInteger balance) {
        return new MachineSave(balance, state.level(), getSlotXp(),
                slotGrid.getAdditionalSevenSymbolChance(), slotGrid.getAdditionalTier4SymbolChance(),
                slotGrid.getAdditionalTier3SymbolChance(), slotGrid.getAdditionalTier2SymbolChance(),
                slotGrid.getTier1SymbolChance(), slotGrid.getAdditionalX2SymbolChance(),
                upgradeArea.addedGrids, state.pet().getTier(), slotGrid.getColumnCount(), autoCooldown,
                manualCooldown, autoSpinUnlocked, minIncreased, maxIncreased,
                upgradeArea.purchaseCount(UpgradeArea.Upgrade.SEVEN),
                upgradeArea.purchaseCount(UpgradeArea.Upgrade.TIER4),
                upgradeArea.purchaseCount(UpgradeArea.Upgrade.TIER3),
                upgradeArea.purchaseCount(UpgradeArea.Upgrade.TIER2),
                upgradeArea.purchaseCount(UpgradeArea.Upgrade.X2), state.unlockedSymbols());
    }

    /** Restores the grid before its odds because grid expansion resets odds. */
    public void restore(MachineSave state) {
        slotLevelComponent.setSlotLevel(state.level());
        slotLevelComponent.setSlotXp(state.xp());
        slotGrid.setColumnCount(state.columns());
        this.state.setUnlockedSymbols(state.unlockedSymbols());
        slotGrid.setAdditionalSevenSymbolChance(state.sevenChance());
        slotGrid.setAdditionalTier4SymbolChance(state.tier4Chance());
        slotGrid.setAdditionalTier3SymbolChance(state.tier3Chance());
        slotGrid.setAdditionalTier2SymbolChance(state.tier2Chance());
        slotGrid.setTier1SymbolChance(state.tier1Chance());
        slotGrid.setAdditionalX2SymbolChance(state.x2Chance());
        upgradeArea.addedGrids = state.addedPaylines();
        upgradeArea.restorePurchaseCounts(state);
        setPet(new Pet(state.petTier()));
        autoCooldown = state.autoCooldown();
        manualCooldown = state.manualCooldown();
        autoSpinUnlocked = state.autoUnlocked();
        minIncreased = state.minUpgrades();
        maxIncreased = state.maxUpgrades();
        updateComponentsWithData();
    }

    private void buildInitialComponents() {
        setupLabels();
        setupSpinButton();
        setupStakeButtons();
        setupGridSizeUpgrade();
        upgradeArea.setBounds(20, 305, 370, 160);
        machinePanel.add(upgradeArea);
    }

    private void setupGridSizeUpgrade() {
        addColumnUpgradeButton =
                new CasinoUpgradeButton(columnUpgradePrice, slotTier, "+1",
                        machinePanel, PriceLabelPosition.INSIDE, UpgradePrice.Growth.MILESTONE);
        addColumnUpgradeButton.setBounds(75 + 50 * slotGrid.getColumnCount(), 150, 50, 150);
        addColumnUpgradeButton.addCustomActionListener(e -> {
            if (slotGrid.getColumnCount() < 5) {
                upgradeArea.upgradeManager.upgradeGrid();
                state.pet().setTier(-1);
                setPetImage(state.pet().getIcon());
                refreshTimerForPet();
                moveUpgradeGridButton();
            }

        });
        machinePanel.add(addColumnUpgradeButton);
    }

    private void restoreGridUpgradeState() {
        for (int column = 3; column < slotGrid.getColumnCount(); column++) {
            addColumnUpgradeButton.increasePrice();
        }
        moveUpgradeGridButton();
    }

    private void restoreManualCooldownUpgradeState() {
        if (manualCooldownUpgradeButton != null) {
            int purchasedUpgrades = Math.max(0, (500 - manualCooldown) / 50);
            for (int i = 0; i < purchasedUpgrades; i++) {
                manualCooldownUpgradeButton.increasePrice();
            }
            if (manualCooldown <= 200) {
                manualCooldownUpgradeButton.removeFromMachinePanel(machinePanel);
                manualCooldownUpgradeButton = null;
            }
        }
    }

    protected void moveUpgradeGridButton(){
        if (slotGrid.getColumnCount() == 5) {
            addColumnUpgradeButton.removeFromMachinePanel(machinePanel);
        } else {
            addColumnUpgradeButton.setBounds(75 + 50 * slotGrid.getColumnCount(), 150, 50, 150);
        }
        machinePanel.repaint();
    }

    private void setupAutoSpinControls() {
        if (!autoSpinUnlocked) {
            autoSpinUnlockButton =
                    new CasinoUpgradeButton(BigInteger.valueOf(10000), slotTier, "<html>Unlock<br>AutoSpin</html>",
                            machinePanel, PriceLabelPosition.INSIDE);
            autoSpinUnlockButton.addCustomActionListener(e -> {
                autoSpinUnlocked = true;
                changeAutoSpinCheckbox.setEnabled(true);
                autoSpinUnlockButton.removeFromMachinePanel(machinePanel);
                autoSpinUnlockButton = null;
                machinePanel.repaint();
            });
            machinePanel.add(autoSpinUnlockButton);
        }
        setupAutoSpinCheckbox();

    }

    private void setupSpinButton() {
        spinBtt = new CasinoButton();
        spinBtt.addActionListener(e -> {
            if (!spinCooldownActivated) {
                spinCooldownActivated = true;
                spinBtt.setEnabled(false);
                timer.start();
                performSpin();
            }
        });
        machinePanel.add(spinBtt);
        if (manualCooldown > 300) {
            manualCooldownUpgradeButton =
                    new CasinoUpgradeButton(BigInteger.valueOf(1000), slotTier, "-50ms", machinePanel, PriceLabelPosition.RIGHT);
            manualCooldownUpgradeButton.addCustomActionListener(e -> {
                manualCooldown -= 50;
                spinBtt.setText("<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                updateTimer();
                timer.start();
                if (manualCooldown == 200) {
                    manualCooldownUpgradeButton.removeFromMachinePanel(machinePanel);
                    manualCooldownUpgradeButton = null;
                    machinePanel.repaint();
                }
            });
            machinePanel.add(manualCooldownUpgradeButton);
        }
        updateTimer();
    }

    private void setupAutoSpinCheckbox() {
        changeAutoSpinCheckbox.setSelected(false);
        changeAutoSpinCheckbox.setEnabled(autoSpinUnlocked);
        changeAutoSpinCheckbox.setBorder(BorderFactory.createEmptyBorder());
        changeAutoSpinCheckbox.setMargin(new Insets(0, 0, 0, 0));
        changeAutoSpinCheckbox.setForeground(Color.white);
        changeAutoSpinCheckbox.setFocusPainted(false);
        changeAutoSpinCheckbox.setContentAreaFilled(false);
        changeAutoSpinCheckbox.addItemListener(f -> {
            spinBtt.setEnabled(!changeAutoSpinCheckbox.isSelected());
            updateTimer();
            timer.setRepeats(changeAutoSpinCheckbox.isSelected());
            timer.start();
        });
        machinePanel.add(changeAutoSpinCheckbox);
        if (autoCooldown > 200) {
            autoCooldownUpgradeButton =
                    new CasinoUpgradeButton(BigInteger.valueOf(2500), slotTier, "-50ms",
                            machinePanel, PriceLabelPosition.UNDER);
            int purchasedUpgrades = Math.max(0, (1000 - autoCooldown) / 50);
            for (int i = 0; i < purchasedUpgrades; i++) {
                autoCooldownUpgradeButton.increasePrice();
            }
            autoCooldownUpgradeButton.addCustomActionListener(e -> {
                autoCooldown -= 50;
                spinBtt.setText("<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                updateTimer();
                timer.start();
                if (autoCooldown == 200) {
                    autoCooldownUpgradeButton.removeFromMachinePanel(machinePanel);
                    autoCooldownUpgradeButton = null;
                    machinePanel.repaint();
                }
            });
            machinePanel.add(autoCooldownUpgradeButton);
        }

    }

    private void setupLabels() {
        spinAmountLabel.setForeground(Color.white);
        spinAmountLabel.setBorder(normalBorder);
        spinAmountLabel.setBackground(null);
        spinAmountLabel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                spinAmountLabel.setBackground(Color.decode("#24598E"));
            }

            @Override
            public void focusLost(FocusEvent e) {
                spinAmountLabel.setBackground(null);
                if (slotGrid.isFreeSpinsActivated()) {
                    updateSpinAmount(state.stake());
                    return;
                }
                try {
                    BigInteger valuePerLane = spinAmountLabel.getAmount();
                    if (valuePerLane.compareTo(getMaximumAmountPerLane()) > 0) {
                        updateSpinAmount(state.maximumStake());
                    } else if (valuePerLane.compareTo(getMinimumCoinsPerLane()) < 0) {
                        updateSpinAmount(state.minimumStake());
                    } else {
                        updateSpinAmount(valuePerLane);
                    }
                } catch (NumberFormatException | ArithmeticException ex) {
                    updateSpinAmount(state.stake());
                    JOptionPane.showMessageDialog(machinePanel, "Enter a whole number for the stake.",
                            "Invalid stake", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        machinePanel.add(spinAmountLabel);

        winLabel.setForeground(Color.GREEN);
        winLabel.setFont(new Font("Arial", Font.BOLD, 20));
        machinePanel.add(winLabel);
    }

    private void setupStakeButtons() {
        stakeUpBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeUpBtt.setBackground(new Color(0xD4B483));
        stakeUpBtt.addActionListener(e -> {
            // Stake changes are disabled while free spins are active.
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(state.stake().add(state.maximumStake().divide(BigInteger.valueOf(10))));
                if (state.stake().compareTo(state.maximumStake()) > 0) {
                    updateSpinAmount(state.maximumStake());
                }
                if (state.stake().compareTo(state.minimumStake()) < 0) {
                    updateSpinAmount(state.minimumStake());
                }
            }
        });
        machinePanel.add(stakeUpBtt);

        stakeDownBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeDownBtt.setBackground(new Color(0xD4B483));
        stakeDownBtt.addActionListener(e -> {
            // Stake changes are disabled while free spins are active.
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(state.stake().subtract(state.maximumStake().divide(BigInteger.valueOf(10))));
                if (state.stake().compareTo(state.minimumStake()) < 0) {
                    updateSpinAmount(state.minimumStake());
                }
            }


        });
        machinePanel.add(stakeDownBtt);


        maxStakeBtt.setBackground(new Color(0xD4B483));
        maxStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(state.maximumStake());
                if (state.stake().compareTo(PlayerManager.getCoins()) > 0 && PlayerManager.getCoins().compareTo(BigInteger.valueOf(0)) > 0) {
                    updateSpinAmount(PlayerManager.getCoins());
                    if (state.stake().compareTo(state.minimumStake()) < 0) {
                        updateSpinAmount(state.minimumStake());
                    }
                }
            }
        });
        machinePanel.add(maxStakeBtt);

        increaseMaxSpinAmountBtt = new CasinoUpgradeButton(BigInteger.valueOf(500), slotTier, "upg max", machinePanel, PriceLabelPosition.UNDER);
        increaseMaxSpinAmountBtt.addCustomActionListener(e -> {
            increaseMaxSpinAmount();
        });
        machinePanel.add(increaseMaxSpinAmountBtt);


        minStakeBtt.setBackground(new Color(0xD4B483));
        minStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(state.minimumStake());
                spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.orange));

            }
        });
        machinePanel.add(minStakeBtt);

        increaseMinSpinAmountBtt = new CasinoUpgradeButton(BigInteger.valueOf(500), slotTier, "upg min", machinePanel, PriceLabelPosition.UNDER);
        increaseMinSpinAmountBtt.addCustomActionListener(e -> {
            increaseMinSpinAmount();
        });
        machinePanel.add(increaseMinSpinAmountBtt);
    }

    /** Rebuilds the Swing timer using the active manual or automatic cooldown. */
    private void updateTimer() {
        if (timer != null) {
            timer.stop();
        }

        if (changeAutoSpinCheckbox.isSelected()) {
            currentCooldown = autoCooldown - state.pet().getAutoCooldownBonus();
        } else {
            currentCooldown = manualCooldown;
        }
        timer = new Timer(currentCooldown, e -> {
            if (changeAutoSpinCheckbox.isSelected()) {
                performSpin();
            } else {
                spinCooldownActivated = false;
                spinBtt.setEnabled(true);
            }
        });
        if (!changeAutoSpinCheckbox.isSelected()) {
            timer.setRepeats(false);
        }
    }

    private void refreshTimerForPet() {
        if (timer != null) {
            boolean running = timer.isRunning();
            updateTimer();
            if (running) {
                timer.start();
            }
        }
    }

    /** Locks this machine's purchases and stake controls until free spins finish. */
    void updateFreeSpinControls() {
        boolean available = !slotGrid.isFreeSpinsActivated();
        spinAmountLabel.setEnabled(available);
        stakeUpBtt.setEnabled(available);
        stakeDownBtt.setEnabled(available);
        minStakeBtt.setEnabled(available);
        maxStakeBtt.setEnabled(available);
        for (Component component : machinePanel.getComponents()) {
            if (component instanceof CasinoUpgradeButton button) {
                button.setEnabled(available);
            }
        }
        upgradeArea.setPurchasesEnabled(available);
    }

    /** Executes the current stake and refreshes the displayed win. */
    private void performSpin() {
        BigInteger wholeSpinWin = BigInteger.valueOf(0);
        if (state.stake().compareTo(getMinimumCoinsPerLane()) == 0) {
            wholeSpinWin = slotGrid.spinSlotMachine(state.stake());
        } else if (state.stake().compareTo(PlayerManager.getCoins()) <= 0 || slotGrid.isFreeSpinsActivated()) {
            wholeSpinWin = slotGrid.spinSlotMachine(state.stake());
        }
        if (wholeSpinWin.compareTo(BigInteger.valueOf(0)) == 0) {
            winLabel.setVisible(false);
        } else {
            winLabel.setCoins(wholeSpinWin);
            winLabel.setVisible(true);
        }
    }

    /** Samples a pet tier when this machine levels up and keeps only improvements. */
    protected void choosePetForLevelUp() {
        int tier = Pet.chooseTier(petRandom);
        if (Pet.multiplierForTier(tier) > state.pet().getMulti()) {
            state.pet().setTier(tier);
            setPetImage(state.pet().getIcon());
            refreshTimerForPet();
            LOGGER.log(Level.FINE, "Won a pet with multiplier {0}", Pet.multiplierForTier(tier));
        }
    }

    protected void setPetImage(ImageIcon icon) {
        petImageLabel.setVisible(true);
        petImageLabel.setIcon(icon);
        petImageLabel.setText(state.pet().getMulti() + "x");
        petImageLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        petImageLabel.setForeground(Color.orange);
    }

    /** Assigns the machine's pet and refreshes the pet indicator. */
    public void setPet(Pet pet) {
        state.setPet(pet);
        petImageLabel.setBackground(Color.white);
        petImageLabel.setVisible(false);
        machinePanel.add(petImageLabel);
        setPetImage(pet.getIcon());
        refreshTimerForPet();
    }


    /** Returns the current minimum stake amount. */
    public BigInteger getMinimumCoinsPerLane() {
        return state.minimumStake();
    }

    private void increaseMinSpinAmount() {
        if (state.minimumStake().compareTo(BigInteger.valueOf(20)) > 0) {
            state.setMinimumStake(state.minimumStake().add(state.minimumStake().divide(BigInteger.valueOf(5))));
        } else {
            state.setMinimumStake(state.minimumStake().add(BigInteger.valueOf(4)));
        }
        updateSpinAmount(state.minimumStake());
        minIncreased++;
    }

    private void increaseMaxSpinAmount() {
        if (state.maximumStake().compareTo(BigInteger.valueOf(20)) > 0) {
            state.setMaximumStake(state.maximumStake().add(state.maximumStake().divide(BigInteger.valueOf(2))));
        } else {
            state.setMaximumStake(state.maximumStake().add(BigInteger.valueOf(4)));
        }
        maxIncreased++;
    }

    private BigInteger getMaximumAmountPerLane() {
        return state.maximumStake();
    }

    private void updateSpinAmount(BigInteger amount) {
        state.setStake(amount);
        if (state.stake().compareTo(state.minimumStake()) <= 0) {
            state.setStake(state.minimumStake());
            spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        } else if (state.stake().compareTo(state.maximumStake()) >= 0) {
            state.setStake(state.maximumStake());
            spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        } else {
            spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        }
        spinAmountLabel.updateFormat(state.stake());
    }

}
