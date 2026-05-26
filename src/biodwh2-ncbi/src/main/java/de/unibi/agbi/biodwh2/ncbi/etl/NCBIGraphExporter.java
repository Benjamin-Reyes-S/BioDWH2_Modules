package de.unibi.agbi.biodwh2.ncbi.etl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;
import de.unibi.agbi.biodwh2.ncbi.model.GeneAccession;
import de.unibi.agbi.biodwh2.ncbi.model.GeneGo;
import de.unibi.agbi.biodwh2.ncbi.model.GeneInfo;
import de.unibi.agbi.biodwh2.ncbi.model.GeneRelationship;
import de.unibi.agbi.biodwh2.ncbi.model.ProteinRecord;
import de.unibi.agbi.biodwh2.ncbi.parser.NCBITaxonParser;

public class NCBIGraphExporter extends GraphExporter<NCBIDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(NCBIGraphExporter.class);

    private Map<Long, Long> geneIdNodeIdMap;
    private Map<String, Long> proteinIdNodeIdMap;
    private Map<String, Long> taxonIdNodeIdMap;

    public NCBIGraphExporter(final NCBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 15;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Taxon", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Gene", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Protein", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Accession", "protein_accession.version",
                                                IndexDescription.Type.NON_UNIQUE));

        taxonIdNodeIdMap = new HashMap<>();
        geneIdNodeIdMap = new HashMap<>();
        proteinIdNodeIdMap = new HashMap<>();

        try {
            exportTaxonDatabase(workspace, dataSource, graph);
            LOGGER.info("Exported TaxonDatabase :D...");

            //exportGeneDatabase(workspace, dataSource, graph);   
            /* 
            final NCBIProteinParser proteinParser = new NCBIProteinParser();
            proteinParser.readFile(workspace, dataSource, protein -> {
                exportProteinRecord(protein, graph);
            });
*/
        } catch (IOException e) {
            throw new ExporterException("Failed to export NCBI database", e);
        }

        return true;
    }

    // TAXON EXPORTER
    private void exportTaxonDatabase(final Workspace workspace, final DataSource dataSource,
                                     final Graph graph) throws IOException {
        LOGGER.info("Exporting taxdump.tar.gz...");
        final NCBITaxonParser taxonParser = new NCBITaxonParser();
        final Map<String, Map<String, List<String>>> taxonPropertiesByTaxId = taxonParser.parseNames(workspace,
                                                                                                      dataSource);
        taxonParser.parseNodes(workspace, dataSource, taxonPropertiesByTaxId);

        for (final Map.Entry<String, Map<String, List<String>>> taxonEntry : taxonPropertiesByTaxId.entrySet()) {
            final String taxId = taxonEntry.getKey();
            final Node taxonNode = graph.addNode("Taxon");
            taxonNode.setProperty("id", taxId);

            for (final Map.Entry<String, List<String>> propertyEntry : taxonEntry.getValue().entrySet())
                setTaxonProperty(taxonNode, propertyEntry.getKey(), propertyEntry.getValue());

            taxonIdNodeIdMap.put(taxId, taxonNode.getId());
            graph.update(taxonNode);
        }
        for (final Map.Entry<String, Map<String, List<String>>> taxonEntry : taxonPropertiesByTaxId.entrySet()) {
                    final String taxId = taxonEntry.getKey();
                    final List<String> parentTaxIds = taxonEntry.getValue().get("parent_tax_id");
                    if (parentTaxIds == null || parentTaxIds.isEmpty())
                        continue;

                    final String parentTaxId = parentTaxIds.get(0);
                    if (taxId.equals(parentTaxId))
                        continue;
                    LOGGER.info("Match taxId with parentalTaxId found");
                    final Long taxonNodeId = taxonIdNodeIdMap.get(taxId);
                    final Long parentTaxonNodeId = taxonIdNodeIdMap.get(parentTaxId);
                    if (taxonNodeId == null || parentTaxonNodeId == null)
                        continue;

                    final Edge edge = graph.addEdge(parentTaxonNodeId,taxonNodeId, "PARENT_OF");
                    edge.setProperty("type", "parent");
                    graph.update(edge);
                }
    }

    // GENES EXPORTER
    private void exportGeneDatabase(final Workspace workspace, final DataSource dataSource,
                                    final Graph graph) throws IOException {
        LOGGER.info("Exporting gene_info.gz...");
        MappingIterator<GeneInfo> geneInfos = FileUtils.openGzipTsv(workspace, dataSource, "gene_info.gz",
                                                                    GeneInfo.class);
        while (geneInfos.hasNext()) {
            GeneInfo geneInfo = geneInfos.next();
            if (!geneInfo.taxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(geneInfo.geneId);
            Node geneNode = graph.addNode("Gene");
            geneNode.setProperty("id", geneId);
            setPropertyIfNotDash(geneNode, "symbol", geneInfo.symbol);
            setPropertyIfNotDash(geneNode, "chromosome", geneInfo.chromosome);
            setPropertyIfNotDash(geneNode, "locus_tag", geneInfo.locusTag);
            setPropertyIfNotDash(geneNode, "type", geneInfo.typeOfGene);
            setPropertyIfNotDash(geneNode, "description", geneInfo.description);
            setArrayPropertyIfNotDash(geneNode, "synonyms", geneInfo.synonyms);
            setArrayPropertyIfNotDash(geneNode, "xrefs", geneInfo.dbXrefs);
            setArrayPropertyIfNotDash(geneNode, "feature_types", geneInfo.featureType);
            setPropertyIfNotDash(geneNode, "nomenclature_status", geneInfo.nomenclatureStatus);
            setArrayPropertyIfNotDash(geneNode, "other_designations", geneInfo.otherDesignations);
            geneIdNodeIdMap.put(geneId, geneNode.getId());
            graph.update(geneNode);
        }

        LOGGER.info("Exporting gene2accession.gz...");
        MappingIterator<GeneAccession> accessions = FileUtils.openGzipTsv(workspace, dataSource, "gene2accession.gz",
                                                                          GeneAccession.class);

        while (accessions.hasNext()) {
            GeneAccession accession = accessions.next();

            if (!accession.taxonomyId.equals("9606"))
                continue;

            long geneId = Long.parseLong(accession.geneId);
            Node accessionNode = createAccessionNode(graph, accession);
            graph.addEdge(geneIdNodeIdMap.get(geneId), accessionNode, "HAS_ACCESSION");
        }

        LOGGER.info("Exporting gene2go.gz...");
        MappingIterator<GeneGo> goAnnotations = FileUtils.openGzipTsv(workspace, dataSource, "gene2go.gz",
                                                                      GeneGo.class);

        while (goAnnotations.hasNext()) {
            GeneGo go = goAnnotations.next();

            if (!go.taxonomyId.equals("9606"))
                continue;

            long geneId = Long.parseLong(go.geneId);

            Node goTermNode = graph.findNode("GoTerm", "id", go.goId);
            if (goTermNode == null) {
                goTermNode = graph.addNode("GoTerm");
                goTermNode.setProperty("id", go.goId);
                goTermNode.setProperty("category", go.category);
                goTermNode.setProperty("term", go.goTerm);
                graph.update(goTermNode);
            }

            Edge edge = graph.addEdge(geneIdNodeIdMap.get(geneId), goTermNode, "HAS_GO_TERM");
            setPropertyIfNotDash(edge, "evidence", go.evidence);
            setPropertyIfNotDash(edge, "qualifier", go.qualifier);
            setArrayPropertyIfNotDash(edge, "pubmed_ids", go.pubMedIds);
            graph.update(edge);
        }

        LOGGER.info("Exporting gene_group.gz...");
        MappingIterator<GeneRelationship> groups = FileUtils.openGzipTsv(workspace, dataSource, "gene_group.gz",
                                                                         GeneRelationship.class);

        while (groups.hasNext()) {
            GeneRelationship group = groups.next();
            if (!group.taxonomyId.equals("9606") || !group.otherTaxonomyId.equals("9606"))
                continue;

            long geneId = Long.parseLong(group.geneId);
            long otherGeneId = Long.parseLong(group.otherGeneId);

            Long geneNodeId = geneIdNodeIdMap.get(geneId);
            Long otherGeneNodeId = geneIdNodeIdMap.get(otherGeneId);

            if (geneNodeId == null || otherGeneNodeId == null)
                continue;

            Edge edge = graph.addEdge(geneNodeId, otherGeneNodeId, "RELATED_TO");
            edge.setProperty("type", group.relationship);
            graph.update(edge);
        }

        LOGGER.info("Exporting gene_orthologs.gz...");
        MappingIterator<GeneRelationship> orthologs = FileUtils.openGzipTsv(workspace, dataSource,
                                                                            "gene_orthologs.gz",
                                                                            GeneRelationship.class);

        while (orthologs.hasNext()) {
            GeneRelationship ortholog = orthologs.next();
            if (!ortholog.taxonomyId.equals("9606") || !ortholog.otherTaxonomyId.equals("9606"))
                continue;

            long geneId = Long.parseLong(ortholog.geneId);
            long otherGeneId = Long.parseLong(ortholog.otherGeneId);

            Long geneNodeId = geneIdNodeIdMap.get(geneId);
            Long otherGeneNodeId = geneIdNodeIdMap.get(otherGeneId);

            if (geneNodeId == null || otherGeneNodeId == null)
                continue;

            Edge edge = graph.addEdge(geneNodeId, otherGeneNodeId, "RELATED_TO");
            edge.setProperty("type", ortholog.relationship);
            graph.update(edge);
        }
    }

    // PROTEIN EXPORTER
    private void exportProteinRecord(final ProteinRecord protein,
                                    final Graph graph) {
        if (protein == null || protein.getProteinId() == null) {
            return;
        }

        String proteinId = protein.getProteinId();
        String version = protein.getVersion();

        Node proteinNode = graph.addNode("Protein");
        proteinNode.setProperty("id", proteinId);

        setPropertyIfNotDash(proteinNode, "version", version);
        setPropertyIfNotDash(proteinNode, "definition", protein.getDefinition());
        setPropertyIfNotDash(proteinNode, "locus", protein.getLocus());
        setPropertyIfNotDash(proteinNode, "db_link", protein.getDbLink());
        setPropertyIfNotDash(proteinNode, "keyword", protein.getKeyword());
        setPropertyIfNotDash(proteinNode, "source", protein.getSource());

        graph.update(proteinNode);
        proteinIdNodeIdMap.put(proteinId, proteinNode.getId());

        if (version == null) {
            return;
        }

        Node accessionNode = graph.findNode("Accession", "protein_accession.version", version);
        if (accessionNode == null) {
            return;
        }

        Edge edge = graph.addEdge(proteinNode.getId(), accessionNode, "HAS_ACCESSION");
        graph.update(edge);
    }

    private Node createAccessionNode(final Graph graph, final GeneAccession accession) {
        Node accessionNode = graph.addNode("Accession");
        setPropertyIfNotDash(accessionNode, "status", accession.status);
        setLongPropertyIfNotDash(accessionNode, "rna_nucleotide_gi", accession.rnaNucleotideGi);
        setPropertyIfNotDash(accessionNode, "rna_nucleotide_accession.version",
                             accession.rnaNucleotideAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "protein_gi", accession.proteinGi);
        setPropertyIfNotDash(accessionNode, "protein_accession.version", accession.proteinAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "genomic_nucleotide_gi", accession.genomicNucleotideGi);
        setPropertyIfNotDash(accessionNode, "genomic_nucleotide_accession.version",
                             accession.genomicNucleotideAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "mature_peptide_gi", accession.maturePeptideGi);
        setPropertyIfNotDash(accessionNode, "mature_peptide_accession.version",
                             accession.maturePeptideAccessionVersion);
        setLongPropertyIfNotDash(accessionNode, "start_position_on_the_genomic_accession",
                                 accession.startPositionOnTheGenomicAccession);
        setLongPropertyIfNotDash(accessionNode, "end_position_on_the_genomic_accession",
                                 accession.endPositionOnTheGenomicAccession);
        setPropertyIfNotDash(accessionNode, "assembly", accession.assembly);
        setPropertyIfNotDash(accessionNode, "orientation", accession.orientation);
        graph.update(accessionNode);
        return accessionNode;
    }

    private void setPropertyIfNotDash(final MVStoreModel container, final String propertyKey, final String value) {
        if (value != null && !"-".equals(value) && !"null".equals(value))
            container.setProperty(propertyKey, value);
    }

    private void setLongPropertyIfNotDash(final MVStoreModel container, final String propertyKey, final String value) {
        if (value != null && !"-".equals(value) && !"null".equals(value))
            container.setProperty(propertyKey, Long.parseLong(value));
    }

    private void setArrayPropertyIfNotDash(final MVStoreModel container, final String propertyKey, final String value) {
        if (value != null && !"-".equals(value) && !"null".equals(value) && value.trim().length() > 0)
            container.setProperty(propertyKey, StringUtils.split(value, "|"));
    }

    private void setTaxonProperty(final MVStoreModel container, final String propertyKey, final List<String> values) {
        if (values != null)
            container.setProperty(propertyKey, values.toArray(new String[0]));
    }
}
