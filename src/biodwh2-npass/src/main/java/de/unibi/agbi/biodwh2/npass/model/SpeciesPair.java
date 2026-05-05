package de.unibi.agbi.biodwh2.npass.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "src_org_pair", "org_id", "np_id", "new_cp_found", "org_isolation_part", "org_collect_location",
        "org_collect_time", "ref_type", "ref_id", "ref_id_type", "ref_url"
})
@GraphEdgeLabel("EXTRACTED_FROM")
public class SpeciesPair {
    @JsonProperty("src_org_pair")
    public String srcOrgPair;
    @JsonProperty("org_id")
    public String orgId;
    @JsonProperty("np_id")
    public String npId;
    @JsonProperty("new_cp_found")
    public String newCpFound;
    @JsonProperty("org_isolation_part")
    @GraphProperty(value = "org_isolation_part", emptyPlaceholder = "n.a.")
    public String orgIsolationPart;
    @JsonProperty("org_collect_location")
    @GraphProperty(value = "org_collect_location", emptyPlaceholder = "n.a.")
    public String orgCollectLocation;
    @JsonProperty("org_collect_time")
    @GraphProperty(value = "org_collect_time", emptyPlaceholder = "n.a.")
    public String orgCollectTime;
    @JsonProperty("ref_type")
    @GraphProperty(value = "ref_type", emptyPlaceholder = "n.a.")
    public String refType;
    @JsonProperty("ref_id")
    @GraphProperty(value = "ref_id", emptyPlaceholder = "n.a.")
    public String refId;
    @JsonProperty("ref_id_type")
    @GraphProperty(value = "ref_id_type", emptyPlaceholder = "n.a.")
    public String refIdType;
    @JsonProperty("ref_url")
    @GraphProperty(value = "ref_url", emptyPlaceholder = "n.a.")
    public String refUrl;
}
