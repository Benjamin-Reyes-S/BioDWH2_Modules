package de.unibi.agbi.biodwh2.lmsd;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.lmsd.etl.LMSDGraphExporter;
import de.unibi.agbi.biodwh2.lmsd.etl.LMSDMappingDescriber;
import de.unibi.agbi.biodwh2.lmsd.etl.LMSDUpdater;

public class LMSDDataSource extends DataSource {
    @Override
    public String getId() {
        return "LMSD";
    }

    @Override
    public String getFullName() {
        return "LIPID MAPS Structure Database (LMSD)";
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
        return new LMSDUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new LMSDGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new LMSDMappingDescriber(this);
    }
}
