package de.unibi.agbi.biodwh2.npass.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npass.etl.NPASSGraphExporter;

@JsonPropertyOrder({
        "org_id", "org_name", "org_tax_level", "org_tax_id", "subspecies_tax_id", "subspecies_name", "species_tax_id",
        "species_name", "genus_tax_id", "genus_name", "family_tax_id", "family_name", "kingdom_tax_id", "kingdom_name",
        "superkingdom_tax_id", "superkingdom_name"
})
@GraphNodeLabel(NPASSGraphExporter.SPECIES_LABEL)
public class Species {
    @JsonProperty("org_id")
    @GraphProperty(GraphExporter.ID_KEY)
    public String orgId;
    @JsonProperty("org_name")
    @GraphProperty("name")
    public String orgName;
    @JsonProperty("org_tax_level")
    @GraphProperty(value = "tax_level", emptyPlaceholder = "n.a.")
    public String orgTaxLevel;
    @JsonProperty("org_tax_id")
    @GraphNumberProperty(value = "tax_id", emptyPlaceholder = "n.a.")
    public String orgTaxId;
    @JsonProperty("subspecies_tax_id")
    @GraphNumberProperty(value = "subspecies_tax_id", emptyPlaceholder = "n.a.", ignoreOnError = true)
    public String subspeciesTaxId;
    @JsonProperty("subspecies_name")
    @GraphProperty(value = "subspecies_name", emptyPlaceholder = "n.a.")
    public String subspeciesName;
    @JsonProperty("species_tax_id")
    @GraphNumberProperty(value = "species_tax_id", emptyPlaceholder = "n.a.")
    public String speciesTaxId;
    @JsonProperty("species_name")
    @GraphProperty(value = "species_name", emptyPlaceholder = "n.a.")
    public String speciesName;
    @JsonProperty("genus_tax_id")
    @GraphNumberProperty(value = "genus_tax_id", emptyPlaceholder = "n.a.")
    public String genusTaxId;
    @JsonProperty("genus_name")
    @GraphProperty(value = "genus_name", emptyPlaceholder = "n.a.")
    public String genusName;
    @JsonProperty("family_tax_id")
    @GraphNumberProperty(value = "family_tax_id", emptyPlaceholder = "n.a.")
    public String familyTaxId;
    @JsonProperty("family_name")
    @GraphProperty(value = "family_name", emptyPlaceholder = "n.a.")
    public String familyName;
    @JsonProperty("kingdom_tax_id")
    @GraphNumberProperty(value = "kingdom_tax_id", emptyPlaceholder = "n.a.")
    public String kingdomTaxId;
    @JsonProperty("kingdom_name")
    @GraphProperty(value = "kingdom_name", emptyPlaceholder = "n.a.")
    public String kingdomName;
    @JsonProperty("superkingdom_tax_id")
    @GraphNumberProperty(value = "superkingdom_tax_id", emptyPlaceholder = "n.a.")
    public String superkingdomTaxId;
    @JsonProperty("superkingdom_name")
    @GraphProperty(value = "superkingdom_name", emptyPlaceholder = "n.a.")
    public String superkingdomName;
}
