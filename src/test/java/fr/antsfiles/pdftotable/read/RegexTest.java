package fr.antsfiles.pdftotable.read;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 *
 */
class RegexTest {

    @Test
    void testRegex() {
        String l = "02.10.23 VIR SEPA RECU /FRM BDL CAPITAL 02.10.23 13 200,00";
        String r = "\\d{2}.\\d{2}.\\d{2}";

        System.out.println("line: " + l);
        System.out.println("regs: " + r);

        String[] lines = l.split(r);
        for (String o : lines) {
            System.out.println("=> " + o);
        }

        Pattern pattern = Pattern.compile(r);
        Matcher matcher = pattern.matcher(l);
        while (matcher.find()) {
            System.out.println("group: => " + matcher.group());
        }
    }

    @Test
    void testRegex2() {
        String l = "02.10.23 VIR SEPA RECU /FRM BDL CAPITAL 02.10.23 13 200,00";

        String l0 = "02.10.23 VIR SEPA RECU /FRM BDL CAPITAL 02.10.23 200,00";
        String r = "\\s\\d{1,3}(?:[\\s\\.]{0,2}\\d{3})*(?:,\\d{2})";

        System.out.println("line: " + l);
        System.out.println("regs: " + r);

        String[] lines = l.split(r);
        for (String o : lines) {
            System.out.println("=> " + o);
        }

        Pattern pattern = Pattern.compile(r);
        Matcher matcher = pattern.matcher(l);
        while (matcher.find()) {
            System.out.println("group: => " + matcher.group());
        }
    }
}
