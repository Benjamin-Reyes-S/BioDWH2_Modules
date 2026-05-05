package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;

public class CanadianNutrientFileUpdater extends Updater<CanadianNutrientFileDataSource> {
    static final String FILE_NAME = "cnf-fcen-csv.zip";
    private static final String CNF_DOWNLOAD_URL =
            "https://www.canada.ca/content/dam/hc-sc/migration/hc-sc/fn-an/alt_formats/zip/nutrition/fiche-nutri-data/" +
            FILE_NAME;

    public CanadianNutrientFileUpdater(final CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final var dateTime = HTTPClient.peekZipModificationDateTime(CNF_DOWNLOAD_URL);
            if (dateTime != null)
                return new Version(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version", e);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, CNF_DOWNLOAD_URL, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
