/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.parser;

import fr.antsfiles.pdftotable.model.TableHeader;
import fr.antsfiles.pdftotable.read.ExtractPdf;
import fr.antsfiles.pdftotable.model.Page;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class ZoneTableDetectTest {

    private static final String FILE_PDF_CE = "src/test/resources/bnp.pdf";

    /**
     * Test of detectTable method, of class ZoneTableDetect.
     */
    @Test
    public void testDetectTable() {
        System.out.println("detectTable");
        File file = new File("src/test/resources/bnp.pdf");
        TableHeader tableHeader = new ParserBNP().getTableHeader();

        ExtractPdf extractPdf = new ExtractPdf();
        extractPdf.setLayoutStripper(true);
        try {
            List<Page> pages = extractPdf.read(file);
            ZoneTableDetect zoneTableDetect = new ZoneTableDetect();
            zoneTableDetect.detectTable(tableHeader, pages);

        } catch (IOException ex) {
            fail(ex);
        }
    }

}
