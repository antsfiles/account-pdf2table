/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.antsfiles.pdftotable.read;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.antsfiles.pdftotable.model.Page;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import io.github.jonathanlink.PDFLayoutTextStripper;

/**
 *
 */
public class ExtractPdf {

    private boolean layoutStripper = false;

    public void setLayoutStripper(boolean layoutStripper) {
        this.layoutStripper = layoutStripper;
    }

    public List<Page> read(File file) throws IOException {
        List<Page> pages = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(file)) {

            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                //throw new IOException("You do not have permission to extract text");
            }
            PDFTextStripper stripper;
            if (layoutStripper) {
                stripper = new PDFLayoutTextStripper();
            } else {
                stripper = new PDFTextStripper();
            }

            stripper.setSortByPosition(true);

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                // Set the page interval to extract. If you don't, then all pages would be extracted.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // let the magic happen
                String text = stripper.getText(document);

                // do some nice output with a header
                String pageStr = String.format("page %d:", p);
                System.out.println(pageStr);
                for (int i = 0; i < pageStr.length(); ++i) {
                    System.out.print("-");
                }
                System.out.println();
                System.out.println(text.trim());
                pages.add(new Page(text.trim()));
                System.out.println();
            }
        }
        return pages;
    }
}
