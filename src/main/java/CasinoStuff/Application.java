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

public class Application {

    private final boolean resetData = false;
    private final ArrayList<JPanel> machinePanels = new ArrayList<>();
    private final String saveDataPath = "SaveData";
    public static JFrame mainFrame;
    public static int maxSevenChance = 10;
    public static int maxTier4Chance = 20;
    public static int maxTier3Chance = 30;
    public static int maxTier2Chance = 35;
    public static int maxX2Chance = 5;
    private BigInteger newMachinePrice = BigInteger.valueOf(50000000000L);
    private int slotAmount = 0;
    private final ArrayList<Integer> startGridYCords = new ArrayList<>(Arrays.asList(60, 60, 60, 60, 550, 550, 550, 550));
    private final ArrayList<Integer> startGridXCords = new ArrayList<>(Arrays.asList(10, 420, 830, 1240, 10, 420, 830, 1240));

    private final ArrayList<SlotMachine> machines;

    public Application() {
        mainFrame = new MainFrame();
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                mainFrame.dispose();
                System.exit(0);
            }
        });

        machines = new ArrayList<>();
    }

    public static void generateIconsFromFolder(ArrayList<String> fileNames, ArrayList<ImageIcon> iconsList, String folder, int size) {
        fileNames.forEach(name -> {
            ImageIcon original = new ImageIcon(folder + "/" + name + ".png");
            Image image = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            iconsList.add(new ImageIcon(image));
        });
    }

    private void loadSlotAmount() {
        try (BufferedReader br = new BufferedReader(new FileReader(saveDataPath))) {
            slotAmount = 0;
            while (br.readLine() != null) {
                slotAmount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveDataPath))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                generateSlotMachine(currentLine);
                if (currentLine == 0) {
                    PlayerManager.setCoins(new BigInteger(values[0]));
                }
                SlotMachine currentMachine = machines.get(currentLine);
                currentMachine.slotLevelComponent.setSlotLevel(Integer.parseInt(values[1]));
                currentMachine.slotLevelComponent.setSlotXp(new BigInteger(values[2]));
                currentMachine.slotGrid.setAdditionalSevenSymbolChance(Integer.parseInt(values[3]));
                currentMachine.slotGrid.setAdditionalTier4SymbolChance(Integer.parseInt(values[4]));
                currentMachine.slotGrid.setAdditionalTier3SymbolChance(Integer.parseInt(values[5]));
                currentMachine.slotGrid.setAdditionalTier2SymbolChance(Integer.parseInt(values[6]));
                currentMachine.slotGrid.setTier1SymbolChance(Integer.parseInt(values[7]));
                currentMachine.slotGrid.setAdditionalX2SymbolChance(Integer.parseInt(values[8]));
                currentMachine.upgradeArea.addedGrids = Integer.parseInt(values[9]);
                currentMachine.setPet(new Pet(Integer.parseInt(values[10])));
                currentMachine.slotGrid.setRowAmount(Integer.parseInt(values[11]));
                currentMachine.autoCooldown = Integer.parseInt(values[12]);
                currentMachine.manualCooldown = Integer.parseInt(values[13]);
                currentMachine.autoSpinUnlocked = Boolean.parseBoolean(values[14]);
                currentMachine.minIncreased = Integer.parseInt(values[15]);
                currentMachine.maxIncreased = Integer.parseInt(values[16]);
                currentMachine.updateComponentsWithData();
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveDataPath))) {
            if (resetData) {
                writer.print("12345678910111213,1,0,0,0,0,0,53,0,0,-1,3,1000,500,false,0,0");
            } else {
                StringBuilder sb = new StringBuilder();
                for (SlotMachine machine : machines) {
                    sb.append(PlayerManager.getCoins())
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
                    writer.println(sb);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        loadSlotAmount();
        PlayerManager.setMainFrame(mainFrame);
        resizeMainFrame();
        loadData();
        mainFrame.setVisible(true);
    }

    private void resizeMainFrame() {
        if (slotAmount < 5) {
            mainFrame.setSize(startGridXCords.get(slotAmount - 1) + (20 + 480) * (1 - slotAmount / 5), (1 + (slotAmount / 5)) * 450 + 100);
            PlayerManager.setSizeToMainFrameSize();
        } else if (slotAmount == 5) {
            mainFrame.setSize(mainFrame.getWidth(), (1 + (slotAmount / 5)) * 450 + 100);
            PlayerManager.setSizeToMainFrameSize();
        }
    }

    private void generateSlotMachine(int slotNumber) {
        machinePanels.add(new JPanel());
        JPanel currentMachinePanel = machinePanels.getLast();
        currentMachinePanel.setBounds(startGridXCords.get(slotNumber), startGridYCords.get(slotNumber), 400, 450);
        currentMachinePanel.setBackground(null);
        currentMachinePanel.setLayout(null);
        machines.add(new SlotMachine(machinePanels.getLast(), machines.size() + 1));
        if (machines.size() == slotAmount && slotAmount < 8) {
            CasinoUpgradeButton newMachineButton =
                    new CasinoUpgradeButton(newMachinePrice, machines.getLast().getSlotTier(), "<html>Add<br>New<br>Machine</html>"
                            , mainFrame, PriceLabelPosition.UNDER);
            newMachineButton.addCustomActionListener(e -> {
                slotAmount++;
                resizeMainFrame();
                newMachinePrice = newMachineButton.getPrice();
                generateSlotMachine(slotAmount - 1);
                machines.get(slotAmount - 1).updateComponentsWithData();
                newMachineButton.removeFromMainframe(mainFrame);
                mainFrame.repaint();
            });
            newMachineButton.setBounds(startGridXCords.get(slotNumber) + 420, startGridYCords.get(slotNumber) + 165, 60, 80);
            mainFrame.add(newMachineButton);
        }
        mainFrame.add(currentMachinePanel);
    }
}
