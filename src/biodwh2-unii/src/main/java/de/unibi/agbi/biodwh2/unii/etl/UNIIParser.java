package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import de.unibi.agbi.biodwh2.unii.model.UNIIDataEntry;
import de.unibi.agbi.biodwh2.unii.model.UNIIEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipInputStream;

public class UNIIParser extends Parser<UNIIDataSource> {
    public UNIIParser(final UNIIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        parseNamesFile(workspace, dataSource);
        parseDataFile(workspace, dataSource);
        return true;
    }

    private void parseNamesFile(final Workspace workspace, final UNIIDataSource dataSource) throws ParserException {
        try {
            FileUtils.forEachZipEntryWithPrefix(workspace, dataSource, UNIIUpdater.UNIIS_FILE_NAME, "UNII_Names_",
                                                (stream, entry) -> dataSource.uniiEntries = parseZipStream(stream,
                                                                                                           UNIIEntry.class));
        } catch (Exception e) {
            throw new ParserFormatException("Failed to parse the file '" + UNIIUpdater.UNIIS_FILE_NAME + "'", e);
        }
    }

    private void parseDataFile(final Workspace workspace, final UNIIDataSource dataSource) throws ParserException {
        try {
            FileUtils.forEachZipEntryWithPrefix(workspace, dataSource, UNIIUpdater.UNII_DATA_FILE_NAME, "UNII_Records_",
                                                (stream, entry) -> {
                                                    final var dataEntries = parseZipStream(stream, UNIIDataEntry.class);
                                                    dataSource.uniiDataEntries = new HashMap<>();
                                                    for (final UNIIDataEntry dataEntry : dataEntries)
                                                        dataSource.uniiDataEntries.put(dataEntry.unii, dataEntry);
                                                });
        } catch (Exception e) {
            throw new ParserFormatException("Failed to parse the file '" + UNIIUpdater.UNII_DATA_FILE_NAME + "'", e);
        }
    }

    private <T> List<T> parseZipStream(final ZipInputStream zipInputStream,
                                       final Class<T> typeClass) throws IOException {
        return FileUtils.openSeparatedValuesFile(zipInputStream, typeClass, '\t', true).readAll();
    }
}
