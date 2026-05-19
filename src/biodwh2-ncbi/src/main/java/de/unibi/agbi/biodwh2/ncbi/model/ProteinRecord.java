package de.unibi.agbi.biodwh2.ncbi.model;

public class ProteinRecord {
    private String proteinId;
    private String version;
    private String locus;
    private String dbLink;
    private String keyword;
    private String source;
    private String definition;

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(final String proteinId) {
        this.proteinId = proteinId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getLocus() {
        return locus;
    }

    public void setLocus(final String locus) {
        this.locus = locus;
    }

    public String getDbLink() {
        return dbLink;
    }

    public void setDbLink(final String dbLink) {
        this.dbLink = dbLink;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(final String definition) {
        this.definition = definition;
    }
}