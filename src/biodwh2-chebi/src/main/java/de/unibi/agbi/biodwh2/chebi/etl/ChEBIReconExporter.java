package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.chebi.ChEBIDataSource;
import de.unibi.agbi.biodwh2.chebi.model.ChEBIIdInchi;
import de.unibi.agbi.biodwh2.chebi.model.DBAccession;
import de.unibi.agbi.biodwh2.chebi.model.Name;
import de.unibi.agbi.biodwh2.chebi.model.Structure;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ReconException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

import java.io.IOException;
import java.util.Locale;

public class ChEBIReconExporter extends ReconExporter<ChEBIDataSource> {
    public ChEBIReconExporter(final ChEBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) throws ReconException {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.INCHI_FILE_NAME, ChEBIIdInchi.class,
                                        (entry) -> createStructureRelationFromInchi(graph, IdentifierType.CHEBI.build(
                                                entry.chebiId), entry.inchi));
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + ChEBIUpdater.INCHI_FILE_NAME + "'", e);
        }
        try {
            FileUtils.openGzipTsvWithHeader(workspace, dataSource, ChEBIUpdater.NAMES_FILE_NAME, Name.class,
                                            (entry) -> createNameRelation(graph,
                                                                          IdentifierType.CHEBI.build(entry.compoundId),
                                                                          entry.name));
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + ChEBIUpdater.NAMES_FILE_NAME + "'", e);
        }
        try {
            FileUtils.openGzipCsvWithHeader(workspace, dataSource, ChEBIUpdater.STRUCTURES_FILE_NAME, Structure.class,
                                            (entry) -> {
                                                final var id = IdentifierType.CHEBI.build(entry.compoundId);
                                                switch (entry.type.toLowerCase(Locale.ROOT)) {
                                                    case "inchi":
                                                        createStructureRelationFromInchi(graph, id, entry.structure);
                                                        break;
                                                    case "smiles":
                                                        createXrefRelation(graph, id, prefixIdentifier(SMILES_PREFIX,
                                                                                                       entry.structure));
                                                        break;
                                                    case "inchikey":
                                                        createXrefRelation(graph, id, prefixIdentifier(INCHIKEY_PREFIX,
                                                                                                       entry.structure));
                                                        break;
                                                    case "mol":
                                                        createStructureRelationFromMol(graph, id, entry.structure);
                                                        break;
                                                }
                                            });
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + ChEBIUpdater.STRUCTURES_FILE_NAME + "'", e);
        }
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, ChEBIUpdater.DATABASE_ACCESSION_FILE_NAME,
                                        DBAccession.class, (entry) -> {
                        final var id = IdentifierType.CHEBI.build(entry.compoundId);
                        switch (entry.type) {
                            case "CAS Registry Number":
                                createXrefRelation(graph, id, IdentifierType.CAS.build(entry.accessionNumber));
                                break;
                            case "DrugBank accession":
                                createXrefRelation(graph, id, IdentifierType.DRUG_BANK.build(entry.accessionNumber));
                                break;
                            case "KEGG GLYCAN accession":
                            case "KEGG DRUG accession":
                            case "KEGG COMPOUND accession":
                                createXrefRelation(graph, id, IdentifierType.KEGG.build(entry.accessionNumber));
                                break;
                            case "Drug Central accession":
                                createXrefRelation(graph, id, IdentifierType.DRUG_CENTRAL.build(entry.accessionNumber));
                                break;
                            case "Pubchem accession":
                                // TODO: CID or SID?
                                createXrefRelation(graph, id,
                                                   IdentifierType.PUB_CHEM_COMPOUND.build(entry.accessionNumber));
                                break;
                            case "Chemspider accession":
                                createXrefRelation(graph, id, IdentifierType.CHEMSPIDER.build(entry.accessionNumber));
                                break;
                            case "Pesticides accession": // /derivatives/2,4-d-choline
                            case "PDB accession": // 1A0Q
                            case "MolBase accession": // 1006
                            case "LINCS accession": // LSM-1001
                            case "FooDB accession": // FDB000082
                            case "GlyGen accession": // G00024MO
                            case "GlyTouCan accession": // G00024MO
                            case "Gmelin Registry Number": // 100331
                            case "COMe accession": // BIM000134, MOL000016
                            case "Beilstein Registry Number": // 0035194
                            case "BPDB accession": // 1069
                            case "PPDB accession": // 987
                            case "PPR": // PPR103739
                            case "YMDB accession": // YMDB00110
                            case "ECMDB accession": // ECMDB00902
                            case "MetaCyc accession": // CPD-1132
                            case "KNApSAcK accession": // C00007530
                            case "Reaxys Registry Number": // 1910025
                            case "HMDB accession": // HMDB0006465
                            case "PDBeChem accession": // RYN
                            case "LIPID MAPS instance accession":
                            case "LIPID MAPS class accession":
                            case "Agricola citation":
                            case "WebElements accession": // Pb
                            case "VSDB accession": // 625
                            case "UM-BBD compID": // c1128
                            case "SMID accession": // oscr%2332%0D
                            case "RESID accession": // AA0537
                                break;
                        }
                    });
        } catch (IOException e) {
            throw new ReconException("Failed to export '" + ChEBIUpdater.DATABASE_ACCESSION_FILE_NAME + "'", e);
        }
    }
}
