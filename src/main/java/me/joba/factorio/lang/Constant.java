package me.joba.factorio.lang;

import me.joba.factorio.Accessor;

public class Constant extends Symbol {

    private final int val;
    private final VarType type;

    public Constant(int val) {
        this(val, VarType.INT);
    }

    public Constant(int val, VarType type) {
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
    public Accessor toAccessor(Context context) {
        return Accessor.constant(val);
    }

    @Override
    public VarType getType() {
        return type;
    }

    @Override
    public int getTickDelay() {
        return 0;
    }
}
