package me.joba.factorio.game.entities;

import java.util.function.IntBinaryOperator;

public enum ArithmeticOperator implements IntBinaryOperator {
    ADD("+", "+", (a, b) -> a + b),
    SUB("-", "-", (a, b) -> a - b),
    MUL("*", "*", (a, b) -> a * b),
    DIV("/", "/", (a, b) -> b == 0 ? 0 : a / b),
    MOD("%", "%", (a, b) -> a % b),
    LSH("<<", "<<", (a, b) -> a << b),
    RSH(">>", ">>", (a, b) -> a >> b),
    AND("&", "AND", (a, b) -> a & b),
    OR("|", "OR", (a, b) -> a | b),
    XOR("^", "XOR", (a, b) -> a ^ b);

    private final String codeSymbol;
    private final String factorioBlueprintText;
    private final IntBinaryOperator operation;

    ArithmeticOperator(String codeSymbol, String factorioBlueprintText, IntBinaryOperator operation) {
        this.codeSymbol = codeSymbol;
        this.factorioBlueprintText = factorioBlueprintText;
        this.operation = operation;
    }

    public String getCodeSymbol() {
        return codeSymbol;
    }

    public String getBlueprintCode() {
        return factorioBlueprintText;
    }

    public static ArithmeticOperator getOperator(String op) {
        for (var x : ArithmeticOperator.values()) {
            if (x.getCodeSymbol().equals(op)) return x;
        }
        throw new UnsupportedOperationException("Unknown operation " + op);
    }

    @Override
    public int applyAsInt(int left, int right) {
        return operation.applyAsInt(left, right);
    }
}
