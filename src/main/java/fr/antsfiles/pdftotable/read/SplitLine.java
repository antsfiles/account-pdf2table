/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.read;

import fr.antsfiles.pdftotable.model.TableHeader;
import fr.antsfiles.pdftotable.model.TableHeaderName;

/**
 *
 * @author tonio
 */
public class SplitLine {

    public String proceed(String line, TableHeader header, TableHeaderName headerName) {

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

        return line;
    }
}
