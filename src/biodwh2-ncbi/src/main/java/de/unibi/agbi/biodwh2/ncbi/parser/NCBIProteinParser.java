package de.unibi.agbi.biodwh2.ncbi.parser;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.ncbi.model.ProteinRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class NCBIProteinParser {
    private static final Logger LOGGER = LogManager.getLogger(NCBIProteinParser.class);

    public void readFile(final Workspace workspace,
                         final DataSource dataSource,
                         final Consumer<ProteinRecord> proteinConsumer) throws IOException {
        File workspaceDir = workspace.getDataSourceDirectory(dataSource.getId()).resolve("source").toFile();
        Pattern pattern = Pattern.compile("vertebrate_mammalian\\.\\d+\\.protein\\.gpff\\.gz");

        LOGGER.info("Looking for protein files in: " + workspaceDir.getAbsolutePath());

        File[] proteinFiles = workspaceDir.listFiles(
                (dir, name) -> pattern.matcher(name).matches()
        );

        if (proteinFiles == null || proteinFiles.length == 0) {
            LOGGER.warn("No vertebrate_mammalian protein files found in workspace");
            return;
        }

        Arrays.sort(proteinFiles, Comparator.comparing(File::getName));

        for (File file : proteinFiles) {
            LOGGER.info("Reading protein file: " + file.getName());

            try (final BufferedReader reader = FileUtils.createBufferedReaderFromStream(
                    FileUtils.openGzip(workspace, dataSource, file.getName()))) {

                final StringBuilder record = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    record.append(line).append("\n");

                    if (line.equals("//")) {
                        ProteinRecord protein = parseProtein(record.toString());

                        if (protein != null) {
                            proteinConsumer.accept(protein);
                        }

                        record.setLength(0);
                    }
                }
            }

            LOGGER.info("Protein file read: " + file.getName());
        }
    }

    public ProteinRecord parseProtein(final String record) {
        String proteinId = null;
        String version = null;
        String locus = null;
        String dbLink = null;
        String keyword = null;
        String source = null;

        StringBuilder definitionBuilder = new StringBuilder();
        boolean inDefinition = false;

        for (String entry : record.split("\n")) {
            if (entry.startsWith("LOCUS")) {
                locus = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("DEFINITION")) {
                definitionBuilder.append(entry.substring(12).trim());
                inDefinition = true;
            } else if (entry.startsWith("ACCESSION")) {
                proteinId = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("VERSION")) {
                version = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("DBLINK")) {
                dbLink = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("KEYWORDS")) {
                keyword = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("SOURCE")) {
                source = entry.substring(12).trim();
                inDefinition = false;
            } else if (inDefinition && entry.startsWith("  ")) {
                definitionBuilder.append(" ").append(entry.trim());
            } else {
                inDefinition = false;
            }
        }

        if (proteinId == null) {
            return null;
        }

        ProteinRecord protein = new ProteinRecord();
        protein.setProteinId(proteinId);
        protein.setVersion(version);
        protein.setLocus(locus);
        protein.setDbLink(dbLink);
        protein.setKeyword(keyword);
        protein.setSource(source);

        if (definitionBuilder.length() > 0) {
            protein.setDefinition(definitionBuilder.toString());
        }

        return protein;
    }
}