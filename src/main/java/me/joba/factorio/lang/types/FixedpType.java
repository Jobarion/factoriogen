package me.joba.factorio.lang.types;

import java.util.Objects;

public class FixedpType implements Type{

    private final int fractionBits;

    public FixedpType(int fractionBits) {
        this.fractionBits = fractionBits;
    }

    public int getFractionBits() {
        return fractionBits;
    }

    @Override
    public String toString() {
        return "fixedp<" + fractionBits + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedpType that = (FixedpType) o;
        return fractionBits == that.fractionBits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fractionBits);
    }

    @Override
    public int getSize() {
        return 1;
    }
}
