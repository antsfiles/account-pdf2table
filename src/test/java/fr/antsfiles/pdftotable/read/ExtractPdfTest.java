package fr.antsfiles.pdftotable.read;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.fail;

import fr.antsfiles.pdftotable.model.Page;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class ExtractPdfTest {

    public static final String RESOURCES = "/home/tonio/Desktop/didier/2024-12-13/";//"src/test/resources";

    /**
     * Test of read method, of class ExtractPdf.
     */
    @Test
    public void testPdfToTxt() throws Exception {
        System.out.println("testPdfToTxt");
        File dirPdf = new File(RESOURCES);
        File[] files = dirPdf.listFiles();
        Stream.of(files).filter(f -> f.getName().endsWith(".pdf")).forEach(f -> {
            System.out.println("testPdfToTxt " + f + " ...");
            ExtractPdf instance = new ExtractPdf();
            try {
                List<Page> pages = instance.read(f);
                List<String> lines = pages.stream().map(p -> p.getAlllines()).collect(Collectors.toList());
                Files.write(Path.of(f.getPath().replaceAll(".pdf", ".txt")), lines);
            } catch (IOException ex) {
                fail(ex);
            }
        });
    }

    @Test
    public void testPdfToTxtLayout() throws Exception {
        System.out.println("testPdfToTxt");
        File dirPdf = new File(RESOURCES);
        File[] files = dirPdf.listFiles();
        Stream.of(files).filter(f -> f.getName().endsWith(".pdf")).forEach(f -> {
            System.out.println("testPdfToTxt " + f + " ...");
            ExtractPdf instance = new ExtractPdf();
            instance.setLayoutStripper(false);
            try {
                List<Page> pages = instance.read(f);
                List<String> lines = pages.stream().map(p -> p.getAlllines()).collect(Collectors.toList());
                Files.write(Path.of(f.getPath().replaceAll(".pdf", "-layout.txt")), lines);
            } catch (IOException ex) {
                fail(ex);
            }
        });
    }

}
