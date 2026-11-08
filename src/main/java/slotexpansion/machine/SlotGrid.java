package slotexpansion.machine;

import slotexpansion.app.Application;
import slotexpansion.player.PlayerManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Owns one machine's symbols, paylines, probability upgrades, and spin outcomes.
 *
 * <p>Grid arrays and paylines use row-major positions, with {@code columnCount}
 * consecutive cells in each displayed row.</p>
 */
public class SlotGrid {
    private static final Logger LOGGER = Logger.getLogger(SlotGrid.class.getName());

    private final boolean showWinningPaylines = true;

    private final int[] tier1Win3Symbol = {0, 1, 1, 1, 3, 3, 3, 4, 4, 6, 6, 10};
    private final int[] tier2Win3Symbol = {0, 1, 1, 1, 2, 2, 2, 3, 3, 5, 5, 7};
    private final int[] tier3Win3Symbol = {0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5};

    private final int[] tier2Win4Symbol = {0, 3, 3, 3, 4, 4, 4, 6, 6, 10, 10, 15};
    private final int[] tier3Win4Symbol = {0, 2, 2, 2, 3, 3, 3, 5, 5, 7, 7, 10};

    private final int[] tier3Win5Symbol = {0, 5, 5, 5, 10, 10, 10, 20, 20, 30, 30, 100};

    private int[] currentWin3Symbol;
    private int[] currentWin4Symbol;
    private int[] currentWin5Symbol;
    private final JPanel machinePanel;
    private final ArrayList<String> imageNames = new ArrayList<>(
            Arrays.asList("donner", "jack", "queen", "king", "kirsche", "orange", "zitrone", "leaf", "bell", "bar", "stern", "seven", "x2"));
    private int additionalSevenSymbolChance = 0;
    private int additionalTier4SymbolChance = 0;
    private int additionalTier3SymbolChance = 0;
    private int additionalTier2SymbolChance = 0;
    private int tier1SymbolChance = 53;
    private int additionalX2SymbolChance = 0;
    private boolean freeSpinsActivated = false;
    private int freeSpinsLeft = 0;

    protected boolean tier2SymbolsUnlocked = true;
    protected boolean tier3SymbolsUnlocked = true;
    protected boolean tier4SymbolsUnlocked = true;
    protected boolean sevenSymbolsUnlocked = true;

    private final JLabel freeSpinsLabel;
    private final JLabel currentPayoutLaneLabel;
    private final SlotMachine machine;
    private final int[] tier3SlotGrid = new int[15];
    private final int[] tier2SlotGrid = new int[12];
    private final int[] tier1SlotGrid = new int[9];
    private int[] currentSlotGrid;
    private int columnCount = 3;
    private final List<JLabel> labels = new ArrayList<>();
    private final ArrayList<ImageIcon> icons = new ArrayList<>();
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);
    private final Border winningLineBorder = BorderFactory.createLineBorder(Color.YELLOW);
    private final Border nextPayoutBorder = BorderFactory.createLineBorder(Color.GREEN);
    private final Border freeSpinBorder = BorderFactory.createLineBorder(Color.BLUE);
    private List<int[]> winningGrids;
    private List<int[]> currentAdditionalWinningGrid;
    private final Random rand = new Random();
    private Timer winHighlightTimer;
    private List<int[]> lastPayoutGrids;
    private BigInteger[] lastPayouts;
    private int payoutGridHighlightLoopCounter = 0;



    public SlotGrid(JPanel machinePanel, SlotMachine machine) {
        this.machinePanel = machinePanel;
        this.machine = machine;
        currentSlotGrid = tier1SlotGrid;
        currentWin3Symbol = tier1Win3Symbol;
        winningGrids = getTier1WinningGrids();
        currentAdditionalWinningGrid = getAdditionalTier1WinningGrids();
        Application.generateIconsFromFolder(imageNames,icons,"SlotImages",50);
        generateSlotGridWithImages(3);
        freeSpinsLabel = new JLabel();
        freeSpinsLabel.setForeground(Color.CYAN);
        freeSpinsLabel.setBounds(0,  90, 50, 20);
        freeSpinsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        freeSpinsLabel.setVisible(false);
        currentPayoutLaneLabel = new JLabel();
        currentPayoutLaneLabel.setForeground(Color.ORANGE);
        currentPayoutLaneLabel.setBounds(30,  235, 50, 20);
        currentPayoutLaneLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentPayoutLaneLabel.setVisible(true);
        machinePanel.add(freeSpinsLabel);
        machinePanel.add(currentPayoutLaneLabel);
    }

    /** Returns the number of paylines currently active on this machine. */
    public int getWinLanes() {
        return winningGrids.size();
    }

    private List<int[]> getTier1WinningGrids() {
        return new ArrayList<>(Arrays.asList(
                new int[]{1, 1, 1,
                        0, 0, 0,
                        0, 0, 0,},
                new int[]{0, 0, 0,
                        1, 1, 1,
                        0, 0, 0,},
                new int[]{0, 0, 0,
                        0, 0, 0,
                        1, 1, 1,},
                new int[]{1, 0, 0,
                        0, 1, 0,
                        0, 0, 1,},
                new int[]{0, 0, 1,
                        0, 1, 0,
                        1, 0, 0,}

        ));
    }

    private List<int[]> getTier2WinningGrids() {
        return new ArrayList<>(Arrays.asList(
                new int[]{1, 1, 1, 1,
                        0, 0, 0, 0,
                        0, 0, 0, 0},
                new int[]{0, 0, 0, 0,
                        1, 1, 1, 1,
                        0, 0, 0, 0},
                new int[]{0, 0, 0, 0,
                        0, 0, 0, 0,
                        1, 1, 1, 1},
                new int[]{1, 0, 0, 1,
                        0, 1, 1, 0,
                        0, 0, 0, 0},
                new int[]{0, 0, 0, 0,
                        1, 0, 0, 1,
                        0, 1, 1, 0},
                new int[]{0, 0, 0, 0,
                        0, 1, 1, 0,
                        1, 0, 0, 1},
                new int[]{0, 1, 1, 0,
                        1, 0, 0, 1,
                        0, 0, 0, 0},
                new int[]{1, 0, 0, 0,
                        0, 1, 1, 0,
                        0, 0, 0, 1},
                new int[]{0, 0, 0, 1,
                        0, 1, 1, 0,
                        1, 0, 0, 0}
        ));
    }

    private List<int[]> getTier3WinningGrids() {
        return new ArrayList<>(Arrays.asList(
                new int[]{1, 1, 1, 1, 1,
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1,
                        0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1},
                new int[]{1, 0, 0, 0, 1,
                        0, 1, 0, 1, 0,
                        0, 0, 1, 0, 0},
                new int[]{0, 0, 1, 0, 0,
                        0, 1, 0, 1, 0,
                        1, 0, 0, 0, 1},
                new int[]{0, 1, 1, 1, 0,
                        1, 0, 0, 0, 1,
                        0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0,
                        1, 0, 0, 0, 1,
                        0, 1, 1, 1, 0},
                new int[]{0, 0, 0, 0, 1,
                        0, 1, 1, 1, 0,
                        1, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0,
                        0, 1, 1, 1, 0,
                        1, 0, 0, 0, 1},
                new int[]{1, 0, 0, 0, 1,
                        0, 1, 1, 1, 0,
                        0, 0, 0, 0, 0},
                new int[]{1, 0, 0, 0, 0,
                        0, 1, 1, 1, 0,
                        0, 0, 0, 0, 1},
                new int[]{0, 1, 1, 1, 0,
                        0, 0, 0, 0, 0,
                        1, 0, 0, 0, 1},
                new int[]{1, 0, 0, 0, 1,
                        0, 0, 0, 0, 0,
                        0, 1, 1, 1, 0},
                new int[]{0, 0, 0, 1, 1,
                        0, 0, 1, 0, 0,
                        1, 1, 0, 0, 0},
                new int[]{1, 1, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 1, 1}
        ));
    }

    /** Returns the optional paylines for the initial 3-by-3 grid. */
    public List<int[]> getAdditionalTier1WinningGrids() {
        return new ArrayList<>(Arrays.asList(
                new int[]{0, 1, 0,
                        1, 0, 1,
                        0, 0, 0,},
                new int[]{0, 0, 0,
                        0, 1, 0,
                        1, 0, 1,},
                new int[]{1, 0, 1,
                        0, 1, 0,
                        0, 0, 0,},
                new int[]{0, 0, 0,
                        1, 0, 1,
                        0, 1, 0,}));
    }

    /** Returns the optional paylines for the 3-by-4 grid. */
    public List<int[]> getAdditionalTier2WinningGrids() {
        return new ArrayList<>(Arrays.asList(
                new int[]{0, 1, 1, 0,
                        0, 0, 0, 0,
                        1, 0, 0, 1},
                new int[]{1, 0, 0, 1,
                        0, 0, 0, 0,
                        0, 1, 1, 0},
                new int[]{0, 0, 0, 0,
                        0, 1, 0, 1,
                        1, 0, 1, 0},
                new int[]{0, 1, 0, 1,
                        1, 0, 1, 0,
                        0, 0, 0, 0},
                new int[]{0, 1, 0, 1,
                        0, 0, 0, 0,
                        1, 0, 1, 0},
                new int[]{1, 0, 1, 0,
                        0, 1, 0, 1,
                        0, 0, 0, 0},
                new int[]{0, 0, 0, 0,
                        1, 0, 1, 0,
                        0, 1, 0, 1},
                new int[]{1, 0, 1, 0,
                        0, 0, 0, 0,
                        0, 1, 0, 1},
                new int[]{0, 0, 1, 1,
                        0, 0, 0, 0,
                        1, 1, 0, 0},
                new int[]{1, 1, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 1, 1}));
    }

    /** Returns the optional paylines for the 3-by-5 grid. */
    public List<int[]> getAdditionalTier3WinningGrids() {
        return new ArrayList<>(Arrays.asList(new int[]{1, 1, 0, 1, 1,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 0, 0},

                new int[]{0, 0, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        1, 1, 0, 1, 1},

                new int[]{0, 0, 1, 0, 0,
                        1, 1, 0, 1, 1,
                        0, 0, 0, 0, 0},

                new int[]{0, 0, 0, 0, 0,
                        1, 1, 0, 1, 1,
                        0, 0, 1, 0, 0},

                new int[]{1, 0, 1, 0, 1,
                        0, 1, 0, 1, 0,
                        0, 0, 0, 0, 0},

                new int[]{0, 0, 0, 0, 0,
                        1, 0, 1, 0, 1,
                        0, 1, 0, 1, 0},

                new int[]{0, 1, 0, 1, 0,
                        1, 0, 1, 0, 1,
                        0, 0, 0, 0, 0},

                new int[]{0, 0, 0, 0, 0,
                        0, 1, 0, 1, 0,
                        1, 0, 1, 0, 1},

                new int[]{1, 0, 1, 0, 1,
                        0, 0, 0, 0, 0,
                        0, 1, 0, 1, 0},

                new int[]{0, 1, 0, 1, 0,
                        0, 0, 0, 0, 0,
                        1, 0, 1, 0, 1},

                new int[]{1, 1, 0, 1, 1,
                        0, 0, 0, 0, 0,
                        0, 0, 1, 0, 0},

                new int[]{0, 0, 1, 0, 0,
                        0, 0, 0, 0, 0,
                        1, 1, 0, 1, 1},

                new int[]{1, 0, 0, 0, 1,
                        0, 0, 1, 0, 0,
                        0, 1, 0, 1, 0},

                new int[]{0, 1, 0, 1, 0,
                        0, 0, 1, 0, 0,
                        1, 0, 0, 0, 1},

                new int[]{0, 1, 0, 1, 0,
                        1, 0, 0, 0, 1,
                        0, 0, 1, 0, 0},

                new int[]{0, 0, 1, 0, 0,
                        1, 0, 0, 0, 1,
                        0, 1, 0, 1, 0},

                new int[]{0, 1, 0, 0, 0,
                        1, 0, 1, 0, 1,
                        0, 0, 0, 1, 0},

                new int[]{0, 0, 0, 1, 0,
                        1, 0, 1, 0, 1,
                        0, 1, 0, 0, 0}));
    }

    /** Activates one of the currently available optional paylines. */
    public void addNewWinningGrid(int newGrid) {
        winningGrids.add(currentAdditionalWinningGrid.get(newGrid));
    }

    /** Indicates whether this machine is currently consuming free spins. */
    public boolean isFreeSpinsActivated() {
        return freeSpinsActivated;
    }

    /** Advances the machine to its next grid size and resets symbol odds. */
    public void upgradeGrid() {
        if (columnCount == 3) {
            currentSlotGrid = tier2SlotGrid;
            currentWin3Symbol = tier2Win3Symbol;
            currentWin4Symbol = tier2Win4Symbol;
            winningGrids = getTier2WinningGrids();
            currentAdditionalWinningGrid = getAdditionalTier2WinningGrids();
            generateSlotGridWithImages(4);
            resetAllChances();
        } else {
            currentSlotGrid = tier3SlotGrid;
            currentWin3Symbol = tier3Win3Symbol;
            currentWin4Symbol = tier3Win4Symbol;
            currentWin5Symbol = tier3Win5Symbol;
            winningGrids = getTier3WinningGrids();
            currentAdditionalWinningGrid = getAdditionalTier3WinningGrids();
            generateSlotGridWithImages(5);
            resetAllChances();
        }
    }

    private void resetAllChances() {
        additionalSevenSymbolChance = 0;
        additionalTier4SymbolChance = 0;
        additionalTier3SymbolChance = 0;
        additionalTier2SymbolChance = 0;
        tier1SymbolChance = 53;
        additionalX2SymbolChance = 0;
    }

    private void generateSlotGridWithImages(int columnCount) {
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
            label.setBounds(75+xoffset, 170+ yoffset, 50, 50);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setBorder(normalBorder);
            labels.add(label);
            xoffset += 50;
        }
        this.columnCount = columnCount;
        labels.forEach(machinePanel::add);
        labels.getFirst().setIcon(icons.getFirst());
    }

    private int calculateSlotSymbol(int currentSpot) {
        // The cumulative symbol bands shift as chance upgrades are purchased.
        int chance = rand.nextInt(0, 100);
        if (!isX2Symbol(currentSpot, chance)) {
            if (chance < 5 + additionalX2SymbolChance + additionalSevenSymbolChance && sevenSymbolsUnlocked) {
                return icons.size() - 2;
            } else if (chance < 11 + additionalX2SymbolChance + additionalSevenSymbolChance + additionalTier4SymbolChance && tier4SymbolsUnlocked) {
                return randomInt(icons.size() - 4, icons.size()) - 2;
            } else if (chance < 23 + additionalX2SymbolChance + additionalSevenSymbolChance + additionalTier4SymbolChance + additionalTier3SymbolChance && tier3SymbolsUnlocked) {
                return randomInt(icons.size() - 6, icons.size() - 4);
            } else if (chance < 43 + additionalX2SymbolChance + additionalSevenSymbolChance + additionalTier4SymbolChance + additionalTier3SymbolChance + additionalTier2SymbolChance && tier2SymbolsUnlocked) {
                return randomInt(icons.size() - 9, icons.size() - 6);
            } else if (chance < 96) {
                return randomInt(1, icons.size() - 9);
            } else return 0;
        } else return icons.size() - 1;
    }

    private boolean isX2Symbol(int currentSpot, int chance) {
        // Multiplier symbols are excluded from the first and last column.
        if (columnCount == 3) {
            return chance < 2 + additionalX2SymbolChance
                    && currentSpot != 0 && currentSpot != 3 && currentSpot != 6
                    && currentSpot != 2 && currentSpot != 5 && currentSpot != 8;
        } else if (columnCount == 4) {
            return chance < 2 + additionalX2SymbolChance
                    && currentSpot != 0 && currentSpot != 4 && currentSpot != 8
                    && currentSpot != 3 && currentSpot != 7 && currentSpot != 11;
        }
        return chance < 2 + additionalX2SymbolChance
                && currentSpot != 0 && currentSpot != 5 && currentSpot != 10
                && currentSpot != 4 && currentSpot != 9 && currentSpot != 14;
    }

    /** Generates one outcome and settles its stake, winnings, and free spins. */
    public BigInteger spinSlotMachine(BigInteger spinAmount) {
        if (winHighlightTimer != null) {
            winHighlightTimer.stop();
        }
        payoutGridHighlightLoopCounter = 0;
        lastPayoutGrids = new ArrayList<>();
        lastPayouts = new BigInteger[winningGrids.size()];
        currentPayoutLaneLabel.setVisible(false);
        resetLabelBorders();

        machine.slotLevelComponent.addXp(spinAmount);
        for (int i = 0; i < currentSlotGrid.length; i++) {
            int symbol = calculateSlotSymbol(i);
            currentSlotGrid[i] = symbol;
            labels.get(i).setIcon(icons.get(symbol));
        }
        // The current stake is charged only on paid spins above the per-payline minimum.
        if (!freeSpinsActivated) {
            if (spinAmount.divide(BigInteger.valueOf(getWinLanes())).compareTo(machine.getMinimumCoinsPerLane()) > 0) {
                PlayerManager.decreaseCoins(spinAmount);
            }
        } else {
            freeSpinsLeft--;
            if (freeSpinsLeft == 0) {
                freeSpinsActivated = false;
                freeSpinsLabel.setVisible(false);
                additionalX2SymbolChance -= 5;
            }
        }
        BigInteger win = calculateTotalWin(spinAmount);
        boolean freeSpinsAwarded = checkForFreeSpins();
        if (freeSpinsAwarded) {
            freeSpinsActivated = true;
            freeSpinsLabel.setVisible(true);
        }
        freeSpinsLabel.setText(String.valueOf(freeSpinsLeft));
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

    private BigInteger calculateTotalWin(BigInteger spinAmount) {
        BigInteger totalWin = BigInteger.ZERO;
        for (int[] winningGrid : winningGrids) {
            totalWin = totalWin.add(calculateWinForPayline(winningGrid, spinAmount));
        }
        if (totalWin.signum() > 0) {
            PlayerManager.increaseCoins(totalWin);

        }
        return totalWin;
    }

    /**
     * Evaluates one payline from left to right and returns its payout.
     *
     * <p>{@code winningGrid} is a row-major mask with a {@code 1} at each cell
     * belonging to the payline. The scan starts in the leftmost column. In each
     * column, it checks the three row positions and computes each cell index
     * as {@code row * columnCount + column}.</p>
     *
     * <p>The first encountered symbol becomes the target symbol. Matching target
     * symbols and, after the target is selected, {@code x2} symbols extend the
     * matching prefix. A different ordinary symbol before three matches
     * invalidates the line; a different symbol after three or more matches ends
     * the prefix, leaving the already matched symbols eligible for a payout.</p>
     *
     * <p>Prefixes of three, four, or five symbols use the corresponding current
     * payout table. The line stake is multiplied by that table value, then by
     * two for each counted {@code x2} symbol and by the machine pet's multiplier.
     * {@code calculateTotalWin} adds the combined payout to the player's balance.
     * When {@code showWinningPaylines} is enabled, matched cells use the win
     * border and the remaining cells in the same line use the next-payout
     * border. A Swing timer later restores their borders.</p>
     *
     * @param winningGrid row-major mask identifying the cells in this payline
     * @param spinAmount stake used to calculate this payline's payout
     * @return the payout for this line, or zero when it does not win
     */
    private BigInteger calculateWinForPayline(int[] winningGrid, BigInteger spinAmount) {
        int winningSymbol = -1;
        boolean symbolLockedIn = false;
        int matchingSymbols = 0;
        int multiplierSymbols = 0;
        BigInteger lineWin = BigInteger.ZERO;
        // Check the three row-major cell positions in each column.
        boolean lineCanContinue = true;

        // Each iteration moves one column to the right along the payline.
        for (int column = 0; column < columnCount && lineCanContinue; column++) {
            for (int row = 0; row < 3; row++) {
                int cellIndex = row * columnCount + column;
                if (winningGrid[cellIndex] != 1) {
                    continue;
                }

                if (symbolLockedIn) {
                    if (currentSlotGrid[cellIndex] == winningSymbol) {
                        matchingSymbols++;
                    } else if (currentSlotGrid[cellIndex] == icons.size() - 1) {
                        // x2 acts as a wildcard only after a target symbol was selected.
                        multiplierSymbols++;
                        matchingSymbols++;
                    } else if (matchingSymbols < 3) {
                        // Fewer than three consecutive matches cannot win; skip the rest of this line.
                        matchingSymbols = 0;
                        lineCanContinue = false;
                    } else {
                        // Keep the winning prefix, but do not count symbols after its first mismatch.
                        symbolLockedIn = false;
                    }
                } else if (matchingSymbols < 3) {
                    // Lock the first payline symbol as the target for subsequent cells.
                    winningSymbol = currentSlotGrid[cellIndex];
                    matchingSymbols++;
                    symbolLockedIn = true;
                }

                break;
            }
        }

        // Only three-, four-, and five-symbol prefixes have payout tables.
        if (matchingSymbols > 2) {
            switch (matchingSymbols) {
                case 3:
                    lineWin = spinAmount.multiply(BigInteger.valueOf(currentWin3Symbol[winningSymbol]));
                    break;
                case 4:
                    lineWin = spinAmount.multiply(BigInteger.valueOf(currentWin4Symbol[winningSymbol]));
                    break;
                case 5:
                    lineWin = spinAmount.multiply(BigInteger.valueOf(currentWin5Symbol[winningSymbol]));
                    break;
            }

            // Apply x2 and pet multipliers to the table-based line payout.
            if (multiplierSymbols != 0) {
                lineWin = lineWin.multiply(BigInteger.valueOf(multiplierSymbols * 2L));
            }
            addPayoutGrid(matchingSymbols,winningGrid, lineWin);
            lineWin = lineWin.multiply(BigInteger.valueOf(machine.pet.getMulti()));
            if (lineWin.signum() > 0) {
                LOGGER.info("Winning line: symbols=" + matchingSymbols + ", symbol=" + winningSymbol
                        + ", win=" + lineWin + ", multiplier=" + lineWin.divide(spinAmount));
            }
        }

        return lineWin;
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
            currentPayoutLaneLabel.setText(winAmount.toString());
        });
        winHighlightTimer.setRepeats(lastPayoutGrids.size() > 1);
        winHighlightTimer.start();
    }

    /** Adds the winning grid to the payout grids to later display them and the corresponding win one after another*/
    private void addPayoutGrid(int matchingSymbols, int[] winningGrid, BigInteger winAmount) {
        int[] payoutGrid = winningGrid.clone();
        int matchesRemaining = matchingSymbols;
        int payoutIndex = lastPayoutGrids.size();

        for (int column = 0; column < columnCount; column++) {
            for (int row = 0; row < 3; row++) {
                int cellIndex = row * columnCount + column;
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

    /** Adds free spins when at least three thunder symbols appear in the grid. */
    private boolean checkForFreeSpins() {
        int freeSpinSymbolAmount = 0;
        // Thunder symbols are counted to determine whether the spin awards free spins.
        for (int i = 0; i < currentSlotGrid.length; i++) {
            if (currentSlotGrid[i] == 0) {
                freeSpinSymbolAmount++;
            }
        }
        if (freeSpinSymbolAmount >= 3) {
            for (int i = 0; i < currentSlotGrid.length; i++) {
                if (currentSlotGrid[i] == 0) {
                    labels.get(i).setBorder(freeSpinBorder);
                }
            }
        }
        if (freeSpinSymbolAmount == 3) {
            if (!freeSpinsActivated) {
                additionalX2SymbolChance += 5;
            }
            freeSpinsLeft += 10;
            return true;
            // Each thunder symbol beyond three adds another ten free spins.
        } else if (freeSpinSymbolAmount > 3) {
            if (!freeSpinsActivated) {
                additionalX2SymbolChance += 5;
            }

            freeSpinsLeft += (freeSpinSymbolAmount - 3) * 10 + 10;
            return true;
        }
        return false;
    }

    /** Returns an integer from {@code [begin, end)}. */
    private int randomInt(int begin, int end) {
        return rand.nextInt(begin, end);
    }

    public int getTier1SymbolChance() {
        return tier1SymbolChance;
    }

    public void setTier1SymbolChance(int tier1SymbolChance) {
        this.tier1SymbolChance = tier1SymbolChance;
    }

    public int getAdditionalTier3SymbolChance() {
        return additionalTier3SymbolChance;
    }

    public void setAdditionalTier3SymbolChance(int additionalTier3SymbolChance) {
        this.additionalTier3SymbolChance = additionalTier3SymbolChance;
    }

    public int getAdditionalTier2SymbolChance() {
        return additionalTier2SymbolChance;
    }

    public void setAdditionalTier2SymbolChance(int additionalTier2SymbolChance) {
        this.additionalTier2SymbolChance = additionalTier2SymbolChance;
    }

    public int getAdditionalSevenSymbolChance() {
        return additionalSevenSymbolChance;
    }

    public void setAdditionalSevenSymbolChance(int additionalSevenSymbolChance) {
        this.additionalSevenSymbolChance = additionalSevenSymbolChance;
    }

    public int getAdditionalTier4SymbolChance() {
        return additionalTier4SymbolChance;
    }

    public void setAdditionalTier4SymbolChance(int additionalTier4SymbolChance) {
        this.additionalTier4SymbolChance = additionalTier4SymbolChance;
    }

    public int getAdditionalX2SymbolChance() {
        return additionalX2SymbolChance;
    }

    public void setAdditionalX2SymbolChance(int additionalX2SymbolChance) {
        this.additionalX2SymbolChance = additionalX2SymbolChance;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        int previousColumnCount = this.columnCount;
        for (int i = 0; i < columnCount - previousColumnCount; i++) {
            upgradeGrid();
        }
    }

    public List<int[]> getWinningGrids() {
        return winningGrids;
    }

    public List<int[]> getCurrentAdditionalWinningGrid() {
        return currentAdditionalWinningGrid;
    }
}
