/*
 *  Copyright AD
 *
 */
package fr.antsfiles.pdftotable.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author tonio
 */
public class Operation {

    private String date;
    private String description;
    private double amount;
    private double creditamount = Double.NaN;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCreditamount() {
        return creditamount;
    }

    public void setCreditamount(double creditamount) {
        this.creditamount = creditamount;
    }


    @Override
    public String toString() {

        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df.setGroupingUsed(false);
        df.setMinimumFractionDigits(2);

        if (!Double.isNaN(getCreditamount())) {
            return getDate().strip() + "\t" + getDescription().strip() + "\t\t" + df.format(getCreditamount());
        }
        return getDate().strip() + "\t" + getDescription().strip() + "\t" + df.format(getAmount());
    }

    public String toPrettyString() {
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df.setGroupingUsed(false);
        df.setMinimumFractionDigits(2);
        return getDate().strip() + "\t" + getDescription().strip() + "\t" + df.format(getAmount()) + "\t" + df.format(getCreditamount());
    }

}
