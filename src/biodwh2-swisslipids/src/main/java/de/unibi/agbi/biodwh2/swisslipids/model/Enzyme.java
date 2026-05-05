package de.unibi.agbi.biodwh2.swisslipids.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "SwissLipids ID", "UniProtKB AC(s)", "Gene name", "Protein taxon", "Taxon scientific name", "Rhea ID",
        "Reaction text", "Evidence tag ID", ""
})
public class Enzyme {
    @JsonProperty("SwissLipids ID")
    public String id;
    @JsonProperty("UniProtKB AC(s)")
    public String uniprotAccessions;
    @JsonProperty("Gene name")
    public String geneName;
    @JsonProperty("Protein taxon")
    public Integer proteinTaxon;
    @JsonProperty("Taxon scientific name")
    public String taxonScientificName;
    @JsonProperty("Rhea ID")
    public String rheaId;
    @JsonProperty("Reaction text")
    public String reactionText;
    @JsonProperty("Evidence tag ID")
    public String evidenceTagId;
    @JsonProperty("")
    public String overflowColumn;
}
