package fr.antsfiles.pdftotable;

import fr.antsfiles.pdftotable.parser.AbstractParser;
import fr.antsfiles.pdftotable.model.Operation;
import fr.antsfiles.pdftotable.parser.ParserBNP;
import fr.antsfiles.pdftotable.parser.ParserBRED;
import fr.antsfiles.pdftotable.parser.ParserBankPopulaire2;
import fr.antsfiles.pdftotable.parser.ParserBanqPopulaire;
import fr.antsfiles.pdftotable.parser.ParserCaisseEpargne;
import fr.antsfiles.pdftotable.parser.ParserCic;
import fr.antsfiles.pdftotable.parser.ParserCreditAgricole;
import fr.antsfiles.pdftotable.parser.ParserCreditNord;
import fr.antsfiles.pdftotable.parser.ParserLine;
import fr.antsfiles.pdftotable.parser.ParserMillies;
import fr.antsfiles.pdftotable.parser.ParserQonto;
import fr.antsfiles.pdftotable.parser.ParserSG;
import fr.antsfiles.pdftotable.model.TableHeader;
import fr.antsfiles.pdftotable.parser.ZoneTableDetect;
import fr.antsfiles.pdftotable.read.ExtractPdf;
import fr.antsfiles.pdftotable.model.Page;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class MainControler {

    private static final Logger LOGGER = Logger.getLogger(MainControler.class.getName());

    private final BehaviorSubject<String> status = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> year = BehaviorSubject.createDefault("2023");
    private final BehaviorSubject<String> outText = BehaviorSubject.createDefault("");
    private final BehaviorSubject<List<Operation>> outOperation = BehaviorSubject.createDefault(List.of());
    private final BehaviorSubject<String> inText = BehaviorSubject.createDefault("");
    private final BehaviorSubject<ParserLine.FORMAT> format = BehaviorSubject.createDefault(ParserLine.FORMAT.BRED);

    private List<AbstractParser> parsers;
    private List<Page> pages;
    private TableHeader tableHeaderSetting;

    private File file;

    public MainControler() {
        resetParsers();
    }

    private void resetParsers() {
        parsers = List.of(new ParserBNP(), new ParserBRED(), new ParserBankPopulaire2(),
                new ParserBanqPopulaire(), new ParserCaisseEpargne(), new ParserCic(),
                new ParserCreditAgricole(), new ParserCreditNord(), new ParserQonto(), new ParserSG(),
                new ParserMillies());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void readFile(File f) {
        try {
            resetParsers();
            status.onNext("read " + f.getName());
            ExtractPdf extractPdf = new ExtractPdf();
            pages = extractPdf.read(f);
            status.onNext("read " + f.getName() + " ok. Nb Pages: " + pages.size());

            if (!pages.isEmpty()) {
                parsers.stream().filter(p -> p.isMine(pages.get(0).getAlllines())).findFirst().ifPresent(p -> {
                    format.onNext(p.getFormat());
                });
            }

            StringBuilder sbIn = new StringBuilder();
            List<Operation> opsAll = new ArrayList<>();
            for (Page page : pages) {
                sbIn.append(page.getAlllines());
                List<Operation> ops = getFormattedFromOperations(page.getAlllines(), format.getValue());
                opsAll.addAll(ops);
            }
            StringBuilder sbOut = new StringBuilder();
            for (Operation op : opsAll) {
                sbOut.append(op).append("\n");
            }
            inText.onNext(sbIn.toString());
            outText.onNext(sbOut.toString());
            outOperation.onNext(opsAll);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            status.onNext(ex.getMessage());
        }
    }

    public void setTableHeaderSetting(TableHeader tableHeaderSetting) {
        this.tableHeaderSetting = tableHeaderSetting;
    }

    public void readFileWithLayout(File f) {
        readFileWithLayout(f, tableHeaderSetting);
    }

    public void readFileWithLayout(File f, TableHeader tableHeaderManual) {
        try {
            resetParsers();
            status.onNext("read " + f.getName());
            ExtractPdf extractPdf = new ExtractPdf();
            extractPdf.setLayoutStripper(true);
            pages = extractPdf.read(f);
            status.onNext("read " + f.getName() + " ok. Nb Pages: " + pages.size());

            AbstractParser parser = parsers.get(0);
            if (!pages.isEmpty()) {
                Optional<AbstractParser> oparser = parsers.stream().filter(p -> p.isMine(pages.get(0).getAlllines())).findFirst();
                if (oparser.isPresent()) {
                    format.onNext(oparser.get().getFormat());
                    parser = oparser.get();
                } else {
                    parser = parsers.stream().filter(p -> p.getFormat() == format.getValue()).findFirst().get();
                }
            }
            TableHeader tableHeader = tableHeaderManual != null ? tableHeaderManual : parser.getTableHeader();
            ZoneTableDetect zoneTableDetect = new ZoneTableDetect();
            zoneTableDetect.detectTable(tableHeader, pages);

            StringBuilder sbIn = new StringBuilder();
            List<Operation> opsAll = zoneTableDetect.getOperations();
            for (Page page : pages) {
                sbIn.append("-\n");
                sbIn.append(page.getAlllines());
            }

            inText.onNext(sbIn.toString());
            updateOutTxt(opsAll);
            outOperation.onNext(opsAll);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            status.onNext(ex.getMessage());
        }
    }

    public void updateOutTxt(List<Operation> operations) {
        StringBuilder sbOut = new StringBuilder();
        for (Operation op : operations) {
            sbOut.append(op).append("\n");
        }
        outText.onNext(sbOut.toString());
    }

    public String getRawFromText(String t) {
        if (t.contains("¨")) {
            String nexTxt = t.replaceAll("\n", " ");
            t = nexTxt.replaceAll("¨", "\n");
        }
        return t;
    }

    public List<Operation> getFormattedFromOperations(String txt, ParserLine.FORMAT format) {
        AbstractParser parser = parsers.stream().filter(p -> p.getFormat() == format).findFirst().get();
        String[] lines = txt.split("\n");
        ParserLine pl = new ParserLine(year.getValue(), parser);
        for (String line : lines) {
            try {
                pl.addLine(line);
            } catch (Exception e) {
                status.onNext("line: " + line + " ERROR:" + e.getMessage());
            }

        }
        outOperation.onNext(pl.getOperations());
        return pl.getOperations();
    }

    public String getFormattedFromRawText(String txt, ParserLine.FORMAT format) {
        List<Operation> ops = getFormattedFromOperations(txt, format);
        StringBuilder sb = new StringBuilder();
        for (Operation op : ops) {
            sb.append(op).append("\n");
        }
        return sb.toString();
    }

    public BehaviorSubject<String> status() {
        return status;
    }

    public BehaviorSubject<String> year() {
        return year;
    }

    public BehaviorSubject<String> outText() {
        return outText;
    }

    public BehaviorSubject<ParserLine.FORMAT> format() {
        return format;
    }

    public BehaviorSubject<String> inText() {
        return inText;
    }

    public BehaviorSubject<List<Operation>> outOperation() {
        return outOperation;
    }

}
