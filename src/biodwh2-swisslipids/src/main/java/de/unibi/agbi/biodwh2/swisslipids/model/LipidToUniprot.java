package de.unibi.agbi.biodwh2.swisslipids.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "metabolite id", "UniprotKB IDs", "level", "metabolite name", "abbreviations", "synonyms", "lipid class",
        "components", "PMIDs", "SMILES (pH7.3)", "InChI (pH7.3)", "InChI key (pH7.3)", "Formula (pH7.3)",
        "Mass (pH7.3)", "Charge (pH7.3)", "Exact Mass (neutral form)", "Exact m/z of [M.]+", "Exact m/z of [M+H]+",
        "Exact m/z of [M+K]+", "Exact m/z of [M+Na]+", "Exact m/z of [M+Li]+", "Exact m/z of [M+NH4]+",
        "Exact m/z of [M-H]-", "Exact m/z of [M+Cl]-", "Exact m/z of [M+OAc]-", "ChEBI", "LipidMaps", "HMDB",
        "Mapping level"
})
public class LipidToUniprot {
    @JsonProperty("metabolite id")
    public String metaboliteId;
    @JsonProperty("UniprotKB IDs")
    public String uniprotIds;
    @JsonProperty("level")
    public String level;
    @JsonProperty("metabolite name")
    public String metaboliteName;
    @JsonProperty("abbreviations")
    public String abbreviations;
    @JsonProperty("synonyms")
    public String synonyms;
    @JsonProperty("lipid class")
    public String lipidClass;
    @JsonProperty("components")
    public String components;
    @JsonProperty("PMIDs")
    public String pmids;
    @JsonProperty("SMILES (pH7.3)")
    public String smilespH7_3;
    @JsonProperty("InChI (pH7.3)")
    public String inchipH7_3;
    @JsonProperty("InChI key (pH7.3)")
    public String inchiKeypH7_3;
    @JsonProperty("Formula (pH7.3)")
    public String formulapH7_3;
    @JsonProperty("Charge (pH7.3)")
    public String chargepH7_3;
    @JsonProperty("Mass (pH7.3)")
    public String masspH7_3;
    @JsonProperty("Exact Mass (neutral form)")
    public String exactMassNeutralForm;
    @JsonProperty("Exact m/z of [M.]+")
    public String exactMZofM;
    @JsonProperty("Exact m/z of [M+H]+")
    public String exactMZofMHPlus;
    @JsonProperty("Exact m/z of [M+K]+")
    public String exactMZofMK;
    @JsonProperty("Exact m/z of [M+Na]+")
    public String exactMZofMNa;
    @JsonProperty("Exact m/z of [M+Li]+")
    public String exactMZofMLi;
    @JsonProperty("Exact m/z of [M+NH4]+")
    public String exactMZofMNH4;
    @JsonProperty("Exact m/z of [M-H]-")
    public String exactMZofMHMinus;
    @JsonProperty("Exact m/z of [M+Cl]-")
    public String exactMZofMCl;
    @JsonProperty("Exact m/z of [M+OAc]-")
    public String exactMZofMOAc;
    @JsonProperty("ChEBI")
    public String chebi;
    @JsonProperty("LipidMaps")
    public String lipidMaps;
    @JsonProperty("HMDB")
    public String hmdb;
    @JsonProperty("Mapping level")
    public String mappingLevel;
}
