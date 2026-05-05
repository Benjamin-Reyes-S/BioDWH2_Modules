package de.unibi.agbi.biodwh2.ttd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.chem.CAS;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.ttd.TTDDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TTDReconExporter extends ReconExporter<TTDDataSource> {
    public TTDReconExporter(final TTDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try (final var reader = openFlatFile(workspace, TTDUpdater.DRUG_RAW_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = prefixIdentifier("TTD", entry.getFirst("DRUG__ID"));
                if (id == null)
                    continue;
                createNameRelation(graph, id, entry.getFirst("TRADNAME"));
                createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, entry.getFirst("DRUGINKE")));
                createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.getFirst("DRUGSMIL")));
                createStructureRelationFromInchi(graph, id, entry.getFirst("DRUGINCH"));
            }
        } catch (IOException e) {
            throw new ReconException("Failed to export TTD Flat File Drug", e);
        }
        try (final var reader = openFlatFile(workspace, TTDUpdater.DRUG_CROSSREF_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = prefixIdentifier("TTD", entry.getFirst("TTDDRUID"));
                if (id == null)
                    continue;
                createNameRelation(graph, id, entry.getFirst("DRUGNAME"));
                final String cas = entry.getFirst("CASNUMBE");
                if (StringUtils.isNotBlank(cas))
                    createXrefRelation(graph, id, IdentifierType.CAS.build(cas.replace("CAS", "")));
                if (entry.properties.get("PUBCHCID") != null)
                    for (final var pubChemId : entry.getArray("PUBCHCID"))
                        createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(pubChemId));
                if (entry.properties.get("PUBCHSID") != null)
                    for (final var pubChemId : entry.getArray("PUBCHSID"))
                        createXrefRelation(graph, id, IdentifierType.PUB_CHEM_SUBSTANCE.build(pubChemId));
                createXrefRelation(graph, id, IdentifierType.CHEBI.build(entry.getFirst("ChEBI_ID")));
            }
        } catch (IOException e) {
            throw new ReconException("Failed to export TTD Flat File Drug Cross", e);
        }
        try (final var reader = openFlatFile(workspace, TTDUpdater.DRUG_SYNONYMS_FLAT_FILE)) {
            for (final FlatFileTTDEntry entry : reader) {
                final String id = prefixIdentifier("TTD", entry.getFirst("TTDDRUID"));
                if (id != null) {
                    createNameRelation(graph, id, entry.getFirst("DRUGNAME"));
                    for (final String synonym : entry.properties.get("SYNONYMS")) {
                        if (IdentifierType.CHEBI.matchesGlobal(synonym)) {
                            createXrefRelation(graph, id, synonym);
                        } else if (synonym.startsWith("UNII-")) {
                            createXrefRelation(graph, id, IdentifierType.UNII.build(synonym.substring(5)));
                        } else if (CAS.isCasNumber(synonym)) {
                            createXrefRelation(graph, id, IdentifierType.CAS.build(synonym));
                        } else {
                            createNameRelation(graph, id, synonym);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ReconException("Failed to export TTD Flat File Drug synonyms", e);
        }
    }

    private FlatFileTTDReader openFlatFile(final Workspace workspace, final String fileName) throws IOException {
        return new FlatFileTTDReader(FileUtils.openInput(workspace, dataSource, fileName), StandardCharsets.UTF_8);
    }
}
