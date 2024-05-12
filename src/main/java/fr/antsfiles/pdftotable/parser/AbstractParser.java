/*
 *  Copyright AD
 *
 */
package fr.antsfiles.pdftotable.parser;

import fr.antsfiles.pdftotable.model.Operation;
import fr.antsfiles.pdftotable.model.TableHeader;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public abstract class AbstractParser {

    public String year = "2023";

    protected List<Operation> operations = new ArrayList<>();
    protected Operation lastOperation;
    protected int lineOperation = 0;

    public AbstractParser() {
    }

    public abstract ParserLine.FORMAT getFormat();

    public abstract boolean isMine(String pageLines);

    public abstract void readLine(String line);

    public abstract TableHeader getTableHeader();

    public void reset() {
        lineOperation = 0;
        lastOperation = null;
        getOperations().clear();
    }

    protected double readAmount(String amountString, char decimalSeparator, char groupingSeparator) {
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(decimalSeparator);
        symbols.setGroupingSeparator(groupingSeparator);
        df.setDecimalFormatSymbols(symbols);
        Number number;
        try {
            number = df.parse(amountString);
            return number.doubleValue();
        } catch (ParseException ex) {
            Logger.getLogger(ParserLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0.;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public boolean isDone() {
        return false;
    }

}
