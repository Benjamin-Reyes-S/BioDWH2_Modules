package de.unibi.agbi.biodwh2.ncbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "tax_id",
    "parent_tax_id",
    "rank",
    "embl_code",
    "division_id",
    "inherited_div_flag",
    "genetic_code_id",
    "inherited_gc_flag",
    "mitochondrial_genetic_code_id",
    "inherited_mgc_flag",
    "genbank_hidden_flag",
    "hidden_subtree_root_flag",
    "comments",
    "extra"
})
public class TaxonNode {
    @JsonProperty("tax_id")
    public String taxId;

    @JsonProperty("parent_tax_id")
    public String parentTaxId;

    @JsonProperty("rank")
    public String rank;

    @JsonProperty("embl_code")
    public String emblCode;

    @JsonProperty("division_id")
    public String divisionId;

    @JsonProperty("inherited_div_flag")
    public String inheritedDivFlag;

    @JsonProperty("genetic_code_id")
    public String geneticCodeId;

    @JsonProperty("inherited_gc_flag")
    public String inheritedGcFlag;

    @JsonProperty("mitochondrial_genetic_code_id")
    public String mitochondrialGeneticCodeId;

    @JsonProperty("inherited_mgc_flag")
    public String inheritedMgcFlag;

    @JsonProperty("genbank_hidden_flag")
    public String genbankHiddenFlag;

    @JsonProperty("hidden_subtree_root_flag")
    public String hiddenSubtreeRootFlag;

    @JsonProperty("comments")
    public String comments;

    @JsonProperty("extra")
    public String extra;
}