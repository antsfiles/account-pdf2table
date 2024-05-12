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
public class ParserBNP extends AbstractParser {

    List<TableHeaderName> names = List.of(new TableHeaderName("Date", ColumnType.DATE),
            new TableHeaderName(List.of("Nature", "des", "opérations"), ColumnType.DESCRIPTION),
            new TableHeaderName("Valeur", ColumnType.UNUSED),
            new TableHeaderName("Débit", ColumnType.DEBIT),
            new TableHeaderName("Crédit", ColumnType.CREDIT));
    TableHeader tableHeader = new TableHeader(names);

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.BNP;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("BNPAFRPPXXX");
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * Example of text:
     * <pre>
     * 06.02 PRLV SEPA URSSAF ILE DE FRANCE 06.02 480,00
     * ECH/060223 ID EMETTEUR/FR86ZZZ112534
     * MDT/000117
     * REF/TD671
     * LIB/UR 117000001552871947 FEV
     * 23323125457558060223
     * 07.02 VIR SEPA RECU 6 600,00
     * /MOTIF RETRO JANV 2023 /REF RETRO JANV
     * 2023
     * 10.02 PRLV SEPA SA ECH/100223 ID 10.02 100,97
     * EMETTEUR/FR1
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
