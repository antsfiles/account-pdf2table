/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.view;

import fr.antsfiles.pdftotable.JTableOperationModel;
import fr.antsfiles.pdftotable.MainControler;
import fr.antsfiles.pdftotable.model.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.ListSelectionModel;

/**
 *
 */
public class OutPanel extends javax.swing.JPanel {

    MainControler mainControler;
    List<Operation> operations;
    Consumer<List<Operation>> onUpdate;

    /**
     * Creates new form OutPanel
     */
    public OutPanel() {
        initComponents();
        jTableOut.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    }

    public void setMainControler(MainControler mainControler) {
        this.mainControler = mainControler;

        mainControler.outText().subscribe(jTextAreaOut::setText);
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void setOnUpdate(Consumer<List<Operation>> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void updateTable() {
        jTableOut.setModel(new JTableOperationModel(operations));
        jTableOut.revalidate();

    }

    public void updateOutTxt(String txt) {
        jTextAreaOut.setText(txt);
    }

    public String getOutTxt() {
        return jTextAreaOut.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTableOut = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaOut = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jButtonSelected = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jTableOut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "date", "description", "debit", "credit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableOut);

        add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jTextAreaOut.setColumns(20);
        jTextAreaOut.setRows(5);
        jScrollPane2.setViewportView(jTextAreaOut);

        add(jScrollPane2, java.awt.BorderLayout.SOUTH);

        jButtonSelected.setText("del. selected");
        jButtonSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectedActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonSelected);

        add(jPanel1, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectedActionPerformed
        int[] indexes = jTableOut.getSelectionModel().getSelectedIndices();
        List<Operation> toRemove = new ArrayList<>();
        for (int i : indexes) {
            toRemove.add(operations.get(i));
        }
        operations.removeAll(toRemove);
        if (onUpdate != null) {
            onUpdate.accept(operations);
        }
        updateTable();

        mainControler.updateOutTxt(operations);
    }//GEN-LAST:event_jButtonSelectedActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSelected;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableOut;
    private javax.swing.JTextArea jTextAreaOut;
    // End of variables declaration//GEN-END:variables
}
