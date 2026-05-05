package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.TaxonNodeMappingDescription;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class NCBITaxonOntologyDataSource extends OBOFoundryOntologyDataSource {
    public NCBITaxonOntologyDataSource() {
        super("NCBITaxonOntology", "ncbitaxon.obo", License.CC0_1_0, "NCBITaxon Ontology", "NCBITaxon",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NCBITaxonOntologyMappingDescriber(this);
    }

    private static class NCBITaxonOntologyMappingDescriber extends MappingDescriber {
        private final Map<String, TaxonNodeMappingDescription.Rank> rankMap = new HashMap<>();

        public NCBITaxonOntologyMappingDescriber(final DataSource dataSource) {
            super(dataSource);
            rankMap.put("NCBITaxon:kingdom", TaxonNodeMappingDescription.Rank.KINGDOM);
            rankMap.put("NCBITaxon:phylum", TaxonNodeMappingDescription.Rank.PHYLUM);
            rankMap.put("NCBITaxon:class", TaxonNodeMappingDescription.Rank.CLASS);
            rankMap.put("NCBITaxon:order", TaxonNodeMappingDescription.Rank.ORDER);
            rankMap.put("NCBITaxon:family", TaxonNodeMappingDescription.Rank.FAMILY);
            rankMap.put("NCBITaxon:genus", TaxonNodeMappingDescription.Rank.GENUS);
            rankMap.put("NCBITaxon:species", TaxonNodeMappingDescription.Rank.SPECIES);
            /* TODO
            rankMap.put("NCBITaxon:subspecies", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:strain", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subfamily", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:isolate", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:serotype", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:varietas", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:tribe", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subtribe", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:section", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:clade", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:forma", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subgenus", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("http://purl.obolibrary.org/obo/NCBITaxon_forma_specialis", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:serogroup", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:superfamily", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:suborder", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subclass", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:species_group", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:superkingdom", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:infraclass", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subphylum", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:biotype", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:infraorder", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:superorder", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:parvorder", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:superphylum", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:species_subgroup", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subsection", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:cohort", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subcohort", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:genotype", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:morph", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:series", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:pathogroup", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:superclass", TaxonNodeMappingDescription.Rank.UNKNOWN);
            rankMap.put("NCBITaxon:subkingdom", TaxonNodeMappingDescription.Rank.UNKNOWN);
            */
        }

        @Override
        public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
            if (OntologyGraphExporter.TERM_LABEL.equals(localMappingLabel)) {
                final String id = node.getProperty(GraphExporter.ID_KEY);
                if ("NCBITaxon:1".equals(id)) {
                    return null;
                }
                final String[] idParts = StringUtils.split(id, ":", 2);
                if (!NumberUtils.isDigits(idParts[1])) {
                    return null;
                }
                final TaxonNodeMappingDescription description = new TaxonNodeMappingDescription();
                description.setRank(extractRank(node.getProperty("property_values")));
                description.addIdentifier(IdentifierType.NCBI_TAXON, Integer.parseInt(idParts[1]));
                description.addName(node.getProperty("name"));
                return new NodeMappingDescription[]{description};
            }
            return null;
        }

        private TaxonNodeMappingDescription.Rank extractRank(final String[] propertyValues) {
            if (propertyValues != null) {
                for (final String propertyValue : propertyValues) {
                    if (propertyValue.startsWith("has_rank ")) {
                        final String rankName = StringUtils.split(propertyValue, " ", 2)[1];
                        return rankMap.get(rankName);
                    }
                }
            }
            return null;
        }

        @Override
        public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
            return null;
        }

        @Override
        protected String[] getNodeMappingLabels() {
            return new String[]{OntologyGraphExporter.TERM_LABEL};
        }

        @Override
        protected PathMapping[] getEdgePathMappings() {
            return new PathMapping[0];
        }
    }
}
