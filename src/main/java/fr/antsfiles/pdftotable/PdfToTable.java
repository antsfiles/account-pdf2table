package fr.antsfiles.pdftotable;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 */
public class PdfToTable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Account Pdf 2 Table");
            f.setSize(1000, 700);
            f.setContentPane(new MainPanel());
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
