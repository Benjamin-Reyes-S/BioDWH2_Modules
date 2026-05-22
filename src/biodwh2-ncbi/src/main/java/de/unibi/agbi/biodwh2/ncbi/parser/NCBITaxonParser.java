package de.unibi.agbi.biodwh2.ncbi.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.ncbi.model.TaxonName;
import de.unibi.agbi.biodwh2.ncbi.model.TaxonNode;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NCBITaxonParser {
    private static final Logger LOGGER = LogManager.getLogger(NCBITaxonParser.class);

    private static final String TAXDUMP_ARCHIVE = "taxdump.tar.gz";

    // parse names.dmp into map with tax_id as key and property names as nested keys
    public Map<String, Map<String, List<String>>> parseNames(final Workspace workspace, final DataSource dataSource)
            throws IOException {
        final Map<String, Map<String, List<String>>> taxonPropertiesByTaxId = new LinkedHashMap<>();

        try (final MappingIterator<TaxonName> iterator = FileUtils.openTarGzipTsvEntry(
                workspace,
                dataSource,
                TAXDUMP_ARCHIVE,
                "names.dmp",
                TaxonName.class
        )) {
            while (iterator.hasNext()) {
                final TaxonName taxonName = iterator.next();
                final Map<String, List<String>> taxonProperties = taxonPropertiesByTaxId.computeIfAbsent(
                        taxonName.taxId, key -> new LinkedHashMap<>());

                addProperty(taxonProperties, "name_txt", taxonName.nameTxt);
                addProperty(taxonProperties, "unique_name", taxonName.uniqueName);
                addProperty(taxonProperties, "name_class", taxonName.nameClass);
            }
        }

        return taxonPropertiesByTaxId;
    }

    // parse nodes.dmp and add node properties to taxonPropertiesByTaxId
    public void parseNodes(
            final Workspace workspace,
            final DataSource dataSource,
            final Map<String, Map<String, List<String>>> taxonPropertiesByTaxId
    ) throws IOException {
        try (final MappingIterator<TaxonNode> iterator = FileUtils.openTarGzipTsvEntry(
                workspace,
                dataSource,
                TAXDUMP_ARCHIVE,
                "nodes.dmp",
                TaxonNode.class
        )) {
            while (iterator.hasNext()) {
                final TaxonNode taxonNode = iterator.next();
                final Map<String, List<String>> taxonProperties = taxonPropertiesByTaxId.computeIfAbsent(
                        taxonNode.taxId, key -> new LinkedHashMap<>());

                addProperty(taxonProperties, "parent_tax_id", taxonNode.parentTaxId);
                addProperty(taxonProperties, "rank", taxonNode.rank);
                addProperty(taxonProperties, "embl_code", taxonNode.emblCode);
                addProperty(taxonProperties, "division_id", taxonNode.divisionId);
                addProperty(taxonProperties, "inherited_div_flag", taxonNode.inheritedDivFlag);
                addProperty(taxonProperties, "genetic_code_id", taxonNode.geneticCodeId);
                addProperty(taxonProperties, "inherited_gc_flag", taxonNode.inheritedGcFlag);
                addProperty(taxonProperties, "mitochondrial_genetic_code_id", taxonNode.mitochondrialGeneticCodeId);
                addProperty(taxonProperties, "inherited_mgc_flag", taxonNode.inheritedMgcFlag);
                addProperty(taxonProperties, "genbank_hidden_flag", taxonNode.genbankHiddenFlag);
                addProperty(taxonProperties, "hidden_subtree_root_flag", taxonNode.hiddenSubtreeRootFlag);
                addProperty(taxonProperties, "comments", taxonNode.comments);
            }
        }
    }

    private void addProperty(final Map<String, List<String>> taxonProperties, final String propertyName,
                             final String value) {
        taxonProperties.computeIfAbsent(propertyName, key -> new ArrayList<>()).add(value);
    }
}
