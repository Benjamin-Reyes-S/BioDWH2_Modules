package de.unibi.agbi.biodwh2.ncbi.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.FileUtils;

public class NCBITaxonParser {
    private static final Logger LOGGER = LogManager.getLogger(NCBITaxonParser.class);

    private static final String TAXDUMP_ARCHIVE = "taxdump.tar.gz";

    // parse names.dmp into map with tax_id as key and property names as nested keys
    public Map<String, Map<String, List<String>>> parseNames(final Workspace workspace, final DataSource dataSource)
            throws IOException {
        final Map<String, Map<String, List<String>>> taxonPropertiesByTaxId = new LinkedHashMap<>();

        try (final BufferedReader reader = FileUtils.openTarGzipDmpEntry(
                workspace,
                dataSource,
                TAXDUMP_ARCHIVE,
                "names.dmp"
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split("\\|", -1);

                final String taxId = parts[0].trim();
                final String nameTxt = parts[1].trim();
                final String uniqueName = parts[2].trim();
                final String nameClass = parts[3].trim();

                final Map<String, List<String>> taxonProperties = taxonPropertiesByTaxId.computeIfAbsent(
                        taxId, key -> new LinkedHashMap<>());

                addProperty(taxonProperties, "name_txt", nameTxt);
                addProperty(taxonProperties, "unique_name", uniqueName);
                addProperty(taxonProperties, "name_class", nameClass);
            }
        }

        return taxonPropertiesByTaxId;
    }

    // parse nodes.dmp and add node properties only if tax_id already exists from names.dmp
    public void parseNodes(
            final Workspace workspace,
            final DataSource dataSource,
            final Map<String, Map<String, List<String>>> taxonPropertiesByTaxId
    ) throws IOException {
        try (final BufferedReader reader = FileUtils.openTarGzipDmpEntry(
                workspace,
                dataSource,
                TAXDUMP_ARCHIVE,
                "nodes.dmp"
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split("\\|", -1);

                final String taxId = parts[0].trim();

                if (!taxonPropertiesByTaxId.containsKey(taxId))
                    continue;

                final Map<String, List<String>> taxonProperties = taxonPropertiesByTaxId.get(taxId);

                addProperty(taxonProperties, "parent_tax_id", parts[1].trim());
                addProperty(taxonProperties, "rank", parts[2].trim());
                addProperty(taxonProperties, "embl_code", parts[3].trim());
                addProperty(taxonProperties, "division_id", parts[4].trim());
                addProperty(taxonProperties, "inherited_div_flag", parts[5].trim());
                addProperty(taxonProperties, "genetic_code_id", parts[6].trim());
                addProperty(taxonProperties, "inherited_gc_flag", parts[7].trim());
                addProperty(taxonProperties, "mitochondrial_genetic_code_id", parts[8].trim());
                addProperty(taxonProperties, "inherited_mgc_flag", parts[9].trim());
                addProperty(taxonProperties, "genbank_hidden_flag", parts[10].trim());
                addProperty(taxonProperties, "hidden_subtree_root_flag", parts[11].trim());
                addProperty(taxonProperties, "comments", parts[12].trim());
            }
        }
    }

    private void addProperty(final Map<String, List<String>> taxonProperties, final String propertyName,
                             final String value) {
        taxonProperties.computeIfAbsent(propertyName, key -> new ArrayList<>()).add(value);
    }
}