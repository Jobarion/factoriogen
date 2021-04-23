package me.joba.factorio.lang.types;

import java.util.Arrays;

public class TupleType implements Type {

    private final Type[] types;
    private final int size;

    public TupleType(Type[] types) {
        this.types = types;
        int size = 0;
        for(Type t : types) {
            size += t.getSize();
        }
        this.size = size;
    }

    public Type[] getSubtypes() {
        return types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleType tupleType = (TupleType) o;
        return Arrays.equals(types, tupleType.types);
    }

    @Override
    public String toString() {
        return Arrays.toString(types);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(types);
    }

    @Override
    public int getSize() {
        return size;
    }
}