package CasinoStuff;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.decode("#04396E"));
        setResizable(false);
        setToMiddleOfScreen();
        repaint();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        setToMiddleOfScreen();
    }

    private void setToMiddleOfScreen(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Framegröße holen
        int frameWidth = getSize().width;
        int frameHeight = getSize().height;

        // Berechnung der Position, um das Fenster mittig zu setzen
        int x = (screenWidth - frameWidth) / 2;
        int y = (screenHeight - frameHeight) / 2;

        // Position des JFrame setzen
        setLocation(x, y);
    }
}
