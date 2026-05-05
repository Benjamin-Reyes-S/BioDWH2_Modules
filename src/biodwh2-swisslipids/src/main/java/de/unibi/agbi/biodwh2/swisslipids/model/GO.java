package de.unibi.agbi.biodwh2.swisslipids.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Lipid ID", "Lipid name", "GO ID", "GO term", "Taxon ID", "Taxon scientific name", "Evidence tag ID"
})
public class GO {
    @JsonProperty("Lipid ID")
    public String lipidId;
    @JsonProperty("Lipid name")
    public String lipidName;
    @JsonProperty("GO ID")
    public String goId;
    @JsonProperty("GO term")
    public String goTerm;
    @JsonProperty("Taxon ID")
    public Integer taxonId;
    @JsonProperty("Taxon scientific name")
    public String taxonScientificName;
    @JsonProperty("Evidence tag ID")
    public String evidenceTagId;
}
