package de.unibi.agbi.biodwh2.ttd.etl;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatFileTTDEntry {
    private String id;
    public final Map<String, List<String>> properties;

    public FlatFileTTDEntry() {
        properties = new HashMap<>();
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getFirst(final String key) {
        final List<String> values = properties.get(key);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }


    public String[] getArray(final String key) {
        final String[] result = StringUtils.split(getFirst(key), ';');
        for (int i = 0; i < result.length; i++)
            result[i] = result[i].trim();
        return result;
    }
}
