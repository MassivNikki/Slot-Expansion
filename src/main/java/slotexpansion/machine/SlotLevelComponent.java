package slotexpansion.machine;

import slotexpansion.ui.CustomProgressBarUI;
import slotexpansion.ui.PriceLabel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Displays one machine's level, current experience, and progress to the next level.
 */
public class SlotLevelComponent extends JComponent {
    private final PriceLabel slotLevelLabel = new PriceLabel();
    private final JProgressBar progressBar = new JProgressBar();
    private final PriceLabel currentXpLabel = new PriceLabel();
    private final PriceLabel nextLevelXpLabel = new PriceLabel();
    private final JPanel machinePanel;
    private int progressBarWidth;
    private BigInteger amountForLevelUp;
    private boolean levelUpCheckActive = false;
    private final SlotMachine machine;

    public SlotLevelComponent(JPanel machinePanel, SlotMachine machine) {
        this.machinePanel = machinePanel;
        this.machine = machine;
        progressBarWidth = machinePanel.getWidth()-10;
        buildProgressDisplay();
    }

    private void buildProgressDisplay() {
        setSlotLevel(1);
        setSlotXp(BigInteger.valueOf(0));
        slotLevelLabel.setForeground(Color.GREEN);
        slotLevelLabel.setBounds( progressBarWidth / 2, 0, 50, 20);
        slotLevelLabel.setFont(new Font("Arial", Font.BOLD, 15));
        machinePanel.add(slotLevelLabel);

        progressBar.setUI(new CustomProgressBarUI());
        progressBar.setForeground(Color.GREEN);
        progressBar.setBounds(machinePanel.getWidth()/2-progressBarWidth/2, 16, progressBarWidth, 10);
        progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        progressBar.setBackground(null);
        machinePanel.add(progressBar);

        nextLevelXpLabel.setForeground(Color.white);
        nextLevelXpLabel.setBounds(progressBar.getX()+progressBarWidth-100, 25, 100, 15);
        nextLevelXpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        machinePanel.add(nextLevelXpLabel);

        currentXpLabel.updateFormat(machine.state.xp());
        currentXpLabel.setForeground(Color.white);
        currentXpLabel.setBounds(progressBar.getX(), 25, 100, 15);
        currentXpLabel.setHorizontalAlignment(SwingConstants.LEFT);
        machinePanel.add(currentXpLabel);

        calculateNextLevelXpAmount();
    }

    /** Sets the displayed level and recalculates its next-level requirement. */
    public void setSlotLevel(int level) {
        machine.state.setLevel(level);
        slotLevelLabel.setCoins(BigInteger.valueOf(level));
        calculateNextLevelXpAmount();
    }

    /** Replaces the machine's experience and checks whether it has leveled up. */
    public void setSlotXp(BigInteger xp) {
        machine.state.setXp(xp);
        currentXpLabel.updateFormat(machine.state.xp());
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }
    }

    /** Adds spin experience and processes any levels earned by that amount. */
    public void addXp(BigInteger amount){
        machine.state.setXp(machine.state.xp().add(amount));
        currentXpLabel.updateFormat(machine.state.xp());
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }
    }

    protected void checkForLevelUp() {
        // Continue leveling while the remaining experience reaches each threshold.
        while (amountForLevelUp.compareTo(machine.state.xp()) <= 0) {
            levelUpCheckActive = true;
            setSlotLevel(machine.state.level() + 1);
            setSlotXp(machine.state.xp().subtract(amountForLevelUp));
            machine.choosePetForLevelUp();
        }
        levelUpCheckActive = false;
        calculateProgressBar();
    }


    protected void calculateNextLevelXpAmount() {
        // A quadratic threshold keeps early levels attainable while requiring more XP over time.
        BigInteger level = BigInteger.valueOf(machine.state.level());
        amountForLevelUp = level.multiply(level).multiply(BigInteger.valueOf(200));
        nextLevelXpLabel.updateFormat(amountForLevelUp);
    }

    protected void calculateProgressBar() {
        BigDecimal currentXp = new BigDecimal(machine.state.xp());
        BigDecimal xpThreshold = new BigDecimal(amountForLevelUp);
        BigDecimal progressPercent = currentXp.divide(xpThreshold, 10, RoundingMode.FLOOR).multiply(BigDecimal.valueOf(100));
        progressBar.setValue(progressPercent.intValue());
    }

}
