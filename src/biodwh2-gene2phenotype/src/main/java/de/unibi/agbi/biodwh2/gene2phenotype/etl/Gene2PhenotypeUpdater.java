package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.gene2phenotype.Gene2PhenotypeDataSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gene2PhenotypeUpdater extends Updater<Gene2PhenotypeDataSource> {
    private static final String FTP_URL = "https://ftp.ebi.ac.uk/pub/databases/gene2phenotype/G2P_data_downloads/";
    private static final Pattern DATE_PATTERN = Pattern.compile("([0-9]{4})_([0-9]{2})_([0-9]{2})/");
    static final String[] FILE_NAMES = new String[]{
            "CancerG2P.csv.gz", "CardiacG2P.csv.gz", "DDG2P.csv.gz", "EyeG2P.csv.gz", "HearingLossG2P.csv.gz",
            "SkeletalG2P.csv.gz", "SkinG2P.csv.gz"
    };
    private String ftpVersionPath;

    public Gene2PhenotypeUpdater(final Gene2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final var client = new HTTPFTPClient(FTP_URL);
        Version newestVersion = null;
        try {
            for (final var entry : client.listDirectory()) {
                final Matcher matcher = DATE_PATTERN.matcher(entry.name);
                if (matcher.matches()) {
                    final Version version = new Version(Integer.parseInt(matcher.group(1)),
                                                        Integer.parseInt(matcher.group(2)),
                                                        Integer.parseInt(matcher.group(3)));
                    if (newestVersion == null || newestVersion.compareTo(version) < 0) {
                        newestVersion = version;
                        ftpVersionPath = entry.fullUrl;
                    }
                }
            }
        } catch (final IOException e) {
            throw new UpdaterException(e);
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final var client = new HTTPFTPClient(ftpVersionPath);
        try {
            for (final var entry : client.listDirectory()) {
                if (entry.name.endsWith(".csv.gz")) {
                    downloadFileAsBrowser(workspace, entry.fullUrl,
                                          entry.name.substring(0, entry.name.lastIndexOf('_')) + ".csv.gz");
                }
            }
        } catch (final IOException e) {
            throw new UpdaterException(e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return FILE_NAMES;
    }
}
