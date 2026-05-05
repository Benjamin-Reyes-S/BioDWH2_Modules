package de.unibi.agbi.biodwh2.swisslipids.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Lipid ID", "Lipid name", "Tissue/Cell ID", "Tissue/Cell name", "Taxon ID", "Taxon scientific name",
        "Evidence tag ID"
})
public class Tissue {
    @JsonProperty("Lipid ID")
    public String lipidId;
    @JsonProperty("Lipid name")
    public String lipidName;
    @JsonProperty("Tissue/Cell ID")
    public String tissueCellId;
    @JsonProperty("Tissue/Cell name")
    public String tissueCellName;
    @JsonProperty("Taxon ID")
    public Integer taxonId;
    @JsonProperty("Taxon scientific name")
    public String taxonScientificName;
    @JsonProperty("Evidence tag ID")
    public String evidenceTagId;
}
