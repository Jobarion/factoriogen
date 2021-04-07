package me.joba.factorio.lang;

import me.joba.factorio.Accessor;

public class Constant extends Symbol {

    private final int val;
    private final Type type;

    public Constant(int val) {
        this(val, PrimitiveType.INT);
    }

    public Constant(int val, Type type) {
        this.val = val;
        this.type = type;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Const(" + val + ")";
    }

    @Override
    public Accessor toAccessor(FunctionContext context) {
        return Accessor.constant(val);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int getTickDelay() {
        return 0;
    }
}
