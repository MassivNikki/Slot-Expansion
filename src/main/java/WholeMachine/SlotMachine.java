package WholeMachine;

import CasinoStuff.CasinoButton;
import CasinoStuff.PlayerManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Random;

public class SlotMachine {

    private static final int progressBarWidth = 350;
    private final JFrame mainFrame;
    private final JLabel petImageLabel = new JLabel();
    private final JLabel slotLevelLabel = new JLabel();
    private final JLabel progressBarLabel = new JLabel();
    private final JLabel slotXpLabel = new JLabel();
    private final JLabel xpForLevelUpLabel = new JLabel();
    private final int startYCord;
    private final int startXCord;
    private final JCheckBox changeAutoSpinStatus = new JCheckBox("Auto");
    private final int[] petChances = {1000, 652, 422, 272, 149, 114, 84, 59, 39, 24, 14, 7, 2, 0};
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);
    protected SlotGrid slotGrid;
    protected UpgradeArea upgradeArea;
    private int slotTier;
    private JLabel spinAmountLabel = new JLabel();
    private CasinoButton spinBtt;
    private long spinAmount = 1;
    private Pet pet;
    private int slotLevel = 0;
    private long slotXp = 0;
    private long amountForLevelUp;
    private long spinAmountPerLane = 1;
    private JLabel winLabel = new JLabel();
    private Timer timer;
    private boolean spinCooldownActivated = false;
    private int currentCooldown = 1000;
    private int autoCooldown = 1000;
    private int manualCooldown = 500;

    public SlotMachine(int startXCord, int startYCord, JFrame mainFrame) {
        this.startXCord = startXCord;
        this.startYCord = startYCord;
        this.mainFrame = mainFrame;
        slotGrid = new SlotGrid(mainFrame, startXCord, startYCord, this);
        upgradeArea = new UpgradeArea(mainFrame, slotGrid, startXCord, startYCord);
        pet = new Pet(-1);
        generateInitialComponentsWithoutSavedDate();
    }

    public void updateComponentsWithData() {
        spinAmountPerLane = getMinimumCoinsPerLane();
        spinAmount = spinAmountPerLane * slotGrid.getWinLanes();
        if (spinAmountPerLane == getMinimumCoinsPerLane()) {
            spinAmountLabel.setText(spinAmount + "(free)");
        } else {
            spinAmountLabel.setText(String.valueOf(spinAmount));
        }
        arrangeLabelsBasedOnSize();
        upgradeArea.initiate();
    }


    private void generateInitialComponentsWithoutSavedDate() {
        setupLabels();
        setupSpinButton();
        setupAutoSpinUpgrade();
        setupStakeButtons();
    }

    public SlotGrid getSlotGrid() {
        return slotGrid;
    }

    public UpgradeArea getUpgradeArea() {
        return upgradeArea;
    }

    private void setupAutoSpinUpgrade() {
        JLabel autoSpinPriceLabel = new JLabel("5000000");
        autoSpinPriceLabel.setForeground(Color.YELLOW);
        autoSpinPriceLabel.setBounds(startXCord + 291, startYCord + 50, 50, 20);
        mainFrame.add(autoSpinPriceLabel);
        CasinoButton autoSpinButton = new CasinoButton("<html>Activate<br>AutoSpin</html>");
        autoSpinButton.setBounds(startXCord + 291, startYCord, 60, 50);
        autoSpinButton.addActionListener(e -> {
            if (Integer.parseInt(autoSpinPriceLabel.getText()) <= PlayerManager.getCoins()) {
                PlayerManager.setCoins(PlayerManager.getCoins() - Integer.parseInt(autoSpinPriceLabel.getText()));
                autoSpinButton.setVisible(false);
                autoSpinPriceLabel.setVisible(false);
                changeAutoSpinStatus.setSelected(false);
                changeAutoSpinStatus.setBounds(startXCord + 291, startYCord, 60, 20);
                changeAutoSpinStatus.setBorder(BorderFactory.createEmptyBorder());
                changeAutoSpinStatus.setMargin(new Insets(0, 0, 0, 0));
                changeAutoSpinStatus.setForeground(Color.white);
                changeAutoSpinStatus.setFocusPainted(false);
                changeAutoSpinStatus.setContentAreaFilled(false);
                changeAutoSpinStatus.addItemListener(f -> {
                    spinBtt.setEnabled(!changeAutoSpinStatus.isSelected());
                    updateTimer();
                    timer.setRepeats(changeAutoSpinStatus.isSelected());
                    timer.start();
                });
                mainFrame.add(changeAutoSpinStatus);
            }

        });
        mainFrame.add(autoSpinButton);

        JLabel autoSpinCooldownUpgradePriceLabel = new JLabel("50000");
        autoSpinCooldownUpgradePriceLabel.setForeground(Color.YELLOW);
        autoSpinCooldownUpgradePriceLabel.setBounds(startXCord + 291, startYCord + 33, 50, 20);
        mainFrame.add(autoSpinCooldownUpgradePriceLabel);
        CasinoButton autoSpinCooldownUpgradeBtt = new CasinoButton("-50ms");
        autoSpinCooldownUpgradeBtt.setBounds(startXCord + 291, startYCord + 17, 50, 20);
        autoSpinCooldownUpgradeBtt.addActionListener(e -> {
            if (autoCooldown > 200) {
                if (PlayerManager.getCoins() > Integer.parseInt(autoSpinCooldownUpgradePriceLabel.getText())) {
                    autoCooldown -= 50;
                    updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                    updateTimer();
                    timer.start();
                }
            }
        });
        mainFrame.add(autoSpinCooldownUpgradeBtt);

    }

    private void setupLabels() {

        spinAmountLabel.setForeground(Color.white);
        spinAmountLabel.setBounds(startXCord, startYCord, 100, 20);
        spinAmountLabel.setBorder(normalBorder);
        mainFrame.add(spinAmountLabel);

        winLabel.setForeground(Color.CYAN);
        winLabel.setBounds(startXCord + 100, startYCord - 20, 100, 20);
        mainFrame.add(winLabel);
        setupXpLabels();
    }

    private void setupXpLabels() {
        setSlotLevel(1);
        setSlotXp(0);
        slotLevelLabel.setForeground(Color.GREEN);
        slotLevelLabel.setBounds(startXCord + progressBarWidth / 2, startYCord - 45, 50, 20);
        slotLevelLabel.setFont(new Font("Arial", Font.BOLD, 15));
        mainFrame.add(slotLevelLabel);

        progressBarLabel.setForeground(Color.GREEN);
        progressBarLabel.setBounds(startXCord, startYCord - 30, progressBarWidth, 10);
        progressBarLabel.setFont(new Font("Arial", Font.BOLD, 14));
        progressBarLabel.setBorder(normalBorder);
        mainFrame.add(progressBarLabel);

        xpForLevelUpLabel.setForeground(Color.white);
        xpForLevelUpLabel.setBounds(startXCord + progressBarWidth, startYCord - 18, 60, 15);
        mainFrame.add(xpForLevelUpLabel);

        slotXpLabel.setText(String.valueOf(slotXp));
        slotXpLabel.setForeground(Color.white);
        slotXpLabel.setBounds(startXCord, startYCord - 18, 200, 15);
        mainFrame.add(slotXpLabel);

        calculateNextLevelXpAmount();
    }

    private void setupStakeButtons() {
        CasinoButton stakeUpBtt = new CasinoButton("\uD83D\uDD3C");
        stakeUpBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeUpBtt.setBounds(startXCord + 120, startYCord, 20, 20);
        stakeUpBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane += slotLevel;
                if (spinAmountPerLane > getMaximumAmountPerLane()) {
                    spinAmountPerLane = getMaximumAmountPerLane();
                }
                calculateSpinAmount();
            }
            spinAmountLabel.setText(String.valueOf(spinAmount));
        });
        mainFrame.add(stakeUpBtt);

        CasinoButton stakeDownBtt = new CasinoButton("\uD83D\uDD3D");
        stakeDownBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeDownBtt.setBounds(startXCord + 140, startYCord, 20, 20);
        stakeDownBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane -= slotLevel;
                if (spinAmountPerLane < getMinimumCoinsPerLane()) {
                    spinAmountPerLane = getMinimumCoinsPerLane();
                }
                calculateSpinAmount();
            }

            if (spinAmountPerLane == getMinimumCoinsPerLane()) {
                spinAmountLabel.setText(spinAmount + "(free)");
            } else {
                spinAmountLabel.setText(String.valueOf(spinAmount));
            }
        });
        mainFrame.add(stakeDownBtt);

        CasinoButton maxStakeBtt = new CasinoButton("max");
        maxStakeBtt.setBounds(startXCord + 160, startYCord, 30, 20);
        maxStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = getMaximumAmountPerLane();
                calculateSpinAmount();
                if (spinAmount > PlayerManager.getCoins()) {
                    spinAmountPerLane = PlayerManager.getCoins() / slotGrid.getWinLanes();
                    if (spinAmountPerLane < getMinimumCoinsPerLane()) {
                        spinAmountPerLane = getMinimumCoinsPerLane();
                    }
                    calculateSpinAmount();
                }
                spinAmountLabel.setText(String.valueOf(spinAmount));
            }
        });
        mainFrame.add(maxStakeBtt);

        CasinoButton minStakeBtt = new CasinoButton("min");
        minStakeBtt.setBounds(startXCord + 160, startYCord + 20, 30, 20);
        minStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = getMinimumCoinsPerLane();
                calculateSpinAmount();
                spinAmountLabel.setText(spinAmount + "(free)");
            }
        });
        mainFrame.add(minStakeBtt);
    }

    private void setupSpinButton() {
        spinBtt = new CasinoButton("<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
        spinBtt.setBounds(startXCord + 190, startYCord, 100, 35);
        spinBtt.addActionListener(e -> {
            if (!spinCooldownActivated) {
                spinCooldownActivated = true;
                spinBtt.setEnabled(false);
                timer.start();
                spinMachine();
            }
        });
        mainFrame.add(spinBtt);

        JLabel spinCooldownUpgradePriceLabel = new JLabel("500");
        spinCooldownUpgradePriceLabel.setForeground(Color.YELLOW);
        spinCooldownUpgradePriceLabel.setBounds(startXCord + 230, startYCord + 35, 50, 15);
        mainFrame.add(spinCooldownUpgradePriceLabel);
        CasinoButton autoSpinCooldownUpgradeBtt = new CasinoButton("-50ms");
        autoSpinCooldownUpgradeBtt.setMargin(new Insets(0, 0, 0, 0));
        autoSpinCooldownUpgradeBtt.setBounds(startXCord + 190, startYCord + 35, 40, 15);
        autoSpinCooldownUpgradeBtt.setBorder(BorderFactory.createEmptyBorder());
        autoSpinCooldownUpgradeBtt.addActionListener(e -> {
            if (Integer.parseInt(spinCooldownUpgradePriceLabel.getText()) < PlayerManager.getCoins()) {
                if (manualCooldown > 300) {
                    manualCooldown -= 50;
                    updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                    updateTimer();
                    timer.start();
                }
            }

        });
        mainFrame.add(autoSpinCooldownUpgradeBtt);
        updateTimer();
    }

    private void updateTextOfButton(CasinoButton button, String text) {
        button.setText(text);
    }

    private void updateTimer() {
        if (timer != null) {
            timer.stop();
        }
        if (changeAutoSpinStatus.isSelected()) {
            currentCooldown = autoCooldown - pet.autoCooldownBonus;
        } else {
            currentCooldown = manualCooldown;
        }
        timer = new Timer(currentCooldown, e -> {
            if (changeAutoSpinStatus.isSelected()) {
                spinMachine();
            } else {
                spinCooldownActivated = false;
                spinBtt.setEnabled(true);
            }
        });
        if (!changeAutoSpinStatus.isSelected()) {
            timer.setRepeats(false);
        }
    }

    private void spinMachine() {
        long wholeSpinWin = 0;
        if (spinAmountPerLane == getMinimumCoinsPerLane()) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        } else if (spinAmount <= PlayerManager.getCoins() || slotGrid.isFreeSpinsActivated()) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        }
        //wenn nichts gewonnen wurde wird auch nichts angezeigt
        if (wholeSpinWin == 0) {
            winLabel.setVisible(false);
        } else {
            //ansonsten bekommt der spieler den gewinn als xp
            setSlotXp(slotXp + wholeSpinWin);
            winLabel.setText(String.valueOf(wholeSpinWin));
            winLabel.setVisible(true);
        }
    }

    private void choosePetForLevelUp() {
        int rand = new Random().nextInt(0, 1000);
        int tier = 0;
        for (int i = 0; i < petChances.length - 1; i++) {
            if (rand < petChances[i] && rand > petChances[i + 1]) {
                tier = i;
            }
        }
        if (pet.petMultis[tier] > pet.getMulti()) {
            pet.setTier(tier);
            setPetImage(pet.getIcon());
        }
        System.out.println("Won a pet with multi of: " + pet.petMultis[tier]);
    }

    protected void setPetImage(ImageIcon icon) {
        petImageLabel.setVisible(true);
        petImageLabel.setIcon(icon);
        petImageLabel.setText(pet.getMulti() + "x");
        petImageLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        petImageLabel.setForeground(Color.orange);
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        petImageLabel.setBackground(Color.white);
        petImageLabel.setBounds(startXCord + 50, startYCord + 20, 80, 40);
        petImageLabel.setVisible(false);
        mainFrame.add(petImageLabel);
        setPetImage(pet.getIcon());
    }

    public int getPetTier() {
        return pet.petTier;
    }

    public int getSlotLevel() {
        return slotLevel;
    }

    public void setSlotLevel(int givenPlayerLevel) {
        slotLevel = givenPlayerLevel;
        slotLevelLabel.setText(String.valueOf(givenPlayerLevel));
        calculateNextLevelXpAmount();
    }

    public long getSlotXp() {
        return slotXp;
    }

    public void setSlotXp(long givenPlayerXp) {
        int coinAmount = String.valueOf(slotXp).length();
        slotXp = givenPlayerXp;
        if (coinAmount < String.valueOf(slotXp).length()) {
            arrangeLabelsBasedOnSize();
        }
        slotXpLabel.setText(String.valueOf(slotXp));
        checkForLevelUp();
    }

    public int getMinimumCoinsPerLane() {
        if (slotLevel > 3) {
            return slotLevel / 2;
        } else {
            return 1;
        }
    }

    private long getMaximumAmountPerLane() {
        if (slotLevel > 1) {
            return (long) Math.pow(3, slotLevel - 1);
        } else {
            return (long) Math.pow(3, slotLevel);
        }

    }

    private void calculateSpinAmount() {
        spinAmount = spinAmountPerLane * slotGrid.getWinLanes();
    }

    protected void arrangeLabelsBasedOnSize() {
        FontMetrics fontMetrics = xpForLevelUpLabel.getFontMetrics(xpForLevelUpLabel.getFont());
        int textWidth = fontMetrics.stringWidth(xpForLevelUpLabel.getText());
        xpForLevelUpLabel.setPreferredSize(new Dimension(textWidth, xpForLevelUpLabel.getHeight()));
        xpForLevelUpLabel.setBounds(startXCord + progressBarWidth - textWidth, startYCord - 18, textWidth, 15);

        fontMetrics = slotLevelLabel.getFontMetrics(slotLevelLabel.getFont());
        textWidth = fontMetrics.stringWidth(slotLevelLabel.getText());
        slotLevelLabel.setPreferredSize(new Dimension(textWidth, slotLevelLabel.getHeight()));
        slotLevelLabel.setBounds(startXCord + progressBarWidth / 2, startYCord - 45, textWidth, 15);
    }

    protected void checkForLevelUp() {
        //solange die xp des spieler die levelup grenze überschreitet wird ein levelup durchgeführt
        while (amountForLevelUp <= slotXp) {
            slotLevel++;
            setSlotXp(slotXp - amountForLevelUp);
            PlayerManager.setCoins(PlayerManager.getCoins() + slotLevel * 100L);
            calculateNextLevelXpAmount();
            arrangeLabelsBasedOnSize();
            choosePetForLevelUp();
        }
        slotLevelLabel.setText(String.valueOf(slotLevel));
        slotXpLabel.setText(String.valueOf(slotXp));
        xpForLevelUpLabel.setText(String.valueOf(amountForLevelUp));
        progressBarLabel.setText("");
        calculateProgressBar();
    }

    protected void calculateNextLevelXpAmount() {
        //berechnet die anzahl die zum nächsten level up zu erreichen ist
        amountForLevelUp = (long) (Math.pow(slotLevel, 6) / 3) + 200;
        xpForLevelUpLabel.setText(String.valueOf(amountForLevelUp));
    }

    protected void calculateProgressBar() {
        //es wird die Anzeige aktualisiert, damit man weis wie weit die xp sind
        StringBuilder builder = new StringBuilder();
        double progress = (double) slotXp / amountForLevelUp;
        int bars = (int) (progress * progressBarWidth / 10);
        for (int i = 0; i < bars; i++) {
            builder.append("█");
        }
        progressBarLabel.setText(builder.toString());
    }
}
