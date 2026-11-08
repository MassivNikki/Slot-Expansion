package CasinoStuff;

import WholeMachine.Pet;
import WholeMachine.SlotMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Coordinates the game window, saved progress, and the set of slot machines.
 */
public class Application {

    private final boolean resetData = false;
    private final ArrayList<JPanel> machinePanels = new ArrayList<>();
    private final String saveDataFilePath = "SaveData";
    public static JFrame mainFrame;
    public static int maxSevenChance = 10;
    public static int maxTier4Chance = 20;
    public static int maxTier3Chance = 30;
    public static int maxTier2Chance = 35;
    public static int maxX2Chance = 5;
    private BigInteger machinePurchasePrice = BigInteger.valueOf(50000000000L);
    private int machineCount = 0;
    private final ArrayList<Integer> machineYPositions = new ArrayList<>(Arrays.asList(60, 60, 60, 60, 550, 550, 550, 550));
    private final ArrayList<Integer> machineXPositions = new ArrayList<>(Arrays.asList(10, 420, 830, 1240, 10, 420, 830, 1240));

    private final ArrayList<SlotMachine> machines;

    public Application() {
        mainFrame = new MainFrame();
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveProgress();
                mainFrame.dispose();
                System.exit(0);
            }
        });

        machines = new ArrayList<>();
    }

    /**
     * Loads and scales a set of PNG assets whose names do not include extensions.
     */
    public static void generateIconsFromFolder(ArrayList<String> fileNames, ArrayList<ImageIcon> iconsList, String folder, int size) {
        fileNames.forEach(name -> {
            ImageIcon original = new ImageIcon(folder + "/" + name + ".png");
            Image image = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            iconsList.add(new ImageIcon(image));
        });
    }

    /**
     * Counts saved machine records so the window can be sized before loading them.
     */
    private void countSavedMachines() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveDataFilePath))) {
            machineCount = 0;
            while (reader.readLine() != null) {
                machineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates each saved machine and restores its persisted state.
     */
    private void loadSavedMachines() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveDataFilePath))) {
            String line;
            int machineIndex = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                createMachine(machineIndex);
                if (machineIndex == 0) {
                    PlayerManager.setCoins(new BigInteger(values[0]));
                }
                restoreMachineState(machines.get(machineIndex), values);
                machineIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the machine fields stored in one comma-separated save record.
     * Columns are balance, level, XP, symbol chances, added paylines, pet tier,
     * grid size, cooldowns, auto-spin state, and stake-limit upgrade counts.
     */
    private void restoreMachineState(SlotMachine machine, String[] values) {
        machine.slotLevelComponent.setSlotLevel(Integer.parseInt(values[1]));
        machine.slotLevelComponent.setSlotXp(new BigInteger(values[2]));
        machine.slotGrid.setAdditionalSevenSymbolChance(Integer.parseInt(values[3]));
        machine.slotGrid.setAdditionalTier4SymbolChance(Integer.parseInt(values[4]));
        machine.slotGrid.setAdditionalTier3SymbolChance(Integer.parseInt(values[5]));
        machine.slotGrid.setAdditionalTier2SymbolChance(Integer.parseInt(values[6]));
        machine.slotGrid.setTier1SymbolChance(Integer.parseInt(values[7]));
        machine.slotGrid.setAdditionalX2SymbolChance(Integer.parseInt(values[8]));
        machine.upgradeArea.addedGrids = Integer.parseInt(values[9]);
        machine.setPet(new Pet(Integer.parseInt(values[10])));
        machine.slotGrid.setRowAmount(Integer.parseInt(values[11]));
        machine.autoCooldown = Integer.parseInt(values[12]);
        machine.manualCooldown = Integer.parseInt(values[13]);
        machine.autoSpinUnlocked = Boolean.parseBoolean(values[14]);
        machine.minIncreased = Integer.parseInt(values[15]);
        machine.maxIncreased = Integer.parseInt(values[16]);
        machine.updateComponentsWithData();
    }

    /**
     * Writes the current balance and machine upgrades when the window closes.
     */
    private void saveProgress() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveDataFilePath))) {
            if (resetData) {
                writer.print("12345678910111213,1,0,0,0,0,0,53,0,0,-1,3,1000,500,false,0,0");
            } else {
                StringBuilder sb = new StringBuilder();
                for (SlotMachine machine : machines) {
                    appendMachineState(sb, machine);
                    writer.println(sb);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends fields in the established SaveData column order.
     * Keep this order aligned with {@link #restoreMachineState(SlotMachine, String[])}.
     */
    private void appendMachineState(StringBuilder saveRecord, SlotMachine machine) {
        saveRecord.append(PlayerManager.getCoins())
                .append(",").append(machine.slotLevel)
                .append(",").append(machine.getSlotXp())
                .append(",").append(machine.slotGrid.getAdditionalSevenSymbolChance())
                .append(",").append(machine.slotGrid.getAdditionalTier4SymbolChance())
                .append(",").append(machine.slotGrid.getAdditionalTier3SymbolChance())
                .append(",").append(machine.slotGrid.getAdditionalTier2SymbolChance())
                .append(",").append(machine.slotGrid.getTier1SymbolChance())
                .append(",").append(machine.slotGrid.getAdditionalX2SymbolChance())
                .append(",").append(machine.upgradeArea.addedGrids)
                .append(",").append(machine.pet.petTier)
                .append(",").append(machine.slotGrid.getRowAmount())
                .append(",").append(machine.autoCooldown)
                .append(",").append(machine.manualCooldown)
                .append(",").append(machine.autoSpinUnlocked)
                .append(",").append(machine.minIncreased)
                .append(",").append(machine.maxIncreased);
    }

    /**
     * Initializes the balance display, restores saved machines, then shows the window.
     */
    public void run() {
        countSavedMachines();
        PlayerManager.setMainFrame(mainFrame);
        resizeMainFrame();
        loadSavedMachines();
        mainFrame.setVisible(true);
    }

    private void resizeMainFrame() {
        if (machineCount < 5) {
            mainFrame.setSize(machineXPositions.get(machineCount - 1) + (20 + 480) * (1 - machineCount / 5), (1 + (machineCount / 5)) * 450 + 100);
            PlayerManager.setSizeToMainFrameSize();
        } else if (machineCount == 5) {
            mainFrame.setSize(mainFrame.getWidth(), (1 + (machineCount / 5)) * 450 + 100);
            PlayerManager.setSizeToMainFrameSize();
        }
    }

    private void createMachine(int machineIndex) {
        machinePanels.add(new JPanel());
        JPanel currentMachinePanel = machinePanels.getLast();
        currentMachinePanel.setBounds(machineXPositions.get(machineIndex), machineYPositions.get(machineIndex), 400, 450);
        currentMachinePanel.setBackground(null);
        currentMachinePanel.setLayout(null);
        machines.add(new SlotMachine(machinePanels.getLast(), machines.size() + 1));
        if (machines.size() == machineCount && machineCount < 8) {
            CasinoUpgradeButton newMachineButton =
                    new CasinoUpgradeButton(machinePurchasePrice, machines.getLast().getSlotTier(), "<html>Add<br>New<br>Machine</html>"
                            , mainFrame, PriceLabelPosition.UNDER);
            newMachineButton.addCustomActionListener(e -> {
                machineCount++;
                resizeMainFrame();
                machinePurchasePrice = newMachineButton.getPrice();
                createMachine(machineCount - 1);
                machines.get(machineCount - 1).updateComponentsWithData();
                newMachineButton.removeFromMainframe(mainFrame);
                mainFrame.repaint();
            });
            newMachineButton.setBounds(machineXPositions.get(machineIndex) + 420, machineYPositions.get(machineIndex) + 165, 60, 80);
            mainFrame.add(newMachineButton);
        }
        mainFrame.add(currentMachinePanel);
    }
}
