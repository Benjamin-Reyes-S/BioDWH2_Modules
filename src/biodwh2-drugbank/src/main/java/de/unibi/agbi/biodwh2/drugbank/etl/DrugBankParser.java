package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.DrugStructure;
import de.unibi.agbi.biodwh2.drugbank.model.DrugbankMetaboliteId;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DrugBankParser extends Parser<DrugBankDataSource> {
    public DrugBankParser(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        return parseDrugSdfFile(workspace, dataSource) && parseMetaboliteSdfFile(workspace, dataSource);
    }

    private boolean parseDrugSdfFile(final Workspace workspace,
                                     final DrugBankDataSource dataSource) throws ParserException {
        try {
            FileUtils.forEachZipEntryWithSuffix(workspace, dataSource, DrugBankUpdater.STRUCTURES_SDF_FILE_NAME, ".sdf",
                                                (stream, e) -> {
                                                    final var reader = new SdfReader(stream, StandardCharsets.UTF_8);
                                                    dataSource.drugStructures = new ArrayList<>();
                                                    for (final SdfEntry entry : reader)
                                                        dataSource.drugStructures.add(drugFromSdfEntry(entry));
                                                });
            return true;
        } catch (Exception e) {
            throw new ParserFormatException(
                    "Failed to parse the file '" + DrugBankUpdater.STRUCTURES_SDF_FILE_NAME + "'", e);
        }
    }

    private DrugStructure drugFromSdfEntry(final SdfEntry entry) {
        final DrugStructure drug = new DrugStructure();
        drug.databaseId = entry.properties.get("DATABASE_ID");
        drug.databaseName = entry.properties.get("DATABASE_NAME");
        drug.smiles = entry.properties.get("SMILES");
        drug.inchiId = entry.properties.get("INCHI_IDENTIFIER");
        drug.inchiKey = entry.properties.get("INCHI_KEY");
        drug.formula = entry.properties.get("FORMULA");
        drug.molecularWeight = entry.properties.get("MOLECULAR_WEIGHT");
        drug.exactMass = entry.properties.get("EXACT_MASS");
        drug.iupac = entry.properties.get("JCHEM_IUPAC");
        drug.traditionalIupac = entry.properties.get("JCHEM_TRADITIONAL_IUPAC");
        drug.drugbankId = new DrugbankMetaboliteId();
        drug.drugbankId.value = entry.properties.get("DRUGBANK_ID");
        drug.drugbankId.primary = true;
        drug.name = entry.properties.get("GENERIC_NAME");
        drug.ruleOfFive = entry.properties.get("JCHEM_RULE_OF_FIVE");
        drug.ghoseFilter = entry.properties.get("JCHEM_GHOSE_FILTER");
        drug.veberRule = entry.properties.get("JCHEM_VEBER_RULE");
        drug.mddrLikeRule = entry.properties.get("JCHEM_MDDR_LIKE_RULE");
        return drug;
    }

    private boolean parseMetaboliteSdfFile(final Workspace workspace,
                                           final DrugBankDataSource dataSource) throws ParserException {
        try {
            FileUtils.forEachZipEntryWithSuffix(workspace, dataSource,
                                                DrugBankUpdater.METABOLITE_STRUCTURES_SDF_FILE_NAME, ".sdf",
                                                (stream, e) -> {
                                                    final var reader = new SdfReader(stream, StandardCharsets.UTF_8);
                                                    dataSource.metaboliteStructures = new ArrayList<>();
                                                    for (final SdfEntry entry : reader)
                                                        dataSource.metaboliteStructures.add(
                                                                metaboliteFromSdfEntry(entry));
                                                });
            return true;
        } catch (Exception e) {
            throw new ParserFormatException(
                    "Failed to parse the file '" + DrugBankUpdater.METABOLITE_STRUCTURES_SDF_FILE_NAME + "'", e);
        }
    }

    private MetaboliteStructure metaboliteFromSdfEntry(final SdfEntry entry) {
        final MetaboliteStructure metabolite = new MetaboliteStructure();
        metabolite.databaseId = entry.properties.get("DATABASE_ID");
        metabolite.databaseName = entry.properties.get("DATABASE_NAME");
        metabolite.smiles = entry.properties.get("SMILES");
        metabolite.inchiId = entry.properties.get("INCHI_IDENTIFIER");
        metabolite.inchiKey = entry.properties.get("INCHI_KEY");
        metabolite.formula = entry.properties.get("FORMULA");
        metabolite.molecularWeight = entry.properties.get("MOLECULAR_WEIGHT");
        metabolite.exactMass = entry.properties.get("EXACT_MASS");
        metabolite.iupac = entry.properties.get("JCHEM_IUPAC");
        metabolite.traditionalIupac = entry.properties.get("JCHEM_TRADITIONAL_IUPAC");
        metabolite.drugbankId = entry.properties.get("DRUGBANK_ID");
        metabolite.name = entry.properties.get("NAME");
        metabolite.unii = entry.properties.get("UNII");
        metabolite.ruleOfFive = entry.properties.get("JCHEM_RULE_OF_FIVE");
        metabolite.ghoseFilter = entry.properties.get("JCHEM_GHOSE_FILTER");
        metabolite.veberRule = entry.properties.get("JCHEM_VEBER_RULE");
        metabolite.mddrLikeRule = entry.properties.get("JCHEM_MDDR_LIKE_RULE");
        return metabolite;
    }
}
