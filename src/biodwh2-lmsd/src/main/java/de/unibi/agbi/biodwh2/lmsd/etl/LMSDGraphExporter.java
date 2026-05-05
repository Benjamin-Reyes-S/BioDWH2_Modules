package de.unibi.agbi.biodwh2.lmsd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.lmsd.LMSDDataSource;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class LMSDGraphExporter extends GraphExporter<LMSDDataSource> {
    static final String LIPID_LABEL = "Lipid";
    static final String LIPID_CLASSIFICATION_LABEL = "LipidClassification";

    public LMSDGraphExporter(final LMSDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(LIPID_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(LIPID_CLASSIFICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        try {
            FileUtils.forEachZipEntryWithSuffix(workspace, dataSource, LMSDUpdater.FILE_NAME, ".sdf",
                                                (stream, zipEntry) -> {
                                                    final var reader = new SdfReader(stream, StandardCharsets.UTF_8);
                                                    for (final var entry : reader) {
                                                        exportEntry(graph, entry);
                                                    }
                                                });
        } catch (final Exception e) {
            throw new ExporterFormatException("Failed to export '" + LMSDUpdater.FILE_NAME + "'", e);
        }
        return true;
    }

    private void exportEntry(final Graph graph, final SdfEntry entry) {
        final var builder = graph.buildNode(LIPID_LABEL);
        builder.withProperty(ID_KEY, entry.properties.get("LM_ID"));
        builder.withPropertyIfNotNull("exact_mass", entry.properties.get("EXACT_MASS"));
        builder.withPropertyIfNotNull("smiles", entry.properties.get("SMILES"));
        builder.withPropertyIfNotNull("formula", entry.properties.get("FORMULA"));
        builder.withPropertyIfNotNull("name", entry.properties.get("NAME"));
        builder.withPropertyIfNotNull("abbreviation", entry.properties.get("ABBREVIATION"));
        builder.withPropertyIfNotNull("inchi", entry.properties.get("INCHI"));
        builder.withPropertyIfNotNull("inchi_key", entry.properties.get("INCHI_KEY"));
        builder.withPropertyIfNotNull("systematic_name", entry.properties.get("SYSTEMATIC_NAME"));
        final String synonyms = entry.properties.get("SYNONYMS");
        if (synonyms != null)
            builder.withPropertyIfNotNull("synonyms", StringUtils.splitByWholeSeparator(synonyms, "; "));
        builder.withPropertyIfNotNull("pubchem_cid", entry.properties.get("PUBCHEM_CID"));
        builder.withPropertyIfNotNull("chebi_id", entry.properties.get("CHEBI_ID"));
        builder.withPropertyIfNotNull("hmdb_id", entry.properties.get("HMDB_ID"));
        builder.withPropertyIfNotNull("plantfa_id", entry.properties.get("PLANTFA_ID"));
        builder.withPropertyIfNotNull("swisslipids_id", entry.properties.get("SWISSLIPIDS_ID"));
        builder.withPropertyIfNotNull("lipidbank_id", entry.properties.get("LIPIDBANK_ID"));
        builder.withPropertyIfNotNull("kegg_id", entry.properties.get("KEGG_ID"));
        final Node node = builder.build();
        final Classification categoryId = parseClassification(entry.properties.get("CATEGORY"));
        final Classification mainClassId = parseClassification(entry.properties.get("MAIN_CLASS"));
        final Classification subClassId = parseClassification(entry.properties.get("SUB_CLASS"));
        final Classification classLevel4Id = parseClassification(entry.properties.get("CLASS_LEVEL4"));
        Long categoryNode = graph.findNodeId(LIPID_CLASSIFICATION_LABEL, ID_KEY, categoryId.id);
        if (categoryNode == null)
            categoryNode = graph.addNode(LIPID_CLASSIFICATION_LABEL, ID_KEY, categoryId.id, "name", categoryId.name)
                                .getId();
        Long mainClassNode = graph.findNodeId(LIPID_CLASSIFICATION_LABEL, ID_KEY, mainClassId.id);
        if (mainClassNode == null) {
            mainClassNode = graph.addNode(LIPID_CLASSIFICATION_LABEL, ID_KEY, mainClassId.id, "name", mainClassId.name)
                                 .getId();
            graph.addEdge(mainClassNode, categoryNode, "CHILD_OF");
        }
        if (subClassId != null) {
            Long subClassNode = graph.findNodeId(LIPID_CLASSIFICATION_LABEL, ID_KEY, subClassId.id);
            if (subClassNode == null) {
                subClassNode = graph.addNode(LIPID_CLASSIFICATION_LABEL, ID_KEY, subClassId.id, "name", subClassId.name)
                                    .getId();
                graph.addEdge(subClassNode, mainClassNode, "CHILD_OF");
            }
            if (classLevel4Id != null) {
                Long classLevel4Node = graph.findNodeId(LIPID_CLASSIFICATION_LABEL, ID_KEY, classLevel4Id.id);
                if (classLevel4Node == null) {
                    classLevel4Node = graph.addNode(LIPID_CLASSIFICATION_LABEL, ID_KEY, classLevel4Id.id, "name",
                                                    classLevel4Id.name).getId();
                    graph.addEdge(classLevel4Node, subClassNode, "CHILD_OF");
                }
                graph.addEdge(node, classLevel4Node, "BELONGS_TO");
            } else {
                graph.addEdge(node, subClassNode, "BELONGS_TO");
            }
        } else {
            graph.addEdge(node, mainClassNode, "BELONGS_TO");
        }
    }

    private Classification parseClassification(final String value) {
        if (value == null)
            return null;
        final String[] parts = StringUtils.splitByWholeSeparator(value, " [", 2);
        final var classification = new Classification();
        classification.id = parts[1].replace("]", "").trim();
        classification.name = parts[0].trim();
        return classification;
    }

    private static class Classification {
        public String id;
        public String name;
    }
}
