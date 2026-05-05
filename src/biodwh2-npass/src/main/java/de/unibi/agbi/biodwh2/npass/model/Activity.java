package de.unibi.agbi.biodwh2.npass.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npass.etl.NPASSGraphExporter;

@JsonPropertyOrder({
        "np_id", "target_id", "activity_type_grouped", "activity_relation", "activity_type", "activity_value",
        "activity_units", "assay_organism", "assay_tax_id", "assay_strain", "assay_tissue", "assay_cell_type", "ref_id",
        "ref_id_type"
})
@GraphNodeLabel(NPASSGraphExporter.ACTIVITY_LABEL)
public class Activity {
    @JsonProperty("np_id")
    public String npId;
    @JsonProperty("target_id")
    public String targetId;
    @JsonProperty("activity_type_grouped")
    @GraphProperty(value = "activity_type_grouped", emptyPlaceholder = "n.a.")
    public String activityTypeGrouped;
    @JsonProperty("activity_relation")
    @GraphProperty(value = "activity_relation", emptyPlaceholder = "n.a.")
    public String activityRelation;
    @JsonProperty("activity_type")
    @GraphProperty(value = "activity_type", emptyPlaceholder = "n.a.")
    public String activityType;
    @JsonProperty("activity_value")
    @GraphProperty(value = "activity_value", emptyPlaceholder = "n.a.")
    public String activityValue;
    @JsonProperty("activity_units")
    @GraphProperty(value = "activity_units", emptyPlaceholder = "n.a.")
    public String activityUnits;
    @JsonProperty("assay_organism")
    public String assayOrganism;
    @JsonProperty("assay_tax_id")
    public String assayTaxId;
    @JsonProperty("assay_strain")
    @GraphProperty(value = "assay_strain", emptyPlaceholder = "n.a.")
    public String assayStrain;
    @JsonProperty("assay_tissue")
    @GraphProperty(value = "assay_tissue", emptyPlaceholder = "n.a.")
    public String assayTissue;
    @JsonProperty("assay_cell_type")
    @GraphProperty(value = "assay_cell_type", emptyPlaceholder = "n.a.")
    public String assayCellType;
    @JsonProperty("ref_id")
    @GraphProperty(value = "ref_id", emptyPlaceholder = "n.a.")
    public String refId;
    @JsonProperty("ref_id_type")
    @GraphProperty(value = "ref_id_type", emptyPlaceholder = "n.a.")
    public String refIdType;
}
