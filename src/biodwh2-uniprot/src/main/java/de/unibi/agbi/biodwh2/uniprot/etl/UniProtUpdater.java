package de.unibi.agbi.biodwh2.uniprot.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniProtUpdater extends MultiFileFTPWebUpdater<UniProtDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(UniProtUpdater.class);
    static final String SPROT_FILE_NAME = "uniprot_sprot.xml.gz";
    static final String TREMBL_FILE_NAME = "uniprot_trembl.xml.gz";

    public UniProtUpdater(final UniProtDataSource dataSource) {
        super(dataSource);
    }


    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {

        //Check if TrEMBL already downloaded since its heavy and takes long (>30min)
        final File tremblPath = dataSource.resolveSourceFilePath(workspace, TREMBL_FILE_NAME).toFile();
        if (tremblPath.exists() && tremblPath.length() > 0) {
            LOGGER.info("TrEMBL file already downloaded: {}", tremblPath.getAbsolutePath());
            return new String[]{SPROT_FILE_NAME};
        }

        return new String[]{SPROT_FILE_NAME, TREMBL_FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{SPROT_FILE_NAME, TREMBL_FILE_NAME};
    }
}
