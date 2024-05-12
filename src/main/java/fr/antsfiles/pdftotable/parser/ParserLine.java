/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.antsfiles.pdftotable.parser;

import fr.antsfiles.pdftotable.model.Operation;

import java.util.List;

/**
 *
 * @author tonio
 */
public class ParserLine {

    private final String year;
    private final AbstractParser parser;

    public ParserLine(String year, AbstractParser parser) {
        super();
        this.year = year;
        this.parser = parser;
        parser.reset();
    }

    public enum FORMAT {
        BRED,
        CREDIT_NORD,
        SG,
        QONTO,
        BANQ_POPULAIRE,
        CAISSE_EPARGE,
        BNP,
        CREDIT_AGRICOLE,
        BANQ_POPULAIRE2,
        CIC,
        MILLEIS
    }

    public List<Operation> getOperations() {
        return parser.getOperations();
    }

    public void addLine(String line) {
        parser.year = this.year;
        parser.readLine(line);
    }

}
