package de.unibi.agbi.biodwh2.cmaup.etl;

import de.unibi.agbi.biodwh2.cmaup.CMAUPDataSource;
import de.unibi.agbi.biodwh2.cmaup.model.Ingredient;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CMAUPReconExporter extends ReconExporter<CMAUPDataSource> {
    private static final String[] EMPTY_PLACEHOLDERS = {"NA", "n.a."};

    public CMAUPReconExporter(final CMAUPDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, CMAUPUpdater.INGREDIENTS_ONLY_ACTIVE_FILE_NAME,
                                        Ingredient.class, (entry) -> {
                        final var id = "CMAUP:" + entry.id;
                        if (!ArrayUtils.contains(EMPTY_PLACEHOLDERS, entry.chemblId))
                            createXrefRelation(graph, id, IdentifierType.CHEMBL.build(entry.chemblId));
                        if (!ArrayUtils.contains(EMPTY_PLACEHOLDERS, entry.inchiKey))
                            createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, entry.inchiKey));
                        if (!ArrayUtils.contains(EMPTY_PLACEHOLDERS, entry.smiles))
                            createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.smiles));
                        if (!ArrayUtils.contains(EMPTY_PLACEHOLDERS, entry.pubchemCid)) {
                            for (final var pubChemId : StringUtils.split(entry.pubchemCid, ';'))
                                createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(pubChemId));
                        }
                        createNameRelation(graph, id, entry.iupacName, EMPTY_PLACEHOLDERS);
                        createNameRelation(graph, id, entry.prefName, EMPTY_PLACEHOLDERS);
                        if (!ArrayUtils.contains(EMPTY_PLACEHOLDERS, entry.inchi))
                            createStructureRelationFromInchi(graph, id, entry.inchi);
                    });
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + CMAUPUpdater.INGREDIENTS_ONLY_ACTIVE_FILE_NAME + "'", e);
        }
    }
}
