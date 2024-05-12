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
public class ParserCic extends AbstractParser {

    private final TableHeader tableHeader;

    public ParserCic() {
        List<TableHeaderName> names = List.of(new TableHeaderName("Date", ColumnType.DATE),
                new TableHeaderName(List.of("Référence"), ColumnType.DESCRIPTION),
                new TableHeaderName("Débit", ColumnType.DEBIT),
                new TableHeaderName("Crédit", ColumnType.CREDIT),
                new TableHeaderName("Valeur", ColumnType.UNUSED));
        tableHeader = new TableHeader(names);
        tableHeader.setSpace("   ");
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.CIC;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("cic.fr");
    }

    /**
     * Example of text:
     * <pre>
     * 02/05/2023 02/05/2023 VII 1.692,00
     * XX
     * 04/05/2023 02/05/2023 XX 1.692,00
     * XX
     * 05/05/2023 05/05/2023 XX B 12,50
     * 10/05/2023 01/05/2023 FAC.CIC XX 36,31
     * DONT TVA 1,10EUR
     * </pre>
     *
     * @param line
     */
    @Override
    public void readLine(String line) {
        String parenthese = "";
        int pStart = line.indexOf('(');
        int pEnd = line.indexOf(')');
        if (pStart > 0 && pEnd > pStart) {
            parenthese = line.substring(pStart, pEnd + 1);
            line = line.replace(parenthese, "");
        }

        Pattern pattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
        Matcher matcher = pattern.matcher(line);
        String dateFound = null;
        while (matcher.find()) {
            String date = matcher.group();
            if (dateFound == null) {
                dateFound = date;
            }
            line = dateFound + " " + matcher.replaceAll("_");
        }
        pStart = line.indexOf('_');
        pEnd = line.lastIndexOf('_');
        if (pStart > 0 && pEnd > pStart) {
            parenthese = line.substring(pStart, pEnd + 1);
            parenthese = parenthese.replaceAll(" ", "_");
            line = dateFound + " " + parenthese + " " + line.substring(pEnd + 1);
        }

        String regex = "(\\d{2}/\\d{2}/\\d{4})\\s(.*?)\\s(\\d{1,3}(?:.\\d{3})*(,\\d{2}))";

        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            String date1 = matcher.group(1);
            String desc = matcher.group(2).replaceAll("_", " ").trim();
            String montant = matcher.group(3).replaceAll("\\.", "").replace(",", ".");

            System.out.println("Date 1: " + date1);
            System.out.println("Montant: " + montant);
            lastOperation = new Operation();

            lastOperation.setCreditamount(Double.valueOf(montant));
            lastOperation.setDate(date1);
            lastOperation.setDescription(desc);

            String fdesc = desc;
            List<String> containsDeb = List.of("CB**", "DU ", "RETRAIT DAB", "COTIS.FORFAIT", "PRLV ", "VIR SEPA");
            if (desc.startsWith("CB ") || containsDeb.stream().anyMatch(s -> fdesc.contains(s))) {
                lastOperation.setAmount(lastOperation.getCreditamount());
                lastOperation.setCreditamount(Double.NaN);
            }

            operations.add(lastOperation);
        }
    }

}
