package fr.antsfiles.pdftotable;

import fr.antsfiles.pdftotable.model.Operation;
import fr.antsfiles.pdftotable.parser.ParserLine;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.event.CaretEvent;

/**
 *
 */
public class MainPanel extends javax.swing.JPanel {

    private MainControler mainControler = new MainControler();
    private List<Operation> operations = new ArrayList<>();

    /**
     * Creates new form MainPanel
     */
    public MainPanel() {
        initComponents();

        outPanel1.setMainControler(mainControler);
        outPanel1.setOperations(operations);
        outPanel1.updateTable();
        outPanel1.setOnUpdate(ops -> {
            updateTable();
        });
        layoutPanel.setMainControler(mainControler);
        filePanel1.setMainControler(mainControler);

        jComboBoxType.setSelectedItem(mainControler.format().getValue());

        jTextFieldYear.setText(mainControler.year().getValue());
        mainControler.status().subscribe(jLabelStatus::setText);

        mainControler.format().subscribe(jComboBoxType::setSelectedItem);
        jTextFieldYear.addCaretListener((CaretEvent e) -> {
            mainControler.year().onNext(jTextFieldYear.getText());
        });
        mainControler.outOperation().subscribe(ops -> {
            operations.clear();
            operations.addAll(ops);
            updateTable();
        });

    }

    private void updateTable() {
        outPanel1.setOperations(operations);
        outPanel1.updateTable();
        double totalDebit = operations.stream().filter(o -> !Double.isNaN(o.getAmount())).collect(Collectors.summingDouble(d -> d.getAmount()));
        double totalCredit = operations.stream().filter(o -> !Double.isNaN(o.getCreditamount())).collect(Collectors.summingDouble(d -> d.getCreditamount()));
        debitCreditPanel.updateDebitCredit(totalDebit, totalCredit);
        soldePanel.setTotalDebit(totalDebit);
        soldePanel.setTotalCredit(totalCredit);
        soldePanel.updateSoldeAfter();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jPanelHeader = new javax.swing.JPanel();
        jButtonClipboard = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jTextFieldYear = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jButtonToClipboard = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jPanelSoldDebitCredit = new javax.swing.JPanel();
        soldePanel = new fr.antsfiles.pdftotable.view.SoldePanel();
        debitCreditPanel = new fr.antsfiles.pdftotable.view.DebitCreditPanel();
        outPanel1 = new fr.antsfiles.pdftotable.view.OutPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        filePanel1 = new fr.antsfiles.pdftotable.view.FilePanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxType = new javax.swing.JComboBox<>(ParserLine.FORMAT.values());
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        layoutPanel = new fr.antsfiles.pdftotable.view.LayoutPanel();
        jLabelStatus = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanelHeader.setLayout(new java.awt.GridLayout(1, 0));

        jButtonClipboard.setText("from clipboard");
        jButtonClipboard.setPreferredSize(new java.awt.Dimension(200, 30));
        jButtonClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClipboardActionPerformed(evt);
            }
        });
        jPanelHeader.add(jButtonClipboard);
        jPanelHeader.add(jSeparator1);

        jTextFieldYear.setText("2023");
        jPanelHeader.add(jTextFieldYear);
        jPanelHeader.add(jSeparator2);

        jButtonToClipboard.setText("copy to clipboard");
        jButtonToClipboard.setPreferredSize(new java.awt.Dimension(200, 30));
        jButtonToClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonToClipboardActionPerformed(evt);
            }
        });
        jPanelHeader.add(jButtonToClipboard);

        add(jPanelHeader, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerSize(10);
        jSplitPane1.setResizeWeight(0.75);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(800, 191));

        jPanel4.setPreferredSize(new java.awt.Dimension(700, 100));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanelSoldDebitCredit.setMinimumSize(new java.awt.Dimension(25, 25));
        jPanelSoldDebitCredit.setLayout(new java.awt.GridLayout(1, 0));
        jPanelSoldDebitCredit.add(soldePanel);
        jPanelSoldDebitCredit.add(debitCreditPanel);

        jPanel4.add(jPanelSoldDebitCredit, java.awt.BorderLayout.NORTH);
        jPanel4.add(outPanel1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel4);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.GridLayout(2, 2));
        jPanel6.add(filePanel1);

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(3, 1));

        jLabel1.setText("Format:");
        jPanel1.add(jLabel1);

        jComboBoxType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTypeActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxType);

        jPanel5.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel5, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel7.setMaximumSize(null);

        jButton1.setText(">>>");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton1);

        jPanel3.add(jPanel7, java.awt.BorderLayout.EAST);
        jPanel3.add(layoutPanel, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jLabelStatus.setText("jLabelStatus");
        add(jLabelStatus, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String txt = layoutPanel.getInputText();
        String txtOut = mainControler.getFormattedFromRawText(txt, (ParserLine.FORMAT) jComboBoxType.getSelectedItem());
        outPanel1.updateOutTxt(txtOut);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClipboardActionPerformed
        try {
            String t = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            layoutPanel.setInputText(mainControler.getRawFromText(t));
            jButton1ActionPerformed(null);

        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonClipboardActionPerformed

    private void jButtonToClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonToClipboardActionPerformed

        String myString = outPanel1.getOutTxt();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

    }//GEN-LAST:event_jButtonToClipboardActionPerformed

    private void jComboBoxTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTypeActionPerformed
        // update vm
        if (mainControler.format().getValue() != jComboBoxType.getSelectedItem()) {
            mainControler.format().onNext((ParserLine.FORMAT) jComboBoxType.getSelectedItem());
        }
    }//GEN-LAST:event_jComboBoxTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.antsfiles.pdftotable.view.DebitCreditPanel debitCreditPanel;
    private fr.antsfiles.pdftotable.view.FilePanel filePanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonClipboard;
    private javax.swing.JButton jButtonToClipboard;
    private javax.swing.JComboBox<ParserLine.FORMAT> jComboBoxType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelSoldDebitCredit;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTextFieldYear;
    private fr.antsfiles.pdftotable.view.LayoutPanel layoutPanel;
    private fr.antsfiles.pdftotable.view.OutPanel outPanel1;
    private fr.antsfiles.pdftotable.view.SoldePanel soldePanel;
    // End of variables declaration//GEN-END:variables
}
