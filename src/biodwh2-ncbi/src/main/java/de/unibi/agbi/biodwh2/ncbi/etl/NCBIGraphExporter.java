package de.unibi.agbi.biodwh2.ncbi.etl;
//import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//for protein files, delete later when proteins in refseq
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
//prot files end 

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
//import de.unibi.agbi.biodwh2.core.io.biopax.Gene;
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

// FOR COMPOUNDS 
//import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
//import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
//import java.nio.charset.StandardCharsets;

public class NCBIGraphExporter extends GraphExporter<NCBIDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(NCBIGraphExporter.class);
    private Map<Long, Long> geneIdNodeIdMap;
    private Map<String, Long> proteinIdNodeIdMap;

    public NCBIGraphExporter(final NCBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 12;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        //defines schemaic constraints in the graph --> Node labels: Gene and compound
        graph.addIndex(IndexDescription.forNode("Gene", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Protein", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Compound", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Accession", "protein_accession.version", IndexDescription.Type.NON_UNIQUE));
        geneIdNodeIdMap = new HashMap<>();
        proteinIdNodeIdMap = new HashMap<>();
        try {
            exportGeneDatabase(workspace, dataSource, graph);

            exportProteinDatabase(workspace, dataSource, graph);
        } catch (IOException e) {
            throw new ExporterException("Failed to export NCBI Gene database", e);
        }
        //EXPORT FOR COMPOUNDS
        //try {
        //    exportPubChemDatabase(workspace, dataSource, graph);
        //} catch (IOException e) {
        //    throw new ExporterException("Failed to export NCBI PubChem database", e);
        //}
        return true;
    }

    // GENES EXPORTER
    private void exportGeneDatabase(final Workspace workspace, final DataSource dataSource,
                                    final Graph graph) throws IOException {
        LOGGER.info("Exporting gene_info.gz...");
        MappingIterator<GeneInfo> geneInfos = FileUtils.openGzipTsv(workspace, dataSource, "gene_info.gz", 
                                                                    GeneInfo.class);
        while (geneInfos.hasNext()) {
            // most relevant entry node: "Gene" with properties 
            GeneInfo geneInfo = geneInfos.next();
            if (!geneInfo.taxonomyId.equals("9606"))
                continue;
            long geneId = Long.parseLong(geneInfo.geneId); //NCBI gene identifier
            Node geneNode = graph.addNode("Gene"); // Node internal identifier
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
            // reference the NCBI ids
            long geneId = Long.parseLong(group.geneId);
            long otherGeneId = Long.parseLong(group.otherGeneId);

         
            // reference the internal node ids 
            Long geneNodeId = geneIdNodeIdMap.get(geneId);
            Long otherGeneNodeId = geneIdNodeIdMap.get(otherGeneId);

            if (geneNodeId == null || otherGeneNodeId == null)
                continue;

            // check empty relationship type
            Edge edge = graph.addEdge(geneNodeId, otherGeneNodeId, "RELATED_TO");
            edge.setProperty("type", group.relationship);
            graph.update(edge);

        }    
        LOGGER.info("Exporting gene_orthologs.gz...");
        MappingIterator<GeneRelationship> orthologs = FileUtils.openGzipTsv(workspace, dataSource, "gene_orthologs.gz",
                                                                            GeneRelationship.class);            
        

        while (orthologs.hasNext() ) {
            GeneRelationship ortholog = orthologs.next();
            if (!ortholog.taxonomyId.equals("9606") || !ortholog.otherTaxonomyId.equals("9606"))
                continue;
            // reference the NCBI ids
            long geneId = Long.parseLong(ortholog.geneId);
            long otherGeneId = Long.parseLong(ortholog.otherGeneId);
            // reference the internal node ids
            Long geneNodeId = geneIdNodeIdMap.get(geneId);
            Long otherGeneNodeId = geneIdNodeIdMap.get(otherGeneId);

            // check that reference NCBI ids from gene_orthologs.gz are alsp added nodes in the graph from gene_info.gz
            if (geneNodeId == null || otherGeneNodeId == null)
                continue;
            Edge edge = graph.addEdge(geneNodeId, otherGeneNodeId, "RELATED_TO");
            edge.setProperty("type", ortholog.relationship);
            graph.update(edge);

        }
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



    // Protein exporter
    private void exportProteinDatabase(final Workspace workspace,
                                    final DataSource dataSource,
                                    final Graph graph) throws IOException {
        // find all downloaded protein files matching the pattern
        File workspaceDir = workspace.getDataSourceDirectory(dataSource.getId()).resolve("source").toFile();
        Pattern pattern = Pattern.compile("vertebrate_mammalian\\.\\d+\\.protein\\.gpff\\.gz");
        LOGGER.info("Looking for protein files in: " + workspaceDir.getAbsolutePath());

        File[] proteinFiles = workspaceDir.listFiles(
                (dir, name) -> pattern.matcher(name).matches()
        );

        if (proteinFiles == null || proteinFiles.length == 0) {
            LOGGER.warn("No vertebrate_mammalian protein files found in workspace");
            return;
        }

        // sort so they are processed in numeric order (file 1, 2, 3 ...)
        Arrays.sort(proteinFiles, Comparator.comparing(File::getName));
        LOGGER.info("Found " + proteinFiles.length + " protein files to export");

        for (File file : proteinFiles) {
            LOGGER.info("Exporting " + file.getName() + "...");
            try (final BufferedReader reader = FileUtils.createBufferedReaderFromStream(
                    FileUtils.openGzip(workspace, dataSource, file.getName()))) {
                final StringBuilder record = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    record.append(line).append("\n");
                    if (line.equals("//")) {
                        parseProteinRecord(graph, record.toString());
                        record.setLength(0);
                    }
                }
            }
            LOGGER.info("Protein files readed: " + file.getName());
            
        }
    }

    private void parseProteinRecord(final Graph graph, final String record) {
        // pass variables to store the relevant properties of the protein record
        String proteinId = null;
        String version = null;
        String locus = null;
        String db_link=null; 
        String keyword = null;
        String source= null;
        // String organism = null; requires implementation still!!
        // use StringBuilder for definition — it can span multiple continuation lines
        StringBuilder definitionBuilder = new StringBuilder();
        boolean inDefinition = false;

        // parse the record
        for (String entry : record.split("\n")) {
            if (entry.startsWith("LOCUS")) {
                locus = entry.substring(12).trim();
                inDefinition = false;    
            } else if (entry.startsWith("DEFINITION")) {
                definitionBuilder.append(entry.substring(12).trim());
                inDefinition = true;
             } else if (entry.startsWith("ACCESSION")) {
                proteinId = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("VERSION")) {
                version = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("DBLINK")) {
                db_link = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("KEYWORDS")) {
                keyword = entry.substring(12).trim();
                inDefinition = false;
            } else if (entry.startsWith("SOURCE")) {
                source = entry.substring(12).trim();
                inDefinition = false;
            } else if (inDefinition && entry.startsWith("  ")) {
                // continuation line of DEFINITION (starts with whitespace, no keyword)
                definitionBuilder.append(" ").append(entry.trim());                   
            } else {
                inDefinition = false;
            }
        }

        if (proteinId == null){
            return; // skip record if no accession is found
        }
            

        String definition = definitionBuilder.length() > 0 ? definitionBuilder.toString() : null;

        // create Protein node
        Node proteinNode = graph.addNode("Protein");
        proteinNode.setProperty("id", proteinId);
        if (version != null)
            proteinNode.setProperty("version", version);
        if (definition != null)
            proteinNode.setProperty("definition", definition);
        if (locus != null)
            proteinNode.setProperty("locus", locus);
        if (db_link != null)
            proteinNode.setProperty("db_link", db_link);
        if (keyword != null)
            proteinNode.setProperty("keyword", keyword);
        if (source != null)
            proteinNode.setProperty("source", source);
        // update node before adding edges
        graph.update(proteinNode);
        proteinIdNodeIdMap.put(proteinId, proteinNode.getId());

        Node accessionNode = graph.findNode("Accession", "protein_accession.version", version);
        if (accessionNode == null) {
            return; // no gene2accession entry for this protein → skip
        } else {
            System.out.println("Found accession: '" + accessionNode.getProperty("protein_accession.version") + "'");
        }

        // Connect Protein → Accession
        Edge edge = graph.addEdge(proteinNode.getId(), accessionNode, "HAS_ACCESSION");
        graph.update(edge);
    }
}


