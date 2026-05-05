package de.unibi.agbi.biodwh2.lmsd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.lmsd.LMSDDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LMSDUpdater extends Updater<LMSDDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final String VERSION_URL = "https://www.lipidmaps.org/databases/lmsd/download";
    private static final String DOWNLOAD_URL = "https://www.lipidmaps.org/files/?file=LMSD&ext=sdf.zip";
    static final String FILE_NAME = "sdf.zip";

    public LMSDUpdater(final LMSDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, FILE_NAME);
        return true;
    }
}
