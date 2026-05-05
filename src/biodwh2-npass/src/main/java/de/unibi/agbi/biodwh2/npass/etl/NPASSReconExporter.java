package de.unibi.agbi.biodwh2.npass.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.npass.NPASSDataSource;
import de.unibi.agbi.biodwh2.npass.model.Compound;
import de.unibi.agbi.biodwh2.npass.model.Structure;

import java.io.IOException;

public class NPASSReconExporter extends ReconExporter<NPASSDataSource> {
    public NPASSReconExporter(final NPASSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_structureInfo.txt", Structure.class,
                                        (entry) -> reconStructure(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_structureInfo.txt'", e);
        }
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, "naturalProducts_generalInfo.txt", Compound.class,
                                        (entry) -> reconCompound(graph, entry));
        } catch (final IOException e) {
            throw new ExporterFormatException("Failed to export 'naturalProducts_generalInfo.txt'", e);
        }
    }

    private void reconStructure(final Graph graph, final Structure entry) {
        final String id = prefixIdentifier("NPASS", entry.npId);
        if (!"n.a.".equalsIgnoreCase(entry.inchi))
            createStructureRelationFromInchi(graph, id, entry.inchi);
        if (!"n.a.".equalsIgnoreCase(entry.inchiKey))
            createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, entry.inchiKey));
        if (!"n.a.".equalsIgnoreCase(entry.smiles))
            createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.smiles));
    }

    private void reconCompound(final Graph graph, final Compound entry) {
        final String id = prefixIdentifier("NPASS", entry.npId);
        if (!"n.a.".equalsIgnoreCase(entry.iupacName))
            createNameRelation(graph, id, entry.iupacName);
        if (!"n.a.".equalsIgnoreCase(entry.chemblId))
            createXrefRelation(graph, id, IdentifierType.CHEMBL.build(entry.chemblId));
        final var pubChemCIDs = NPASSGraphExporter.parsePubChemCIDs(entry.pubChemCid);
        if (pubChemCIDs != null) {
            for (final var pubChemCid : pubChemCIDs)
                createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(pubChemCid));
        }
    }
}
