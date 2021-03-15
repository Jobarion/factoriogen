package me.joba.factorio;

public class DeciderOperation implements IntBinaryPredicate {

    private final String code;
    private final IntBinaryPredicate operator;

    protected DeciderOperation(String code, IntBinaryPredicate operator) {
        this.code = code;
        this.operator = operator;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean test(int a, int b) {
        return operator.test(a, b);
    }
}
