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
public class ParserMillies extends AbstractParser {

    List<TableHeaderName> names = List.of(new TableHeaderName("DATE", ColumnType.DATE),
            new TableHeaderName(List.of("NATURE", "DE", "L’OPERATION"), ColumnType.DESCRIPTION),
            new TableHeaderName("VALEUR", ColumnType.UNUSED),
            new TableHeaderName("DEBIT", ColumnType.DEBIT),
            new TableHeaderName("CREDIT", ColumnType.CREDIT));
    TableHeader tableHeader = new TableHeader(names);

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.MILLEIS;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("BIC : PRIVFRPP");
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * <pre>
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
        // remove 3e col duplciate date
        String regex = "(\\d{2}.\\d{2})(\\s.*?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        boolean found = false;
        if (matcher.find()) {
            String date1 = matcher.group(1);
            pEnd = line.indexOf(" " + date1);
            String fact = "";
            if (pEnd > 0) {
                fact = line.substring(6, pEnd);
                fact = fact.replaceAll(" ", "_");
                String part1 = line.substring(0, 6);
                String part2 = line.substring(pEnd + 6);
                line = part1 + fact + part2;
            }
        }
        regex = "(\\d{2}\\.\\d{2})\\s(.*?)\\s(\\d{1,3}(?:\\s\\d{3})*(?:,\\d{2})?)";

        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(line);
        found = false;
        if (matcher.find()) {
            String date1 = matcher.group(1).replaceAll("\\.", "/") + "/" + year;
            String desc = matcher.group(2).replaceAll("_", " ").trim() + parenthese;
            String montant = matcher.group(3).replaceAll("\\s", "").replace(",", ".");

            System.out.println("Date 1: " + date1);
            System.out.println("Montant: " + montant);
            found = true;
            lastOperation = new Operation();
            lastOperation.setCreditamount(Double.valueOf(montant));
            lastOperation.setDate(date1);
            lastOperation.setDescription(desc);

            String fdesc = desc;
            List<String> containsDeb = List.of("DU ", "RETRAIT DAB", "COTIS.FORFAIT", "PRLV ", "VIR SEPA");
            if (desc.startsWith("CB ") || containsDeb.stream().anyMatch(s -> fdesc.contains(s))) {
                lastOperation.setAmount(lastOperation.getCreditamount());
                lastOperation.setCreditamount(Double.NaN);
            }

            operations.add(lastOperation);
        }
    }

}
