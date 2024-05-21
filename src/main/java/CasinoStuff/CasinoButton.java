package CasinoStuff;

import javax.swing.*;
import java.awt.*;

public class CasinoButton extends JButton {
    public CasinoButton(String text) {
        super(text);
        setMargin(new Insets(0, 0, 0, 0));
        setBackground(new Color(0x2dce98));
        setForeground(Color.white);
        setUI(new StyledButtonUI());
    }

    public CasinoButton() {
        super();
        setMargin(new Insets(0, 0, 0, 0));
        setBackground(new Color(0x2dce98));
        setForeground(Color.white);
        setUI(new StyledButtonUI());
    }
}
