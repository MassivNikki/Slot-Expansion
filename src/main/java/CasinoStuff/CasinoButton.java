package CasinoStuff;

import javax.swing.*;
import java.awt.*;

public class CasinoButton extends JButton {
    public CasinoButton(String text) {
        super(text);
        styleButton();
    }

    public CasinoButton() {
        super();
        styleButton();
    }
    private void styleButton(){
        setMargin(new Insets(0, 0, 0, 0));
        setBackground(new Color(0x2dce98));
        setForeground(Color.white);
        setUI(new StyledButtonUI());
    }
}
