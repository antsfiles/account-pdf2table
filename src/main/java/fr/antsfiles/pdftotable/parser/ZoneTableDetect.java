/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.parser;

import fr.antsfiles.pdftotable.model.ColumnType;
import static fr.antsfiles.pdftotable.model.ColumnType.DATE;
import fr.antsfiles.pdftotable.model.Operation;
import fr.antsfiles.pdftotable.model.Page;
import fr.antsfiles.pdftotable.model.TableHeader;
import fr.antsfiles.pdftotable.model.TableHeaderName;
import fr.antsfiles.pdftotable.read.DateExtractor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class ZoneTableDetect {

    private static final String REGEX_AMOUNT_POINT_THOUSAND = "\\d{1,3}(?:[\\.]{0,2}\\d{3})*(?:,\\d{2})";
    private static final String REGEX_AMOUNT_SPACE_THOUSAND = "\\d{1,3}(?:[\\s]{0,2}\\d{3})*(?:,\\d{2})";

    private int lineHeader = -1;
    private String year = "2023";
    private TableHeader tableHeader;
    private List<Operation> operations = new ArrayList<>();
    private List<String> excludedFilter = List.of("Solde précédent", "Nouveau solde", "Nouveau  solde");

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void detectTable(TableHeader tableHeader, List<Page> pages) {
        lineHeader = 0;
        operations.clear();
        this.tableHeader = tableHeader;

        int pageNo = 0;
        for (Page page : pages) {
            System.out.println(" --- PAGE " + pageNo + " ");
            detectTableByPage(tableHeader, page);
            pageNo++;
        }
        operations.forEach(o -> {
            System.out.println("op: " + o.toPrettyString());
        });
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void detectTableByPage(TableHeader tableHeader, Page page) {

        int lineNo = 0;
        this.tableHeader = tableHeader;
        Operation lastOp = null;
        int lineSize = 0;
        for (String line : page.getLines()) {
            if (lineSize < line.length()) {
                lineSize = line.length();
            }
        }
        System.out.println("lineSize = " + lineSize);

        for (String line : page.getLines()) {
            //fix error line length
            while (line.length() < lineSize) {
                line = " " + line;
            }
            if (line.contains("€1  432.37")) {
                System.err.println(line);
            }
            if (isHeader(tableHeader, line)) {
                //line header found
                lineHeader = lineNo;
                System.out.println(lineNo + ": header is: " + line);
                final String fline = line;
                tableHeader.getHeaderNames().forEach(h -> findZone(h, fline));
                for (int i = 0; i < tableHeader.getHeaderNames().size(); i++) {
                    TableHeaderName tableHeaderName = tableHeader.getHeaderNames().get(i);
                    TableHeaderName prev = i > 0 ? tableHeader.getHeaderNames().get(i - 1) : null;
                    TableHeaderName next = i < (tableHeader.getHeaderNames().size() - 1) ? tableHeader.getHeaderNames().get(i + 1) : null;
                    //findZoneLimit(tableHeaderName, line, prev, next);
                }
                continue;
            }
            if (lineHeader >= 0) {
                boolean isRow = false;
                for (int i = 0; i < tableHeader.getHeaderNames().size(); i++) {
                    TableHeaderName tableHeaderName = tableHeader.getHeaderNames().get(i);
                    tableHeaderName.setValue("");
                    String s = readCol(tableHeaderName, line);
                    if (s.isBlank() && tableHeaderName.getColumnType() == ColumnType.DATE) {
                        break;
                    }
                    isRow = true;
                    tableHeaderName.setValue(s);
                    if (lineNo == 0 && i == 0) {
                        //special case: if date text start at 0
                        if (line.getBytes()[0] != ' ') {
                            String heading = "";
                            for (int j = 0; j < tableHeaderName.getColMin(); j++) {
                                heading = heading + " ";
                            }
                            line = heading + line;
                        }
                    }
                }
                if (isRow) {
                    try {
                        lastOp = toOperation(tableHeader);
                    } catch (Exception e) {
                        System.err.println("e!" + e);
                    }
                } else {
                    if (lastOp != null) {
                        lastOp.setDescription(lastOp.getDescription() + " " + line.trim());
                        lastOp = null;
                    }
                }
            }
            lineNo++;
        }
    }

    public int getLineHeader() {
        return lineHeader;
    }

    public Operation toOperation(TableHeader tableHeader) {
        Operation operation = new Operation();
        operation.setAmount(Double.NaN);
        operation.setCreditamount(Double.NaN);
        for (int i = 0; i < tableHeader.getHeaderNames().size(); i++) {
            TableHeaderName tableHeaderName = tableHeader.getHeaderNames().get(i);
            if (tableHeaderName.getValue().isBlank() && tableHeaderName.getColumnType() == ColumnType.DATE) {
                break;
            }
            switch (tableHeaderName.getColumnType()) {
                case DATE:
                    operation.setDate(extractPattern(tableHeaderName, tableHeaderName.getValue()).trim());
                    break;
                case DESCRIPTION:
                    operation.setDescription(tableHeaderName.getValue());
                    break;
                case DEBIT:
                    operation.setAmount(toDouble(tableHeaderName.getValue()));
                    break;
                case CREDIT:
                    operation.setCreditamount(toDouble(tableHeaderName.getValue()));
                    break;
                default:
                    break;
            }
        }
        boolean invalid
                = operation.getDescription().trim().isBlank() || operation.getDate().isEmpty() || (Double.isNaN(operation.getAmount()) && Double.isNaN(operation.getCreditamount()));

        if (excludedFilter.contains(operation.getDescription().trim())) {
            return null;
        }
        if (!invalid && (operation.getCreditamount() != operation.getAmount())) {
            operations.add(operation);
            return operation;
        }
        return null;
    }

    private double toDouble(String v) {
        if (v.isBlank()) {
            return Double.NaN;
        }
        if (!v.matches(REGEX_AMOUNT_SPACE_THOUSAND)
                && v.matches(REGEX_AMOUNT_POINT_THOUSAND)) {
            v = v.replace(".", "");
        }

        String str = v.replaceAll("[^\\d.,]", "");
        return Double.parseDouble(str.replace(",", "."));
    }

    private boolean isHeader(TableHeader tableHeader, String txt) {
        for (TableHeaderName tableHeaderName : tableHeader.getHeaderNames()) {
            if (tableHeaderName.getColumnType() != ColumnType.UNUSED && !tableHeaderName.getNames().stream().anyMatch(hn -> txt.contains(hn))) {
                return false;
            }
            System.out.println("fr.antsfiles.pdftotable.parser.ZoneTableDetect.isHeader()");
        }
        return true;
    }

    private void findZone(TableHeaderName headerName, String line) {
        int iMin = line.indexOf(headerName.getNames().get(0));
        String lastName = headerName.getNames().get(headerName.getNames().size() - 1);
        int iMax = line.indexOf(lastName) + lastName.length();
        // give 1 col margin
        headerName.setColMin(iMin > 1 ? iMin - 1 : 0);
        headerName.setColMax(iMax < (line.length() - 2) ? iMax + 1 : line.length() - 1);
        System.out.println("zone " + lastName + ": " + iMin + " - " + iMax);
    }

    private void findZoneLimit(TableHeaderName headerName, String line, TableHeaderName prev, TableHeaderName next) {
        if (prev == null) {
            headerName.setColMinLimit(0);
        } else if (headerName.getColMinLimit() == 0) {
            headerName.setColMinLimit(prev.getColMax());
        }
        if (next == null) {
            headerName.setColMaxLimit(line.length() - 1);
        } else if (headerName.getColMaxLimit() == 0) {
            headerName.setColMaxLimit(next.getColMin());
        }
        System.out.println("zoneLimit " + headerName.getNames().get(0) + ": " + headerName.getColMinLimit() + " - " + headerName.getColMaxLimit());
    }

    private String readCol(TableHeaderName headerName, String line) {
        System.out.println("read: " + line);
        if (line.length() < headerName.getColMaxLimit()) {
            //return "";
            line = line;
        }
        if (line.length() < headerName.getColMinLimit()) {
            return "";
        }
        int max = Math.min(headerName.getColMaxLimit(), line.length());
        String extractLine = line.substring(headerName.getColMinLimit(), max).trim();
        max = Math.min(headerName.getColMax(), line.length());
        String extractCenter = line.substring(headerName.getColMin(), max).trim();
        if (Objects.equals(extractCenter, extractLine)) {
            //we can use this directly
            return extractLine;
        } else {
            if (headerName.getColumnType() == ColumnType.UNUSED) {
                return extractLine;
            }

            if (headerName.getColumnType() == DATE) {
                String v = extractPattern(headerName, extractLine);
                return v;
            }

            if (headerName.getColumnType() == ColumnType.DEBIT || headerName.getColumnType() == ColumnType.CREDIT) {

                if (extractCenter.isBlank()) {
                    return extractCenter;
                }

                //2 values?
                String text = extractLine.trim().replaceAll("\\s+", " "); //reduce space
                if (text.contains("€") && tableHeader.getAmoutFormat() != null && !tableHeader.getAmoutFormat().isBlank()) {
                    // Regex pattern to match currency values with Euro symbol
                    String regex = "€\\d{1,3}(?:\\s?\\d{3})*(?:\\.\\d{2})?";
                    regex = tableHeader.getAmoutFormat();
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        // Extract the first match
                        return matcher.group();
                    }
                }

                extractLine = extractLine.replaceAll(" ", "");

                extractLine = extractLine.replace("EUR", "");
                extractLine = extractLine.replace("€", "").trim();
                if (extractLine.indexOf('-') >= 0 && headerName.getColumnType() == ColumnType.CREDIT) {
                    return "";
                }
                if (extractLine.indexOf('+') >= 0 && headerName.getColumnType() == ColumnType.DEBIT) {
                    return "";
                }

                extractLine = extractLine.replace("+", "");
                extractLine = extractLine.replace("-", "");

                if (".".equals(tableHeader.getValueCentsSeperator())) {
                    extractLine = extractLine.replace(".", ",").trim();
                }
            }

            // make sure we dont get the other column
            // use double space as big separator
            String[] spaces = extractLine.split(tableHeader.getSpace());
            List<String> o = Stream.of(spaces).filter(s -> !s.isBlank()).collect(Collectors.toList());
            if (o.isEmpty()) {
                return "";
            }

            if (headerName.getColumnType() == ColumnType.DESCRIPTION) {
                return o.stream().collect(Collectors.joining(" "));
            }
            for (String s : o) {
                if (checkPattern(headerName, s)) {
                    return s;
                }
            }

            return "";
        }

    }

    private boolean checkPattern(TableHeaderName headerName, String s) {
        for (String r : headerName.getColumnType().getRegexes()) {
            Pattern pattern = Pattern.compile(r);
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    private String extractPattern(TableHeaderName headerName, String s) {
        if (headerName.getColumnType() == DATE) {

            String out = new DateExtractor(tableHeader, year).extractDate(s);
            if (out != null) {
                return out;
            }
            return out == null ? "" : out;
        }

        for (String r : headerName.getColumnType().getRegexes()) {
            Pattern pattern = Pattern.compile(r);
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                if (headerName.getColumnType() == DATE) {
                    String d = matcher.group(1).replaceAll("\\.", "/");
                    pattern = Pattern.compile("(\\d{2}/\\d{2})");
                    matcher = pattern.matcher(d);
                    if (matcher.find()) {
                        return d + "/" + year;
                    }
                    pattern = Pattern.compile("(\\d{1}/\\d{2})");
                    matcher = pattern.matcher(d);
                    if (matcher.find()) {
                        return "0" + d + "/" + year;
                    }
                    return d;
                }
                return s;
            }
        }
        return "";
    }

}
