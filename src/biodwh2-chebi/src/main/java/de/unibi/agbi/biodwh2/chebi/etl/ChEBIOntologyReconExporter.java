package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.chebi.ChEBIOntologyDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.chem.CAS;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.io.obo.OboTerm;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ChEBIOntologyReconExporter extends ReconExporter<ChEBIOntologyDataSource> {
    public ChEBIOntologyReconExporter(final ChEBIOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) {
        final String fileName = ChEBIOntologyDataSource.FILE_NAME;
        final boolean ignoreObsolete = dataSource.getBooleanProperty(workspace, "ignoreObsolete");
        final String filePath = dataSource.resolveSourceFilePath(workspace, fileName).toString();
        try (final var reader = new OboReader(filePath, StandardCharsets.UTF_8)) {
            for (final OboEntry entry : reader) {
                if (entry instanceof OboTerm) {
                    reconTerm(graph, (OboTerm) entry);
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void reconTerm(final Graph graph, final OboTerm term) {
        // TODO: subset, synonym
        if (term.isA() != null) {
            for (final var isAId : term.isA()) {
                createOntologyRelation(graph, term.getId(), isAId, "IS_A");
            }
        }
        if (term.getRelationships() != null) {
            for (final var relationship : term.getRelationships()) {
                final var parts = StringUtils.split(relationship, " ", 2);
                if (parts.length == 2) {
                    createOntologyRelation(graph, term.getId(), parts[1], parts[0].toUpperCase(Locale.ROOT));
                }
            }
        }
        if (term.getPropertyValues() != null) {
            for (final var property : term.getPropertyValues()) {
                final var parts = StringUtils.split(property, ":", 3);
                if (parts.length == 3) {
                    if ("xsd:string".equals(parts[2]))
                        parts[1] = StringUtils.strip(parts[1], "\"");
                    switch (parts[0]) {
                        case "http://purl.obolibrary.org/obo/chebi/inchi":
                            createStructureRelationFromInchi(graph, term.getId(), parts[1]);
                            break;
                        case "http://purl.obolibrary.org/obo/chebi/inchikey":
                            createXrefRelation(graph, term.getId(), prefixIdentifier(INCHIKEY_PREFIX, parts[1]));
                            break;
                        case "http://purl.obolibrary.org/obo/chebi/smiles":
                            createXrefRelation(graph, term.getId(), prefixIdentifier(SMILES_PREFIX, parts[1]));
                            break;
                        case "http://purl.obolibrary.org/obo/chebi/charge":
                            // TODO: "-1" xsd:string
                            break;
                    }
                }
            }
        }
        if (term.getXrefs() != null) {
            for (final var xref : term.getXrefs()) {
                final var xrefId = StringUtils.split(xref, " ", 2)[0];
                final var parts = StringUtils.split(xrefId, ":", 2);
                if (parts.length == 2) {
                    switch (parts[0]) {
                        case "CAS":
                            if (CAS.isCasNumber(parts[1])) {
                                createXrefRelation(graph, term.getId(), IdentifierType.CAS.build(parts[1]));
                            }
                            break;
                        case "KEGG":
                            createXrefRelation(graph, term.getId(), IdentifierType.KEGG.build(parts[1]));
                            break;
                        case "DrugBank":
                            createXrefRelation(graph, term.getId(), IdentifierType.DRUG_BANK.build(parts[1]));
                            break;
                        case "Chemspider":
                            createXrefRelation(graph, term.getId(), IdentifierType.CHEMSPIDER.build(parts[1]));
                            break;
                        // Reaxys
                        // MetaCyc
                        // Beilstein
                        // FooDB
                        // HMDB
                        // MolBase
                    }
                }
            }
        }
    }
}
