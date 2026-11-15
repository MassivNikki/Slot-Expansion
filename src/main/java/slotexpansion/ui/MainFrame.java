package slotexpansion.ui;

import javax.swing.*;
import java.awt.*;

/** Main game window; its placement is controlled by the window manager and player. */
public class MainFrame extends JFrame {
    public MainFrame() {
        super();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.decode("#04396E"));
        setResizable(false);
        setLocationByPlatform(true);
        repaint();
    }
}
