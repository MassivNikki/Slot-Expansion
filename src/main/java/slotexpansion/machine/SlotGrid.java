package slotexpansion.machine;

import slotexpansion.app.Application;
import slotexpansion.player.PlayerManager;
import slotexpansion.ui.PriceLabel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Displays one machine's grid and applies the results produced by {@link SpinCalculator}.
 *
 * <p>Grid arrays and paylines use row-major positions, with {@code columnCount}
 * consecutive cells in each displayed row. Symbol generation and chance
 * upgrades are handled by {@link SlotSymbolGenerator}.</p>
 */
public class SlotGrid {
    private final boolean showWinningPaylines = true;
    private final JPanel machinePanel;
    private final MachineState state;
    private final SlotSymbolGenerator symbolGenerator;
    private final ArrayList<String> imageNames = new ArrayList<>(
            Arrays.asList("donner", "jack", "queen", "king", "kirsche", "orange", "zitrone", "leaf", "bell", "bar", "stern", "seven", "x2"));

    private final JLabel freeSpinsLabel;
    private final PriceLabel currentPayoutLaneLabel;
    private final SlotMachine machine;
    private final int[] tier3SlotGrid = new int[15];
    private final int[] tier2SlotGrid = new int[12];
    private final int[] tier1SlotGrid = new int[9];
    private int[] currentSlotGrid;
    private final List<JLabel> labels = new ArrayList<>();
    private final ArrayList<ImageIcon> icons = new ArrayList<>();
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);
    private final Border winningLineBorder = BorderFactory.createLineBorder(Color.YELLOW,2);
    private final Border nextPayoutBorder = BorderFactory.createLineBorder(Color.GRAY,2);
    private final Border freeSpinBorder = BorderFactory.createLineBorder(Color.CYAN);
    private Timer winHighlightTimer;
    private List<int[]> lastPayoutGrids;
    private BigInteger[] lastPayouts;
    private int payoutGridHighlightLoopCounter = 0;



    SlotGrid(JPanel machinePanel, SlotMachine machine, MachineState state) {
        this.machinePanel = machinePanel;
        this.machine = machine;
        this.state = state;
        this.symbolGenerator = state.symbols();
        currentSlotGrid = tier1SlotGrid;
        Application.generateIconsFromFolder(imageNames,icons,"SlotImages",50);
        rebuildGridCells(3);
        freeSpinsLabel = new JLabel();
        freeSpinsLabel.setForeground(Color.CYAN);
        freeSpinsLabel.setBorder(freeSpinBorder);
        freeSpinsLabel.setBounds(110,  90, 30, 20);
        freeSpinsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        freeSpinsLabel.setVisible(false);
        currentPayoutLaneLabel = new PriceLabel();
        currentPayoutLaneLabel.setForeground(Color.ORANGE);
        currentPayoutLaneLabel.setBounds(10,  215, 70, 20);
        currentPayoutLaneLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentPayoutLaneLabel.setVisible(true);
        machinePanel.add(freeSpinsLabel);
        machinePanel.add(currentPayoutLaneLabel);
    }

    /** Returns the number of paylines currently active on this machine. */
    public int getWinLanes() {
        return state.paylines().size();
    }
    /** Activates one of the currently available optional paylines. */
    public void addNewWinningGrid(int newGrid) {
        state.addPayline(newGrid);
    }

    /** Indicates whether this machine is currently consuming free spins. */
    public boolean isFreeSpinsActivated() {
        return state.hasFreeSpins();
    }

    /** Advances the machine to its next grid size and resets symbol odds. */
    public void upgradeGrid() {
        if (state.columns() == 3) {
            currentSlotGrid = tier2SlotGrid;
            state.setColumns(4);
            rebuildGridCells(4);
            resetChanceUpgradesForGridExpansion();
        } else if (state.columns() == 4) {
            currentSlotGrid = tier3SlotGrid;
            state.setColumns(5);
            rebuildGridCells(5);
            resetChanceUpgradesForGridExpansion();
        } else {
            throw new IllegalStateException("The grid is already at its maximum width");
        }
    }

    private void resetChanceUpgradesForGridExpansion() {
        symbolGenerator.resetForGridUpgrade();
        state.setUnlockedSymbols(0);
    }

    private void rebuildGridCells(int columnCount) {
        int xoffset = 0;
        int yoffset = 0;
        labels.forEach(machinePanel::remove);
        labels.clear();
        for (int i = 0; i < currentSlotGrid.length; i++) {
            JLabel label = new JLabel();
            // Keep each new cell populated with a valid icon before its first spin.
            currentSlotGrid[i] = 0;
            label.setIcon(icons.getFirst());
            if (i % columnCount == 0 && i != 0) {
                yoffset += 50;
                xoffset = 0;
            }
            label.setBounds(75+xoffset, 150+ yoffset, 50, 50);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setBorder(normalBorder);
            labels.add(label);
            xoffset += 50;
        }
        labels.forEach(machinePanel::add);
        labels.getFirst().setIcon(icons.getFirst());
    }

    /** Generates one outcome and settles its stake, winnings, and free spins. */
    public BigInteger spinSlotMachine(BigInteger spinAmount) {
        if (winHighlightTimer != null) {
            winHighlightTimer.stop();
        }
        payoutGridHighlightLoopCounter = 0;
        lastPayoutGrids = new ArrayList<>();
        lastPayouts = new BigInteger[state.paylines().size()];
        currentPayoutLaneLabel.setVisible(false);
        resetLabelBorders();

        SpinCalculator.Result result = SpinCalculator.spin(symbolGenerator, state.columns(), icons.size(),
                state.paylines(), spinAmount, state.pet().getMulti(), state.level(), state.hasFreeSpins(),
                state.isUnlocked(MachineState.SymbolTier.SEVEN), state.isUnlocked(MachineState.SymbolTier.TIER4),
                state.isUnlocked(MachineState.SymbolTier.TIER3), state.isUnlocked(MachineState.SymbolTier.TIER2));
        for (int i = 0; i < currentSlotGrid.length; i++) {
            int symbol = result.symbols()[i];
            currentSlotGrid[i] = symbol;
            labels.get(i).setIcon(icons.get(symbol));
        }
        // The minimum stake is free; larger paid stakes are charged once per spin.
        if (!state.hasFreeSpins()) {
            if (spinAmount.compareTo(machine.getMinimumCoinsPerLane()) > 0) {
                PlayerManager.decreaseCoins(spinAmount);
            }
        } else {
            state.consumeFreeSpin();
            if (!state.hasFreeSpins()) {
                freeSpinsLabel.setVisible(false);
            }
        }
        BigInteger win = result.totalPayout();
        if (win.signum() > 0) {
            PlayerManager.increaseCoins(win);
            machine.slotLevelComponent.addXp(win);
            for (SpinCalculator.WinningLine line : result.wins()) {
                addPayoutGrid(line.matches(), line.payline(), line.payout());
            }
        }
        if (result.freeSpinsAwarded() > 0) {
            state.awardFreeSpins(result.freeSpinsAwarded());
            freeSpinsLabel.setVisible(true);
            highlightThunderSymbols();
        }
        freeSpinsLabel.setText(String.valueOf(state.freeSpins()));
        machine.updateFreeSpinControls();
        if (showWinningPaylines && win.signum() > 0) {
            scheduleWinHighlight();
        }
        machinePanel.repaint();
        return win;
    }

    private void resetLabelBorders(){
        labels.forEach(label -> {
            if (label.getBorder() != normalBorder) {
                label.setBorder(normalBorder);
            }
        });
    }

    /** Restores grid borders after the temporary win-line or free-spin highlight. */
    private void scheduleWinHighlight() {
        winHighlightTimer = new Timer(1500, event -> {
            resetLabelBorders();
            int index = payoutGridHighlightLoopCounter % lastPayoutGrids.size();
            int[] winGrid = lastPayoutGrids.get(index);
            BigInteger winAmount = lastPayouts[index];
            for (int i = 0; i < winGrid.length; i++) {
                if (winGrid[i] == 1) {
                    labels.get(i).setBorder(winningLineBorder);
                } else if (winGrid[i] == 2) {
                    labels.get(i).setBorder(nextPayoutBorder);
                }
            }
            machinePanel.repaint();
            if(lastPayoutGrids.size()> 1){
                payoutGridHighlightLoopCounter++;
            }
            currentPayoutLaneLabel.setVisible(true);
            currentPayoutLaneLabel.setCoins(winAmount);
        });
        winHighlightTimer.setRepeats(lastPayoutGrids.size() > 1);
        winHighlightTimer.start();
    }

    /** Adds the winning grid to the payout grids to later display them and the corresponding win one after another*/
    private void addPayoutGrid(int matchingSymbols, int[] winningGrid, BigInteger winAmount) {
        int[] payoutGrid = winningGrid.clone();
        int matchesRemaining = matchingSymbols;
        int payoutIndex = lastPayoutGrids.size();

        for (int column = 0; column < state.columns(); column++) {
            for (int row = 0; row < 3; row++) {
                int cellIndex = row * state.columns() + column;
                if (winningGrid[cellIndex] != 1) {
                    continue;
                }

                if (matchesRemaining > 0) {
                    matchesRemaining--;
                } else {
                    payoutGrid[cellIndex] = 2;
                }
                break; // This payline has one cell per column.
            }
        }

        lastPayouts[payoutIndex] = winAmount;
        lastPayoutGrids.add(payoutGrid);
    }

    private void highlightThunderSymbols() {
        for (int i = 0; i < currentSlotGrid.length; i++) {
            if (currentSlotGrid[i] == 0) {
                labels.get(i).setBorder(freeSpinBorder);
            }
        }
    }

    public int getTier1SymbolChance() {
        return symbolGenerator.getTier1SymbolChance();
    }

    public void setTier1SymbolChance(int tier1SymbolChance) {
        symbolGenerator.setTier1SymbolChance(tier1SymbolChance);
    }

    public int getAdditionalTier3SymbolChance() {
        return symbolGenerator.getAdditionalTier3SymbolChance();
    }

    public void setAdditionalTier3SymbolChance(int additionalTier3SymbolChance) {
        symbolGenerator.setAdditionalTier3SymbolChance(additionalTier3SymbolChance);
    }

    public int getAdditionalTier2SymbolChance() {
        return symbolGenerator.getAdditionalTier2SymbolChance();
    }

    public void setAdditionalTier2SymbolChance(int additionalTier2SymbolChance) {
        symbolGenerator.setAdditionalTier2SymbolChance(additionalTier2SymbolChance);
    }

    public int getAdditionalSevenSymbolChance() {
        return symbolGenerator.getAdditionalSevenSymbolChance();
    }

    public void setAdditionalSevenSymbolChance(int additionalSevenSymbolChance) {
        symbolGenerator.setAdditionalSevenSymbolChance(additionalSevenSymbolChance);
    }

    public int getAdditionalTier4SymbolChance() {
        return symbolGenerator.getAdditionalTier4SymbolChance();
    }

    public void setAdditionalTier4SymbolChance(int additionalTier4SymbolChance) {
        symbolGenerator.setAdditionalTier4SymbolChance(additionalTier4SymbolChance);
    }

    public int getAdditionalX2SymbolChance() {
        return symbolGenerator.getAdditionalX2SymbolChance();
    }

    public void setAdditionalX2SymbolChance(int additionalX2SymbolChance) {
        symbolGenerator.setAdditionalX2SymbolChance(additionalX2SymbolChance);
    }

    public int getColumnCount() {
        return state.columns();
    }

    public void setColumnCount(int columnCount) {
        int previousColumnCount = state.columns();
        for (int i = 0; i < columnCount - previousColumnCount; i++) {
            upgradeGrid();
            machine.moveUpgradeGridButton();
        }

    }

    int getAdditionalPaylineCount() {
        return state.optionalPaylineCount();
    }
}
