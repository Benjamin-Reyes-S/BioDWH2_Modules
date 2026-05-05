package de.unibi.agbi.biodwh2.npass.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npass.etl.NPASSGraphExporter;

@JsonPropertyOrder({
        "target_id", "target_type", "target_name", "target_organism_tax_id", "target_organism", "uniprot_id"
})
@GraphNodeLabel(NPASSGraphExporter.TARGET_LABEL)
public class Target {
    @JsonProperty("target_id")
    @GraphProperty(GraphExporter.ID_KEY)
    public String targetId;
    @JsonProperty("target_type")
    @GraphProperty("type")
    public String targetType;
    @JsonProperty("target_name")
    @GraphProperty("name")
    public String targetName;
    @JsonProperty("target_organism_tax_id")
    public String targetOrganismTaxId;
    @JsonProperty("target_organism")
    public String targetOrganism;
    @JsonProperty("uniprot_id")
    public String uniProtId;
}
