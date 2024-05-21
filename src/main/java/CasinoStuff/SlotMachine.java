package CasinoStuff;

import UpgradeStuff.UpgradeArea;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class SlotMachine {

    protected SlotGrid slotGrid;
    protected UpgradeArea upgradeArea;
    private int slotTier;
    private JFrame mainFrame;
    private JLabel spinAmountLabel;
    private CasinoButton spinBtt;
    private long spinAmount = 1;

    private long spinAmountPerLane = 1;
    private JLabel winLabel;
    private int startYCord;
    private int startXCord;
    private Timer timer;
    private boolean spinCooldownActivated = false;
    private int currentCooldown = 1000;
    private int autoCooldown = 1000;
    private int manualCooldown = 500;
    private JCheckBox changeAutoSpinStatus = new JCheckBox("Auto");

    private Border normalBorder = BorderFactory.createLineBorder(Color.black);

    public SlotMachine(int startXCord, int startYCord, JFrame mainFrame) {
        this.startXCord = startXCord;
        this.startYCord = startYCord;
        this.mainFrame = mainFrame;
        slotGrid = new SlotGrid(mainFrame, startXCord, startYCord);
        upgradeArea = new UpgradeArea(mainFrame, slotGrid, startXCord, startYCord);
    }

    public SlotGrid getSlotGrid() {
        return slotGrid;
    }

    public UpgradeArea getUpgradeArea() {
        return upgradeArea;
    }

    public void instantiate() {
        spinAmountPerLane = PlayerManager.getMinimumCoinsPerLane();
        spinAmount = spinAmountPerLane * slotGrid.getWinLanes();
        setupLabels();
        setupButtons();
        setupSpinButton();
        setupAutoSpinUpgrade();
        upgradeArea.initiate();
    }

    private void setupAutoSpinUpgrade() {
        JLabel autoSpinPriceLabel = new JLabel("500000");
        autoSpinPriceLabel.setForeground(Color.YELLOW);
        autoSpinPriceLabel.setBounds(startXCord + 321, startYCord + 15, 50, 20);
        mainFrame.add(autoSpinPriceLabel);
        CasinoButton autoSpinButton = new CasinoButton("<html>Activate<br>AutoSpin</html>");
        autoSpinButton.setBounds(startXCord + 251, startYCord, 60, 50);
        autoSpinButton.addActionListener(e -> {
            if (Integer.parseInt(autoSpinPriceLabel.getText()) <= PlayerManager.getCoins()) {
                PlayerManager.setCoins(PlayerManager.getCoins() - Integer.parseInt(autoSpinPriceLabel.getText()));
                autoSpinButton.setVisible(false);
                autoSpinPriceLabel.setVisible(false);
                changeAutoSpinStatus.setSelected(false);
                changeAutoSpinStatus.setBounds(startXCord + 251, startYCord, 60, 20);
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
        autoSpinCooldownUpgradePriceLabel.setBounds(startXCord + 251, startYCord + 33, 50, 20);
        mainFrame.add(autoSpinCooldownUpgradePriceLabel);
        CasinoButton autoSpinCooldownUpgradeBtt = new CasinoButton("-50ms");
        autoSpinCooldownUpgradeBtt.setBounds(startXCord + 251, startYCord + 17, 50, 20);
        autoSpinCooldownUpgradeBtt.addActionListener(e -> {
            if (autoCooldown > 200) {
                if (PlayerManager.getCoins() > Integer.parseInt(autoSpinCooldownUpgradePriceLabel.getText())) {
                    autoCooldown -= 50;
                    updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                    updateTimer();
                    timer.start();
                }
            }else{

            }
        });
        mainFrame.add(autoSpinCooldownUpgradeBtt);

    }

    private void updateTextOfButton(CasinoButton button, String text) {
        button.setText(text);
    }


    private void setupLabels() {
        if (spinAmountPerLane == PlayerManager.getMinimumCoinsPerLane()) {
            spinAmountLabel = new JLabel(spinAmount + "(free)");
        } else {
            spinAmountLabel = new JLabel(String.valueOf(spinAmount));
        }
        spinAmountLabel.setForeground(Color.white);
        spinAmountLabel.setBounds(startXCord, startYCord, 100, 20);
        spinAmountLabel.setBorder(normalBorder);
        mainFrame.add(spinAmountLabel);

        winLabel = new JLabel();
        winLabel.setForeground(Color.CYAN);
        winLabel.setBounds(startXCord + 100, startYCord - 20, 100, 20);
        mainFrame.add(winLabel);
    }

    private void updateTimer() {
        if (timer != null) {
            timer.stop();
        }
        if (changeAutoSpinStatus.isSelected()) {
            currentCooldown = autoCooldown - Pet.autoCooldownBonus;
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

    private void setupSpinButton() {
        spinBtt = new CasinoButton("<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
        spinBtt.setBounds(startXCord + 150, startYCord, 100, 35);
        spinBtt.addActionListener(e -> {
            if (!spinCooldownActivated) {
                spinCooldownActivated = true;
                spinBtt.setEnabled(false);
                timer.start();
                spinMachine();
            }
        });
        mainFrame.add(spinBtt);

        JLabel spinCooldownUpgradeLabel = new JLabel("500");
        spinCooldownUpgradeLabel.setForeground(Color.YELLOW);
        spinCooldownUpgradeLabel.setBounds(startXCord + 190, startYCord + 35, 50, 15);
        mainFrame.add(spinCooldownUpgradeLabel);
        CasinoButton autoSpinCooldownUpgradeBtt = new CasinoButton("-50ms");
        autoSpinCooldownUpgradeBtt.setMargin(new Insets(0, 0, 0, 0));
        autoSpinCooldownUpgradeBtt.setBounds(startXCord + 150, startYCord + 35, 40, 15);
        autoSpinCooldownUpgradeBtt.setBorder(BorderFactory.createEmptyBorder());
        autoSpinCooldownUpgradeBtt.addActionListener(e -> {
            if (manualCooldown > 300) {
                manualCooldown -= 50;
                updateTextOfButton(spinBtt, "<html>Spin(" + manualCooldown + "ms)<br>Auto(" + autoCooldown + "ms)</html>");
                updateTimer();
                timer.start();
            }
        });
        mainFrame.add(autoSpinCooldownUpgradeBtt);
        updateTimer();
    }


    private void setupButtons() {
        CasinoButton stakeUpBtt = new CasinoButton("\uD83D\uDD3C");
        stakeUpBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeUpBtt.setBounds(startXCord + 120, startYCord, 20, 20);
        stakeUpBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (spinAmount < Math.pow(3, PlayerManager.getPlayerLevel() - 1) * slotGrid.getWinLanes() && !slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane += PlayerManager.getPlayerLevel();
                ;
                spinAmount = spinAmountPerLane * slotGrid.getWinLanes();
            }
            spinAmountLabel.setText(String.valueOf(spinAmount));
        });
        mainFrame.add(stakeUpBtt);

        CasinoButton stakeDownBtt = new CasinoButton("\uD83D\uDD3D");
        stakeDownBtt.setFont(stakeUpBtt.getFont().deriveFont(22f));
        stakeDownBtt.setBounds(startXCord + 120, startYCord + 30, 20, 20);
        stakeDownBtt.addActionListener(e -> {
            //es kann nur geändert werden, wenn man genügend coins hat und keine freispiele sind
            if (!slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane -= PlayerManager.getPlayerLevel();
                spinAmount = spinAmountPerLane * slotGrid.getWinLanes();
            }
            if (spinAmountPerLane <= PlayerManager.getMinimumCoinsPerLane() && !slotGrid.isFreeSpinsActivated()) {
                spinAmountPerLane = PlayerManager.getMinimumCoinsPerLane();
                spinAmount = spinAmountPerLane * slotGrid.getWinLanes();
            } else if (spinAmountPerLane == PlayerManager.getMinimumCoinsPerLane()) {
                spinAmountLabel.setText(spinAmount + " (free)");
            } else {
                spinAmountLabel.setText(String.valueOf(spinAmount));
            }
        });
        mainFrame.add(stakeDownBtt);

    }

    private void spinMachine() {
        long wholeSpinWin = 0;
        if (spinAmountPerLane == PlayerManager.getMinimumCoinsPerLane()) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        } else if (spinAmount <= PlayerManager.getCoins() || slotGrid.isFreeSpinsActivated()) {
            wholeSpinWin = slotGrid.spinSlotMachine(spinAmount);
        }
        //wenn nichts gewonnen wurde wird auch nichts angezeigt
        if (wholeSpinWin == 0) {
            winLabel.setVisible(false);
        } else {
            //ansonsten bekommt der spieler den gewinn als xp
            PlayerManager.setPlayerXp(PlayerManager.getPlayerXp() + wholeSpinWin);
            winLabel.setText(String.valueOf(wholeSpinWin));
            winLabel.setVisible(true);
        }
    }
}
