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
public class ParserBanqPopulaire extends AbstractParser {

    private boolean ended = true;
    private TableHeader tableHeader;

    public ParserBanqPopulaire() {
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
        return ParserLine.FORMAT.BANQ_POPULAIRE;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("banque-populaire");
    }

    /**
     * Example of text:
     * <pre>
     * 28/11/2022 PRELEVEMENT SEPA INTERENTREPRISE
     * WIA POUR CSE FREN22
     * -220,00 EUR 28/11/2022
     * 21/11/2022 VIREMENT SEPA EMIS
     * MON
     * REMBOURS
     * CREGBBXXX
     * -78,00 EUR 21/11/2022
     * 21/11/2022 VIREMENT SEPA EMIS
     * CHO POMLLE
     * REMBOUR 22
     * AGRIFRP0
     * -20,00 EUR 21/11/2022
     * </pre>
     *
     * @param line
     */
    @Override
    public void readLine(String line) {

        String firstLineRegex = "^(\\d{2}/\\d{2}/\\d{4})(.*)";

        Matcher matcher = Pattern.compile(firstLineRegex).matcher(line);
        boolean found = matcher.find();
        if (found && ended) {
            ended = false;
            lineOperation = 0;
            lastOperation = new Operation();
            System.out.println("date: " + matcher.group(1));
            System.out.println("desc: " + matcher.group(2));

            lastOperation.setDate(matcher.group(1).replace('.', '/'));
            lastOperation.setDescription(matcher.group(2));

            //cas 1 line: 07/03/2022 COTISATION GOLD BUSINESS -168,00 EUR 07/03/2022
            if (line.contains("EUR") && line.contains(",") && line.contains("-")) {
                int e = line.indexOf("EUR");
                int i = line.indexOf("-");
                char sep = ',';
                lastOperation.setDescription(lastOperation.getDescription().substring(0, lastOperation.getDescription().indexOf("-")));
                lastOperation.setAmount(readAmount(line.substring(i, e), sep, ' '));

                ended = true;
            }

            operations.add(lastOperation);
        } else {
            lineOperation++;
            if (lastOperation != null && line.contains(",")
                    && (line.contains("€") || line.contains("EUR"))) {
                String v = line.replaceAll(" ", "");
                v = v.replaceAll("\\+", "");
                v = v.replaceAll("\\-", "");
                char sep = ',';

                v = v.replaceAll("€", "");
                int i = v.indexOf("EUR");
                if (i > 0) {
                    v = v.substring(0, i);
                }
                double val = readAmount(v, sep, ' ');
                if (!line.startsWith("-")) {
                    lastOperation.setCreditamount(val);
                } else {
                    lastOperation.setAmount(val);
                }
                ended = true;
            } else if (lastOperation != null && lineOperation < 3) {
                lastOperation.setDescription(lastOperation.getDescription() + " " + line);
            }
        }
    }

}
