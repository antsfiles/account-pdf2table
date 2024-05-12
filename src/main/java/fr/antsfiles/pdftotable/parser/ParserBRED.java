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
public class ParserBRED extends AbstractParser {

    private final TableHeader tableHeader;

    public ParserBRED() {
        List<TableHeaderName> names = List.of(new TableHeaderName("Date", ColumnType.DATE).withCols(0, 21),
                new TableHeaderName(List.of("Référence"), ColumnType.DESCRIPTION).withCols(21, 90),
                new TableHeaderName("Débit", ColumnType.DEBIT).withCols(92, 111),
                new TableHeaderName("Crédit", ColumnType.CREDIT).withCols(111, 136),
                new TableHeaderName("Valeur", ColumnType.UNUSED));
        tableHeader = new TableHeader(names);
        tableHeader.setSpace("  ");
    }

    @Override
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    @Override
    public ParserLine.FORMAT getFormat() {
        return ParserLine.FORMAT.BRED;
    }

    @Override
    public boolean isMine(String pageLines) {
        return pageLines.contains("BREDFRPPXXX     FR");
    }

    /**
     * Example of text:
     * <pre>
     * 05.05 Prélèvement SEPA in0 4.739,00 05.05.22
     * dgfip
     * spb
     * assurance mobile spb telecom
     *  sepa
     *  rcur
     * deb-spb-876-20220426-000063493013
     * 06.05 Carte 0117010 17,49 06.05.22
     * deliveroo le 06/05/22
     * ref cb.
     * 06.05 Carte 1519591 72,00 06.05.22
     * o2switch.fr le 05/05/22
     * ref cb.
     * 06.05 Carte 0142346 25,77 06.05.22
     * </pre>
     *
     * @param line
     */
    @Override
    public void readLine(String line) {
        //
        String regexBred = "\\b(\\d{2}\\.\\d{2})\\s*(.+?)\\s*((?:\\d{1,3}(?:[\\.]\\d{3})*|\\d+)\\,\\d{2})\\b";
        Matcher matcher = Pattern.compile(regexBred).matcher(line);

        if (matcher.find()) {
            lineOperation = 0;
            lastOperation = new Operation();
            System.out.println("Chaine: " + matcher.group(1));
            System.out.println("Description: " + matcher.group(2));
            System.out.println("Montant: " + matcher.group(3));
            lastOperation.setDate(matcher.group(1).replace('.', '/') + "/" + year);
            lastOperation.setDescription(matcher.group(2));

            String[] splited = lastOperation.getDescription().split(" ");
            if (splited.length > 1) {
                //remove last string (the number)
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < splited.length - 1; i++) {
                    String string = splited[i];
                    sb.append(string).append(" ");
                }
                lastOperation.setDescription(sb.toString());
            }

            lastOperation.setAmount(readAmount(matcher.group(3), ',', '.'));
            operations.add(lastOperation);
        } else {
            lineOperation++;
            if (lastOperation != null && lineOperation < 3) {
                lastOperation.setDescription(lastOperation.getDescription() + line);
            }
        }
    }

}
