/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.read;

import fr.antsfiles.pdftotable.parser.AbstractParser;
import fr.antsfiles.pdftotable.parser.ParserBNP;
import fr.antsfiles.pdftotable.parser.ParserBRED;
import fr.antsfiles.pdftotable.parser.ParserBankPopulaire2;
import fr.antsfiles.pdftotable.parser.ParserBanqPopulaire;
import fr.antsfiles.pdftotable.parser.ParserCaisseEpargne;
import fr.antsfiles.pdftotable.parser.ParserCic;
import fr.antsfiles.pdftotable.parser.ParserCreditAgricole;
import fr.antsfiles.pdftotable.parser.ParserCreditNord;
import fr.antsfiles.pdftotable.parser.ParserMillies;
import fr.antsfiles.pdftotable.parser.ParserQonto;
import fr.antsfiles.pdftotable.parser.ParserSG;
import fr.antsfiles.pdftotable.model.TableHeader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class SettingsPersistanceTest {

    /**
     * Test of save method, of class SettingsPersistance.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        List<AbstractParser> parsers = List.of(new ParserBNP(), new ParserBRED(), new ParserBankPopulaire2(),
                new ParserBanqPopulaire(), new ParserCaisseEpargne(), new ParserCic(),
                new ParserCreditAgricole(), new ParserCreditNord(), new ParserQonto(), new ParserSG(),
                new ParserMillies());
        for (AbstractParser p : parsers) {
            TableHeader tableHeader = p.getTableHeader();
            SettingsPersistance instance = new SettingsPersistance();
            instance.save(p.getFormat().name(), tableHeader);
        }
    }

    /**
     * Test of load method, of class SettingsPersistance.
     */
    @Test
    public void testLoad() {
        System.out.println("load");
        SettingsPersistance instance = new SettingsPersistance();

        List<AbstractParser> parsers = List.of(new ParserBNP(), new ParserBRED(), new ParserBankPopulaire2(),
                new ParserBanqPopulaire(), new ParserCaisseEpargne(), new ParserCic(),
                new ParserCreditAgricole(), new ParserCreditNord(), new ParserQonto(), new ParserSG(),
                new ParserMillies());
        for (AbstractParser p : parsers) {
            TableHeader tableHeader = p.getTableHeader();
            TableHeader result = instance.load(p.getFormat().name());

            assertEquals(tableHeader, result);
        }

    }

    @Test
    public void testGetAll() {
        System.out.println("testGetAll");
        SettingsPersistance instance = new SettingsPersistance();
        List<String> l = instance.getAll();
        l.forEach(System.out::println);
    }

}
