package de.unibi.agbi.biodwh2.core.chem;

public class Isotope {
    private final Atom atom;
    private final int neutrons;

    private Isotope(final Atom atom, final int neutrons) {
        this.atom = atom;
        this.neutrons = neutrons;
    }

    public Atom getAtom() {
        return atom;
    }

    public int getNeutrons() {
        return neutrons;
    }

    public int getProtons() {
        return atom.getProtons();
    }

    public int getElectrons() {
        return atom.getElectrons();
    }

    public int getMassNumber() {
        return neutrons + atom.getProtons();
    }

    public static Isotope of(final Atom atom, final int neutrons) {
        return new Isotope(atom, neutrons);
    }
}
