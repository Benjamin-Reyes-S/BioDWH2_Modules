package de.unibi.agbi.biodwh2.core.chem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InChITest {
    @Test
    void isValid() {
        assertFalse(new InChI(null).isValid());
        assertFalse(new InChI("").isValid());
        assertFalse(new InChI("InChI=").isValid());
        assertFalse(new InChI("InChI=1S/").isValid());
        assertTrue(new InChI("InChI=1S/Mg").isValid());
        assertTrue(new InChI("InChI=1S/Mg/q+2").isValid());
    }

    @Test
    void isStandardInChI() {
        assertFalse(new InChI(null).isStandardInChI());
        assertFalse(new InChI("").isStandardInChI());
        assertFalse(new InChI("InChI=").isStandardInChI());
        assertFalse(new InChI("InChI=1/").isStandardInChI());
        assertTrue(new InChI("InChI=1S/").isStandardInChI());
        assertTrue(new InChI("InChI=1S/Mg").isStandardInChI());
        assertTrue(new InChI("InChI=1S/Mg/q+2").isStandardInChI());
    }

    @Test
    void getLayer() {
        assertNull(new InChI("InChI=1S/Mg").getLayer(InChI.LAYER_KEY_CHARGE_SUB_CHARGE));
        assertEquals("+2", new InChI("InChI=1S/Mg/q+2").getLayer(InChI.LAYER_KEY_CHARGE_SUB_CHARGE));
    }
}