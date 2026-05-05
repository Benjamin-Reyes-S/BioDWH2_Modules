package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.gene2phenotype.Gene2PhenotypeDataSource;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Gene2PhenotypeGraphExporter extends GraphExporter<Gene2PhenotypeDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(Gene2PhenotypeGraphExporter.class);
    static final String PUBLICATION_LABEL = "Publication";
    static final String GENE_LABEL = "Gene";
    static final String PHENOTYPE_LABEL = "Phenotype";
    static final String DISEASE_LABEL = "Disease";

    public Gene2PhenotypeGraphExporter(final Gene2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "hgnc_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PHENOTYPE_LABEL, "hpo_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "mim", IndexDescription.Type.UNIQUE));
        exportGeneDiseasePairs(workspace, graph);
        return true;
    }

    private void exportGeneDiseasePairs(final Workspace workspace, final Graph graph) {
        for (final String fileName : Gene2PhenotypeUpdater.FILE_NAMES) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting {}...", fileName);
            try {
                FileUtils.openGzipCsvWithHeader(workspace, dataSource, fileName, GeneDiseasePair.class,
                                                (entry) -> exportGeneDiseasePair(graph, entry));
            } catch (final IOException e) {
                throw new ExporterFormatException(e);
            }
        }
    }

    private void exportGeneDiseasePair(final Graph graph, final GeneDiseasePair association) {
        final Node associationNode = graph.addNodeFromModel(association);
        final Node geneNode = getOrCreateGeneNode(graph, association.geneSymbol, association.hgncId,
                                                  association.geneMim, association.previousGeneSymbols);
        graph.addEdge(geneNode, associationNode, "ASSOCIATED_WITH");
        final Node diseaseNode = getOrCreateDiseaseNode(graph, association.diseaseName, association.diseaseMim,
                                                        association.diseaseMondo);
        graph.addEdge(associationNode, diseaseNode, "ASSOCIATED_WITH");
        if (association.phenotypes != null)
            for (final String hpoId : StringUtils.splitByWholeSeparator(association.phenotypes, "; "))
                graph.addEdge(associationNode, getOrCreatePhenotypeNode(graph, hpoId), "SHOWS");
        if (association.publications != null) {
            for (final String pmid : StringUtils.splitByWholeSeparator(association.publications, "; ")) {
                graph.addEdge(associationNode, getOrCreatePublicationNode(graph, Integer.parseInt(pmid)), "REFERENCES");
            }
        }
    }

    private Node getOrCreateGeneNode(final Graph graph, final String symbol, final Integer hgncId, final Integer mim,
                                     final String prevSymbols) {
        Node node = graph.findNode(GENE_LABEL, "hgnc_id", hgncId);
        if (node == null) {
            final var builder = graph.buildNode(GENE_LABEL);
            builder.withProperty("hgnc_id", hgncId);
            builder.withProperty("hgnc_symbol", symbol);
            builder.withPropertyIfNotNull("mim", mim);
            if (StringUtils.isNotEmpty(prevSymbols)) {
                final String[] prevSymbolsArray = StringUtils.splitByWholeSeparator(prevSymbols, "; ");
                if (prevSymbolsArray.length > 0)
                    builder.withProperty("previous_symbols", prevSymbolsArray);
            }
            node = builder.build();
        }
        return node;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final String name, final String mim, String mondo) {
        final Integer mimNumber = tryParseMim(mim);
        Node node = null;
        if (mimNumber != null)
            node = graph.findNode(DISEASE_LABEL, "mim", mimNumber);
        if (node == null)
            node = graph.findNode(DISEASE_LABEL, "name", name);
        if (node == null) {
            final var builder = graph.buildNode(DISEASE_LABEL);
            builder.withPropertyIfNotNull("name", name);
            builder.withPropertyIfNotNull("mim", mimNumber);
            if (StringUtils.isNotEmpty(mondo))
                builder.withProperty("mondo_id", mondo);
            node = builder.build();
        }
        return node;
    }

    private Integer tryParseMim(final String mim) {
        try {
            return Integer.parseInt(mim);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    private Node getOrCreatePhenotypeNode(final Graph graph, final String hpoId) {
        Node node = graph.findNode(PHENOTYPE_LABEL, "hpo_id", hpoId);
        if (node == null)
            node = graph.addNode(PHENOTYPE_LABEL, "hpo_id", hpoId);
        return node;
    }

    private Node getOrCreatePublicationNode(final Graph graph, final Integer pmid) {
        Node node = graph.findNode(PUBLICATION_LABEL, "pmid", pmid);
        if (node == null)
            node = graph.addNode(PUBLICATION_LABEL, "pmid", pmid);
        return node;
    }
}
