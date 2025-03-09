package fr.antsfiles.pdftotable.read;

import fr.antsfiles.pdftotable.model.TableHeader;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateExtractor {

    private TableHeader tableHeader;
    private String year;

    public DateExtractor(TableHeader tableHeader, String year) {
        this.tableHeader = tableHeader;
        this.year = year;
    }

    // Table de correspondance des mois abrégés
    Map<Long, String> moisAbreges = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(1L, "janv."), new AbstractMap.SimpleEntry<>(2L, "févr."),
            new AbstractMap.SimpleEntry<>(3L, "mars"), new AbstractMap.SimpleEntry<>(4L, "avr."),
            new AbstractMap.SimpleEntry<>(5L, "mai"), new AbstractMap.SimpleEntry<>(6L, "juin"),
            new AbstractMap.SimpleEntry<>(7L, "juil."), new AbstractMap.SimpleEntry<>(8L, "août"),
            new AbstractMap.SimpleEntry<>(9L, "sept."), new AbstractMap.SimpleEntry<>(10L, "oct."),
            new AbstractMap.SimpleEntry<>(11L, "nov."), new AbstractMap.SimpleEntry<>(12L, "déc.")
    );

    public String extractDate(String input) {

        if (tableHeader.getDateFormat() != null && !tableHeader.getDateFormat().isBlank()) {

            DateTimeFormatter formatterBase = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toFormatter(Locale.ENGLISH);

            DateTimeFormatter formatter0 = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(DateTimeFormatter.ofPattern(tableHeader.getDateFormat())).toFormatter(Locale.ENGLISH);

            DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(DateTimeFormatter.ofPattern(tableHeader.getDateFormat())).toFormatter(Locale.FRENCH);

            DateTimeFormatter formatterShort = new DateTimeFormatterBuilder()
                    .appendPattern("dd ") // Jour
                    .appendText(ChronoField.MONTH_OF_YEAR, moisAbreges) // Mois abrégé avec point
                    .appendPattern("[.]") // Optionnel si le mois a un point
                    .optionalStart()
                    .appendText(ChronoField.MONTH_OF_YEAR)
                    .optionalEnd()
                    .appendPattern(" yyyy") // Année
                    .toFormatter(Locale.FRENCH);

            DateTimeFormatter formatterShort2 = new DateTimeFormatterBuilder()
                    .appendPattern("d ") // Jour
                    .appendText(ChronoField.MONTH_OF_YEAR, moisAbreges) // Mois abrégé avec point
                    .appendPattern("[.]") // Optionnel si le mois a un point
                    .optionalStart()
                    .appendText(ChronoField.MONTH_OF_YEAR)
                    .optionalEnd()
                    .appendPattern(" yyyy") // Année
                    .toFormatter(Locale.FRENCH);

            List<DateTimeFormatter> formatters = List.of(formatterBase, formatter0, formatter1, formatterShort, formatterShort2);

            for (DateTimeFormatter dtf : formatters) {
                try {
                    String text = input.trim().replaceAll("\\s+", " "); //reduce space
                    //System.out.println("Try parse date in '" + text + "':" + tableHeader.getDateFormat());
                    TemporalAccessor parsedDate = dtf.parse(text);

                    //LocalDate date1 = LocalDate.parse(text, dtf);
                    //System.out.println("OK parse date in '" + input + "':" + parsedDate);
                    String y = tableHeader.getDateFormat().toLowerCase().contains("y") ? parsedDate.get(ChronoField.YEAR) + "" : year;
                    String out = String.format("%02d/%02d/%s", MonthDay.from(parsedDate).getDayOfMonth(), MonthDay.from(parsedDate).getMonthValue(), y);
                    System.out.println("OK read date in '" + input + "':" + out);
                    return out;
                } catch (Exception e) {
                    System.out.println("Err read date in '" + input + "':" + e.getMessage());
                }
            }
        }
        return null;
    }

}
