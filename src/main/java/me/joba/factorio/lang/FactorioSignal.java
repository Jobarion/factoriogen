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
    SIGNAL_RED,
    SIGNAL_GREEN,
    SIGNAL_YELLOW,
    SIGNAL_BLUE,
    SIGNAL_WHITE,
    SIGNAL_BLACK;

    public String getFactorioName() {
        return this.name().toLowerCase().replace('_', '-');
    }
}
