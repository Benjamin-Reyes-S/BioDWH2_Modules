package de.unibi.agbi.biodwh2.core.chem;

import java.math.BigDecimal;

public final class ChemConstants {
    /**
     * The reciprocal mole (mol<sup>-1</sup>) is defined as the amount of substance containing exactly
     * <em>6.02214076 x 10<sup>23</sup></em> particles (as per the 2019 revision of the mol). This means 1 mole of a
     * substance is exactly the product of the Avogadro number and the average mass of its particles.
     */
    public static final BigDecimal AVOGADRO_CONSTANT = new BigDecimal("6.02214076E23");
    /**
     * 1 dalton (Da) or 1 unified atomic mass unit (u) in kilogram (Kg).
     * <p>
     * 1 dalton (Da) or 1 unified atomic mass unit (u) is defined as 1/12 of the mass of an unbound neutral
     * <sup>12</sup>C atom in its nuclear and electronic ground state and at rest.
     */
    public static final BigDecimal DALTON_KG = new BigDecimal("1.66053906892E−27");
    /**
     * 1 dalton (Da) or 1 unified atomic mass unit (u) in Mega electron-volt (MeV).
     * <p>
     * 1 dalton (Da) or 1 unified atomic mass unit (u) is defined as 1/12 of the mass of an unbound neutral
     * <sup>12</sup>C atom in its nuclear and electronic ground state and at rest.
     */
    public static final BigDecimal DALTON_MeV = new BigDecimal("931.49410372");
    /**
     * Electron mass in kilogram (Kg).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?me">NIST reference</a>.
     */
    public static final BigDecimal ELECTRON_MASS_KG = new BigDecimal("9.1093837139E-31");
    /**
     * Electron mass in unified atomic mass unit (u) or dalton (Da).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?meu">NIST reference</a>.
     */
    public static final BigDecimal ELECTRON_MASS_U = new BigDecimal("5.485799090441E-4");
    /**
     * Electron mass in Mega electron-volt (MeV).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mec2mev">NIST reference</a>.
     */
    public static final BigDecimal ELECTRON_MASS_MeV = new BigDecimal("0.51099895069");
    /**
     * Proton mass in kilogram (Kg).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mp">NIST reference</a>.
     */
    public static final BigDecimal PROTON_MASS_KG = new BigDecimal("1.67262192595E-27");
    /**
     * Proton mass in unified atomic mass unit (u) or dalton (Da).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mpu">NIST reference</a>.
     */
    public static final BigDecimal PROTON_MASS_U = new BigDecimal("1.0072764665789");
    /**
     * Proton mass in Mega electron-volt (MeV).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mpc2mev">NIST reference</a>.
     */
    public static final BigDecimal PROTON_MASS_MeV = new BigDecimal("938.27208943");
    /**
     * Neutron mass in kilogram (Kg).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mn">NIST reference</a>.
     */
    public static final BigDecimal NEUTRON_MASS_KG = new BigDecimal("1.67492750056E-27");
    /**
     * Neutron mass in unified atomic mass unit (u).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mnu">NIST reference</a>.
     */
    public static final BigDecimal NEUTRON_MASS_U = new BigDecimal("1.00866491606");
    /**
     * Neutron mass in Mega electron-volt (MeV).
     * <p>
     * For updated values, see the <a href="https://physics.nist.gov/cgi-bin/cuu/Value?mnc2mev">NIST reference</a>.
     */
    public static final BigDecimal NEUTRON_MASS_MeV = new BigDecimal("939.56542194");

    private ChemConstants() {
    }
}
