package me.joba.factorio.game.combinators;

public enum DeciderOperator implements IntBinaryPredicate {
    GT(">", ">", (a, b) -> a > b),
    GEQ(">=", ">=", (a, b) -> a >= b),
    LT("<", "<", (a, b) -> a < b),
    LEQ("<=", "<=", (a, b) -> a <= b),
    EQ("==", "=", (a, b) -> a == b),
    NEQ("!=", "!=", (a, b) -> a != b);

    private final String codeSymbol;
    private final String factorioBlueprintText;
    private DeciderOperator inverted, flipped;
    private final IntBinaryPredicate operation;

    static {
        GT.inverted = LEQ;
        GT.flipped = LT;
        LT.inverted = GEQ;
        LT.flipped = GT;

        GEQ.inverted = LT;
        GEQ.flipped = LEQ;
        LEQ.inverted = GT;
        LEQ.flipped = GEQ;

        EQ.inverted = NEQ;
        EQ.flipped = EQ;
        NEQ.inverted = EQ;
        NEQ.flipped = NEQ;
    }

    DeciderOperator(String codeSymbol, String factorioBlueprintText, IntBinaryPredicate operation) {
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

    public IntBinaryPredicate getOperation() {
        return operation;
    }

    public static DeciderOperator getOperator(String op) {
        for (var x : DeciderOperator.values()) {
            if (x.getCodeSymbol().equals(op)) return x;
        }
        throw new UnsupportedOperationException("Unknown operation " + op);
    }

    public DeciderOperator getInverted() {
        return inverted;
    }

    public DeciderOperator getFlipped() {
        return flipped;
    }

    @Override
    public boolean test(int a, int b) {
        return operation.test(a, b);
    }
}
