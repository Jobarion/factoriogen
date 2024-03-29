package me.joba.factorio.lang.types;

public enum PrimitiveType implements Type {
    INT(1),
    UNASSIGNED_FIXEDP(2),
    BOOLEAN(1),
    VOID(0);

    private final int size;

    PrimitiveType(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }
}
