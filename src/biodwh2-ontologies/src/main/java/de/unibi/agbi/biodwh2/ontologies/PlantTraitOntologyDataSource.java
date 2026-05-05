package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class PlantTraitOntologyDataSource extends OBOFoundryOntologyDataSource {
    public PlantTraitOntologyDataSource() {
        super("PlantTraitOntology", "to.obo", License.CC_BY_4_0, "Plant Trait Ontology (TO)", "TO",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
