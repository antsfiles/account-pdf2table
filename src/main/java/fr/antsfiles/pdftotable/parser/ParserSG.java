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
public class ParserSG extends AbstractParser {

    private final TableHeader tableHeader;

    public ParserSG() {
        List<TableHeaderName> names = List.of(new TableHeaderName("Date", ColumnType.DATE).withCols(5, 19),
                new TableHeaderName("Valeur", ColumnType.UNUSED).withCols(21, 31),
                new TableHeaderName(List.of("Nature de l'opération"), ColumnType.DESCRIPTION).withCols(31, 100),
                new TableHeaderName("Débit", ColumnType.DEBIT).withCols(107, 123),
                new TableHeaderName("Crédit", ColumnType.CREDIT).withCols(125, 143));
        tableHeader = new TableHeader(names);
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.SG;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("société-générale") || pageLines.contains("societegenerale");
    }

    /**
     * Example of text:
     * <pre>
     * 04/01/2022 31/12/2021 CIONS TENUE DE COMPTE
     * AU 31/12/21 :
     * MIN PERCEPTION
     * -CION MOUVEMENT EUR 24,00
     * -FRAIS FIXES EUR 77,10
     * 101,10 *
     * 19/01/2022 18/01/2022 CHEQUE XX 500,00
     * 20/01/2022 20/01/2022 PRLV XX B2B 1XX45
     * POUR CPTE DX
     * ID: XX
     * MOTIF: XXX
     * REF: XX
     * MANDAT ++XX
     * 445,00
     * </pre>
     *
     * @param line
     */
    @Override
    public void readLine(String line) {
        //
        String onlineRegex = "\\b(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}/\\d{2}/\\d{4}).\\s*(.+?)\\s*((?:\\d{1,3}(?:[\\.]\\d{3})*|\\d+)\\,\\d{2})\\b";
        String multilineRegex = "^(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}/\\d{2}/\\d{4})(.*)";
        String multilineRegexAmount = "\\b(\\d+,\\d+( +))?\\*";
        boolean online = true;

        Matcher matcher = Pattern.compile(onlineRegex).matcher(line);
        boolean found = matcher.find();
        if (!found) {
            matcher = Pattern.compile(multilineRegex).matcher(line);
            found = matcher.find();
            online = false;
        }
        if (found) {
            lineOperation = 0;
            lastOperation = new Operation();
            System.out.println("date1: " + matcher.group(1));
            System.out.println("date2: " + matcher.group(2));

            lastOperation.setDate(matcher.group(1).replace('.', '/'));
            if (online) {
                System.out.println("desc: " + matcher.group(3));
                lastOperation.setDescription(matcher.group(3));
                System.out.println("Montant: " + matcher.group(4));
                lastOperation.setAmount(readAmount(matcher.group(4), ',', ' '));
            } else {
                lastOperation.setDescription(matcher.group(3));
            }
            operations.add(lastOperation);
            if (online) {
                lastOperation = null;
            }
        } else {
            lineOperation++;
            matcher = Pattern.compile(multilineRegexAmount).matcher(line);
            if (lastOperation != null && matcher.find()) {
                System.out.println("val: " + matcher.group(1));
                lastOperation.setAmount(readAmount(matcher.group(1), ',', '.'));
            } else if (lastOperation != null && lineOperation < 3) {
                lastOperation.setDescription(lastOperation.getDescription() + line);
            }
        }
    }

}
