package de.unibi.agbi.biodwh2.ncbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"tax_id", "name_txt", "unique_name", "name_class", "extra"})
public class TaxonName {
    @JsonProperty("tax_id")
    public String taxId;

    @JsonProperty("name_txt")
    public String nameTxt;

    @JsonProperty("unique_name")
    public String uniqueName;

    @JsonProperty("name_class")
    public String nameClass;

    @JsonProperty("extra")
    public String extra;
}