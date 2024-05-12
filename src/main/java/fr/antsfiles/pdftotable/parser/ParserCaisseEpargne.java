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
public class ParserCaisseEpargne extends AbstractParser {

    private final TableHeader tableHeader;

    public ParserCaisseEpargne() {
        List<TableHeaderName> names = List.of(new TableHeaderName("Date", ColumnType.DATE),
                new TableHeaderName("Valeur", ColumnType.UNUSED),
                new TableHeaderName(List.of("Détail", "des", "opérations", "en", "euros"), ColumnType.DESCRIPTION),
                new TableHeaderName("Débit", ColumnType.DEBIT),
                new TableHeaderName("Crédit", ColumnType.CREDIT));
        tableHeader = new TableHeader(names);
        tableHeader.setSpace("   ");
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.CAISSE_EPARGE;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("caisse-epargne");
    }

    /**
     * Example of text:
     * <pre>
     * 04/01 04/01 VIR SEPA BELIEVE 13 440,00
     * 2022-12-016
     * -Réf. donneur d'ordre : XX
     * 05/01 05/01 VIR SEPA XX 7 452,93
     * 92277800
     * -Réf. donn/XX/XX/1
     * 09/01 09/01 XX DR IDF ALL 942,08
     * 56 899 6314S 09012023
     * -Réf. donneur d'XXQ
     * 10/01 10/01 VIR INST MLLE XXQ 100,00
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
        pStart = line.indexOf("CB ");
        pEnd = line.indexOf("FACT ");
        String fact = "";
        if (pStart > 0 && pEnd > pStart) {
            fact = line.substring(pStart, pEnd + 6);
            fact = fact.replaceAll(" ", "_");
            String part1 = line.substring(0, pStart);
            String part2 = line.substring(pEnd + 6);
            line = part1 + fact + part2;
        }

        //replace spacew with _ to avoid error regex
        List<String> specials = List.of("FACT", "CB", "FRANPRIX", "LE", " LES", "DAB", "RETRAIT",
                "ATM", "LIDL", "RELAY", "GBP", "PICARD", "SHELL", "AUCHAN", "MADALOZZO", "DU", "CARNOT", "PARIS",
                "USD", "FC", "BOUCH.", "FR", "BANGKOK");
        for (String s : specials) {
            if (line.contains(s + " ")) {
                line = line.replace(s + " ", s + "_");
            }
        }
        String regex = "(\\d{2}/\\d{2})\\s(\\d{2}/\\d{2})(\\s.*?)\\s(\\d{1,3}(?:\\s\\d{3})*(?:,\\d{2})?)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        boolean found = false;
        if (matcher.find()) {
            String date1 = matcher.group(1);
            String date2 = matcher.group(2);
            String desc = matcher.group(3).replaceAll("_", " ").trim() + parenthese;
            String montant = matcher.group(4).replaceAll("\\s", "").replace(",", ".");

            System.out.println("Date 1: " + date1);
            System.out.println("Date 2: " + date2);
            System.out.println("Montant: " + montant);
            found = true;
            lastOperation = new Operation();
            lastOperation.setCreditamount(Double.valueOf(montant));
            lastOperation.setDate(date1);

            lastOperation.setDescription(desc);

            String fdesc = desc;
            List<String> containsDeb = List.of("RETRAIT DAB", "COTIS.FORFAIT", "PRLV ", "VIR SEPA");
            if (desc.startsWith("CB ") || containsDeb.stream().anyMatch(s -> fdesc.contains(s))) {
                lastOperation.setAmount(lastOperation.getCreditamount());
                lastOperation.setCreditamount(Double.NaN);
            }

            operations.add(lastOperation);
        }
    }

}
