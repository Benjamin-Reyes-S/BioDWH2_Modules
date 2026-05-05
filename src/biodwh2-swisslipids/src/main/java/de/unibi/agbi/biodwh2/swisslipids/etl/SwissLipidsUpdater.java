package de.unibi.agbi.biodwh2.swisslipids.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.swisslipids.SwissLipidsDataSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SwissLipidsUpdater extends Updater<SwissLipidsDataSource> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd yyyy", Locale.US);
    private static final String DOWNLOAD_URL_PREFIX = "https://www.swisslipids.org/api/file.php?cas=download_files&file=";
    static final String ENZYMES_FILE_NAME = "enzymes.tsv.gz";
    static final String EVIDENCES_FILE_NAME = "evidences.tsv.gz";
    static final String GO_FILE_NAME = "go.tsv";
    static final String LIPIDS_FILE_NAME = "lipids.tsv.gz";
    static final String LIPIDS2UNIPROT_FILE_NAME = "lipids2uniprot.tsv.gz";
    static final String TISSUES_FILE_NAME = "tissues.tsv.gz";

    public SwissLipidsUpdater(final SwissLipidsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("https://www.swisslipids.org/api/index.php/downloadData");
        final JsonNode downloadFilesNode = parseJsonSource(source);
        Version latestVersion = null;
        for (final JsonNode node : downloadFilesNode) {
            final String dateText = node.get("date").asText(null);
            if (dateText != null) {
                final var date = LocalDate.parse(dateText, DATE_FORMATTER);
                final Version version = new Version(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
                if (latestVersion == null || version.compareTo(latestVersion) > 0) {
                    latestVersion = version;
                }
            }
        }
        return latestVersion;
    }

    private JsonNode parseJsonSource(final String source) throws UpdaterMalformedVersionException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(source);
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                ENZYMES_FILE_NAME, EVIDENCES_FILE_NAME, GO_FILE_NAME, LIPIDS_FILE_NAME, LIPIDS2UNIPROT_FILE_NAME,
                TISSUES_FILE_NAME
        };
    }
}
