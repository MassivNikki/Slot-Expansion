package CasinoStuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private Image backgroundImage;
    public MainFrame() {
        super();
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.decode("#04395E"));
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponents(g);
    }

}