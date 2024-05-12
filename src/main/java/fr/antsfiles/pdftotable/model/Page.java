package fr.antsfiles.pdftotable.model;

/**
 *
 */
public class Page {

    private final String alllines;

    public Page(String alllines) {
        this.alllines = alllines;
    }

    public String getAlllines() {
        return alllines;
    }

    public String[] getLines() {
        return alllines.split("\n");
    }
}
