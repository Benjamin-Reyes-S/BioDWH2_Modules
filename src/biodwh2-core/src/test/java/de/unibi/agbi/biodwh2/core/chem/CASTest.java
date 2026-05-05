package de.unibi.agbi.biodwh2.core.chem;

import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CASTest {
    @Test
    void isCasNumber() {
        assertFalse(IdentifierUtils.isCasNumber(null));
        assertFalse(IdentifierUtils.isCasNumber(""));
        assertFalse(IdentifierUtils.isCasNumber("not a cas number"));
        assertFalse(IdentifierUtils.isCasNumber("999998732-18-5"));
        assertTrue(IdentifierUtils.isCasNumber("7732-18-5"));
        assertFalse(IdentifierUtils.isCasNumber("8732-18-5"));
    }
}