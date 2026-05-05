package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.model.WorkspaceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphRecon {
    private static final Logger LOGGER = LogManager.getLogger(GraphRecon.class);

    public void recon(final Workspace workspace, final DataSource[] dataSources) throws ReconException {
        final long start = System.currentTimeMillis();
        // TODO: reuse previous state
        try (final Graph graph = new Graph(workspace.getFilePath(WorkspaceFileType.RECON_GRAPH), false)) {
            graph.addIndex(IndexDescription.forNode(ReconExporter.IDENTIFIER_LABEL, ReconExporter.ID_KEY,
                                                    IndexDescription.Type.UNIQUE));
            graph.addIndex(IndexDescription.forNode(ReconExporter.SYNONYM_LABEL, ReconExporter.NAME_KEY,
                                                    IndexDescription.Type.UNIQUE));
            graph.addIndex(IndexDescription.forNode(ReconExporter.STRUCTURE_LABEL, ReconExporter.INCHI_KEY,
                                                    IndexDescription.Type.UNIQUE));
            graph.addIndex(IndexDescription.forNode(ReconExporter.STRUCTURE_LABEL, ReconExporter.MOL_KEY,
                                                    IndexDescription.Type.UNIQUE));
            for (final var dataSource : dataSources) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Recon of data source '{}' started", dataSource.getId());
                dataSource.getReconExporter().recon(workspace, graph);
            }
        } catch (final Exception ex) {
            throw new ReconException(ex);
        }
        final long stop = System.currentTimeMillis();
        LOGGER.info("Recon finished within {}", DurationFormatUtils.formatDuration(stop - start, "HH:mm:ss.S"));
    }
}
