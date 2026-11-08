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
    private final JLabel slotLevelLabel = new JLabel();
    private final JProgressBar progressBar = new JProgressBar();
    private final PriceLabel currentXpLabel = new PriceLabel();
    private final PriceLabel nextLevelXpLabel = new PriceLabel();
    private final JPanel machinePanel;
    private int progressBarWidth;
    protected int slotLevel = 0;
    protected BigInteger slotXp = BigInteger.valueOf(0);
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

        currentXpLabel.updateFormat(slotXp);
        currentXpLabel.setForeground(Color.white);
        currentXpLabel.setBounds(progressBar.getX(), 25, 100, 15);
        currentXpLabel.setHorizontalAlignment(SwingConstants.LEFT);
        machinePanel.add(currentXpLabel);

        calculateNextLevelXpAmount();
    }

    /** Sets the displayed level and recalculates its next-level requirement. */
    public void setSlotLevel(int level) {
        slotLevel = level;
        machine.slotLevel = slotLevel;
        slotLevelLabel.setText(String.valueOf(level));
        calculateNextLevelXpAmount();
    }

    /** Replaces the machine's experience and checks whether it has leveled up. */
    public void setSlotXp(BigInteger xp) {
        slotXp = xp;
        currentXpLabel.updateFormat(slotXp);
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }
    }

    /** Adds spin experience and processes any levels earned by that amount. */
    public void addXp(BigInteger amount){
        slotXp = slotXp.add(amount);
        currentXpLabel.updateFormat(slotXp);
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }
    }

    protected void checkForLevelUp() {
        // Continue leveling while the remaining experience reaches each threshold.
        while (amountForLevelUp.compareTo(slotXp) <= 0) {
            levelUpCheckActive = true;
            slotLevel++;
            setSlotXp(slotXp.subtract(amountForLevelUp));
            calculateNextLevelXpAmount();
            machine.choosePetForLevelUp();
            machine.slotLevel = slotLevel;
        }
        levelUpCheckActive = false;
        slotLevelLabel.setText(String.valueOf(slotLevel));
        calculateProgressBar();
    }


    protected void calculateNextLevelXpAmount() {
        // A quadratic threshold keeps early levels attainable while requiring more XP over time.
        BigInteger level = BigInteger.valueOf(slotLevel);
        amountForLevelUp = level.multiply(level).multiply(BigInteger.valueOf(200));
        nextLevelXpLabel.updateFormat(amountForLevelUp);
    }

    protected void calculateProgressBar() {
        BigDecimal currentXp = new BigDecimal(slotXp);
        BigDecimal xpThreshold = new BigDecimal(amountForLevelUp);
        BigDecimal progressPercent = currentXp.divide(xpThreshold, 10, RoundingMode.FLOOR).multiply(BigDecimal.valueOf(100));
        progressBar.setValue(progressPercent.intValue());
    }

}
