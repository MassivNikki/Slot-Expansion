package CasinoStuff;

import WholeMachine.Pet;
import WholeMachine.SlotMachine;
import WholeMachine.UpgradeArea;
import WholeMachine.UpgradeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Objects;

public class Application {

    private final boolean resetData = false;
    private final String saveDataPath = "SaveData";
    private final JFrame mainFrame;
    public static int maxSevenChance = 10;
    public static int maxTier4Chance = 20;
    public static int maxTier3Chance = 30;
    public static int maxTier2Chance = 35;
    public static int maxX2Chance = 5;
    private BigInteger newMachinePrice = BigInteger.valueOf(50000000000L);
    private int slotAmount = 0;
    private final ArrayList<Integer> startGridYCords = new ArrayList<>(Arrays.asList(100, 100, 100, 100, 550, 550, 550, 550));
    private final ArrayList<Integer> startGridXCords = new ArrayList<>(Arrays.asList(20, 390, 760, 1130, 20, 390, 760, 1130));

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
                currentMachine.setSlotLevel(Integer.parseInt(values[1]));
                currentMachine.setSlotXp(new BigInteger(values[2]));
                currentMachine.getSlotGrid().setAdditionalSevenSymbolChance(Integer.parseInt(values[3]));
                currentMachine.getSlotGrid().setAdditionalTier4SymbolChance(Integer.parseInt(values[4]));
                currentMachine.getSlotGrid().setAdditionalTier3SymbolChance(Integer.parseInt(values[5]));
                currentMachine.getSlotGrid().setAdditionalTier2SymbolChance(Integer.parseInt(values[6]));
                currentMachine.getSlotGrid().setTier1SymbolChance(Integer.parseInt(values[7]));
                currentMachine.getSlotGrid().setAdditionalX2SymbolChance(Integer.parseInt(values[8]));
                currentMachine.getUpgradeArea().setAddedGrids(Integer.parseInt(values[9]));
                currentMachine.setPet(new Pet(Integer.parseInt(values[10])));
                currentMachine.getSlotGrid().setRowAmount(Integer.parseInt(values[11]));
                currentMachine.setAutoCooldown(Integer.parseInt(values[12]));
                currentMachine.setManualCooldown(Integer.parseInt(values[13]));
                currentMachine.setAutoSpinUnlocked(Boolean.parseBoolean(values[14]));
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
                writer.print("12345678910111213,1,0,0,0,0,0,46,0,0,-1,3,1000,500,false");
            } else {
                for (SlotMachine machine : machines) {
                    writer.println(PlayerManager.getCoins()
                            + "," + machine.getSlotLevel()
                            + "," + machine.getSlotXp()
                            + "," + machine.getSlotGrid().getAdditionalSevenSymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalTier4SymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalTier3SymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalTier2SymbolChance()
                            + "," + machine.getSlotGrid().getTier1SymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalX2SymbolChance()
                            + "," + machine.getUpgradeArea().getAddedGrids()
                            + "," + machine.getPetTier()
                            + "," + machine.getSlotGrid().getRowAmount()
                            + "," + machine.getAutoCooldown()
                            + "," + machine.getManualCooldown()
                            + "," + machine.isAutoSpinUnlocked());
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
        if (slotAmount < 6) {
            mainFrame.setSize(20 + slotAmount * 370 + 20, (1 + (slotAmount / 5)) * 450 + 100);
            PlayerManager.setSizeToMainFrameSize();
        }
    }

    private void generateSlotMachine(int slotNumber) {
        machines.add(new SlotMachine(startGridXCords.get(slotNumber), startGridYCords.get(slotNumber), mainFrame, machines.size()+1));
        if (machines.size() == slotAmount) {
            PriceLabel addNewMachinePriceLabel = new PriceLabel(newMachinePrice,machines.getLast().getSlotTier());
            addNewMachinePriceLabel.setBounds(startGridXCords.get(slotNumber) + 280, startGridYCords.get(slotNumber) + 180, 80, 20);
            mainFrame.add(addNewMachinePriceLabel);
            CasinoButton addNewSlotMachineBtt = new CasinoButton("<html>Add<br>New<br>Machine</html>");
            addNewSlotMachineBtt.setBounds(startGridXCords.get(slotNumber) + 280, startGridYCords.get(slotNumber) + 110, 70, 70);
            addNewSlotMachineBtt.addActionListener(e -> {
                if (addNewMachinePriceLabel.getPrice().compareTo(PlayerManager.getCoins()) <= 0) {
                    slotAmount++;
                    resizeMainFrame();
                    PlayerManager.decreaseCoins(addNewMachinePriceLabel.getPrice());
                    newMachinePrice = addNewMachinePriceLabel.increasePrice();
                    generateSlotMachine(slotAmount - 1);
                    machines.get(slotAmount - 1).updateComponentsWithData();
                    mainFrame.remove(addNewSlotMachineBtt);
                    mainFrame.remove(addNewMachinePriceLabel);
                    mainFrame.repaint();
                }

            });
            mainFrame.add(addNewSlotMachineBtt);
            if (slotAmount == 8) {
                addNewSlotMachineBtt.setVisible(false);
                addNewMachinePriceLabel.setVisible(false);
            }
        }

    }
}
