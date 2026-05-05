package de.unibi.agbi.biodwh2.core.model.graph.mapping;

import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;

import java.util.HashMap;
import java.util.Map;

public class TaxonNodeMappingDescription extends NodeMappingDescription {
    public enum Rank {
        UNKNOWN(null),
        KINGDOM("kingdom"),
        PHYLUM("phylum"),
        CLASS("class"),
        ORDER("order"),
        FAMILY("family"),
        GENUS("genus"),
        SPECIES("species");

        final String value;

        Rank(final String value) {
            this.value = value;
        }
    }

    private Rank rank;

    public TaxonNodeMappingDescription() {
        super(NodeType.TAXON);
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(final Rank rank) {
        this.rank = rank;
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        final Map<String, Object> result = new HashMap<>();
        result.put("rank", rank != null ? rank.value : null);
        return result;
    }
}
