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
        setOriginalColor();
        setForeground(Color.white);
        setUI(new StyledButtonUI());
    }
    protected void setOriginalColor(){
        setBackground(new Color(0x7EB2DD));
    }
}
