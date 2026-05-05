package de.unibi.agbi.biodwh2.swisslipids.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.swisslipids.SwissLipidsDataSource;
import de.unibi.agbi.biodwh2.swisslipids.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwissLipidsGraphExporter extends GraphExporter<SwissLipidsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(SwissLipidsGraphExporter.class);
    static final String EVIDENCE_LABEL = "Evidence";
    static final String ENZYME_LABEL = "Enzyme";
    static final String LIPID_LABEL = "Lipid";
    static final String REACTION_LABEL = "Reaction";

    public SwissLipidsGraphExporter(final SwissLipidsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(EVIDENCE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ENZYME_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(LIPID_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REACTION_LABEL, "rhea_id", IndexDescription.Type.NON_UNIQUE));
        exportEvidences(workspace, graph);
        exportEnzymes(workspace, graph);
        final Map<String, LipidMapping> lipidMapping = collectLipidsToUniProt(workspace);
        exportLipids(workspace, graph, lipidMapping);
        exportTissues(workspace, graph);
        exportGOTerms(workspace, graph);
        return true;
    }

    private void exportEvidences(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting evidences...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, SwissLipidsUpdater.EVIDENCES_FILE_NAME,
                                            Evidence.class, (entry) -> exportEvidence(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export evidences", e);
        }
    }

    private void exportEvidence(final Graph graph, final Evidence entry) {
        final Node node = graph.addNode(EVIDENCE_LABEL, ID_KEY, entry.id, "pmid", entry.pmid, "figure_legend",
                                        entry.figureLegend);
        if (StringUtils.isNotEmpty(entry.ecoId))
            graph.addEdge(node, getOrCreateOntologyProxyTerm(graph, entry.ecoId), "OF_TYPE");
    }

    private void exportEnzymes(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting enzymes...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, SwissLipidsUpdater.ENZYMES_FILE_NAME, Enzyme.class,
                                            (entry) -> exportEnzyme(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export enzymes", e);
        }
    }

    private void exportEnzyme(final Graph graph, final Enzyme entry) {
        // TODO: remove fix, once TSV format has been corrected
        if (StringUtils.isNotEmpty(entry.overflowColumn)) {
            entry.reactionText = entry.evidenceTagId;
            entry.evidenceTagId = entry.overflowColumn;
        }
        Node node = graph.findNode(ENZYME_LABEL, ID_KEY, entry.id);
        if (node == null) {
            node = graph.addNode(ENZYME_LABEL, ID_KEY, entry.id, "gene_name", entry.geneName, "uniprot_accessions",
                                 StringUtils.split(entry.uniprotAccessions, '-'));
            graph.addEdge(node, getOrCreateTaxonNode(graph, entry.proteinTaxon), "BELONGS_TO");
        }
        final Node reactionNode = graph.addNode(REACTION_LABEL, "rhea_id", entry.rheaId, "text", entry.reactionText);
        graph.addEdge(node, reactionNode, "CATALYZES");
        connectEvidence(graph, reactionNode, entry.evidenceTagId);
    }

    private Long getOrCreateTaxonNode(final Graph graph, final Integer ncbiTaxId) {
        return getOrCreateOntologyProxyTerm(graph, "NCBITaxon:" + ncbiTaxId);
    }

    private void connectEvidence(final Graph graph, final Node node, final String evidenceIds) {
        for (final String evidenceId : StringUtils.splitByWholeSeparator(evidenceIds, " | ")) {
            final Long evidenceNode = graph.findNodeId(EVIDENCE_LABEL, ID_KEY, Integer.parseInt(evidenceId));
            if (evidenceNode != null)
                graph.addEdge(node, evidenceNode, "HAS_EVIDENCE");
        }
    }

    private Map<String, LipidMapping> collectLipidsToUniProt(final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting lipid UniProt mappings...");
        final Map<String, LipidMapping> result = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, SwissLipidsUpdater.LIPIDS_FILE_NAME,
                                            LipidToUniprot.class, (entry) -> collectLipidToUniProt(entry, result));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export collect lipid UniProt mappings", e);
        }
        return result;
    }

    private void collectLipidToUniProt(final LipidToUniprot entry, final Map<String, LipidMapping> lipidMapping) {
        final LipidMapping mapping = new LipidMapping();
        mapping.uniprotIds = entry.uniprotIds;
        mapping.mappingLevel = entry.mappingLevel;
        lipidMapping.put(entry.metaboliteId, mapping);
    }

    private void exportLipids(final Workspace workspace, final Graph graph,
                              final Map<String, LipidMapping> lipidMapping) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting lipids...");
        final Map<Long, String> lipidParentMap = new HashMap<>();
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, SwissLipidsUpdater.LIPIDS_FILE_NAME, Lipid.class,
                                            (entry) -> exportLipid(graph, entry, lipidMapping.get(entry.id),
                                                                   lipidParentMap));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export lipids", e);
        }
        for (final Long nodeId : lipidParentMap.keySet()) {
            final Long parentNodeId = graph.findNodeId(LIPID_LABEL, ID_KEY, lipidParentMap.get(nodeId));
            graph.addEdge(nodeId, parentNodeId, "CHILD_OF");
        }
    }

    private void exportLipid(final Graph graph, final Lipid entry, final LipidMapping lipidMapping,
                             final Map<Long, String> lipidParentMap) {
        final var builder = graph.buildNode().withLabel(LIPID_LABEL).withProperty(ID_KEY, entry.id);
        builder.withPropertyIfNotNull("name", entry.name);
        builder.withPropertyIfNotNull("level", entry.level);
        builder.withPropertyIfNotNull("abbreviation", entry.abbreviation);
        builder.withPropertyIfNotNull("synonyms", entry.synonyms);
        builder.withPropertyIfNotNull("lipid_class", entry.lipidClass);
        builder.withPropertyIfNotNull("components", entry.components);
        builder.withPropertyIfNotNull("chebi", entry.chebi);
        builder.withPropertyIfNotNull("lipid_maps", entry.lipidMaps);
        builder.withPropertyIfNotNull("hmdb", entry.hmdb);
        builder.withPropertyIfNotNull("metanetx", entry.metaNetX);
        builder.withPropertyIfNotNull("pmids", entry.pmids);
        builder.withPropertyIfNotNull("smiles", entry.smilespH7_3);
        builder.withPropertyIfNotNull("inchi", entry.inchipH7_3);
        builder.withPropertyIfNotNull("inchi_key", entry.inchiKeypH7_3);
        builder.withPropertyIfNotNull("formula", entry.formulapH7_3);
        builder.withPropertyIfNotNull("charge", entry.chargepH7_3);
        builder.withPropertyIfNotNull("mass", entry.masspH7_3);
        builder.withPropertyIfNotNull("exact_mass", entry.exactMassNeutralForm);
        builder.withPropertyIfNotNull("uniprot_accessions", lipidMapping.uniprotIds);
        builder.withPropertyIfNotNull("uniprot_mapping_level", lipidMapping.mappingLevel);
        // TODO: exactMZofM, exactMZofMHPlus, exactMZofMK, exactMZofMNa, exactMZofMLi, exactMZofMNH4,
        //  exactMZofMHMinus, exactMZofMCl, exactMZofMOAc
        final Node node = builder.build();
        if (StringUtils.isNotEmpty(entry.parent))
            lipidParentMap.put(node.getId(), entry.parent);
    }

    private void exportTissues(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting tissues...");
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, SwissLipidsUpdater.TISSUES_FILE_NAME, Tissue.class,
                                            (entry) -> exportTissue(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export tissues", e);
        }
    }

    private void exportTissue(final Graph graph, final Tissue entry) {
        final Long tissueNode = getOrCreateOntologyProxyTerm(graph, entry.tissueCellId);
        final Node lipidNode = graph.findNode(LIPID_LABEL, ID_KEY, entry.lipidId);
        final Node associationNode = graph.addNode("TissueAssociation");
        graph.addEdge(associationNode, tissueNode, "HAS_TISSUE");
        graph.addEdge(lipidNode, associationNode, "ASSOCIATED_WITH");
        graph.addEdge(lipidNode, getOrCreateTaxonNode(graph, entry.taxonId), "BELONGS_TO");
        connectEvidence(graph, associationNode, entry.evidenceTagId);
    }

    private void exportGOTerms(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting GO terms...");
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, SwissLipidsUpdater.GO_FILE_NAME, GO.class,
                                        (entry) -> exportGOTerm(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export GO terms", e);
        }
    }

    private void exportGOTerm(final Graph graph, final GO entry) {
        final Long termNode = getOrCreateOntologyProxyTerm(graph, entry.goId);
        final Node lipidNode = graph.findNode(LIPID_LABEL, ID_KEY, entry.lipidId);
        final Node associationNode = graph.addNode("GOAssociation");
        graph.addEdge(associationNode, termNode, "HAS_TERM");
        graph.addEdge(lipidNode, associationNode, "ASSOCIATED_WITH");
        graph.addEdge(lipidNode, getOrCreateTaxonNode(graph, entry.taxonId), "BELONGS_TO");
        connectEvidence(graph, associationNode, entry.evidenceTagId);
    }

    private static class LipidMapping {
        public String uniprotIds;
        public String mappingLevel;
    }
}
