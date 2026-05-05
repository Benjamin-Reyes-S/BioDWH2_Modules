package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import de.unibi.agbi.biodwh2.unii.model.UNIIDataEntry;
import de.unibi.agbi.biodwh2.unii.model.UNIIEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UNIIReconExporter extends ReconExporter<UNIIDataSource> {
    public UNIIReconExporter(final UNIIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        final Map<String, List<UNIIEntry>> uniiEntriesMap = new HashMap<>();
        try {
            FileUtils.forEachZipEntryWithPrefix(workspace, dataSource, UNIIUpdater.UNIIS_FILE_NAME, "UNII_Names_",
                                                (stream, e) -> {
                                                    FileUtils.openTsvWithHeader(stream, UNIIEntry.class, (entry) -> {
                                                        if (!uniiEntriesMap.containsKey(entry.unii))
                                                            uniiEntriesMap.put(entry.unii, new ArrayList<>());
                                                        uniiEntriesMap.get(entry.unii).add(entry);
                                                    });
                                                });
            FileUtils.forEachZipEntryWithPrefix(workspace, dataSource, UNIIUpdater.UNII_DATA_FILE_NAME, "UNII_Records_",
                                                (stream, e) -> {
                                                    FileUtils.openTsvWithHeader(stream, UNIIDataEntry.class,
                                                                                (entry) -> {
                                                                                    reconEntry(graph,
                                                                                               uniiEntriesMap.get(
                                                                                                       entry.unii),
                                                                                               entry);
                                                                                });
                                                });
        } catch (Exception e) {
            throw new ReconException("Failed to parse the file '" + UNIIUpdater.UNIIS_FILE_NAME + "'", e);
        }
    }

    private void reconEntry(final Graph graph, final List<UNIIEntry> entries, final UNIIDataEntry dataEntry) {
        final String id = IdentifierType.UNII.build(dataEntry.unii);
        createXrefRelation(graph, id, IdentifierType.CAS.build(dataEntry.rn));
        createXrefRelation(graph, id, IdentifierType.RX_NORM_CUI.build(dataEntry.rxCui));
        createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(dataEntry.pubchem));
        createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX, dataEntry.inchiKey));
        createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, dataEntry.smiles));
        if (entries != null) {
            for (final var entry : entries) {
                // Type: "of" -> "official_names", "sys" -> "systematic_names", "cn" -> "common_names", "cd" -> "codes",
                //       "bn" -> "brand_names"
                if ("cd".equals(entry.type))
                    continue;
                createNameRelation(graph, id, entry.name);
            }
        }
    }
}
