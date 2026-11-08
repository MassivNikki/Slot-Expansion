package CasinoStuff;

import javax.swing.*;
import java.awt.*;

/** Main, fixed-size game window, centered on the current display. */
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

        // Read the current display and window dimensions.
        int frameWidth = getSize().width;
        int frameHeight = getSize().height;

        // Calculate the top-left point that centers the window.
        int x = (screenWidth - frameWidth) / 2;
        int y = (screenHeight - frameHeight) / 2;

        // Apply the centered position.
        setLocation(x, y);
    }
}
