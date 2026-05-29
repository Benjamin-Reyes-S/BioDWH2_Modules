package de.unibi.agbi.biodwh2.ncbi.etl;

// import java.io.BufferedReader;
import java.io.IOException;
// import java.io.InputStreamReader;
// import java.net.HttpURLConnection;
// import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;

public class NCBIUpdater extends MultiFileFTPWebUpdater<NCBIDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(NCBIUpdater.class);

    public NCBIUpdater(NCBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.ncbi.nih.gov/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        // final String genePrefix = "gene/DATA/";
        // final String proteinPrefix = "refseq/release/vertebrate_mammalian/";
        final String taxonPrefix = "pub/taxonomy/";

        List<String> files = new ArrayList<>();

        Collections.addAll(files,
                // genePrefix + "gene_group.gz",
                // genePrefix + "gene_history.gz",
                // genePrefix + "gene2accession.gz",
                // genePrefix + "gene_info.gz",
                // genePrefix + "gene_neighbors.gz",
                // genePrefix + "gene_orthologs.gz",
                // genePrefix + "gene_refseq_uniprotkb_collab.gz",
                // genePrefix + "gene2ensembl.gz",
                // genePrefix + "gene2go.gz",
                // genePrefix + "gene2pubmed.gz",
                // genePrefix + "go_process.dtd",
                // genePrefix + "go_process.xml",
                // genePrefix + "mim2gene_medgen",
                // genePrefix + "stopwords_gene",
                // genePrefix + "README",
                // genePrefix + "README_ensembl",
                taxonPrefix + "taxdump.tar.gz"
        );

        /* PROTEIN FILE DISCOVERY — commented out, not needed for taxon-only build
        try {
            String directoryUrl = getFTPIndexUrl() + proteinPrefix;
            HttpURLConnection connection = (HttpURLConnection) new URL(directoryUrl).openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                Pattern pattern = Pattern.compile(
                        "vertebrate_mammalian\\.\\d+\\.protein\\.gpff\\.gz");
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String filename = matcher.group();
                        files.add(proteinPrefix + filename);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to list protein files from FTP directory", e);
        }
        /**/

        LOGGER.info("Downloading " + files.size() + " files total");
        return files.toArray(new String[0]);
    }
}