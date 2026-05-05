package de.unibi.agbi.biodwh2.npass;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.npass.etl.NPASSGraphExporter;
import de.unibi.agbi.biodwh2.npass.etl.NPASSMappingDescriber;
import de.unibi.agbi.biodwh2.npass.etl.NPASSReconExporter;
import de.unibi.agbi.biodwh2.npass.etl.NPASSUpdater;

public class NPASSDataSource extends DataSource {
    @Override
    public String getId() {
        return "NPASS";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new NPASSUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new NPASSGraphExporter(this);
    }

    @Override
    public ReconExporter<? extends DataSource> getReconExporter() {
        return new NPASSReconExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NPASSMappingDescriber(this);
    }
}
