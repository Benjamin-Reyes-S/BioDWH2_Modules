package de.unibi.agbi.biodwh2.gene2phenotype.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "g2p id", "gene symbol", "gene mim", "hgnc id", "previous gene symbols", "disease name", "disease mim",
        "disease MONDO", "allelic requirement", "cross cutting modifier", "confidence", "variant consequence",
        "variant types", "molecular mechanism", "molecular mechanism categorisation", "molecular mechanism evidence",
        "phenotypes", "publications", "panel", "comments", "date of last review"
})
@GraphNodeLabel("Association")
public class GeneDiseasePair {
    @JsonProperty("g2p id")
    @GraphProperty(GraphExporter.ID_KEY)
    public String id;
    @JsonProperty("gene symbol")
    public String geneSymbol;
    @JsonProperty("gene mim")
    public Integer geneMim;
    @JsonProperty("hgnc id")
    public Integer hgncId;
    @JsonProperty("previous gene symbols")
    public String previousGeneSymbols;
    @JsonProperty("disease name")
    public String diseaseName;
    @JsonProperty("disease mim")
    public String diseaseMim;
    @JsonProperty("disease MONDO")
    public String diseaseMondo;
    @JsonProperty("allelic requirement")
    @GraphProperty("allelic_requirement")
    public String allelicRequirement;
    @JsonProperty("cross cutting modifier")
    @GraphArrayProperty(value = "cross_cutting_modifiers", arrayDelimiter = "; ")
    public String crossCuttingModifier;
    @JsonProperty("confidence")
    @GraphProperty("confidence")
    public String confidence;
    @JsonProperty("variant consequence")
    @GraphArrayProperty(value = "variant_consequences", arrayDelimiter = "; ")
    public String variantConsequence;
    @JsonProperty("variant types")
    @GraphArrayProperty(value = "variant_types", arrayDelimiter = "; ")
    public String variantTypes;
    @JsonProperty("molecular mechanism")
    @GraphProperty("molecular_mechanism")
    public String molecularMechanism;
    @JsonProperty("molecular mechanism categorisation")
    @GraphProperty("molecular_mechanism_categorisation")
    public String molecularMechanismCategorisation;
    @JsonProperty("molecular mechanism evidence")
    @GraphProperty("molecular_mechanism_evidence")
    public String molecularMechanismEvidence;
    @JsonProperty("phenotypes")
    public String phenotypes;
    @JsonProperty("publications")
    public String publications;
    @JsonProperty("panel")
    @GraphArrayProperty(value = "panel", arrayDelimiter = "; ")
    public String panel;
    @JsonProperty("comments")
    @GraphProperty("comments")
    public String comments;
    @JsonProperty("date of last review")
    @GraphProperty("last_reviewed_at")
    public String dateOfLastReview;
}
