package fr.antsfiles.pdftotable;

import fr.antsfiles.pdftotable.model.Operation;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 */
public class JTableOperationModel extends AbstractTableModel {

    final List<Operation> operations;

    public JTableOperationModel(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0 -> {
                return "date";
            }
            case 1 -> {
                return "description";
            }
            case 2 -> {
                return "debit";
            }
            case 3 -> {
                return "credit";
            }
            default -> {
            }
        }
        return "";
    }

    @Override
    public int getRowCount() {
        return operations.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0 -> {
                return operations.get(rowIndex).getDate();
            }
            case 1 -> {
                return operations.get(rowIndex).getDescription();
            }
            case 2 -> {
                double v = operations.get(rowIndex).getAmount();
                return Double.isNaN(v) ? "": v;
            }
            case 3 -> {
                double v = operations.get(rowIndex).getCreditamount();
                return Double.isNaN(v) ? "": v;
            }
            default -> {
            }
        }

        return "";
    }

}
