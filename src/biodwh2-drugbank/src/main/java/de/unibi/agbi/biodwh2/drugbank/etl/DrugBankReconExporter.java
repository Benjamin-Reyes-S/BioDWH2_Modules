package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;

import java.nio.charset.StandardCharsets;

public class DrugBankReconExporter extends ReconExporter<DrugBankDataSource> {
    public DrugBankReconExporter(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try {
            FileUtils.forEachZipEntryWithSuffix(workspace, dataSource, DrugBankUpdater.STRUCTURES_SDF_FILE_NAME, ".sdf",
                                                (stream, e) -> {
                                                    final var reader = new SdfReader(stream, StandardCharsets.UTF_8);
                                                    for (final SdfEntry entry : reader)
                                                        reconStructureEntry(graph, entry);
                                                });
        } catch (Exception e) {
            throw new ReconException("Failed to export '" + DrugBankUpdater.STRUCTURES_SDF_FILE_NAME + "'", e);
        }
        try {
            FileUtils.forEachZipEntryWithSuffix(workspace, dataSource,
                                                DrugBankUpdater.METABOLITE_STRUCTURES_SDF_FILE_NAME, ".sdf",
                                                (stream, e) -> {
                                                    final var reader = new SdfReader(stream, StandardCharsets.UTF_8);
                                                    for (final SdfEntry entry : reader)
                                                        reconMetaboliteEntry(graph, entry);
                                                });
        } catch (Exception e) {
            throw new ReconException("Failed to export '" + DrugBankUpdater.METABOLITE_STRUCTURES_SDF_FILE_NAME + "'",
                                     e);
        }
    }

    private void reconStructureEntry(final Graph graph, final SdfEntry entry) {
        final var id = IdentifierType.DRUG_BANK.build(entry.properties.get("DATABASE_ID"));
        createNameRelation(graph, id, entry.properties.get("JCHEM_IUPAC"));
        createNameRelation(graph, id, entry.properties.get("JCHEM_TRADITIONAL_IUPAC"));
        createNameRelation(graph, id, entry.properties.get("GENERIC_NAME"));
        createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.properties.get("SMILES")));
        createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, entry.properties.get("INCHI_KEY")));
        createXrefRelation(graph, id, IdentifierType.DRUG_BANK.build(entry.properties.get("DRUGBANK_ID")));
        createStructureRelationFromInchi(graph, id, entry.properties.get("INCHI_IDENTIFIER"));
        // SYNONYMS
    }

    private void reconMetaboliteEntry(final Graph graph, final SdfEntry entry) {
        final var id = IdentifierType.DRUG_BANK.build(entry.properties.get("DATABASE_ID"));
        createNameRelation(graph, id, entry.properties.get("NAME"));
        createNameRelation(graph, id, entry.properties.get("JCHEM_IUPAC"));
        createNameRelation(graph, id, entry.properties.get("JCHEM_TRADITIONAL_IUPAC"));
        createNameRelation(graph, id, entry.properties.get("GENERIC_NAME"));
        createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.properties.get("SMILES")));
        createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, entry.properties.get("INCHI_KEY")));
        createXrefRelation(graph, id, IdentifierType.DRUG_BANK.build(entry.properties.get("DRUGBANK_ID")));
        createXrefRelation(graph, id, IdentifierType.UNII.build(entry.properties.get("UNII")));
        createStructureRelationFromInchi(graph, id, entry.properties.get("INCHI_IDENTIFIER"));
        // SYNONYMS
    }
}
