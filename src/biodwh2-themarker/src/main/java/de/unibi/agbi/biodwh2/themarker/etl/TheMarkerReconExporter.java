package de.unibi.agbi.biodwh2.themarker.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.themarker.TheMarkerDataSource;
import de.unibi.agbi.biodwh2.themarker.model.Drug;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TheMarkerReconExporter extends ReconExporter<TheMarkerDataSource> {
    private static final String EMPTY_PLACEHOLDER = ".";

    public TheMarkerReconExporter(final TheMarkerDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, TheMarkerUpdater.DRUGS_FILE_NAME, Drug.class,
                                        (entry) -> reconDrug(graph, entry));
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + TheMarkerUpdater.DRUGS_FILE_NAME + "'", e);
        }
        try (final var reader = new SdfReader(
                FileUtils.openInput(workspace, dataSource, TheMarkerUpdater.DRUGS_SDF_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final SdfEntry entry : reader) {
                final var id = prefixIdentifier("TheMarker", entry.getTitle());
                createStructureRelationFromMol(graph, id, entry.getConnectionTable());
            }
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + TheMarkerUpdater.DRUGS_SDF_FILE_NAME + "'", e);
        }
    }

    private void reconDrug(final Graph graph, final Drug entry) {
        final var id = prefixIdentifier("TheMarker", entry.id);
        if (!EMPTY_PLACEHOLDER.equals(entry.name))
            createNameRelation(graph, id, entry.name);
        if (!EMPTY_PLACEHOLDER.equals(entry.iupacName))
            createNameRelation(graph, id, entry.iupacName);
        if (!EMPTY_PLACEHOLDER.equals(entry.synonyms)) {
            for (final var synonym : StringUtils.splitByWholeSeparator(entry.synonyms, "; "))
                createNameRelation(graph, id, synonym);
        }
        if (!EMPTY_PLACEHOLDER.equals(entry.pubChemCID))
            createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(entry.pubChemCID));
        if (!EMPTY_PLACEHOLDER.equals(entry.drugBankId))
            createXrefRelation(graph, id, IdentifierType.DRUG_BANK.build(entry.drugBankId));
        if (!EMPTY_PLACEHOLDER.equals(entry.ttdDrugId))
            createXrefRelation(graph, id, prefixIdentifier("TTD", entry.ttdDrugId));
        if (!EMPTY_PLACEHOLDER.equals(entry.canonicalSmiles))
            createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.canonicalSmiles));
        if (!EMPTY_PLACEHOLDER.equals(entry.isoSmiles))
            createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.isoSmiles));
        if (!EMPTY_PLACEHOLDER.equals(entry.inchiKey))
            createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, entry.inchiKey));
    }
}
