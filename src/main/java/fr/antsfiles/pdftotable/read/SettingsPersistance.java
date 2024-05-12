/*
 *  antsfiles
 */
package fr.antsfiles.pdftotable.read;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.antsfiles.pdftotable.model.TableHeader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * read the folder and get all predefined settings. Also save new settings.
 */
public class SettingsPersistance {

    private static final Logger LOGGER = Logger.getLogger(SettingsPersistance.class.getName());
    public static final String SETTINGS_FOLDER = "config";

    public List<String> getAll() {

        try {
            return Files.walk(Path.of(SETTINGS_FOLDER)).filter(p -> p.getFileName().toString().endsWith(".json")).map(p -> p.getFileName().toString().replace(".json", "")).collect(Collectors.toList());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return List.of();
        }
    }

    public void save(String name, TableHeader tableHeader) {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(tableHeader);
            Files.write(Path.of("config", name + ".json"), List.of(json));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public TableHeader load(String name) {

        ObjectReader or = new ObjectMapper().reader();
        try {
            String s = Files.readString(Path.of("config", name + ".json"));
            TableHeader tableHeader = or.readValue(s, TableHeader.class);
            return tableHeader;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
