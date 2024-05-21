package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Application {

    private boolean resetData = false;
    private String saveDataPath = "SaveData";
    private JFrame mainFrame;
    public static int maxSevenChance = 10;
    public static int maxTier4Chance = 20;
    public static int maxTier3Chance = 30;
    public static int maxTier2Chance = 40;
    public static int maxX2Chance = 5;

    private int slotAmount = 0;
    private ArrayList<Integer> startGridYCords = new ArrayList<>(Arrays.asList(100, 100, 100, 100,550,550,550,550));
    private ArrayList<Integer> startGridXCords = new ArrayList<>(Arrays.asList(20, 370, 720, 1070, 20, 370, 720, 1070));

    private ArrayList<SlotMachine> machines;

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
        PlayerManager.setMainFrame(mainFrame);
        machines = new ArrayList<>();
    }

    public static void generateIconsFromFolder(ArrayList<String> fileNames, ArrayList<ImageIcon> iconsList, String folder, int size) {
        fileNames.forEach(name -> {
            ImageIcon original = new ImageIcon(folder + "/" + name + ".png");
            Image image = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            iconsList.add(new ImageIcon(image));
        });
    }


    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(saveDataPath))) {
            slotAmount = 0;
            while (br.readLine() != null) {
                slotAmount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(saveDataPath))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                generateSlotMachine(currentLine);
                if (currentLine == 0) {
                    PlayerManager.setCoins(Integer.parseInt(values[0]));
                    PlayerManager.setPlayerLevel(Integer.parseInt(values[1]));
                    PlayerManager.setPlayerXp(Integer.parseInt(values[2]));
                    Pet.setPet(Integer.parseInt(values[10]));
                }
                machines.get(currentLine).getSlotGrid().setAdditionalSevenSymbolChance(Integer.parseInt(values[3]));
                machines.get(currentLine).getSlotGrid().setAdditionalTier4SymbolChance(Integer.parseInt(values[4]));
                machines.get(currentLine).getSlotGrid().setAdditionalTier3SymbolChance(Integer.parseInt(values[5]));
                machines.get(currentLine).getSlotGrid().setAdditionalTier2SymbolChance(Integer.parseInt(values[6]));
                machines.get(currentLine).getSlotGrid().setTier1SymbolChance(Integer.parseInt(values[7]));
                machines.get(currentLine).getSlotGrid().setAdditionalX2SymbolChance(Integer.parseInt(values[8]));
                machines.get(currentLine).getUpgradeArea().setAddedGrids(Integer.parseInt(values[9]));
                machines.get(currentLine).getSlotGrid().setRowAmount(Integer.parseInt(values[11]));
                machines.get(currentLine).instantiate();
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveDataPath))) {
            if (resetData) {
                writer.print(0 + "," + 1 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 46 + "," + 0 + "," + 0 + "," + -1 + "," + 3);
            } else {
                for (SlotMachine machine : machines) {
                    writer.println(PlayerManager.getCoins() + "," + PlayerManager.getPlayerLevel() + "," + PlayerManager.getPlayerXp()
                            + "," + machine.getSlotGrid().getAdditionalSevenSymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalTier4SymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalTier3SymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalTier2SymbolChance()
                            + "," + machine.getSlotGrid().getTier1SymbolChance()
                            + "," + machine.getSlotGrid().getAdditionalX2SymbolChance()
                            + "," + machine.getUpgradeArea().getAddedGrids() + "," + Pet.petTier + ","+ machine.getSlotGrid().getRowAmount());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        loadData();

        if(slotAmount < 6){
            mainFrame.setSize((slotAmount-(slotAmount/5))*350 + 100,(1+(slotAmount/8))*450+100);
        }
        PlayerManager.arrangeLabelsBasedOnSize();
        PlayerManager.rearrangeCoinLabel();
        mainFrame.setVisible(true);
    }

    private void generateSlotMachine(int slotNumber) {
        machines.add(new SlotMachine(startGridXCords.get(slotNumber), startGridYCords.get(slotNumber), mainFrame));
        if (machines.size() == slotAmount) {
            JLabel addNewMachinePriceLabel = new JLabel("500000000");
            addNewMachinePriceLabel.setForeground(Color.YELLOW);
            addNewMachinePriceLabel.setBounds(startGridXCords.get(slotNumber) + 290, startGridYCords.get(slotNumber) + 160, 80, 20);
            mainFrame.add(addNewMachinePriceLabel);
            CasinoButton addNewSlotMachineBtt = new CasinoButton("<html>Add<br>New Machine</html>");
            addNewSlotMachineBtt.setBounds(startGridXCords.get(slotNumber) + 270, startGridYCords.get(slotNumber) + 110, 100, 50);
            addNewSlotMachineBtt.addActionListener(e -> {
                if (Integer.parseInt(addNewMachinePriceLabel.getText()) <= PlayerManager.getCoins()) {
                    slotAmount++;
                    if(slotAmount < 6){
                        mainFrame.setSize((slotAmount-(slotAmount/5))*350 + 100,(1+(slotAmount/5))*450+100);
                    }
                    generateSlotMachine(slotAmount - 1);
                    PlayerManager.arrangeLabelsBasedOnSize();
                    PlayerManager.rearrangeCoinLabel();
                    machines.get(slotAmount - 1).instantiate();
                    mainFrame.repaint();
                    addNewSlotMachineBtt.setVisible(false);
                    addNewMachinePriceLabel.setVisible(false);
                }

            });
            mainFrame.add(addNewSlotMachineBtt);
            if(slotAmount == 8){
                addNewSlotMachineBtt.setVisible(false);
                addNewMachinePriceLabel.setVisible(false);
            }
        }

    }
    //150y beginnt der slot und geht bis 300
    //beginnt bei x100
//        mainFrame.add(placeholderLabel3);
//        mainFrame.add(placeholderLabel4);
//        mainFrame.add(placeholderLabel5);
//        mainFrame.add(placeholderLabel6);
//        mainFrame.add(placeholderLabel7);
//        mainFrame.add(placeholderLabel8);
//        mainFrame.add(placeholderLabel9);


}
