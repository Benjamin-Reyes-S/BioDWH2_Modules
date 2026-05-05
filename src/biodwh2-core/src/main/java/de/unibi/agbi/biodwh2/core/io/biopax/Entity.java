package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public abstract class Entity {
    @JacksonXmlProperty(isAttribute = true)
    @GraphProperty("about")
    public String about;
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("comment")
    public String[] comment;
    public ResourceRef dataSource;
    public ResourceRef evidence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] xref;
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("name")
    public String[] name;
    @GraphProperty("display_name")
    public String displayName;
    @GraphProperty("standard_name")
    public String standardName;
    @GraphProperty("availability")
    public String availability;
}
