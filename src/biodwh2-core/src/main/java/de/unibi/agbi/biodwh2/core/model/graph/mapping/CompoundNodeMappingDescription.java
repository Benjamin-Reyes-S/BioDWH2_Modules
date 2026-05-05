package de.unibi.agbi.biodwh2.core.model.graph.mapping;

import de.unibi.agbi.biodwh2.core.chem.InChI;
import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CompoundNodeMappingDescription extends NodeMappingDescription {
    private String formula;
    private String inchi;
    private String inchiKey;

    public CompoundNodeMappingDescription() {
        super(NodeType.COMPOUND);
    }

    public String getInchi() {
        return inchi;
    }

    public void setInchi(final String inchi) {
        final var value = new InChI(inchi);
        // Only add valid and standard InChI
        if (value.isValid() && value.isStandardInChI()) {
            this.inchi = inchi;
            formula = value.getFormula();
        }
    }

    public String getInchiKey() {
        return inchiKey;
    }

    public void setInchiKey(final String inchiKey) {
        this.inchiKey = inchiKey;
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        final Map<String, Object> result = new HashMap<>();
        return result;
    }
}
