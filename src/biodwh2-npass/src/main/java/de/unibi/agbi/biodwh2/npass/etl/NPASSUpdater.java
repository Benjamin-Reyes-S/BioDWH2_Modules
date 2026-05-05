package de.unibi.agbi.biodwh2.npass.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.npass.NPASSDataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NPASSUpdater extends Updater<NPASSDataSource> {
    private static final String VERSION_URL = "https://bidd.group/NPASS/downloadnpass.html";
    private static final Pattern VERSION_PATTERN = Pattern.compile("/NPASSv([0-9]+)\\.([0-9]+)");

    private final List<String> downloadUrls = new ArrayList<>();

    public NPASSUpdater(final NPASSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final var source = getWebsiteSource(VERSION_URL);
        final Document document = Jsoup.parse(source);
        Version newestVersion = null;
        for (final var link : document.getElementsByTag("a")) {
            final var path = link.attr("href");
            final var matcher = VERSION_PATTERN.matcher(path);
            if (matcher.find()) {
                downloadUrls.add(path);
                final var version = new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                if (newestVersion == null || version.compareTo(newestVersion) > 0) {
                    newestVersion = version;
                }
            }
        }
        if (newestVersion != null) {
            final var search = "/NPASSv" + newestVersion.major + "." + newestVersion.minor;
            for (int i = downloadUrls.size() - 1; i >= 0; i--) {
                if (!downloadUrls.get(i).contains(search))
                    downloadUrls.remove(i);
            }
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final var path : downloadUrls) {
            final var startIndex = path.indexOf("download_") + "download_".length();
            downloadFileAsBrowser(workspace, "https://bidd.group/NPASS/" + path, path.substring(startIndex));
        }
        return true;
    }
}
