package de.unibi.agbi.biodwh2.core.chem;

@SuppressWarnings("unused")
public class Atom {
    /**
     * Hydrogen (H) has atom number <em>1</em>, is a <em>non-metal</em>, and exists as H<sub>2</sub> at room
     * temperature.
     */
    public static final Atom HYDROGEN = new Atom(1, "H");
    /**
     * Helium (He) has atom number <em>2</em> and is a <em>non-metal</em>.
     */
    public static final Atom HELIUM = new Atom(2, "He");
    /**
     * Lithium (Li) has atom number <em>3</em> and is a <em>metal</em>.
     */
    public static final Atom LITHIUM = new Atom(3, "Li");
    /**
     * Beryllium (Be) has atom number <em>4</em> and is a <em>metal</em>.
     */
    public static final Atom BERYLLIUM = new Atom(4, "Be");
    /**
     * Boron (B) has atom number <em>5</em> and is a <em>metalloid</em>.
     */
    public static final Atom BORON = new Atom(5, "B");
    /**
     * Carbon (C) has atom number <em>6</em> and is a <em>non-metal</em>.
     */
    public static final Atom CARBON = new Atom(6, "C");
    /**
     * Nitrogen (N) has atom number <em>7</em>, is a <em>non-metal</em>, and exists as N<sub>2</sub> at room
     * temperature.
     */
    public static final Atom NITROGEN = new Atom(7, "N");
    /**
     * Oxygen (O) has atom number <em>8</em>, is a <em>non-metal</em>, and exists as O<sub>2</sub> at room temperature.
     */
    public static final Atom OXYGEN = new Atom(8, "O");
    /**
     * Fluorine (F) has atom number <em>9</em>, is a <em>non-metal</em>, and exists as F<sub>2</sub> at room
     * temperature.
     */
    public static final Atom FLUORINE = new Atom(9, "F");
    /**
     * Neon (Ne) has atom number <em>10</em> and is a <em>non-metal</em>.
     */
    public static final Atom NEON = new Atom(10, "Ne");
    /**
     * Sodium (Na) has atom number <em>11</em> and is a <em>metal</em>.
     */
    public static final Atom SODIUM = new Atom(11, "Na");
    /**
     * Magnesium (Mg) has atom number <em>12</em> and is a <em>metal</em>.
     */
    public static final Atom MAGNESIUM = new Atom(12, "Mg");
    /**
     * Aluminium (Al) has atom number <em>13</em> and is a <em>metal</em>.
     */
    public static final Atom ALUMINIUM = new Atom(13, "Al");
    /**
     * Silicon (Si) has atom number <em>14</em> and is a <em>metalloid</em>.
     */
    public static final Atom SILICON = new Atom(14, "Si");
    /**
     * Phosphorus (P) has atom number <em>15</em> and is a <em>non-metal</em>.
     */
    public static final Atom PHOSPHORUS = new Atom(15, "P");
    /**
     * Sulfur (S) has atom number <em>16</em> and is a <em>non-metal</em>.
     */
    public static final Atom SULFUR = new Atom(16, "S");
    /**
     * Chlorine (Cl) has atom number <em>17</em>, is a <em>non-metal</em>, and exists as Cl<sub>2</sub> at room
     * temperature.
     */
    public static final Atom CHLORINE = new Atom(17, "Cl");
    /**
     * Argon (Ar) has atom number <em>18</em> and is a <em>non-metal</em>.
     */
    public static final Atom ARGON = new Atom(18, "Ar");
    /**
     * Potassium (K) has atom number <em>19</em> and is a <em>metal</em>.
     */
    public static final Atom POTASSIUM = new Atom(19, "K");
    /**
     * Calcium (Ca) has atom number <em>20</em> and is a <em>metal</em>.
     */
    public static final Atom CALCIUM = new Atom(20, "Ca");
    /**
     * Scandium (Sc) has atom number <em>20</em> and is a <em>metal</em>.
     */
    public static final Atom SCANDIUM = new Atom(21, "Sc");
    /**
     * Titanium (Ti) has atom number <em>22</em> and is a <em>metal</em>.
     */
    public static final Atom TITANIUM = new Atom(22, "Ti");
    /**
     * Vanadium (V) has atom number <em>23</em> and is a <em>metal</em>.
     */
    public static final Atom VANADIUM = new Atom(23, "V");
    /**
     * Chromium (Cr) has atom number <em>24</em> and is a <em>metal</em>.
     */
    public static final Atom CHROMIUM = new Atom(24, "Cr");
    /**
     * Manganese (Mn) has atom number <em>25</em> and is a <em>metal</em>.
     */
    public static final Atom MANGANESE = new Atom(25, "Mn");
    /**
     * Iron (Fe) has atom number <em>26</em> and is a <em>metal</em>.
     */
    public static final Atom IRON = new Atom(26, "Fe");
    /**
     * Cobalt (Co) has atom number <em>27</em> and is a <em>metal</em>.
     */
    public static final Atom COBALT = new Atom(27, "Co");
    /**
     * Nickel (Ni) has atom number <em>28</em> and is a <em>metal</em>.
     */
    public static final Atom NICKEL = new Atom(28, "Ni");
    /**
     * Copper (Cu) has atom number <em>29</em> and is a <em>metal</em>.
     */
    public static final Atom COPPER = new Atom(29, "Cu");
    /**
     * Zinc (Zn) has atom number <em>30</em> and is a <em>metal</em>.
     */
    public static final Atom ZINC = new Atom(30, "Zn");
    /**
     * Gallium (Ga) has atom number <em>31</em> and is a <em>metal</em>.
     */
    public static final Atom GALLIUM = new Atom(31, "Ga");
    /**
     * Germanium (Ge) has atom number <em>32</em> and is a <em>metalloid</em>.
     */
    public static final Atom GERMANIUM = new Atom(32, "Ge");
    /**
     * Arsenic (As) has atom number <em>33</em> and is a <em>metalloid</em>.
     */
    public static final Atom ARSENIC = new Atom(33, "As");
    /**
     * Selenium (Se) has atom number <em>34</em> and is a <em>non-metal</em>.
     */
    public static final Atom SELENIUM = new Atom(34, "Se");
    /**
     * Bromine (Br) has atom number <em>35</em>, is a <em>non-metal</em>, and exists as Br<sub>2</sub> at room
     * temperature.
     */
    public static final Atom BROMINE = new Atom(35, "Br");
    /**
     * Krypton (Kr) has atom number <em>36</em> and is a <em>non-metal</em>.
     */
    public static final Atom KRYPTON = new Atom(36, "Kr");
    public static final Atom RUBIDIUM = new Atom(37, "Rb");
    public static final Atom STRONTIUM = new Atom(38, "Sr");
    public static final Atom YTTRIUM = new Atom(39, "Y");
    public static final Atom ZIRCONIUM = new Atom(40, "Zr");
    public static final Atom NIOBIUM = new Atom(41, "Nb");
    public static final Atom MOLYBDENUM = new Atom(42, "Mo");
    public static final Atom TECHNETIUM = new Atom(43, "Tc");
    public static final Atom RUTHENIUM = new Atom(44, "Ru");
    public static final Atom RHODIUM = new Atom(45, "Rh");
    public static final Atom PALLADIUM = new Atom(46, "Pd");
    public static final Atom SILVER = new Atom(47, "Ag");
    public static final Atom CADMIUM = new Atom(48, "Cd");
    public static final Atom INDIUM = new Atom(49, "In");
    public static final Atom TIN = new Atom(50, "Sn");
    /**
     * Antimony (Sb) has atom number <em>51</em> and is a <em>metalloid</em>.
     */
    public static final Atom ANTIMONY = new Atom(51, "Sb");
    /**
     * Tellurium (Te) has atom number <em>52</em> and is a <em>metalloid</em>.
     */
    public static final Atom TELLURIUM = new Atom(52, "Te");
    /**
     * Iodine (I) has atom number <em>53</em>, is a <em>non-metal</em>, and exists as I<sub>2</sub> at room
     * temperature.
     */
    public static final Atom IODINE = new Atom(53, "I");
    /**
     * Xenon (Xe) has atom number <em>54</em> and is a <em>non-metal</em>.
     */
    public static final Atom XENON = new Atom(54, "Xe");
    public static final Atom CAESIUM = new Atom(55, "Cs");
    public static final Atom BARIUM = new Atom(56, "Ba");
    public static final Atom LANTHANUM = new Atom(57, "La");
    public static final Atom CERIUM = new Atom(58, "Ce");
    public static final Atom PRASEODYMIUM = new Atom(59, "Pr");
    public static final Atom NEODYMIUM = new Atom(60, "Nd");
    public static final Atom PROMETHIUM = new Atom(61, "Pm");
    public static final Atom SAMARIUM = new Atom(62, "Sm");
    public static final Atom EUROPIUM = new Atom(63, "Eu");
    public static final Atom GADOLINIUM = new Atom(64, "Gd");
    public static final Atom TERBIUM = new Atom(65, "Tb");
    public static final Atom DYSPROSIUM = new Atom(66, "Dy");
    public static final Atom HOLMIUM = new Atom(67, "Ho");
    public static final Atom ERBIUM = new Atom(68, "Er");
    public static final Atom THULIUM = new Atom(69, "Tm");
    public static final Atom YTTERBIUM = new Atom(70, "Yb");
    public static final Atom LUTETIUM = new Atom(71, "Lu");
    public static final Atom HAFNIUM = new Atom(72, "Hf");
    public static final Atom TANTALUM = new Atom(73, "Ta");
    public static final Atom TUNGSTEN = new Atom(74, "W");
    public static final Atom RHENIUM = new Atom(75, "Re");
    public static final Atom OSMIUM = new Atom(76, "Os");
    public static final Atom IRIDIUM = new Atom(77, "Ir");
    public static final Atom PLATINUM = new Atom(78, "Pt");
    public static final Atom GOLD = new Atom(79, "Au");
    public static final Atom MERCURY = new Atom(80, "Hg");
    public static final Atom THALLIUM = new Atom(81, "Ti");
    public static final Atom LEAD = new Atom(82, "Pb");
    public static final Atom BISMUTH = new Atom(83, "Bi");
    public static final Atom POLONIUM = new Atom(84, "Po");
    /**
     * Astatine (At) has atom number <em>85</em> and is a <em>metalloid</em>.
     */
    public static final Atom ASTATINE = new Atom(85, "At");
    /**
     * Radon (Rn) has atom number <em>86</em> and is a <em>non-metal</em>.
     */
    public static final Atom RADON = new Atom(86, "Rn");
    public static final Atom FRANCIUM = new Atom(87, "Fr");
    public static final Atom RADIUM = new Atom(88, "Ra");
    public static final Atom ACTINIUM = new Atom(89, "Ac");
    public static final Atom THORIUM = new Atom(90, "Th");
    public static final Atom PROTACTINIUM = new Atom(91, "Pa");
    public static final Atom URANIUM = new Atom(92, "U");
    public static final Atom NEPTUNIUM = new Atom(93, "Np");
    public static final Atom PLUTONIUM = new Atom(94, "Pu");
    public static final Atom AMERICIUM = new Atom(95, "Am");
    public static final Atom CURIUM = new Atom(96, "Cm");
    public static final Atom BERKELIUM = new Atom(97, "Bk");
    public static final Atom CALIFORNIUM = new Atom(98, "Cf");
    public static final Atom EINSTEINIUM = new Atom(99, "Es");
    public static final Atom FERMIUM = new Atom(100, "Fm");
    public static final Atom MENDELEVIUM = new Atom(101, "Md");
    public static final Atom NOBELIUM = new Atom(102, "No");
    public static final Atom LAWRENCIUM = new Atom(103, "Lr");
    public static final Atom RUTHERFORDIUM = new Atom(104, "Rf");
    public static final Atom DUBNIUM = new Atom(105, "Db");
    public static final Atom SEABORGIUM = new Atom(106, "Sg");
    public static final Atom BOHRIUM = new Atom(107, "Bh");
    public static final Atom HASSIUM = new Atom(108, "Hs");
    public static final Atom MEITNERIUM = new Atom(109, "Mt");
    public static final Atom DARMSTADTIUM = new Atom(110, "Ds");
    public static final Atom ROENTGENIUM = new Atom(111, "Rg");
    public static final Atom COPERNICIUM = new Atom(112, "Cn");
    public static final Atom NIHONIUM = new Atom(113, "Nh");
    public static final Atom FLEROVIUM = new Atom(114, "Fl");
    public static final Atom MOSCOVIUM = new Atom(115, "Mc");
    public static final Atom LIVERMORIUM = new Atom(116, "Lv");
    public static final Atom TENNESSINE = new Atom(117, "Ts");
    @SuppressWarnings("SpellCheckingInspection")
    public static final Atom ORGANESSON = new Atom(118, "Og");

    private final int protons;
    private final int electrons;
    private final String symbol;

    private Atom(final int number, final String symbol) {
        protons = number;
        electrons = number;
        this.symbol = symbol;
    }

    public Atom(final Atom neutral, final int electrons) {
        this(neutral.protons, electrons, neutral.symbol);
    }

    private Atom(final int protons, final int electrons, final String symbol) {
        this.protons = protons;
        this.electrons = electrons;
        this.symbol = symbol;
        if (protons < 0) {
            throw new IllegalArgumentException("Protons cannot be less than zero");
        }
        if (electrons < 0) {
            throw new IllegalArgumentException("Electrons cannot be less than zero");
        }
    }

    public int getProtons() {
        return protons;
    }

    public int getElectrons() {
        return electrons;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getCharge() {
        return protons - electrons;
    }

    public boolean isIon() {
        return protons != electrons;
    }

    public Isotope toIsotope(final int neutrons) {
        return Isotope.of(this, neutrons);
    }

    public Atom toIon(final int electrons) {
        return new Atom(this, electrons);
    }
}
