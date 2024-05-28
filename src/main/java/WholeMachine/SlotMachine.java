package WholeMachine;

import CasinoStuff.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;

public class SlotMachine {

    private static final int progressBarWidth = 350;
    private boolean levelUpCheckActive = false;
    private final JFrame mainFrame;
    private final JLabel petImageLabel = new JLabel();
    private final JLabel slotLevelLabel = new JLabel();
    private final JLabel progressBarLabel = new JLabel();
    private final PriceLabel slotXpLabel = new PriceLabel();
    private final PriceLabel xpForLevelUpLabel = new PriceLabel();
    private final int startYCord;
    private final int startXCord;
    private final JCheckBox changeAutoSpinStatus = new JCheckBox("Auto");
    private final int[] petChances = {1000, 652, 422, 272, 149, 114, 84, 59, 39, 24, 14, 7, 2, 0};
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);
    protected SlotGrid slotGrid;
    protected UpgradeArea upgradeArea;
    private final PriceLabel spinAmountLabel = new PriceLabel();
    private CasinoButton spinBtt;
    private BigInteger spinAmount = BigInteger.valueOf(1);
    public Pet pet;
    protected int slotTier = 1;
    public int slotLevel = 0;
    public BigInteger slotXp = BigInteger.valueOf(0);
    private BigInteger amountForLevelUp;
    private BigInteger spinAmountPerLane = BigInteger.valueOf(1);
    public boolean autoSpinUnlocked = false;
    private JLabel winLabel = new JLabel();
    private Timer timer;
    private boolean spinCooldownActivated = false;
    private int currentCooldown = 1000;
    public int autoCooldown = 1000;
    public int manualCooldown = 500;

    public SlotMachine(int startXCord, int startYCord, JFrame mainFrame, int tier) {
        this.startXCord = startXCord;
        this.startYCord = startYCord;
        this.mainFrame = mainFrame;
        slotTier = tier;
        slotGrid = new SlotGrid(mainFrame, startXCord, startYCord, this);
        upgradeArea = new UpgradeArea(mainFrame, slotGrid, startXCord, startYCord, this);
        setPet(new Pet(-1));
        generateInitialComponentsWithoutSavedDate();
    }

    public int getSlotTier() {
        return slotTier;
    }

    public void updateComponentsWithData() {
        spinAmountPerLane = BigInteger.valueOf(getMinimumCoinsPerLane());
        spinAmount = spinAmountPerLane.multiply(BigInteger.valueOf(slotGrid.getWinLanes()));
        if (spinAmountPerLane.compareTo(BigInteger.valueOf(getMinimumCoinsPerLane())) == 0) {
            spinAmountLabel.setText(spinAmount + "(free)");
        } else {
            spinAmountLabel.updateFormat(spinAmount);
        }
        arrangeLabelsBasedOnSize();
        setupAutoSpinUpgrade();
        spinBtt.setText("<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
        upgradeArea.initiate();
    }

    private void generateInitialComponentsWithoutSavedDate() {
        setupLabels();
        setupSpinButton();
        setupStakeButtons();
    }

    public SlotGrid getSlotGrid() {
        return slotGrid;
    }

    public UpgradeArea getUpgradeArea() {
        return upgradeArea;
    }

    private void setupAutoSpinUpgrade() {
        if (!autoSpinUnlocked) {
            CasinoUpgradeButton autoSpinUnlockButton =
                    new CasinoUpgradeButton(BigInteger.valueOf(500000), slotTier, "<html>Unlock<br>AutoSpin</html>",
                            startXCord + 290, startYCord, mainFrame, 60, 50, PriceLabelPosition.UNDER);
            autoSpinUnlockButton.addCustomActionListener(e -> {
                    PlayerManager.decreaseCoins(autoSpinUnlockButton.getPrice());
                    addAutoSpinCheckboxToFrame();
                    autoSpinUnlockButton.removeFromMainframe(mainFrame);
                    mainFrame.repaint();
            });
            mainFrame.add(autoSpinUnlockButton);
        } else {
            addAutoSpinCheckboxToFrame();
        }

    }

    private void setupSpinButton() {
        spinBtt = new CasinoButton();
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
        CasinoUpgradeButton spinCoolDownBtt =
                new CasinoUpgradeButton(BigInteger.valueOf(5000), slotTier, "-50ms",
                        startXCord + 190, startYCord + 35, mainFrame, 50, 20, PriceLabelPosition.RIGHT);
        spinCoolDownBtt.addCustomActionListener(e -> {
            manualCooldown -= 50;
            updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
            updateTimer();
            timer.start();
            if (manualCooldown == 200) {
                spinCoolDownBtt.removeFromMainframe(mainFrame);
                mainFrame.repaint();
            }
        });
        mainFrame.add(spinCoolDownBtt);
        updateTimer();
    }

    private void addAutoSpinCheckboxToFrame() {
        changeAutoSpinStatus.setSelected(false);
        changeAutoSpinStatus.setBounds(startXCord + 290, startYCord - 3, 60, 20);
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
        CasinoUpgradeButton autoSpinCooldownUpgradeButton =
                new CasinoUpgradeButton(BigInteger.valueOf(50000), slotTier, "-50ms",
                        startXCord + 290, startYCord + 15, mainFrame, 50, 20, PriceLabelPosition.UNDER);
        autoSpinCooldownUpgradeButton.addCustomActionListener(e -> {
                autoCooldown -= 50;
                updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                updateTimer();
                timer.start();
                if (autoCooldown == 200) {
                    autoSpinCooldownUpgradeButton.removeFromMainframe(mainFrame);
                    mainFrame.repaint();
                }
        });
        mainFrame.add(autoSpinCooldownUpgradeButton);
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
        setSlotXp(BigInteger.valueOf(0));
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
        xpForLevelUpLabel.setBounds(startXCord + progressBarWidth, startYCord - 18, 80, 15);
        mainFrame.add(xpForLevelUpLabel);

        slotXpLabel.updateFormat(slotXp);
        slotXpLabel.setForeground(Color.white);
        slotXpLabel.setBounds(startXCord, startYCord - 18, 80, 15);
        mainFrame.add(slotXpLabel);

        calculateNextLevelXpAmount();
    }

    private void setupStakeButtons() {
        CasinoButton stakeUpBtt = new CasinoButton("\uD83D\uDD3C");
        stakeUpBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeUpBtt.setBackground(new Color(0xD4B483));
        stakeUpBtt.setBounds(startXCord + 120, startYCord, 20, 20);
        stakeUpBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = spinAmountPerLane.add(BigInteger.valueOf(slotLevel));
                if (spinAmountPerLane.compareTo(getMaximumAmountPerLane()) > 0) {
                    spinAmountPerLane = getMaximumAmountPerLane();
                }
                calculateSpinAmount();
            }
            spinAmountLabel.updateFormat(spinAmount);
        });
        mainFrame.add(stakeUpBtt);

        CasinoButton stakeDownBtt = new CasinoButton("\uD83D\uDD3D");
        stakeDownBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeDownBtt.setBackground(new Color(0xD4B483));
        stakeDownBtt.setBounds(startXCord + 140, startYCord, 20, 20);
        stakeDownBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = spinAmountPerLane.subtract(BigInteger.valueOf(slotLevel));
                if (spinAmountPerLane.compareTo(BigInteger.valueOf(getMinimumCoinsPerLane())) <= 0) {
                    spinAmountPerLane = BigInteger.valueOf(getMinimumCoinsPerLane());
                }
                calculateSpinAmount();
            }

            if (spinAmountPerLane.compareTo(BigInteger.valueOf(getMinimumCoinsPerLane())) == 0) {
                spinAmountLabel.setText(spinAmount + "(free)");
            } else {
                spinAmountLabel.updateFormat(spinAmount);
            }
        });
        mainFrame.add(stakeDownBtt);

        CasinoButton maxStakeBtt = new CasinoButton("max");
        maxStakeBtt.setBackground(new Color(0xD4B483));
        maxStakeBtt.setBounds(startXCord + 160, startYCord, 30, 20);
        maxStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = getMaximumAmountPerLane();
                calculateSpinAmount();
                if (spinAmount.compareTo(PlayerManager.getCoins()) > 0 && PlayerManager.getCoins().compareTo(BigInteger.valueOf(0)) > 0) {
                    spinAmountPerLane = PlayerManager.getCoins().divide(BigInteger.valueOf(slotGrid.getWinLanes()));
                    if (spinAmountPerLane.compareTo(BigInteger.valueOf(getMinimumCoinsPerLane())) < 0) {
                        spinAmountPerLane = BigInteger.valueOf(getMinimumCoinsPerLane());
                    }
                    calculateSpinAmount();
                }
                spinAmountLabel.updateFormat(spinAmount);
            }
        });
        mainFrame.add(maxStakeBtt);

        CasinoButton minStakeBtt = new CasinoButton("min");
        minStakeBtt.setBackground(new Color(0xD4B483));
        minStakeBtt.setBounds(startXCord + 160, startYCord + 20, 30, 20);
        minStakeBtt.addActionListener(e -> {
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = BigInteger.valueOf(getMinimumCoinsPerLane());
                calculateSpinAmount();
                spinAmountLabel.setText(spinAmount + "(free)");
            }
        });
        mainFrame.add(minStakeBtt);
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
        BigInteger wholeSpinWin = BigInteger.valueOf(0);
        if (spinAmountPerLane.compareTo(BigInteger.valueOf(getMinimumCoinsPerLane())) == 0) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        } else if (spinAmount.compareTo(PlayerManager.getCoins()) <= 0 || slotGrid.isFreeSpinsActivated()) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        }
        //wenn nichts gewonnen wurde wird auch nichts angezeigt
        if (wholeSpinWin.compareTo(BigInteger.valueOf(0)) == 0) {
            winLabel.setVisible(false);
        } else {
            //ansonsten bekommt der spieler den gewinn als xp
            setSlotXp(slotXp.add(wholeSpinWin));
            winLabel.setText(String.valueOf(wholeSpinWin));
            winLabel.setVisible(true);
        }
    }

    private void choosePetForLevelUp() {
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
        petImageLabel.setBounds(startXCord + 50, startYCord + 20, 80, 40);
        petImageLabel.setVisible(false);
        mainFrame.add(petImageLabel);
        setPetImage(pet.getIcon());
    }

    public void setSlotLevel(int givenPlayerLevel) {
        slotLevel = givenPlayerLevel;
        slotLevelLabel.setText(String.valueOf(givenPlayerLevel));
        calculateNextLevelXpAmount();
    }

    public void setSlotXp(BigInteger givenPlayerXp) {
        int coinAmount = String.valueOf(slotXp).length();
        slotXp = givenPlayerXp;
        if (coinAmount < String.valueOf(slotXp).length()) {
            arrangeLabelsBasedOnSize();
        }
        slotXpLabel.updateFormat(slotXp);
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }

    }

    public long getMinimumCoinsPerLane() {
        if (slotLevel > 3) {
            return slotLevel / 2;
        } else {
            return 1;
        }
    }

    private BigInteger getMaximumAmountPerLane() {
        if (slotLevel > 1) {
            return BigInteger.valueOf((long) Math.pow(3, slotLevel - 1));
        } else {
            return BigInteger.valueOf((long) Math.pow(3, slotLevel));
        }

    }

    private void calculateSpinAmount() {
        spinAmount = spinAmountPerLane.multiply(BigInteger.valueOf(slotGrid.getWinLanes()));
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
        while (amountForLevelUp.compareTo(slotXp) <= 0) {
            levelUpCheckActive = true;
            slotLevel++;
            setSlotXp(slotXp.subtract(amountForLevelUp));
            calculateNextLevelXpAmount();
            arrangeLabelsBasedOnSize();
            choosePetForLevelUp();
        }
        levelUpCheckActive = false;
        slotLevelLabel.setText(String.valueOf(slotLevel));
        progressBarLabel.setText("");
        calculateProgressBar();
    }

    protected void calculateNextLevelXpAmount() {
        //berechnet die anzahl die zum nächsten level up zu erreichen ist
        amountForLevelUp = BigInteger.valueOf(((long) (Math.pow(slotLevel, 7) / 3)) + 200);
        xpForLevelUpLabel.updateFormat(amountForLevelUp);
    }

    protected void calculateProgressBar() {
        //es wird die Anzeige aktualisiert, damit man weis wie weit die xp sind
        StringBuilder builder = new StringBuilder();
        BigDecimal tempXp = new BigDecimal(slotXp);
        BigDecimal tempMaxXp = new BigDecimal(amountForLevelUp);
        BigDecimal progress = tempXp.divide(tempMaxXp, 10, RoundingMode.FLOOR);
        BigDecimal bars = progress.multiply(BigDecimal.valueOf(progressBarWidth / 10));
        for (int i = 0; bars.compareTo(BigDecimal.valueOf(i)) > 0; i++) {
            builder.append("█");
        }
        progressBarLabel.setText(builder.toString());
    }
}
