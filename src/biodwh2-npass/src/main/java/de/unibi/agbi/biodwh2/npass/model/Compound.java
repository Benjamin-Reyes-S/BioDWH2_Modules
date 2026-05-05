package de.unibi.agbi.biodwh2.npass.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "np_id", "pref_name", "iupac_name", "chembl_id", "pubchem_cid", "num_of_organism", "num_of_target",
        "num_of_activity", "if_has_Quantity"
})
public class Compound {
    @JsonProperty("np_id")
    @GraphProperty(GraphExporter.ID_KEY)
    public String npId;
    @JsonProperty("pref_name")
    public String prefName;
    @JsonProperty("iupac_name")
    @GraphProperty(value = "iupac_name", emptyPlaceholder = "n.a.")
    public String iupacName;
    @JsonProperty("chembl_id")
    @GraphProperty(value = "chembl_id", emptyPlaceholder = "n.a.")
    public String chemblId;
    @JsonProperty("pubchem_cid")
    public String pubChemCid;
    @JsonProperty("num_of_organism")
    public String numOfOrganism;
    @JsonProperty("num_of_target")
    public String numOfTarget;
    @JsonProperty("num_of_activity")
    public String numOfActivity;
    @JsonProperty("if_has_Quantity")
    @GraphBooleanProperty(value = "if_has_quantity", truthValue = "Yes")
    public String ifHasQuantity;
}
