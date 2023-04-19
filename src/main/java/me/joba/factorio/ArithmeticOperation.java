package me.joba.factorio;

import java.util.function.IntBinaryOperator;

public class ArithmeticOperation implements IntBinaryOperator {

    private final String code;
    private final IntBinaryOperator operator;

    protected ArithmeticOperation(String code, IntBinaryOperator operator) {
        this.code = code;
        this.operator = operator;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int applyAsInt(int left, int right) {
        return operator.applyAsInt(left, right);
    }

    @Override
    public String toString() {
        return code;
    }
}
