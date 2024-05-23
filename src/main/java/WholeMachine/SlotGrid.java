package WholeMachine;

import CasinoStuff.Application;
import CasinoStuff.PlayerManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SlotGrid {
    private final int[] tier3Win3Symbol = {0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5};
    private final int[] tier3Win4Symbol = {0, 2, 2, 2, 3, 3, 3, 5, 5, 10, 10, 50};
    private final int[] tier3Win5Symbol = {0, 5, 5, 5, 10, 10, 10, 30, 30, 50, 50, 200};
    private final int[] tier2Win3Symbol = {0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5};
    private final int[] tier2Win4Symbol = {0, 2, 2, 2, 3, 3, 3, 5, 5, 10, 10, 50};
    private final int[] tier1Win3Symbol = {0, 3, 3, 3, 5, 5, 5, 10, 10, 20, 20, 50};
    private int[] currentWin3Symbol;
    private int[] currentWin4Symbol;
    private int[] currentWin5Symbol;
    private final boolean stopForWinLines = false;
    private final JFrame mainFrame;
    private final ArrayList<String> imageNames = new ArrayList<>(
            Arrays.asList("donner", "jack", "queen", "king", "kirsche", "orange", "zitrone", "leaf", "bell", "bar", "stern", "seven", "x2"));
    private int additionalSevenSymbolChance = 0;
    private int additionalTier4SymbolChance = 0;
    private int additionalTier3SymbolChance = 0;
    private int additionalTier2SymbolChance = 0;
    private int tier1SymbolChance = 46;
    private int additionalX2SymbolChance = 0;
    private final int startYCord;
    private final int startXCord;
    private boolean freeSpinsActivated = false;
    private int freeSpinsLeft = 0;
    private final int slotMultiplier = 1;

    private final JLabel freeSpinsLabel;
    private final SlotMachine machine;
    private int rand;
    private final int[] tier3SlotGrid = new int[15];
    private final int[] tier2SlotGrid = new int[12];
    private final int[] tier1SlotGrid = new int[9];
    private int[] currentSlotGrid;
    private int rowAmount = 3;
    private final List<JLabel> labels = new ArrayList<>();
    private final ArrayList<ImageIcon> icons = new ArrayList<>();
    private final Border normalBorder = BorderFactory.createLineBorder(Color.black);
    private final Border winBorder = BorderFactory.createLineBorder(Color.YELLOW);
    private final Border slotsLeftBorder = BorderFactory.createLineBorder(Color.GREEN);
    private final Border freeSpinBorder = BorderFactory.createLineBorder(Color.BLUE);
    private List<int[]> winningGrids;
    private List<int[]> currentAdditionalWinningGrid;


    public SlotGrid(JFrame mainFrame, int startXCord, int startYCord, SlotMachine machine) {
        this.mainFrame = mainFrame;
        this.startXCord = startXCord;
        this.startYCord = startYCord + 60;
        this.machine = machine;
        currentSlotGrid = tier1SlotGrid;
        currentWin3Symbol = tier1Win3Symbol;
        winningGrids = getTier1WinningGrids();
        currentAdditionalWinningGrid = getAdditionalTier1WinningGrids();
        Application.generateIconsFromFolder(imageNames,icons,"SlotImages",50);
        generateSlotGridWithImages(3);
        freeSpinsLabel = new JLabel();
        freeSpinsLabel.setForeground(Color.CYAN);
        freeSpinsLabel.setBounds(startXCord, startYCord + 30, 50, 20);
        freeSpinsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        freeSpinsLabel.setVisible(false);
        mainFrame.add(freeSpinsLabel);
    }

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

    public void addNewWinningGrid(int newGrid) {
        winningGrids.add(currentAdditionalWinningGrid.get(newGrid));
    }

    public boolean isFreeSpinsActivated() {
        return freeSpinsActivated;
    }

    public void upgradeGrid() {
        //System.out.println("Grid upgrade tried!");
        if (rowAmount == 3) {
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
        tier1SymbolChance = 46;
        additionalX2SymbolChance = 0;
    }

    private void generateSlotGridWithImages(int laneLength) {
        int xoffset = 0;
        int yoffset = 0;
        labels.forEach(mainFrame::remove);
        labels.clear();
        for (int i = 0; i < currentSlotGrid.length; i++) {
            JLabel label = new JLabel();
            //jedes label bekommt erstmal das selbe bild zur generierung
            currentSlotGrid[i] = 0;
            label.setIcon(icons.getFirst());
            //damit aller 5 bilder die nächste reihe angefangen wird
            if (i % laneLength == 0 && i != 0) {
                yoffset += 50;
                xoffset = 0;
            }
            label.setBounds(startXCord + xoffset, startYCord + yoffset, 50, 50);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setBorder(normalBorder);
            labels.add(label);
            xoffset += 50;
        }
        rowAmount = laneLength;
        labels.forEach(mainFrame::add);
        labels.getFirst().setIcon(icons.getFirst());
    }

    private void showGoodGrids(int[] grid) {
        //zum ausrucken der gewinnlinie damit man es nachvollziehen kann
        if (grid != null) {
            for (int i = 1; i < currentSlotGrid.length + 1; i++) {
                System.out.print("|" + grid[i - 1]);
                if (i % rowAmount == 0) {
                    System.out.println("|");
                }
            }
        }
    }

    private int calculateSlotSymbol(int currentSpot) {
        //hier sind die wahrscheinlichkeiten für das setzen der symbole
        int chance = new Random().nextInt(0, 100);
        if (checkForX2Symbol(currentSpot, chance) != icons.size() - 1) {
            if (chance < 5 + additionalX2SymbolChance + additionalSevenSymbolChance) {
                return icons.size() - 2;
                //10%
            } else if (chance < 15 + additionalX2SymbolChance + additionalSevenSymbolChance + additionalTier4SymbolChance) {
                return randomNumber(icons.size() - 4, icons.size()) - 2;
                //15%
            } else if (chance < 30 + additionalX2SymbolChance + additionalSevenSymbolChance + additionalTier4SymbolChance + additionalTier3SymbolChance) {
                return randomNumber(icons.size() - 6, icons.size() - 4);
                //20%
            } else if (chance < 50 + additionalX2SymbolChance + additionalSevenSymbolChance + additionalTier4SymbolChance + additionalTier3SymbolChance + additionalTier2SymbolChance) {
                return randomNumber(icons.size() - 9, icons.size() - 6);
                //46%
            } else if (chance < 96) {
                return randomNumber(1, icons.size() - 9);
                //4%
            } else return 0;
        } else return icons.size() - 1;

    }

    private int checkForX2Symbol(int currentSpot, int chance) {
        if (rowAmount == 3) {
            if (chance < 2 + additionalX2SymbolChance && currentSpot != 0 && currentSpot != 3 && currentSpot != 6 && currentSpot != 2 && currentSpot != 5 && currentSpot != 8) {
                return icons.size() - 1;
            }
        } else if (rowAmount == 4) {
            if (chance < 2 + additionalX2SymbolChance && currentSpot != 0 && currentSpot != 4 && currentSpot != 8 && currentSpot != 3 && currentSpot != 7 && currentSpot != 11) {
                return icons.size() - 1;
            }
        } else if (chance < 2 + additionalX2SymbolChance && currentSpot != 0 && currentSpot != 5 && currentSpot != 10 && currentSpot != 4 && currentSpot != 9 && currentSpot != 14) {
            return icons.size() - 1;
        }
        return 0;
        //2%

    }

    public long spinSlotMachine(long spinAmount) {
        machine.setSlotXp(machine.getSlotXp() + spinAmount);
        //jede zelle bekommt einen wert,wodurch das bild dazu reinkommt
        for (int i = 0; i < currentSlotGrid.length; i++) {
            rand = calculateSlotSymbol(i);
            currentSlotGrid[i] = rand;
            labels.get(i).setIcon(icons.get(rand));
        }
        //falls keine free spins sind werden coins abgezogen vom geld und der player keirgt es als xp
        if (!freeSpinsActivated) {
            if (spinAmount / getWinLanes() > machine.getMinimumCoinsPerLane()) {
                PlayerManager.setCoins(PlayerManager.getCoins() - spinAmount);
            }
        } else {
            freeSpinsLeft--;
            if (freeSpinsLeft == 0) {
                freeSpinsActivated = false;
                freeSpinsLabel.setVisible(false);
                additionalX2SymbolChance -= 5;
            }
        }
        labels.forEach(label -> label.paintImmediately(label.getVisibleRect()));
        long win = calculateWin(spinAmount);
        if (checkForFreeSpins()) {
            //System.out.println("Free spins!\n You won " + freeSpinsLeft + " free spins.");
            freeSpinsActivated = true;
            freeSpinsLabel.setVisible(true);
        }
        freeSpinsLabel.setText(String.valueOf(freeSpinsLeft));
        return win;
    }

    //berechnet was gewonnen wird

    private long calculateWin(long spinAmount) {
        int winSymbol = -1;
        boolean symbolLockedIn = false;
        int correctSymbols = 0;
        long tempCoinWin;
        long wholeSpinWin = 0;
        int x2Amount = 0;
        List<Integer> firstColumn = new ArrayList<>(Arrays.asList(0, rowAmount, rowAmount * 2));
        List<Integer> currentColumn = new ArrayList<>();
        //der loop geht jede gewinnmöglichkeit einzeln durch
        for (int[] winningGrid : winningGrids) {
            //es wird bei der ersten Spalte begonnen
            currentColumn.addAll(firstColumn);
            tempCoinWin = 0;
            //die walzen werden spaltenweise durchgegangen
            for (int j = 0; j < rowAmount; j++) {
                for (Integer i : currentColumn) {
                    //wenn in der Spalte die gewinnende Zelle gefunden wurde
                    if (winningGrid[i] == 1) {
                        //bekommt diese zuerst eine border, damit man sieht welche gewinnreihe es insgesamt ist
                        if (stopForWinLines) {
                            labels.get(i).setBorder(slotsLeftBorder);
                        }
                        //falls das gewinnende symbol bereits ausgewählt wurde
                        if (symbolLockedIn) {
                            //und das symboyl in der gewinnzelle das gewinnersymbol ist
                            if (currentSlotGrid[i] == winSymbol) {
                                correctSymbols++;
                                if (stopForWinLines) {
                                    labels.get(i).setBorder(winBorder);
                                }
                                //falss es ein x2 ist wird die anzahl erhöht
                            } else if (currentSlotGrid[i] == icons.size() - 1) {
                                x2Amount++;
                                correctSymbols++;
                                //wenn es sich nicht um das gewinnsymbol handelt
                            } else {
                                //und bisher nicht 3 symbole gefunden wurden
                                if (correctSymbols < 3) {
                                    //geht es zum nächsten gewinnmuster
                                    correctSymbols = 0;
                                    j = 6;
                                    //wenn es mehr als 2 sind werden die restlichen zellen noch eingefärbt zur besseren zuordnung
                                } else {
                                    symbolLockedIn = false;
                                }
                            }
                        } else {
                            //wenn noch kein symbol eingelockt ist(erste spalte) wird es genommen und als gewinnsymbol ausgewählt
                            if (correctSymbols < 3) {
                                winSymbol = currentSlotGrid[i];
                                correctSymbols++;
                                symbolLockedIn = true;
                                if (stopForWinLines) {
                                    labels.get(i).setBorder(winBorder);
                                }
                            }
                        }
                        //die momentane spalte wird um eins nach rechts verschoben und es wird weiter geprüft
                        currentColumn.replaceAll(num -> num + 1);
                        break;
                    }

                }
            }
            //falls nach ende des kompletten musters mehr als 2 symbole gefunden wurden
            if (correctSymbols > 2) {
                //wird der gewinn amount bestimmt
                switch (correctSymbols) {
                    case 3:
                        tempCoinWin = currentWin3Symbol[winSymbol] * spinAmount;
                        break;
                    case 4:
                        tempCoinWin = currentWin4Symbol[winSymbol] * spinAmount;
                        break;
                    case 5:
                        tempCoinWin = currentWin5Symbol[winSymbol] * spinAmount;
                        break;
                }
                //falls x2 bonis in der reihe, waren werden diese angewandt
                if (x2Amount != 0) {
                    tempCoinWin *= x2Amount * 2L;
                }
                tempCoinWin *= machine.getPet().getMulti();
                PlayerManager.setCoins(PlayerManager.getCoins() + tempCoinWin);
                //damit der gewinn am ende aller muster angezeigt werden kann
                wholeSpinWin += tempCoinWin;
                System.out.println("-----Winning-----\nCorrect Symbols: " + correctSymbols + "(" + winSymbol + ")\nWin: " + tempCoinWin + "\nMultiplier: " + tempCoinWin / spinAmount);
                showGoodGrids(winningGrid);
                labels.forEach(label -> label.paintImmediately(label.getVisibleRect()));
                //jede gewinnende reihe wird kurz angezeigt und verschwindet dann wieder
                if (stopForWinLines) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            labels.forEach(label -> label.setBorder(normalBorder));
            labels.forEach(label -> label.paintImmediately(label.getVisibleRect()));
            currentColumn.clear();
            correctSymbols = 0;
            symbolLockedIn = false;
            x2Amount = 0;
        }
        return wholeSpinWin;
    }

    private boolean checkForFreeSpins() {
        int freeSpinSymbolAmount = 0;
        //falls im grid blitze(0) sind werden diese gezählt
        for (int i = 0; i < currentSlotGrid.length; i++) {
            if (currentSlotGrid[i] == 0) {
                freeSpinSymbolAmount++;
                labels.get(i).setBorder(freeSpinBorder);
            }
        }
        if (freeSpinSymbolAmount == 3) {
            if (!freeSpinsActivated) {
                additionalX2SymbolChance += 5;
            }
            freeSpinsLeft += 10;
            return true;
            //für jedes weitere freespin symbol bekommt man 10 weitere (bei 5 bekommt man 20 zu den bereits 10)
        } else if (freeSpinSymbolAmount > 3) {
            if (!freeSpinsActivated) {
                additionalX2SymbolChance += 5;
            }

            labels.forEach(label -> label.paintImmediately(label.getVisibleRect()));
            freeSpinsLeft += (freeSpinSymbolAmount - 3) * 10 + 10;
            return true;
        }
        labels.forEach(label -> label.setBorder(normalBorder));
        labels.forEach(label -> label.paintImmediately(label.getVisibleRect()));
        return false;
    }

    private int randomNumber(int begin, int end) {
        return new Random().nextInt(begin, end);
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

    public int getRowAmount() {
        return rowAmount;
    }

    public void setRowAmount(int rowAmount) {
        int tempRowAmount = this.rowAmount;
        for (int i = 0; i < rowAmount-tempRowAmount; i++) {
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
