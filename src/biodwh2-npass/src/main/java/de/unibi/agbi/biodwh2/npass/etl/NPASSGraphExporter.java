package de.unibi.agbi.biodwh2.npass.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.npass.NPASSDataSource;
import de.unibi.agbi.biodwh2.npass.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class NPASSGraphExporter extends GraphExporter<NPASSDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(NPASSGraphExporter.class);
    public static final String SPECIES_LABEL = "Species";
    public static final String TARGET_LABEL = "Target";
    public static final String COMPOUND_LABEL = "Compound";
    public static final String ACTIVITY_LABEL = "Activity";

    public NPASSGraphExporter(final NPASSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, Structure> structureMap = new HashMap<>();
        final Map<Integer, List<Long>> taxIdNodeIdsMap = new HashMap<>();
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Collecting structures...");
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_structureInfo.txt", Structure.class,
                                        (entry) -> structureMap.put(entry.npId, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_structureInfo.txt'", e);
        }
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting species...");
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_speciesInfo.txt", Species.class,
                                        (entry) -> exportSpecies(graph, entry, taxIdNodeIdsMap));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_speciesInfo.txt'", e);
        }
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting compounds...");
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_generalInfo.txt", Compound.class,
                                        (entry) -> exportCompound(graph, entry, structureMap.remove(entry.npId)));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_generalInfo.txt'", e);
        }
        for (final String npId : structureMap.keySet()) {
            graph.addNodeFromModel(structureMap.get(npId), ID_KEY, npId);
        }
        exportTargets(workspace, graph, taxIdNodeIdsMap);
        exportSpeciesCompoundPairs(workspace, graph);
        exportActivities(workspace, graph, taxIdNodeIdsMap);
        return true;
    }

    private void exportSpecies(final Graph graph, final Species entry, final Map<Integer, List<Long>> taxIdNodeIdsMap) {
        final var node = graph.addNodeFromModel(entry);
        if (StringUtils.isNotEmpty(entry.orgTaxId) && !"n.a.".equalsIgnoreCase(entry.orgTaxId)) {
            taxIdNodeIdsMap.computeIfAbsent(Integer.parseInt(entry.orgTaxId), (k) -> new ArrayList<>()).add(
                    node.getId());
        }
    }

    private void exportCompound(final Graph graph, final Compound entry, final Structure structure) {
        final var builder = graph.buildNode(COMPOUND_LABEL).withModel(entry);
        builder.withPropertyIfNotNull("pubchem_cids", parsePubChemCIDs(entry.pubChemCid));
        if (structure != null) {
            builder.withModel(structure);
            if (!entry.prefName.equals(structure.inchiKey))
                builder.withProperty("preferred_name", entry.prefName);
        } else {
            builder.withProperty("preferred_name", entry.prefName);
        }
        builder.build();
    }

    static Integer[] parsePubChemCIDs(final String pubChemCid) {
        if (StringUtils.isEmpty(pubChemCid) || "n.a.".equalsIgnoreCase(pubChemCid))
            return null;
        final List<Integer> parts = new ArrayList<>();
        for (var part : StringUtils.split(pubChemCid, ";")) {
            part = StringUtils.strip(part, ", ");
            if (StringUtils.isNotEmpty(part))
                parts.add(Integer.parseInt(part));
        }
        return parts.isEmpty() ? null : parts.toArray(new Integer[0]);
    }

    private void exportTargets(final Workspace workspace, final Graph graph,
                               final Map<Integer, List<Long>> taxIdNodeIdsMap) {
        final Map<String, List<Target>> idTargetsMap = new HashMap<>();
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting targets...");
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_targetInfo.txt", Target.class,
                                        (entry) -> idTargetsMap.computeIfAbsent(entry.targetId,
                                                                                (k) -> new ArrayList<>()).add(entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_targetInfo.txt'", e);
        }
        for (final var entries : idTargetsMap.values())
            exportTarget(graph, entries, taxIdNodeIdsMap);
    }

    private void exportTarget(final Graph graph, final List<Target> entries,
                              final Map<Integer, List<Long>> taxIdNodeIdsMap) {
        final Set<Integer> organismTaxIds = new HashSet<>();
        final Set<String> uniProtIds = new HashSet<>();
        for (final var entry : entries) {
            if (StringUtils.isNotEmpty(entry.uniProtId) && !"n.a.".equalsIgnoreCase(entry.uniProtId)) {
                uniProtIds.add(entry.uniProtId);
            }
            if (StringUtils.isNotEmpty(entry.targetOrganismTaxId) && !"n.a.".equalsIgnoreCase(
                    entry.targetOrganismTaxId)) {
                organismTaxIds.add(Integer.parseInt(entry.targetOrganismTaxId));
            }
        }
        final var node = graph.addNodeFromModel(entries.get(0), "uniprot_ids", uniProtIds.toArray(new String[0]));
        for (final var organismTaxId : organismTaxIds) {
            final var speciesNodeIds = taxIdNodeIdsMap.get(organismTaxId);
            if (speciesNodeIds != null)
                for (final var speciesNodeId : speciesNodeIds)
                    graph.addEdge(node, speciesNodeId, "BELONGS_TO");
        }
    }

    private void exportSpeciesCompoundPairs(final Workspace workspace, final Graph graph) {
        graph.beginEdgeIndicesDelay("EXTRACTED_FROM");
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting species-compound relations...");
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_species_pair.txt", SpeciesPair.class,
                                        (entry) -> exportSpeciesPair(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_species_pair.txt'", e);
        }
        graph.endEdgeIndicesDelay("EXTRACTED_FROM");
    }

    private void exportSpeciesPair(final Graph graph, final SpeciesPair entry) {
        Long speciesNodeId = graph.findNodeId(SPECIES_LABEL, ID_KEY, entry.orgId);
        if (speciesNodeId == null) {
            speciesNodeId = graph.addNode(SPECIES_LABEL, ID_KEY, entry.orgId).getId();
        }
        Long compoundNodeId = graph.findNodeId(COMPOUND_LABEL, ID_KEY, entry.npId);
        if (compoundNodeId == null) {
            compoundNodeId = graph.addNode(COMPOUND_LABEL, ID_KEY, entry.npId).getId();
        }
        graph.addEdgeFromModel(compoundNodeId, speciesNodeId, entry);
    }

    private void exportActivities(final Workspace workspace, final Graph graph,
                                  final Map<Integer, List<Long>> taxIdNodeIdsMap) {
        graph.beginEdgeIndicesDelay("BELONGS_TO");
        graph.beginEdgeIndicesDelay("HAS_ACTIVITY");
        graph.beginEdgeIndicesDelay("TARGETS");
        try {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting activities...");
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_activities.txt", Activity.class,
                                        (entry) -> exportActivity(graph, entry, taxIdNodeIdsMap));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_activities.txt'", e);
        }
        graph.endEdgeIndicesDelay("BELONGS_TO");
        graph.endEdgeIndicesDelay("HAS_ACTIVITY");
        graph.endEdgeIndicesDelay("TARGETS");
    }

    private void exportActivity(final Graph graph, final Activity entry,
                                final Map<Integer, List<Long>> taxIdNodeIdsMap) {
        final var activityNodeId = graph.addNodeFromModel(entry);
        Long compoundNodeId = graph.findNodeId(COMPOUND_LABEL, ID_KEY, entry.npId);
        if (compoundNodeId == null) {
            compoundNodeId = graph.addNode(COMPOUND_LABEL, ID_KEY, entry.npId).getId();
        }
        final Long targetNodeId = graph.findNodeId(TARGET_LABEL, ID_KEY, entry.targetId);
        graph.addEdge(compoundNodeId, activityNodeId, "HAS_ACTIVITY");
        graph.addEdge(activityNodeId, targetNodeId, "TARGETS");
        if (StringUtils.isNotEmpty(entry.assayTaxId) && !"n.a.".equalsIgnoreCase(entry.assayTaxId)) {
            final var speciesNodeIds = taxIdNodeIdsMap.get(Integer.parseInt(entry.assayTaxId));
            if (speciesNodeIds != null)
                for (final var speciesNodeId : speciesNodeIds)
                    graph.addEdge(activityNodeId, speciesNodeId, "BELONGS_TO");
        }
    }
}
