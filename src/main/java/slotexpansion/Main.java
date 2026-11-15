package slotexpansion;

import slotexpansion.app.Application;
import javax.swing.SwingUtilities;

/** Starts the Slot Expansion desktop application. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Application().run());
    }
}
