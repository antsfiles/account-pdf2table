/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.model;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class TableHeader {

    private String space = "  ";
    private String valueCentsSeperator = ",";
    private List<TableHeaderName> headerNames;

    public TableHeader() {
    }

    public TableHeader(List<TableHeaderName> headerNames) {
        this.headerNames = headerNames;
    }

    public void setHeaderNames(List<TableHeaderName> headerNames) {
        this.headerNames = headerNames;
    }

    public List<TableHeaderName> getHeaderNames() {
        return headerNames;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getValueCentsSeperator() {
        return valueCentsSeperator;
    }

    public void setValueCentsSeperator(String valueCentsSeperator) {
        this.valueCentsSeperator = valueCentsSeperator;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.space);
        hash = 89 * hash + Objects.hashCode(this.valueCentsSeperator);
        hash = 89 * hash + Objects.hashCode(this.headerNames);
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
        final TableHeader other = (TableHeader) obj;
        if (!Objects.equals(this.space, other.space)) {
            return false;
        }
        if (!Objects.equals(this.valueCentsSeperator, other.valueCentsSeperator)) {
            return false;
        }
        return Objects.equals(this.headerNames, other.headerNames);
    }

}
