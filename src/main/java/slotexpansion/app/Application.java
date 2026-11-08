package slotexpansion.app;

import slotexpansion.machine.Pet;
import slotexpansion.machine.SlotMachine;
import slotexpansion.player.PlayerManager;
import slotexpansion.ui.CasinoUpgradeButton;
import slotexpansion.ui.MainFrame;
import slotexpansion.ui.PriceLabelPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Coordinates the game window, saved progress, and the set of slot machines.
 */
public class Application {
    private static final Map<IconKey, ImageIcon> ICON_CACHE = new HashMap<>();
    private static final String SAVE_DATA_HEADER =
            "# balance,slotLevel,slotXp,additionalSevenSymbolChance,additionalTier4SymbolChance,"
                    + "additionalTier3SymbolChance,additionalTier2SymbolChance,tier1SymbolChance,"
                    + "additionalX2SymbolChance,addedPaylines,petTier,columnCount,autoCooldown,"
                    + "manualCooldown,autoSpinUnlocked,minStakeUpgrades,maxStakeUpgrades";
    /** One starting machine and balance for a new or empty save file. */
    private static final String DEFAULT_SAVE_DATA_RECORD =
            "1000,1,0,0,0,0,0,51,0,0,-1,3,1000,500,false,0,0";

    private final ArrayList<JPanel> machinePanels = new ArrayList<>();
    private final String saveDataFilePath = "SaveData";
    private final ArrayList<String> savedMachineRecords = new ArrayList<>();
    public static JFrame mainFrame;
    public static int maxSevenChance = 10;
    public static int maxTier4Chance = 20;
    public static int maxTier3Chance = 30;
    public static int maxTier2Chance = 35;
    public static int maxX2Chance = 5;
    private BigInteger machinePurchasePrice = BigInteger.valueOf(100000);
    private int machineCount = 0;
    private final ArrayList<Integer> machineYPositions = new ArrayList<>(Arrays.asList(60, 60, 60, 60, 550, 550, 550, 550));
    private final ArrayList<Integer> machineXPositions = new ArrayList<>(Arrays.asList(10, 420, 830, 1240, 10, 420, 830, 1240));

    private final ArrayList<SlotMachine> machines;

    private record IconKey(String folder, String name, int size) {
    }

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
        for (String name : fileNames) {
            IconKey key = new IconKey(folder, name, size);
            ImageIcon icon = ICON_CACHE.computeIfAbsent(key, asset -> {
                ImageIcon original = new ImageIcon(asset.folder() + "/" + asset.name() + ".png");
                Image image = original.getImage().getScaledInstance(asset.size(), asset.size(), Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            });
            iconsList.add(icon);
        }
    }

    /**
     * Reads saved records once, using a fresh-game record when the file is empty.
     */
    private void loadSavedMachineRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveDataFilePath))) {
            String record;
            while ((record = reader.readLine()) != null) {
                String trimmedRecord = record.trim();
                if (!trimmedRecord.isEmpty() && !trimmedRecord.startsWith("#")) {
                    savedMachineRecords.add(trimmedRecord);
                }
            }
        } catch (FileNotFoundException e) {
            savedMachineRecords.add(DEFAULT_SAVE_DATA_RECORD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (savedMachineRecords.isEmpty()) {
            savedMachineRecords.add(DEFAULT_SAVE_DATA_RECORD);
        }
        machineCount = savedMachineRecords.size();
        for (int purchasedTier = 1; purchasedTier < machineCount; purchasedTier++) {
            machinePurchasePrice = machinePurchasePrice
                    .multiply(BigInteger.valueOf(2))
                    .multiply(BigInteger.valueOf(purchasedTier).pow(2));
        }
    }

    /**
     * Creates each saved machine and restores its persisted state.
     */
    private void loadSavedMachines() {
        for (int machineIndex = 0; machineIndex < savedMachineRecords.size(); machineIndex++) {
            String[] values = savedMachineRecords.get(machineIndex).split(",");
            createMachine(machineIndex);
            if (machineIndex == 0) {
                PlayerManager.setCoins(new BigInteger(values[0]));
            }
            restoreMachineState(machines.get(machineIndex), values);
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
        machine.slotGrid.setColumnCount(Integer.parseInt(values[11]));
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
            writer.println(SAVE_DATA_HEADER);
            for (SlotMachine machine : machines) {
                StringBuilder saveRecord = new StringBuilder();
                appendMachineState(saveRecord, machine);
                writer.println(saveRecord);
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
                .append(",").append(machine.slotGrid.getColumnCount())
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
        loadSavedMachineRecords();
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
