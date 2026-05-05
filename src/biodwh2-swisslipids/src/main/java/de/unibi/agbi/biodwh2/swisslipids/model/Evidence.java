package de.unibi.agbi.biodwh2.swisslipids.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Evidence ID", "ECO ID", "ECO definition", "PMID ID", "Figure legend"})
public class Evidence {
    @JsonProperty("Evidence ID")
    public Integer id;
    @JsonProperty("ECO ID")
    public String ecoId;
    @JsonProperty("ECO definition")
    public String ecoDefinition;
    @JsonProperty("PMID ID")
    public Integer pmid;
    @JsonProperty("Figure legend")
    public String figureLegend;
}
