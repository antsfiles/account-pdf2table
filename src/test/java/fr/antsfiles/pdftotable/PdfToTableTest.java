package fr.antsfiles.pdftotable;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import io.github.jonathanlink.PDFLayoutTextStripper;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tonio
 */
public class PdfToTableTest {

    private static final String FILE_PDF_CE = "test/JeanXX/2023/1-Relevés/181 - dec.pdf";
    private static final String FILE_PDF_BRED = "test/sXX08-Relevé n°14 daté du 28 août 2023.pdf";
    private static final String FILE_PDF_BNP = "test/7-releve_XXRDNN7 (1).pdf";
    private static final String FILE_PDF = FILE_PDF_BRED;

    /**
     * Test of main method, of class PdfToTable.
     */
    @Disabled
    @Test
    public void testApachePdfBox() throws IOException {
        String expectedText = "Hello World!\n";

        File file = new File(FILE_PDF);
        String text;

        try (PDDocument document = Loader.loadPDF(file)) {

            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            //PDFTextStripper stripper = new PDFTextStripper();
            PDFTextStripper stripper = new PDFLayoutTextStripper();

            stripper.setSortByPosition(true);

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                // Set the page interval to extract. If you don't, then all pages would be extracted.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // let the magic happen
                text = stripper.getText(document);

                // do some nice output with a header
                String pageStr = String.format("page %d:", p);
                System.out.println(pageStr);
                for (int i = 0; i < pageStr.length(); ++i) {
                    System.out.print("-");
                }
                System.out.println();
                System.out.println(text.trim());
                System.out.println();

                // If the extracted text is empty or gibberish, please try extracting text
                // with Adobe Reader first before asking for help. Also read the FAQ
                // on the website:
                // https://pdfbox.apache.org/2.0/faq.html#text-extraction
            }

            text = stripper.getText(document);
        }

        assertEquals(expectedText, text);

    }

    /**
     * Test of main method, of class PdfToTable.
     */
    //@Test
    public void testItext() throws IOException {
        String expectedText = "Hello World!\n";

        System.out.println("------- testItext --------");

        PdfReader reader = new PdfReader(FILE_PDF);
        int pages = reader.getNumberOfPages();
        StringBuilder text = new StringBuilder();
        for (int i = 1; i <= pages; i++) {
            String pagetxt = PdfTextExtractor.getTextFromPage(reader, i);
            text.append(pagetxt);

            System.out.println("---itext page " + i + " --");
            System.out.println(pagetxt);
            System.out.println("-------");
        }
        reader.close();

        System.out.println("-------");

        reader = new PdfReader(FILE_PDF);
        pages = reader.getNumberOfPages();
        text = new StringBuilder();
        LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
        for (int i = 1; i <= pages; i++) {
            String pagetxt = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            text.append(pagetxt);

            System.out.println("---itext loc strategy page " + i + " --");
            System.out.println(pagetxt);
            System.out.println("-------");
        }
        reader.close();

    }

}
