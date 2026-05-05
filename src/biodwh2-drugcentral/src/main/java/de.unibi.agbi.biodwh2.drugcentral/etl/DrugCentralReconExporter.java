package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.Struct2Parent;
import de.unibi.agbi.biodwh2.drugcentral.model.Structure;

import java.io.IOException;

public class DrugCentralReconExporter extends ReconExporter<DrugCentralDataSource> {
    public DrugCentralReconExporter(final DrugCentralDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try {
            FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, "structures.tsv", Structure.class,
                                                      (entry) -> reconStructure(graph, entry));
        } catch (IOException e) {
            throw new ReconException("Failed to export 'structures.tsv'", e);
        }
        try {
            FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, "struct2parent.tsv", Struct2Parent.class,
                                                      (entry) -> reconStruct2Parent(graph, entry));
        } catch (IOException e) {
            throw new ReconException("Failed to export 'structures.tsv'", e);
        }
    }

    private void reconStructure(final Graph graph, final Structure structure) {
        final var id = IdentifierType.DRUG_CENTRAL.build(structure.id);
        createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, structure.inchiKey));
        createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, structure.smiles));
        createStructureRelationFromInchi(graph, id, structure.inchi);
        createNameRelation(graph, id, structure.name);
    }

    private void reconStruct2Parent(final Graph graph, final Struct2Parent entry) {
        final var structId = IdentifierType.DRUG_CENTRAL.build(entry.structId);
        final var parentId = IdentifierType.DRUG_CENTRAL.build(entry.parentId);
        createOntologyRelation(graph, structId, parentId, "IS_A");
    }
}
