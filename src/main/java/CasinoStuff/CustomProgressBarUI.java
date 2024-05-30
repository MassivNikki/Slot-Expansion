package CasinoStuff;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class CustomProgressBarUI extends BasicProgressBarUI {

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        // Entferne die Hintergrundmalerei
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        // Fülle den Fortschrittsbalken
        g.setColor(progressBar.getForeground());
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            g.fillRect(b.left, b.top, amountFull, barRectHeight);
        } else { // VERTICAL
            g.fillRect(b.left, barRectHeight + b.top - amountFull, barRectWidth, amountFull);
        }

        // Zeichne den Text (wenn vorhanden)
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        }
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        // Optional: Anpassungen für den indeterminierten Zustand
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Custom ProgressBar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setUI(new CustomProgressBarUI());
        progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        progressBar.setBackground(frame.getBackground());
        progressBar.setForeground(Color.GREEN);
        progressBar.setStringPainted(true);
        progressBar.setValue(50); // Beispielwert

        frame.setLayout(new BorderLayout());
        frame.add(progressBar, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
