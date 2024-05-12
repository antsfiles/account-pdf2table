package fr.antsfiles.pdftotable.read;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class PdfImageReader {

    @Disabled
    @Test
    void testPdfImage() throws IOException {

        File file = new File("/media/tonio/DATASHUR/Didier_pdf/pdf-image.pdf");
        try (PDDocument document = Loader.loadPDF(file)) {
            extractTextFromScannedDocument(document);
        }
    }

    private String extractTextFromScannedDocument(PDDocument document) throws IOException {

        // Extract images from file
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

            File temp = new File("tempfile_" + page + ".png");

            ImageIO.write(bim, "png", temp);

            File imageFile = temp;
            ITesseract instance = new Tesseract();  // JNA Interface Mapping
            //ITesseract instance = new Tesseract1(); // JNA Direct Mapping
            File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build bundles English data
            instance.setDatapath(tessDataFolder.getPath());

            try {
                String result = instance.doOCR(imageFile);
                System.out.println(result);
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }
        }

        return out.toString();

    }
}
