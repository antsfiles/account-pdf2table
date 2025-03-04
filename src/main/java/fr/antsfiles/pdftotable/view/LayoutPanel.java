/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.view;

import fr.antsfiles.pdftotable.MainControler;
import fr.antsfiles.pdftotable.model.ColumnType;
import fr.antsfiles.pdftotable.model.TableHeader;
import fr.antsfiles.pdftotable.model.TableHeaderName;
import fr.antsfiles.pdftotable.read.SettingsPersistance;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 *
 */
public class LayoutPanel extends javax.swing.JPanel {

    private MainControler mainControler;
    private boolean loading = false;

    TableHeader tableHeaderManual;
    TableHeaderName tableHeaderNameDate;
    TableHeaderName tableHeaderNameDesc;
    TableHeaderName tableHeaderNameDebit;
    TableHeaderName tableHeaderNameCredit;

    Map<ColumnType, Color> colorsForType = Map.of(ColumnType.DATE, Color.CYAN,
            ColumnType.DESCRIPTION, Color.LIGHT_GRAY,
            ColumnType.DEBIT, Color.ORANGE,
            ColumnType.CREDIT, Color.GREEN);

    Map<String, Highlighter> highlighters = new HashMap<>();

    SettingsPersistance persistance = new SettingsPersistance();

    public class ItemAbstractAction extends AbstractAction {

        TableHeaderName headerName;
        String name;

        public ItemAbstractAction(TableHeaderName headerName) {
            super(headerName.getColumnType().name());
            this.headerName = headerName;
            name = headerName.getColumnType().name();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int cmin = jTextAreaInput.getSelectionStart();
            int cmax = jTextAreaInput.getSelectionEnd();
            String txt = jTextAreaInput.getText().substring(0, cmin);
            int lastR = txt.lastIndexOf("\n");
            int col = cmin - lastR;
            int colMax = cmax - cmin + col;
            String txtExtract = jTextAreaInput.getText().substring(cmin, cmax).trim();

            //jLabelStatus.setText(name + ": " + col + " - " + colMax + " " + txtExtract);
            mainControler.status().onNext(name + ": " + col + " - " + colMax + " " + txtExtract);

            headerName.setColMinLimit(col);
            headerName.setColMaxLimit(colMax);
            headerName.setNames(List.of(txtExtract));
            updateManualSettingsLabel();

            try {
                jTextAreaInput.getHighlighter().addHighlight(cmin, cmax,
                        new DefaultHighlighter.DefaultHighlightPainter(colorsForType.get(headerName.getColumnType())));
            } catch (BadLocationException ex) {
                Logger.getLogger(LayoutPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Creates new form LayoutPanel
     */
    public LayoutPanel() {
        initComponents();

        tableHeaderNameDate = new TableHeaderName("DATE", ColumnType.DATE);
        tableHeaderNameDesc = new TableHeaderName("NATURE", ColumnType.DESCRIPTION);
        tableHeaderNameDebit = new TableHeaderName("DEBIT", ColumnType.DEBIT);
        tableHeaderNameCredit = new TableHeaderName("CREDIT", ColumnType.CREDIT);

        List<TableHeaderName> names = List.of(tableHeaderNameDate,
                tableHeaderNameDesc,
                tableHeaderNameDebit,
                tableHeaderNameCredit);
        tableHeaderManual = new TableHeader(names);

        jTextFieldBic.addCaretListener((CaretEvent e) -> {
            if (!loading) {
                tableHeaderManual.setBicOrIdentifier(jTextFieldBic.getText());
            }
        });
        jTextFieldAmoutFormat.addCaretListener((CaretEvent e) -> {
            if (!loading) {
                tableHeaderManual.setAmoutFormat(jTextFieldAmoutFormat.getText());
            }
        });
        jTextFieldDateFormat.addCaretListener((CaretEvent e) -> {
            if (!loading) {
                tableHeaderManual.setDateFormat(jTextFieldDateFormat.getText());
            }
        });

        updateManualSettingsLabel();

        jTextAreaInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int cmin = jTextAreaInput.getSelectionStart();
                    int cmax = jTextAreaInput.getSelectionEnd();
                    String txt = jTextAreaInput.getText().substring(0, cmin);
                    int lastR = txt.lastIndexOf("\n");
                    int col = cmin - lastR;
                    int colMax = cmax - cmin + col;

                    System.out.println("col min = " + col);
                    System.out.println("col max = " + colMax);

                    mainControler.status().onNext("date: " + col + " - " + colMax);

                    JPopupMenu jPopupMenu = new JPopupMenu();
                    jPopupMenu.add(new JMenuItem(new ItemAbstractAction(tableHeaderNameDate)));
                    jPopupMenu.add(new JMenuItem(new ItemAbstractAction(tableHeaderNameDesc)));
                    jPopupMenu.add(new JMenuItem(new ItemAbstractAction(tableHeaderNameDebit)));
                    jPopupMenu.add(new JMenuItem(new ItemAbstractAction(tableHeaderNameCredit)));
                    jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });

        jComboBoxPredefineLayout.setModel(new DefaultComboBoxModel<>(persistance.getAll().toArray(new String[0])));
        updateSelection();
    }

    private void updateSelection() {
        if (jComboBoxPredefineLayout.getSelectedItem() == null) {
            return;
        }
        loading = true;
        String name = jComboBoxPredefineLayout.getSelectedItem().toString();
        TableHeader th = persistance.load(name);
        if (th != null) {
            tableHeaderManual = th;
            jTextFieldLayoutName.setText(name);
            tableHeaderNameDate = th.getHeaderNames().stream().filter(h -> h.getColumnType() == ColumnType.DATE).findFirst().get();
            tableHeaderNameDebit = th.getHeaderNames().stream().filter(h -> h.getColumnType() == ColumnType.DEBIT).findFirst().get();
            tableHeaderNameCredit = th.getHeaderNames().stream().filter(h -> h.getColumnType() == ColumnType.CREDIT).findFirst().get();
            tableHeaderNameDesc = th.getHeaderNames().stream().filter(h -> h.getColumnType() == ColumnType.DESCRIPTION).findFirst().get();
        }
        updateManualSettingsLabel();

        loading = false;
    }

    private void updateManualSettingsLabel() {

        jTextFieldBic.setText(tableHeaderManual.getBicOrIdentifier() != null ? tableHeaderManual.getBicOrIdentifier() : "");
        jTextFieldDateFormat.setText(tableHeaderManual.getDateFormat() != null ? tableHeaderManual.getDateFormat() : "");
        jTextFieldAmoutFormat.setText(tableHeaderManual.getAmoutFormat() != null ? tableHeaderManual.getAmoutFormat() : "");

        String txt = tableHeaderManual.getHeaderNames().stream().
                map(h
                        -> h.getColumnType() + ": " + h.getNames().get(0)
                + " " + h.getColMinLimit() + "-" + h.getColMaxLimit()
                ).collect(Collectors.joining(" "));
        jLabelSettings.setText(txt);

        String maxLine = Arrays.stream(jTextAreaInput.getText().split("\n")).max((s1, s2) -> Integer.compare(s1.length(), s2.length())).get();
        int offset = 0;
        for (String l : jTextAreaInput.getText().split("\n")) {
            if (l.length() == maxLine.length()) {
                break;
            }
            offset = offset + l.length() + 1;
        }

        System.out.println("offset = " + offset);
        System.out.println("maxLine = " + maxLine);

        jTextAreaInput.getHighlighter().removeAllHighlights();
        int foffset = offset;
        tableHeaderManual.getHeaderNames().forEach(h -> {

            try {
                jTextAreaInput.getHighlighter().addHighlight(foffset + h.getColMinLimit(), foffset + h.getColMaxLimit(),
                        new DefaultHighlighter.DefaultHighlightPainter(colorsForType.get(h.getColumnType())));
            } catch (BadLocationException ex) {
                Logger.getLogger(LayoutPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public void setMainControler(MainControler mainControler) {
        this.mainControler = mainControler;

        mainControler.inText().subscribe(t -> {
            jTextAreaInput.setText(t);
            jTextAreaInput.setCaretPosition(0);
        });
    }

    public String getInputText() {
        return jTextAreaInput.getText();
    }

    public void setInputText(String txt) {
        jTextAreaInput.setText(txt);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaInput = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxPredefineLayout = new javax.swing.JComboBox<>();
        jTextFieldLayoutName = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabelManualSettings = new javax.swing.JLabel();
        jButtonReadLayoutManual = new javax.swing.JButton();
        jPanelParam = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldBic = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDateFormat = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldAmoutFormat = new javax.swing.JTextField();
        jLabelSettings = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jTextAreaInput.setColumns(20);
        jTextAreaInput.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTextAreaInput.setRows(5);
        jScrollPane1.setViewportView(jTextAreaInput);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Layouts predefined:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(jLabel1, gridBagConstraints);

        jComboBoxPredefineLayout.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxPredefineLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPredefineLayoutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jComboBoxPredefineLayout, gridBagConstraints);

        jTextFieldLayoutName.setText("LayoutX");
        jTextFieldLayoutName.setMaximumSize(new java.awt.Dimension(500, 2147483647));
        jTextFieldLayoutName.setMinimumSize(new java.awt.Dimension(100, 25));
        jTextFieldLayoutName.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(jTextFieldLayoutName, gridBagConstraints);

        jButton1.setText("save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 7);
        jPanel2.add(jButton1, gridBagConstraints);

        jLabel2.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabelManualSettings.setText("<html>manual settings:<br>1: find the table header line<br>2: highlight the column head with long left click as large as the column is, from left to right<br>3:rigth click then select what is the column, then click the button \"read layout manual\"");
        jLabelManualSettings.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabelManualSettings.setMaximumSize(new java.awt.Dimension(500, 68));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.4;
        jPanel2.add(jLabelManualSettings, gridBagConstraints);

        jButtonReadLayoutManual.setText("read layout manual");
        jButtonReadLayoutManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReadLayoutManualActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jButtonReadLayoutManual, gridBagConstraints);

        jPanelParam.setPreferredSize(new java.awt.Dimension(250, 69));
        jPanelParam.setLayout(new java.awt.GridLayout(3, 2));

        jLabel3.setText("Bic Or Id");
        jPanelParam.add(jLabel3);

        jTextFieldBic.setText("bic");
        jPanelParam.add(jTextFieldBic);

        jLabel4.setText("DateFormat");
        jPanelParam.add(jLabel4);

        jTextFieldDateFormat.setText("dd/mm");
        jPanelParam.add(jTextFieldDateFormat);

        jLabel5.setText("AmountFormat");
        jPanelParam.add(jLabel5);

        jTextFieldAmoutFormat.setText("-##.##");
        jPanelParam.add(jTextFieldAmoutFormat);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jPanelParam, gridBagConstraints);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        jLabelSettings.setText("---");
        jPanel1.add(jLabelSettings, java.awt.BorderLayout.SOUTH);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReadLayoutManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReadLayoutManualActionPerformed
        //mainControler.readFileWithLayout(new File(jLabelFilePath.getText()), tableHeaderManual);

        mainControler.setTableHeaderSetting(tableHeaderManual);
        mainControler.readFileWithLayout(mainControler.getFile());

    }//GEN-LAST:event_jButtonReadLayoutManualActionPerformed

    private void jComboBoxPredefineLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPredefineLayoutActionPerformed

        updateSelection();

    }//GEN-LAST:event_jComboBoxPredefineLayoutActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        List<String> settings = persistance.getAll();
        String newName = jTextFieldLayoutName.getText();
        boolean exists = settings.contains(newName);
        if (exists) {
            // error?
            int input = JOptionPane.showConfirmDialog((Component) evt.getSource(), "Already exist. Overwrite?");
            if (input == JOptionPane.YES_OPTION) {
                exists = false;
            }
        }
        if (!exists) {
            persistance.save(newName, tableHeaderManual);
            jComboBoxPredefineLayout.setModel(new DefaultComboBoxModel<>(persistance.getAll().toArray(new String[0])));
            jComboBoxPredefineLayout.setSelectedItem(newName);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonReadLayoutManual;
    private javax.swing.JComboBox<String> jComboBoxPredefineLayout;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelManualSettings;
    private javax.swing.JLabel jLabelSettings;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelParam;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaInput;
    private javax.swing.JTextField jTextFieldAmoutFormat;
    private javax.swing.JTextField jTextFieldBic;
    private javax.swing.JTextField jTextFieldDateFormat;
    private javax.swing.JTextField jTextFieldLayoutName;
    // End of variables declaration//GEN-END:variables
}
