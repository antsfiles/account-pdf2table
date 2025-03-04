package fr.antsfiles.pdftotable.read;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;

public class ItextTest {

    @Test
    void test() {
        try {
            String file = "file_test.pdf";

            PdfReader reader = new PdfReader(file);
            int n = reader.getNumberOfPages();
            PdfDictionary pdfDictionary = new PdfDictionary();

            PdfDictionary pageDic = reader.getPageN(1);
            PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);

            String str = PdfTextExtractor.getTextFromPage(reader, 1); // Extracting the content from a particular page.
            System.out.println(str);
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
