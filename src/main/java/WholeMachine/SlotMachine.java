package WholeMachine;

import CasinoStuff.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class SlotMachine {

    private static final int progressBarWidth = 350;

    public HashMap<String, JComponent> allComponents = new HashMap<>();
    protected final JPanel machinePanel;
    private final JLabel petImageLabel = new JLabel();
    private final JCheckBox changeAutoSpinCheckbox = new JCheckBox("Auto");
    private final CasinoTextField spinAmountLabel = new CasinoTextField();
    private final JLabel winLabel = new JLabel();
    private CasinoButton stakeUpBtt = new CasinoButton("\uD83D\uDD3C");
    private CasinoButton stakeDownBtt = new CasinoButton("\uD83D\uDD3D");
    private CasinoButton maxStakeBtt = new CasinoButton("max");
    private CasinoButton minStakeBtt = new CasinoButton("min");
    private CasinoUpgradeButton increaseMaxSpinAmountBtt;
    private CasinoUpgradeButton increaseMinSpinAmountBtt;
    public SlotLevelComponent slotLevelComponent;
    protected BigInteger rowPrice = BigInteger.valueOf(1000000000);
    private final int[] petChances = {1000, 652, 422, 272, 149, 114, 84, 59, 39, 24, 14, 7, 2, 0};
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);
    public SlotGrid slotGrid;
    public UpgradeArea upgradeArea;
    private CasinoButton spinBtt;
    protected BigInteger spinAmount = BigInteger.valueOf(1);
    public Pet pet;
    public int slotLevel;
    protected int slotTier = 1;
    private BigInteger minimumSpinAmount = BigInteger.valueOf(5);
    private BigInteger maximumSpinAmount = BigInteger.valueOf(20);
    public int minIncreased = 0;
    public int maxIncreased = 0;
    public boolean autoSpinUnlocked = false;
    private boolean spinCooldownActivated = false;
    private Timer timer;
    private int currentCooldown = 1000;
    public int autoCooldown = 1000;
    public int manualCooldown = 500;

    public SlotMachine(JPanel machinePanel, int tier) {
        this.machinePanel = machinePanel;
        slotTier = tier;
        slotGrid = new SlotGrid(machinePanel, this);
        slotLevelComponent = new SlotLevelComponent(machinePanel, this);
        upgradeArea = new UpgradeArea(slotGrid, this);
        setPet(new Pet(-1));
        generateInitialComponentsWithoutSavedDate();
    }

    public int getSlotTier() {
        return slotTier;
    }

    public void updateComponentsWithData() {
        int tempAmount = maxIncreased;
        for (int i = 0; i < tempAmount; i++) {
            increaseMaxSpinAmount();
            increaseMaxSpinAmountBtt.increasePrice();
            maxIncreased--;
        }
        tempAmount = minIncreased;
        for (int i = 0; i < tempAmount; i++) {
            increaseMinSpinAmount();
            increaseMinSpinAmountBtt.increasePrice();
            minIncreased--;
        }
        updateSpinAmount(minimumSpinAmount);
        setupAutoSpinUpgrade();
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
        winLabel.setBounds(100, 20, 100, 20);
        JComponent comp = allComponents.get("spinCoolDownUpgradeBtt");
        if (comp != null) {
            comp.setBounds(spinBtt.getX(), spinBtt.getY() + spinBtt.getHeight() + 5, 40, 20);
        }
        comp = allComponents.get("autoSpinCooldownUpgradeButton");
        if (comp != null) {
            comp.setBounds(changeAutoSpinCheckbox.getX(), changeAutoSpinCheckbox.getY() + changeAutoSpinCheckbox.getHeight(), changeAutoSpinCheckbox.getWidth(), 20);
        }
        comp = allComponents.get("autoSpinUnlockButton");
        if (comp != null) {
            comp.setBounds(changeAutoSpinCheckbox.getX(), changeAutoSpinCheckbox.getY(), changeAutoSpinCheckbox.getWidth() + 10, changeAutoSpinCheckbox.getHeight() + 50);
        }
    }

    public BigInteger getSlotXp() {
        return slotLevelComponent.slotXp;
    }

    private void generateInitialComponentsWithoutSavedDate() {
        setupLabels();
        setupSpinButton();
        setupStakeButtons();
        generateAddRowButton();
        CasinoButton showUpgrades = new CasinoButton("Upgrades");
        showUpgrades.setBounds(machinePanel.getWidth() / 2 - 75, 350, 150, 50);
        showUpgrades.addActionListener(e -> upgradeArea.setVisible(true));
        showUpgrades.setBackground(new Color(0x2dce98));
        machinePanel.add(showUpgrades);
    }

    private void generateAddRowButton() {
        CasinoUpgradeButton addNewRowUpgradeButton =
                new CasinoUpgradeButton(rowPrice, slotTier, "+1 Row",
                        machinePanel, PriceLabelPosition.UNDER);
        addNewRowUpgradeButton.setBounds(75 + 50 * slotGrid.getRowAmount(), 170, 50, 150);
        addNewRowUpgradeButton.addCustomActionListener(e -> {
            if (slotGrid.getRowAmount() < 5) {
                upgradeArea.upgradeManager.upgradeGrid();
                pet.setTier(-1);
                setPetImage(pet.getIcon());
                machinePanel.repaint();
                if (slotGrid.getRowAmount() == 5) {
                    addNewRowUpgradeButton.removeFromMachinePanel(machinePanel);
                    machinePanel.repaint();
                } else {
                    addNewRowUpgradeButton.setBounds(75 + 50 * slotGrid.getRowAmount(), 170, 50, 150);
                }
            }

        });
        machinePanel.add(addNewRowUpgradeButton);
    }

    private void setupAutoSpinUpgrade() {
        if (!autoSpinUnlocked) {
            CasinoUpgradeButton autoSpinUnlockButton =
                    new CasinoUpgradeButton(BigInteger.valueOf(500000), slotTier, "<html>Unlock<br>AutoSpin</html>",
                            machinePanel, PriceLabelPosition.UNDER);
            autoSpinUnlockButton.addCustomActionListener(e -> {
                autoSpinUnlockButton.removeFromMachinePanel(machinePanel);
                machinePanel.repaint();
            });
            machinePanel.add(autoSpinUnlockButton);
            allComponents.put("autoSpinUnlockButton", autoSpinUnlockButton);
        }
        addAutoSpinCheckboxToFrame();

    }

    private void setupSpinButton() {
        spinBtt = new CasinoButton();
        spinBtt.addActionListener(e -> {
            if (!spinCooldownActivated) {
                spinCooldownActivated = true;
                spinBtt.setEnabled(false);
                timer.start();
                spinMachine();
            }
        });
        machinePanel.add(spinBtt);
        if (manualCooldown > 300) {
            CasinoUpgradeButton spinCoolDownUpgradeBtt =
                    new CasinoUpgradeButton(BigInteger.valueOf(5000), slotTier, "-50ms", machinePanel, PriceLabelPosition.RIGHT);
            spinCoolDownUpgradeBtt.addCustomActionListener(e -> {
                manualCooldown -= 50;
                updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                updateTimer();
                timer.start();
                if (manualCooldown == 200) {
                    spinCoolDownUpgradeBtt.removeFromMachinePanel(machinePanel);
                    machinePanel.repaint();
                }
            });
            machinePanel.add(spinCoolDownUpgradeBtt);
            allComponents.put("spinCoolDownUpgradeBtt", spinCoolDownUpgradeBtt);
        }
        updateTimer();
    }

    private void addAutoSpinCheckboxToFrame() {
        changeAutoSpinCheckbox.setSelected(false);
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
            CasinoUpgradeButton autoSpinCooldownUpgradeButton =
                    new CasinoUpgradeButton(BigInteger.valueOf(50000), slotTier, "-50ms",
                            machinePanel, PriceLabelPosition.UNDER);
            autoSpinCooldownUpgradeButton.addCustomActionListener(e -> {
                autoCooldown -= 50;
                updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                updateTimer();
                timer.start();
                if (autoCooldown == 200) {
                    autoSpinCooldownUpgradeButton.removeFromMachinePanel(machinePanel);
                    machinePanel.repaint();
                }
            });
            machinePanel.add(autoSpinCooldownUpgradeButton);
            allComponents.put("autoSpinCooldownUpgradeButton", autoSpinCooldownUpgradeButton);
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
                BigInteger valuePerLane = spinAmountLabel.getAmount();
                if (valuePerLane.compareTo(getMaximumAmountPerLane()) > 0) {
                    updateSpinAmount(maximumSpinAmount);
                } else if (valuePerLane.compareTo(getMinimumCoinsPerLane()) < 0) {
                    updateSpinAmount(minimumSpinAmount);
                } else {
                    updateSpinAmount(valuePerLane);
                }
            }
        });
        machinePanel.add(spinAmountLabel);

        winLabel.setForeground(Color.CYAN);
        machinePanel.add(winLabel);
    }

    private void setupStakeButtons() {
        stakeUpBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeUpBtt.setBackground(new Color(0xD4B483));
        stakeUpBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(spinAmount.add(maximumSpinAmount.divide(BigInteger.valueOf(10))));
                if (spinAmount.compareTo(maximumSpinAmount) > 0) {
                    updateSpinAmount(maximumSpinAmount);
                }
                if (spinAmount.compareTo(minimumSpinAmount) < 0) {
                    updateSpinAmount(minimumSpinAmount);
                }
            }
        });
        machinePanel.add(stakeUpBtt);

        stakeDownBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeDownBtt.setBackground(new Color(0xD4B483));
        stakeDownBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(spinAmount.subtract(maximumSpinAmount.divide(BigInteger.valueOf(10))));
                if (spinAmount.compareTo(minimumSpinAmount) < 0) {
                    updateSpinAmount(minimumSpinAmount);
                }
            }


        });
        machinePanel.add(stakeDownBtt);


        maxStakeBtt.setBackground(new Color(0xD4B483));
        maxStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(maximumSpinAmount);
                if (spinAmount.compareTo(PlayerManager.getCoins()) > 0 && PlayerManager.getCoins().compareTo(BigInteger.valueOf(0)) > 0) {
                    updateSpinAmount(PlayerManager.getCoins());
                    if (spinAmount.compareTo(minimumSpinAmount) < 0) {
                        updateSpinAmount(minimumSpinAmount);
                    }
                }
            }
        });
        machinePanel.add(maxStakeBtt);

        increaseMaxSpinAmountBtt = new CasinoUpgradeButton(BigInteger.valueOf(10000), slotTier, "upg max", machinePanel, PriceLabelPosition.UNDER);
        increaseMaxSpinAmountBtt.addCustomActionListener(e -> {
            increaseMaxSpinAmount();
        });
        machinePanel.add(increaseMaxSpinAmountBtt);


        minStakeBtt.setBackground(new Color(0xD4B483));
        minStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                updateSpinAmount(minimumSpinAmount);
                spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.orange));

            }
        });
        machinePanel.add(minStakeBtt);

        increaseMinSpinAmountBtt = new CasinoUpgradeButton(BigInteger.valueOf(10000), slotTier, "upg min", machinePanel, PriceLabelPosition.UNDER);
        increaseMinSpinAmountBtt.addCustomActionListener(e -> {
            increaseMinSpinAmount();
        });
        machinePanel.add(increaseMinSpinAmountBtt);
    }

    private void updateTextOfButton(CasinoButton button, String text) {
        button.setText(text);
    }

    private void updateTimer() {
        if (timer != null) {
            timer.stop();
        }
        if (changeAutoSpinCheckbox.isSelected()) {
            currentCooldown = autoCooldown - pet.autoCooldownBonus;
        } else {
            currentCooldown = manualCooldown;
        }
        timer = new Timer(currentCooldown, e -> {
            if (changeAutoSpinCheckbox.isSelected()) {
                spinMachine();
            } else {
                spinCooldownActivated = false;
                spinBtt.setEnabled(true);
            }
        });
        if (!changeAutoSpinCheckbox.isSelected()) {
            timer.setRepeats(false);
        }
    }

    private void spinMachine() {
        BigInteger wholeSpinWin = BigInteger.valueOf(0);
        if (spinAmount.compareTo(getMinimumCoinsPerLane()) == 0) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        } else if (spinAmount.compareTo(PlayerManager.getCoins()) <= 0 || slotGrid.isFreeSpinsActivated()) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        }
        //wenn nichts gewonnen wurde wird auch nichts angezeigt
        if (wholeSpinWin.compareTo(BigInteger.valueOf(0)) == 0) {
            winLabel.setVisible(false);
        } else {
            //ansonsten bekommt der spieler den gewinn als xp
            slotLevelComponent.addXp(wholeSpinWin);
            winLabel.setText(String.valueOf(wholeSpinWin));
            winLabel.setVisible(true);
        }
    }

    protected void choosePetForLevelUp() {
        int rand = new Random().nextInt(0, 1000);
        int tier = -1;
        for (int i = 0; i < petChances.length - 1; i++) {
            if (rand < petChances[i] && rand >= petChances[i + 1]) {
                tier = i;
            }
        }
        if (pet.petMultis[tier] > pet.getMulti()) {
            pet.setTier(tier);
            setPetImage(pet.getIcon());
            System.out.println("Won a pet with multi of: " + pet.petMultis[tier]);
        }
    }

    protected void setPetImage(ImageIcon icon) {
        petImageLabel.setVisible(true);
        petImageLabel.setIcon(icon);
        petImageLabel.setText(pet.getMulti() + "x");
        petImageLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        petImageLabel.setForeground(Color.orange);
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        petImageLabel.setBackground(Color.white);
        petImageLabel.setVisible(false);
        machinePanel.add(petImageLabel);
        setPetImage(pet.getIcon());
    }


    public BigInteger getMinimumCoinsPerLane() {
        return minimumSpinAmount;
    }

    private void increaseMinSpinAmount() {
        if (minimumSpinAmount.compareTo(BigInteger.valueOf(20)) > 0) {
            minimumSpinAmount = minimumSpinAmount.add(minimumSpinAmount.divide(BigInteger.valueOf(5)));
        } else {
            minimumSpinAmount = minimumSpinAmount.add(BigInteger.valueOf(4));
        }
        updateSpinAmount(minimumSpinAmount);
        minIncreased++;
    }

    private void increaseMaxSpinAmount() {
        if (maximumSpinAmount.compareTo(BigInteger.valueOf(20)) > 0) {
            maximumSpinAmount = maximumSpinAmount.add(maximumSpinAmount.divide(BigInteger.valueOf(2)));
        } else {
            maximumSpinAmount = maximumSpinAmount.add(BigInteger.valueOf(4));
        }
        maxIncreased++;
    }

    private BigInteger getMaximumAmountPerLane() {
        return maximumSpinAmount;
    }

    private void updateSpinAmount(BigInteger amount) {
        spinAmount = amount;
        if (spinAmount.compareTo(minimumSpinAmount) <= 0) {
            spinAmount = minimumSpinAmount;
            spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        } else if (spinAmount.compareTo(maximumSpinAmount) >= 0) {
            spinAmount = maximumSpinAmount;
            spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        } else {
            spinAmountLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        }
        spinAmountLabel.updateFormat(spinAmount);
    }

}
