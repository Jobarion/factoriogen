package me.joba.factorio.lang;

import me.joba.factorio.lang.types.Type;

public class ArrayDeclaration {

    private final Type type;
    private final int address;
    private final int size;

    public ArrayDeclaration(Type type, int address, int size) {
        this.type = type;
        this.address = address;
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    public int getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }
}
