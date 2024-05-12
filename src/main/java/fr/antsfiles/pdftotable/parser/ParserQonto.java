/*
 *  Copyright AD
 *
 */
package fr.antsfiles.pdftotable.parser;

import fr.antsfiles.pdftotable.model.ColumnType;
import fr.antsfiles.pdftotable.model.Operation;
import fr.antsfiles.pdftotable.model.TableHeader;
import fr.antsfiles.pdftotable.model.TableHeaderName;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ParserQonto extends AbstractParser {

    private final TableHeader tableHeader;

    public ParserQonto() {
        List<TableHeaderName> names = List.of(new TableHeaderName(List.of("Date", "de", "valeur"), ColumnType.DATE)
                .withCols(19, 30),
                new TableHeaderName(List.of("Transactions"), ColumnType.DESCRIPTION).withCols(42, 90),
                new TableHeaderName("Débit", ColumnType.DEBIT).withCols(95, 115),
                new TableHeaderName("Crédit", ColumnType.CREDIT).withCols(116, 136));
        tableHeader = new TableHeader(names);
        tableHeader.setSpace("  ");
        tableHeader.setValueCentsSeperator(".");
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.QONTO;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("QNTOFR");
    }

    /**
     * Example of text:
     * <pre>
     * 01/01 Qonto
     * Abonnement / Frais additionnels
     * - 10,80 €
     * 01/01 WAXX 6160
     * Carte *XX
     * - 4,12 €
     * 04/01 XX
     * apport Dh compte joint frais janvier 2021
     * - 150,00 €
     * 04/01 XX
     * prestation 122021
     * + 1 200,00 €
     * 04/01 XX
     * Carte **1892
     * - 5,20 €
     * 07/01 BISTROT XX
     * Carte **XX
     * - 37,80 €
     * </pre>
     *
     * @param line
     */
    @Override
    public void readLine(String line) {
        String firstLineRegex = "^(\\d{2}/\\d{2})(.*)";
        String lastLine = "\\b(?:[+-/ ])((?:\\d{1,3}(?:[\\ ]\\d{3})*|\\d+)\\,\\d{2})";
        lastLine = "[+-]?\\s*(\\d{1,3}\\s\\d{3}*,\\d{2})?\\s€";
        boolean online = true;

        Matcher matcher = Pattern.compile(firstLineRegex).matcher(line);
        boolean found = matcher.find();
        if (found) {
            lineOperation = 0;
            lastOperation = new Operation();
            System.out.println("date: " + matcher.group(1));
            System.out.println("desc: " + matcher.group(2));

            lastOperation.setDate(matcher.group(1).replace('.', '/') + "/" + year);
            lastOperation.setDescription(matcher.group(2));

            operations.add(lastOperation);
        } else {
            lineOperation++;

            if (lastOperation != null && (line.startsWith("-") || line.startsWith("+"))
                    && (line.contains("€") || line.contains("EUR"))) {
                String v = line.replaceAll(" ", "");
                v = v.replaceAll("\\+", "");
                v = v.replaceAll("\\-", "");

                char sep = line.contains("EUR") ? '.' : ',';

                v = v.replaceAll("€", "");
                v = v.replaceAll("EUR", "");

                double val = readAmount(v, sep, ' ');
                if (!line.startsWith("-")) {
                    lastOperation.setCreditamount(val);
                } else {
                    lastOperation.setAmount(val);
                }
            } else if (lastOperation != null && lineOperation < 3) {
                lastOperation.setDescription(lastOperation.getDescription() + line);
            }
        }
    }
}
