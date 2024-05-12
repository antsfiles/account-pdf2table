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
public class ParserCreditNord extends AbstractParser {

    private final TableHeader tableHeader;

    public ParserCreditNord() {
        List<TableHeaderName> names = List.of(new TableHeaderName("Date", ColumnType.DATE),
                new TableHeaderName(List.of("Référence"), ColumnType.DESCRIPTION),
                new TableHeaderName("Débit", ColumnType.DEBIT),
                new TableHeaderName("Crédit", ColumnType.CREDIT),
                new TableHeaderName("Valeur", ColumnType.UNUSED));
        tableHeader = new TableHeader(names);
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.CREDIT_NORD;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("credit-du-nord");
    }

    /**
     * Example of text:
     * <pre>
     * 2.05 CARTE 5295 XX 75PARIS 17 LE 2904 13,40 2.05
     * 2.05 E-VIR SEPA Retro avril 2 700,00 2.05
     * VIREMENT (CAS D'UN VIREMENT SEPA
     * OCCASIONNEL
     * 5.05 PRLV SEPA XX ILE DE FRANCE 1 168,00 5.05
     * XX
     * 6.05 VIR CABINET XX 6 000,00 6.05
     * REF :retro avril 2022
     * retro avril 2022
     * </pre>
     *
     * @param line
     */
    @Override
    public void readLine(String line) {
        //
        String regexBred = "\\b(\\d{1}\\.\\d{2})\\s*(.+?)\\s*((?:\\d{1,3}(?:[\\ ]\\d{3})*|\\d+)\\,\\d{2})\\b";
        String dateprefix = "0";
        String regexBred2 = "\\b(\\d{2}\\.\\d{2})\\s*(.+?)\\s*((?:\\d{1,3}(?:[\\ ]\\d{3})*|\\d+)\\,\\d{2})\\b";

        //'LE XXXX ' à supprimer, sinon, problème
        line = line.replaceAll("LE \\d{4}", "  ");

        Matcher matcher = Pattern.compile(regexBred).matcher(line);
        boolean found = matcher.find();
        if (!found) {
            matcher = Pattern.compile(regexBred2).matcher(line);
            found = matcher.find();
            dateprefix = "";
        }
        if (found) {
            lineOperation = 0;
            lastOperation = new Operation();
            System.out.println("Chaine: " + matcher.group(1));
            System.out.println("Description: " + matcher.group(2));
            System.out.println("Montant: " + matcher.group(3));
            lastOperation.setDate(dateprefix + matcher.group(1).replace('.', '/') + "/" + year);
            lastOperation.setDescription(matcher.group(2));
            lastOperation.setAmount(readAmount(matcher.group(3), ',', ' '));
            operations.add(lastOperation);
        } else {
            lineOperation++;
            if (lastOperation != null && lineOperation < 3) {
                lastOperation.setDescription(lastOperation.getDescription() + line);
            }
        }
    }

}
