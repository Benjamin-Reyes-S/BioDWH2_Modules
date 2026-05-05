package de.unibi.agbi.biodwh2.swisslipids;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.swisslipids.etl.SwissLipidsGraphExporter;
import de.unibi.agbi.biodwh2.swisslipids.etl.SwissLipidsMappingDescriber;
import de.unibi.agbi.biodwh2.swisslipids.etl.SwissLipidsUpdater;

public class SwissLipidsDataSource extends DataSource {
    @Override
    public String getId() {
        return "SwissLipids";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new SwissLipidsUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new SwissLipidsGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new SwissLipidsMappingDescriber(this);
    }
}
