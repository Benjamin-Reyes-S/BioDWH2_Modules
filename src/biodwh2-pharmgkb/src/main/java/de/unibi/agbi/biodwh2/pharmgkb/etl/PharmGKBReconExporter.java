package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.chem.CAS;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.ClassMapping;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.Chemical;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class PharmGKBReconExporter extends ReconExporter<PharmGKBDataSource> {
    private static final ClassMapping CHEMICAL_CLASS_MAPPING = ClassMapping.get(Chemical.class);

    public PharmGKBReconExporter(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try {
            FileUtils.forEachZipEntryWithSuffix(workspace, dataSource, PharmGKBUpdater.CHEMICALS_FILE_NAME, ".tsv",
                                                (stream, entry) -> reconChemicals(graph, stream));
        } catch (Exception e) {
            throw new ReconException("Failed to export '" + PharmGKBUpdater.CHEMICALS_FILE_NAME + "'", e);
        }
    }

    private void reconChemicals(final Graph graph, final InputStream stream) throws IOException {
        FileUtils.openTsvWithHeader(stream, Chemical.class, (entry) -> {
            try {
                reconChemical(graph, entry);
            } catch (final IllegalAccessException e) {
                throw new IOException(e);
            }
        });
    }

    private void reconChemical(final Graph graph, final Chemical entry) throws IllegalAccessException {
        final var id = IdentifierType.PHARM_GKB.build(entry.pharmgkbAccessionId);
        createNameRelation(graph, id, entry.name);
        createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX, entry.smiles));
        createStructureRelationFromInchi(graph, id, entry.inchi);
        final var genericNames = CHEMICAL_CLASS_MAPPING.<String[]>getValue(entry, "genericNames");
        if (genericNames != null) {
            for (final var genericName : genericNames)
                createNameRelation(graph, id, genericName);
        }
        final var tradeNames = CHEMICAL_CLASS_MAPPING.<String[]>getValue(entry, "tradeNames");
        if (tradeNames != null) {
            for (final var tradeName : tradeNames)
                createNameRelation(graph, id, tradeName);
        }
        final var pubChemIds = CHEMICAL_CLASS_MAPPING.<String[]>getValue(entry, "pubChemCompoundIdentifiers");
        if (pubChemIds != null) {
            for (final var pubChemId : pubChemIds)
                createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(pubChemId));
        }
        final var rxNormIds = CHEMICAL_CLASS_MAPPING.<String[]>getValue(entry, "rxNormIdentifiers");
        if (rxNormIds != null) {
            for (final var rxNormId : rxNormIds)
                createXrefRelation(graph, id, IdentifierType.RX_NORM_CUI.build(rxNormId));
        }
        final var crossReferences = CHEMICAL_CLASS_MAPPING.<String[]>getValue(entry, "crossReferences");
        if (crossReferences != null) {
            for (final var crossReference : crossReferences) {
                final var parts = StringUtils.split(crossReference, ":", 2);
                if (parts.length != 2)
                    continue;
                switch (parts[0]) {
                    case "ChEBI":
                        createXrefRelation(graph, id, IdentifierType.CHEBI.build(parts[1]));
                        break;
                    case "ChEMBL":
                        createXrefRelation(graph, id, IdentifierType.CHEMBL.build(parts[1]));
                        break;
                    case "ChemSpider":
                        createXrefRelation(graph, id, IdentifierType.CHEMSPIDER.build(parts[1]));
                        break;
                    case "PubChem Compound":
                        createXrefRelation(graph, id, IdentifierType.PUB_CHEM_COMPOUND.build(parts[1]));
                        break;
                    case "PubChem Substance":
                        createXrefRelation(graph, id, IdentifierType.PUB_CHEM_SUBSTANCE.build(parts[1]));
                        break;
                    case "Therapeutic Targets Database":
                        createXrefRelation(graph, id, prefixIdentifier("TTD", parts[1]));
                        break;
                    case "KEGG Compound":
                    case "KEGG Drug":
                        createXrefRelation(graph, id, IdentifierType.KEGG.build(parts[1]));
                        break;
                    case "DrugBank":
                    case "DrugBank Metabolite":
                        createXrefRelation(graph, id, IdentifierType.DRUG_BANK.build(parts[1]));
                        break;
                    case "CAS":
                        if (CAS.isCasNumber(parts[1]))
                            createXrefRelation(graph, id, IdentifierType.CAS.build(parts[1]));
                        break;
                    case "PDB Ligand":
                    case "BindingDB":
                    case "HMDB":
                    case "DPD":
                    case "IUPHAR Ligand":
                    case "NDC": // 0002-0604-40
                        break;
                }
            }
        }
    }
}
