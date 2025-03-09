/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.read;

import fr.antsfiles.pdftotable.model.TableHeader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tonio
 */
public class DateExtractorTest {

    /**
     * Test of extractDate method, of class DateExtractor.
     */
    @Test
    public void testExtractDate() {
        System.out.println("extractDate");

        TableHeader tableHeader = new TableHeader();
        tableHeader.setDateFormat("dd LLLL yyyy");
        DateExtractor dateExtractor = new DateExtractor(tableHeader, "2025");
        String v = dateExtractor.extractDate("26 août 2023 ");
        System.out.println("==> " + v);
        v = dateExtractor.extractDate("29 sept. 2023 ");
        System.out.println("==> " + v);

        //
        v = dateExtractor.extractDate("26 août  2023 ");
        System.out.println("==> " + v);
    }

    @Test
    public void testExtractAmout() {
        System.out.println("testExtractAmout");

        String input = "€830.00           €1  280.00";

        // Regex pattern to match currency values with Euro symbol
        String regex = "€\\d{1,3}(?:\\s?\\d{3})*(?:\\.\\d{2})?";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            // Extract the first match
            System.out.println("First value: " + matcher.group());
        }

        input = "€830.00  ";

        // Regex pattern to match currency values with Euro symbol
        regex = "€\\d{1,3}(?:\\s?\\d{3})*(?:\\.\\d{2})?";

        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(input);

        if (matcher.find()) {
            // Extract the first match
            System.out.println("First value: " + matcher.group());
        }

    }
}
