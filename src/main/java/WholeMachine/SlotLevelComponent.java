package WholeMachine;

import CasinoStuff.CustomProgressBarUI;
import CasinoStuff.PriceLabel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class SlotLevelComponent extends JComponent {
    private final JLabel slotLevelLabel = new JLabel();
    private final JProgressBar progressBarLabel = new JProgressBar();
    private final PriceLabel slotXpLabel = new PriceLabel();
    private final PriceLabel xpForLevelUpLabel = new PriceLabel();
    private JPanel machinePanel;
    private int progressBarWidth;
    protected int slotLevel = 0;
    protected BigInteger slotXp = BigInteger.valueOf(0);
    private BigInteger amountForLevelUp;
    private boolean levelUpCheckActive = false;
    private SlotMachine machine;
    public SlotLevelComponent(JPanel machinePanel, SlotMachine machine) {
        this.machinePanel = machinePanel;
        this.machine = machine;
        progressBarWidth = machinePanel.getWidth()-10;
        setupXpLabels();
    }
    private void setupXpLabels() {
        setSlotLevel(1);
        setSlotXp(BigInteger.valueOf(0));
        slotLevelLabel.setForeground(Color.GREEN);
        slotLevelLabel.setBounds( progressBarWidth / 2, 0, 50, 20);
        slotLevelLabel.setFont(new Font("Arial", Font.BOLD, 15));
        machinePanel.add(slotLevelLabel);

        progressBarLabel.setUI(new CustomProgressBarUI());
        progressBarLabel.setForeground(Color.GREEN);
        progressBarLabel.setBounds(machinePanel.getWidth()/2-progressBarWidth/2, 16, progressBarWidth, 10);
        progressBarLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        progressBarLabel.setBackground(null);
        machinePanel.add(progressBarLabel);

        xpForLevelUpLabel.setForeground(Color.white);
        xpForLevelUpLabel.setBounds(progressBarLabel.getX()+progressBarWidth-100, 25, 100, 15);
        xpForLevelUpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        machinePanel.add(xpForLevelUpLabel);

        slotXpLabel.updateFormat(slotXp);
        slotXpLabel.setForeground(Color.white);
        slotXpLabel.setBounds(progressBarLabel.getX(), 25, 100, 15);
        slotXpLabel.setHorizontalAlignment(SwingConstants.LEFT);
        machinePanel.add(slotXpLabel);

        calculateNextLevelXpAmount();
    }

    public void setSlotLevel(int givenPlayerLevel) {
        slotLevel = givenPlayerLevel;
        machine.slotLevel = slotLevel;
        slotLevelLabel.setText(String.valueOf(givenPlayerLevel));
        calculateNextLevelXpAmount();
    }

    public void setSlotXp(BigInteger givenPlayerXp) {
        slotXp = givenPlayerXp;
        slotXpLabel.updateFormat(slotXp);
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }
    }
    public void addXp(BigInteger amount){
        slotXp = slotXp.add(amount);
        slotXpLabel.updateFormat(slotXp);
        if (!levelUpCheckActive) {
            checkForLevelUp();
        }
    }

    protected void checkForLevelUp() {
        //solange die xp des spieler die levelup grenze überschreitet wird ein levelup durchgeführt
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
        //berechnet die anzahl die zum nächsten level up zu erreichen ist
        amountForLevelUp = BigInteger.valueOf(((long) (Math.pow(slotLevel, 7) / 3)) + 200);
        xpForLevelUpLabel.updateFormat(amountForLevelUp);
    }

    protected void calculateProgressBar() {
        //es wird die Anzeige aktualisiert, damit man weis wie weit die xp sind
        StringBuilder builder = new StringBuilder();
        BigDecimal tempXp = new BigDecimal(slotXp);
        BigDecimal tempMaxXp = new BigDecimal(amountForLevelUp);
        BigDecimal progress = tempXp.divide(tempMaxXp, 10, RoundingMode.FLOOR).multiply(BigDecimal.valueOf(100));
        progressBarLabel.setValue(progress.intValue());
        BigDecimal bars = progress.multiply(BigDecimal.valueOf(progressBarWidth / 10));
        for (int i = 0; bars.compareTo(BigDecimal.valueOf(i)) > 0; i++) {
            builder.append("█");
        }
    }

}
