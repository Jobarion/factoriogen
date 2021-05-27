package me.joba.factorio.lang;

public enum FactorioSignal {

    SIGNAL_0,
    SIGNAL_1,
    SIGNAL_2,
    SIGNAL_3,
    SIGNAL_4,
    SIGNAL_5,
    SIGNAL_6,
    SIGNAL_7,
    SIGNAL_8,
    SIGNAL_9,
    SIGNAL_A("A"),
    SIGNAL_B("B"),
    SIGNAL_C("C"),
    SIGNAL_D("D"),
    SIGNAL_E("E"),
    SIGNAL_F("F"),
    SIGNAL_G("G"),
    SIGNAL_H("H"),
    SIGNAL_I("I"),
    SIGNAL_J("J"),
    SIGNAL_K("K"),
    SIGNAL_L("L"),
    SIGNAL_M("M"),
    SIGNAL_N("N"),
    SIGNAL_O("O"),
    SIGNAL_P("P"),
    SIGNAL_Q("Q"),
    SIGNAL_R("R"),
    SIGNAL_S("S"),
    SIGNAL_T("T"),
    SIGNAL_U("U"),
    SIGNAL_V("V"),
    SIGNAL_W("W"),
    SIGNAL_X("X"),
    SIGNAL_Y("Y"),
    SIGNAL_Z("Z"),
    SIGNAL_RED,
    SIGNAL_GREEN,
    SIGNAL_BLUE,
    SIGNAL_YELLOW,
    SIGNAL_PINK,
    SIGNAL_CYAN,
    SIGNAL_WHITE,
    SIGNAL_GREY,
    SIGNAL_BLACK,
    SIGNAL_CHECK(null, true),
    SIGNAL_INFO(null, true),
    SIGNAL_DOT(null, true);

    private final String suffix;
    private final boolean reserved;

    FactorioSignal(String suffix) {
        this(suffix, false);
    }

    FactorioSignal(String suffix, boolean reserved) {
        this.reserved = reserved;
        this.suffix = suffix;
    }

    FactorioSignal() {
        this(null);
    }

    public boolean isReserved() {
        return reserved;
    }

    public String getFactorioName() {
        if(suffix == null) {
            return this.name().toLowerCase().replace('_', '-');
        }
        return "signal-" + suffix;
    }
}
