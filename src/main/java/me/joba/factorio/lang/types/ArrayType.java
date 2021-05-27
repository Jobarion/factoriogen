package me.joba.factorio.lang.types;

import java.util.Objects;

public class ArrayType implements Type{

    private final Type subType;

    public ArrayType(Type subType) {
        this.subType = subType;
    }

    public Type getSubType() {
        return subType;
    }

    @Override
    public String toString() {
        return subType + "[]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return subType.equals(arrayType.subType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subType);
    }

    @Override
    public int getSize() {
        return 1;
    }
}
