package me.joba.factorio.lang;

import me.joba.factorio.Accessor;

import java.util.Arrays;

public class Constant extends Symbol {

    private final int[] val;

    public Constant(int val) {
        this(PrimitiveType.INT, val);
    }

    public Constant(Type type, int... val) {
        super(type);
        if(val.length != type.getSize()) throw new IllegalArgumentException("Cannot define " + type + " constant with size " + type.getSize() + " using " + val.length + " values.");
        this.val = val;
    }

    public int[] getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Const(" + Arrays.toString(val) + ")";
    }

    @Override
    public Accessor[] toAccessor(FunctionContext context) {
        var accessors = new Accessor[getType().getSize()];
        for(int i = 0; i < accessors.length; i++) {
            accessors[i] = Accessor.constant(val[i]);
        }
        return accessors;
    }

    @Override
    public int getTickDelay() {
        return 0;
    }
}
