package CasinoStuff;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/** Draws the upgrade window's tabs with the game's rounded color treatment. */
public class CustomTabbedPaneUI extends BasicTabbedPaneUI {

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabAreaInsets.left = 10;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 15; // Corner radius for the rounded tab shape.
        if (isSelected) {
            g2d.setColor(new Color(0x2dce98));
        } else {
            g2d.setColor(new Color(0x136F63));
        }
        g2d.fillRoundRect(x, y, w, h, arc, arc);
        g2d.dispose();
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // The rounded backgrounds provide the tab separation.
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = tabPane.getWidth();
        int height = tabPane.getHeight();

        g2d.setColor(Color.DARK_GRAY);
        g2d.dispose();
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // The custom tab design does not draw a focus indicator.
    }
}
