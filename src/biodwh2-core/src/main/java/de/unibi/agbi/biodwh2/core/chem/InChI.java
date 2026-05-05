package de.unibi.agbi.biodwh2.core.chem;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * InChI - IUPAC International Chemical Identifier
 * <p>
 * For more details see the publication: <a href="https://doi.org/10.1186/s13321-015-0068-4">InChI, the IUPAC
 * International Chemical Identifier</a>
 */
public class InChI {
    public static final String PREFIX = "InChI=";
    public static final char LAYER_KEY_SKELETAL_CONNECTIONS = 'c';
    public static final char LAYER_KEY_HYDROGENS = 'h';
    public static final char LAYER_KEY_CHARGE_SUB_CHARGE = 'q';
    public static final char LAYER_KEY_CHARGE_SUB_DE_PROTONATION = 'p';
    public static final char LAYER_KEY_FIXEDH = 'f';
    public static final char LAYER_KEY_STEREOCHEMISTRY_SUB_DOUBLE_BOND = 'b';
    public static final char LAYER_KEY_STEREOCHEMISTRY_SUB_TETRAHEDRAL = 't';
    public static final char LAYER_KEY_ISOTOPIC = 'i';
    public static final char LAYER_KEY_RECONNECTED = 'r';

    private final String value;
    private final String version;
    private final String formula;
    private final Map<Character, String> layers = new HashMap<>();
    private final boolean isValid;

    public InChI(final String value) {
        this.value = value;
        if (StringUtils.isEmpty(value) || !value.startsWith(PREFIX)) {
            isValid = false;
            version = null;
            formula = null;
        } else {
            final String[] parts = StringUtils.split(value.substring(PREFIX.length()), '/');
            if (parts.length < 2) {
                isValid = false;
                version = parts.length > 0 ? parts[0] : null;
                formula = null;
            } else {
                version = parts[0];
                formula = parts[1];
                for (int i = 2; i < parts.length; i++) {
                    final char layerPrefix = parts[i].charAt(0);
                    layers.put(layerPrefix, parts[i].substring(1));
                }
                isValid = true;
            }
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public String getVersion() {
        return version;
    }

    public String getFormula() {
        return formula;
    }

    public Collection<String> getAdditionalLayers() {
        return layers.values();
    }

    public String getLayer(final char key) {
        return layers.get(key);
    }

    public boolean isStandardInChI() {
        return version != null && version.length() > 1 && version.charAt(version.length() - 1) == 'S';
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
