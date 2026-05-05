package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USDAPlantsUpdater extends Updater<USDAPlantsDataSource> {
    private static final String VERSION_URL = "https://plants.usda.gov/release-notes";
    private static final String DOWNLOAD_URL = "https://plants.usda.gov/assets/docs/CompletePLANTSList/plantlst.txt";
    private static final String MAIN_JS_URL_PREFIX = "https://plants.usda.gov/";
    static final String PLANT_LIST_FILE_NAME = "plantlst.txt";

    private static final Pattern MAIN_JS_PATTERN = Pattern.compile("src=\"(main-[a-z0-9A-Z]+\\.js)\"");
    private static final Pattern CHUNK_JS_PATTERN = Pattern.compile("chunk-[a-z0-9A-Z]+\\.js");
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "version:\"([0-9]+\\.[0-9]+\\.[0-9]+(?:\\.[0-9]+)?)\"");

    public USDAPlantsUpdater(final USDAPlantsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final var source = getWebsiteSource(VERSION_URL, 3);
        final Matcher mainJSMatcher = MAIN_JS_PATTERN.matcher(source);
        if (mainJSMatcher.find()) {
            final Set<String> remainingChunks = new HashSet<>();
            remainingChunks.add(mainJSMatcher.group(1));
            final Set<String> visitedChunks = new HashSet<>();
            while (!remainingChunks.isEmpty()) {
                final var fileName = remainingChunks.iterator().next();
                remainingChunks.remove(fileName);
                visitedChunks.add(fileName);
                final var version = collectChunks(fileName, remainingChunks, visitedChunks);
                if (version != null)
                    return version;
            }
        } else {
            throw new UpdaterMalformedVersionException("Failed to retrieve versions");
        }
        return null;
    }

    private Version collectChunks(final String fileName, final Set<String> remaining,
                                  final Set<String> visited) throws UpdaterConnectionException {
        final var source = getWebsiteSource(MAIN_JS_URL_PREFIX + fileName);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find())
            return Version.parse(matcher.group(1));
        final var chunkJSMatcher = CHUNK_JS_PATTERN.matcher(source);
        while (chunkJSMatcher.find()) {
            final var chunkFilename = chunkJSMatcher.group(0);
            if (!visited.contains(chunkFilename)) {
                remaining.add(chunkFilename);
            }
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, PLANT_LIST_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{PLANT_LIST_FILE_NAME};
    }
}
