package slotexpansion.app;

import slotexpansion.machine.MachineSave;
import slotexpansion.machine.SlotMachine;
import slotexpansion.machine.UpgradePrice;
import slotexpansion.player.PlayerManager;
import slotexpansion.ui.CasinoUpgradeButton;
import slotexpansion.ui.MainFrame;
import slotexpansion.ui.PriceLabelPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates the game window, saved progress, and the set of slot machines.
 */
public class Application {
    private static final Map<IconKey, ImageIcon> ICON_CACHE = new HashMap<>();
    private static final int MACHINE_ROW_HEIGHT = 470;
    private static final int MACHINE_HEIGHT = 470;
    private final ArrayList<JPanel> machinePanels = new ArrayList<>();
    private final JPanel machineCanvas = new JPanel(null);
    private final SaveRepository saveRepository;
    public static JFrame mainFrame;
    private BigInteger machinePurchasePrice = UpgradePrice.FIRST_MACHINE_UNLOCK;
    private int machineCount = 0;
    private final ArrayList<SlotMachine> machines;

    private record IconKey(String folder, String name, int size) {
    }

    public Application() {
        this(new SaveRepository(Path.of("SaveData")));
    }

    Application(SaveRepository saveRepository) {
        this.saveRepository = saveRepository;
        mainFrame = new MainFrame();
        machineCanvas.setBackground(Color.decode("#04396E"));
        mainFrame.add(machineCanvas, BorderLayout.CENTER);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (saveProgress()) {
                    mainFrame.dispose();
                    System.exit(0);
                }
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

    private void initializeMachinePrice() {
        UpgradePrice price = new UpgradePrice(machinePurchasePrice, 1, UpgradePrice.Growth.MILESTONE);
        for (int purchasedTier = 1; purchasedTier < machineCount; purchasedTier++) {
            price.advance();
        }
        machinePurchasePrice = price.current();
    }

    private void loadSavedMachines(List<MachineSave> records) {
        for (int machineIndex = 0; machineIndex < records.size(); machineIndex++) {
            MachineSave state = records.get(machineIndex);
            createMachine(machineIndex);
            if (machineIndex == 0) {
                PlayerManager.setCoins(state.balance());
            }
            machines.get(machineIndex).restore(state);
        }
    }

    private boolean saveProgress() {
        try {
            List<MachineSave> records = new ArrayList<>();
            for (SlotMachine machine : machines) {
                records.add(machine.snapshot(PlayerManager.getCoins()));
            }
            saveRepository.save(records);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, "Progress could not be saved: " + e.getMessage(),
                    "Save failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Initializes the balance display, restores saved machines, then shows the window.
     */
    public void run() {
        List<MachineSave> records;
        try {
            records = saveRepository.load();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Progress could not be loaded: " + e.getMessage(),
                    "Load failed", JOptionPane.ERROR_MESSAGE);
            mainFrame.dispose();
            return;
        }
        machineCount = records.size();
        initializeMachinePrice();
        PlayerManager.setMainFrame(mainFrame);
        resizeMainFrame();
        loadSavedMachines(records);
        mainFrame.setVisible(true);
    }

    private void resizeMainFrame() {
        int columns = Math.min(machineCount, 4);
        int rows = (machineCount + 3) / 4;
        int contentWidth = 10 + (columns - 1) * 410 + 500;
        int contentHeight = (rows - 1) * MACHINE_ROW_HEIGHT + MACHINE_HEIGHT + 20;
        machineCanvas.setPreferredSize(new Dimension(contentWidth, contentHeight));
        machineCanvas.revalidate();
        mainFrame.pack();
    }

    private void createMachine(int machineIndex) {
        machinePanels.add(new JPanel());
        JPanel currentMachinePanel = machinePanels.getLast();
        int x = 10 + (machineIndex % 4) * 410;
        int y = (machineIndex / 4) * MACHINE_ROW_HEIGHT;
        currentMachinePanel.setBounds(x, y, 400, MACHINE_HEIGHT);
        currentMachinePanel.setBackground(null);
        currentMachinePanel.setLayout(null);
        machines.add(new SlotMachine(machinePanels.getLast(), machines.size() + 1));
        if (machines.size() == machineCount && machineCount < 8) {
            CasinoUpgradeButton newMachineButton =
                    new CasinoUpgradeButton(machinePurchasePrice, 1, "<html>Add<br>New<br>Machine</html>",
                            machineCanvas, PriceLabelPosition.UNDER, UpgradePrice.Growth.MILESTONE);
            newMachineButton.addCustomActionListener(e -> {
                machineCount++;
                resizeMainFrame();
                machinePurchasePrice = newMachineButton.getPrice();
                createMachine(machineCount - 1);
                machines.get(machineCount - 1).updateComponentsWithData();
                newMachineButton.removeFromMachinePanel(machineCanvas);
                machineCanvas.repaint();
            });
            newMachineButton.setBounds(x + 420, y + 165, 60, 80);
            machineCanvas.add(newMachineButton);
        }
        machineCanvas.add(currentMachinePanel);
    }
}
