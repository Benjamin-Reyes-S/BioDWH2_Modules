package de.unibi.agbi.biodwh2.chebi;

import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIMappingDescriber;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIReconExporter;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;

public class ChEBIDataSource extends DataSource {
    @Override
    public String getId() {
        return "ChEBI";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ChEBIUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ChEBIGraphExporter(this);
    }

    @Override
    public ReconExporter<? extends DataSource> getReconExporter() {
        return new ChEBIReconExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ChEBIMappingDescriber(this);
    }
}
