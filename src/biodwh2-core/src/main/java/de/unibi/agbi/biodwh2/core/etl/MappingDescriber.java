package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;
import de.unibi.agbi.biodwh2.core.model.graph.PathMapping;
import de.unibi.agbi.biodwh2.core.model.graph.PathMappingDescription;

public abstract class MappingDescriber {
    private final DataSource dataSource;

    public MappingDescriber(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract NodeMappingDescription[] describe(final Graph graph, final Node node,
                                                      final String localMappingLabel);

    public abstract PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges);

    protected abstract String[] getNodeMappingLabels();

    final String prefixLabel(final String label) {
        return dataSource.getId() + Graph.LABEL_PREFIX_SEPARATOR + label;
    }

    protected abstract PathMapping[] getEdgePathMappings();

    final String getDataSourceId() {
        return dataSource.getId();
    }
}
