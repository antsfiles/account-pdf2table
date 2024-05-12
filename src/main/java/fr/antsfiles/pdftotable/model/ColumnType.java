/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.model;

import java.util.List;

/**
 *
 */
public enum ColumnType {

    DATE(List.of("(\\d{2}.\\d{2})", "(\\d{2}/\\d{2})", "(\\d{2}/\\d{2}/\\d{4})", "(\\d{1}/\\d{2})")),
    DESCRIPTION(List.of("\\s")),
    DEBIT(List.of("\\d{1,3}(?:[\\s\\.]{0,2}\\d{3})*(?:,\\d{2})")),
    CREDIT(List.of("\\d{1,3}(?:[\\s\\.]{0,2}\\d{3})*(?:,\\d{2})")),
    UNUSED(List.of("\\s"));

    List<String> regex;

    private ColumnType(List<String> regex) {
        this.regex = regex;
    }

    public List<String> getRegexes() {
        return regex;
    }
}
