package me.joba.factorio.lang;

import me.joba.factorio.Accessor;

public class Constant extends Symbol {

    private final int val;

    public Constant(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Const(" + val + ")";
    }

    @Override
    public Accessor toAccessor(Context context) {
        return Accessor.constant(val);
    }

    @Override
    public VarType getType() {
        return VarType.INT;
    }
}
