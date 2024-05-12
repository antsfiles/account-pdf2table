/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.model;

import java.util.List;
import java.util.Objects;

/**
 */
public class TableHeaderName {

    private ColumnType columnType;
    private List<String> names;
    private int colMin = 0;
    private int colMax = 0;
    private int colMinLimit = 0;
    private int colMaxLimit = 0;
    private String value;

    public TableHeaderName() {
    }

    public TableHeaderName(List<String> names, ColumnType columnType) {
        this.names = names;
        this.columnType = columnType;
    }

    public TableHeaderName(String name, ColumnType columnType) {
        this.names = List.of(name);
        this.columnType = columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getColMin() {
        return colMin;
    }

    public void setColMin(int colMin) {
        this.colMin = colMin;
    }

    public int getColMax() {
        return colMax;
    }

    public void setColMax(int colMax) {
        this.colMax = colMax;
    }

    public int getColMinLimit() {
        return colMinLimit;
    }

    public void setColMinLimit(int colMinLimit) {
        this.colMinLimit = colMinLimit;
    }

    public int getColMaxLimit() {
        return colMaxLimit;
    }

    public void setColMaxLimit(int colMaxLimit) {
        this.colMaxLimit = colMaxLimit;
    }

    public TableHeaderName withCols(int min, int max) {
        this.colMinLimit = min;
        this.colMaxLimit = max;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.columnType);
        hash = 37 * hash + Objects.hashCode(this.names);
        hash = 37 * hash + this.colMin;
        hash = 37 * hash + this.colMax;
        hash = 37 * hash + this.colMinLimit;
        hash = 37 * hash + this.colMaxLimit;
        hash = 37 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableHeaderName other = (TableHeaderName) obj;
        if (this.colMin != other.colMin) {
            return false;
        }
        if (this.colMax != other.colMax) {
            return false;
        }
        if (this.colMinLimit != other.colMinLimit) {
            return false;
        }
        if (this.colMaxLimit != other.colMaxLimit) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (this.columnType != other.columnType) {
            return false;
        }
        return Objects.equals(this.names, other.names);
    }

}
