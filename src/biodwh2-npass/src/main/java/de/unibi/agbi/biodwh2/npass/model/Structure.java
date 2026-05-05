package de.unibi.agbi.biodwh2.npass.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npass.etl.NPASSGraphExporter;

@JsonPropertyOrder({"np_id", "InChI", "InChIKey", "SMILES"})
@GraphNodeLabel(NPASSGraphExporter.COMPOUND_LABEL)
public class Structure {
    @JsonProperty("np_id")
    public String npId;
    @JsonProperty("InChI")
    @GraphProperty(value = "inchi", emptyPlaceholder = "n.a.")
    public String inchi;
    @JsonProperty("InChIKey")
    @GraphProperty(value = "inchi_key", emptyPlaceholder = "n.a.")
    public String inchiKey;
    @JsonProperty("SMILES")
    @GraphProperty(value = "smiles", emptyPlaceholder = "n.a.")
    public String smiles;
}
