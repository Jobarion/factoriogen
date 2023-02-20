package me.joba.factorio.lang;

import me.joba.factorio.CombinatorIn;
import me.joba.factorio.lang.types.PrimitiveType;
import me.joba.factorio.lang.types.Type;

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
        if(getType() == PrimitiveType.UNASSIGNED_FIXEDP) {
            return "Const(" + val[0] + "." + String.valueOf((double)Integer.toUnsignedLong(val[1]) / (double)0xFFFFFFFFL).substring(2) + ", " + Arrays.toString(val) + ")<" + getType() + ">";
        }
        else {

        }
        return "Const(" + Arrays.toString(val) + ")<" + getType() + ">";
    }

    @Override
    public CombinatorIn[] toAccessor() {
        var accessors = new CombinatorIn[getType().getSize()];
        for(int i = 0; i < accessors.length; i++) {
            accessors[i] = CombinatorIn.constant(val[i]);
        }
        return accessors;
    }

    @Override
    public int getTickDelay() {
        return 0;
    }
}
