package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public interface ArithmeticCombinator extends Combinator {

    @Override
    default String getFactorioName() {
        return "arithmetic-combinator";
    }

    @Override
    default boolean isOutputOnly() {
        return false;
    }

    static ArithmeticCombinator copying() {
        return withEach(Accessor.constant(0), ADD);
    }

    static ArithmeticCombinator copying(FactorioSignal signal) {
        return withLeftRight(Accessor.signal(signal), Accessor.constant(0), signal, ADD);
    }

    static ArithmeticCombinator withLeftRight(Accessor left, Accessor right, FactorioSignal outSignal, ArithmeticOperation operator) {
        return () -> {
            JSONObject cbehavior = new JSONObject();
            JSONObject conds = new JSONObject();
            cbehavior.put("arithmetic_conditions", conds);
            if(left.isConstant()) {
                conds.put("first_constant", left.toJson());
            }
            else {
                conds.put("first_signal", left.toJson());
            }
            if(right.isConstant()) {
                conds.put("second_constant", right.toJson());
            }
            else {
                conds.put("second_signal", right.toJson());
            }
            conds.put("operation", operator.getCode());
            JSONObject out = new JSONObject();
            out.put("type", "virtual");
            out.put("name", outSignal.getFactorioName());
            conds.put("output_signal", out);
            return cbehavior;
        };
    }

    static ArithmeticCombinator withEach(Accessor right, ArithmeticOperation operator) {
        return () -> {
            JSONObject cbehavior = new JSONObject();
            JSONObject conds = new JSONObject();
            cbehavior.put("arithmetic_conditions", conds);
            JSONObject each = new JSONObject();
            each.put("type", "virtual");
            each.put("name", "signal-each");
            conds.put("first_signal", each);
            if(right.isConstant()) {
                conds.put("second_constant", right.toJson());
            }
            else {
                conds.put("second_signal", right.toJson());
            }
            conds.put("operation", operator.getCode());
            JSONObject out = new JSONObject();
            out.put("type", "virtual");
            out.put("name", "signal-each");
            conds.put("output_signal", out);
            return cbehavior;
        };
    }

    static ArithmeticCombinator withEachMerge(Accessor right, FactorioSignal outSignal, ArithmeticOperation operator) {
        return () -> {
            JSONObject cbehavior = new JSONObject();
            JSONObject conds = new JSONObject();
            cbehavior.put("arithmetic_conditions", conds);
            JSONObject each = new JSONObject();
            each.put("type", "virtual");
            each.put("name", "signal-each");
            conds.put("first_signal", each);
            if(right.isConstant()) {
                conds.put("second_constant", right.toJson());
            }
            else {
                conds.put("second_signal", right.toJson());
            }
            conds.put("operation", operator.getCode());
            JSONObject out = new JSONObject();
            out.put("type", "virtual");
            out.put("name", outSignal.getFactorioName());
            conds.put("output_signal", out);
            return cbehavior;
        };
    }

    ArithmeticOperation MUL =new ArithmeticOperation("*", (a, b) -> a * b);
    ArithmeticOperation ADD =new ArithmeticOperation("+", (a, b) -> a + b);
    ArithmeticOperation SUB =new ArithmeticOperation("-", (a, b) -> a - b);
    ArithmeticOperation DIV =new ArithmeticOperation("/", (a, b) -> a / b);
    ArithmeticOperation MOD =new ArithmeticOperation("%", (a, b) -> a % b);
    ArithmeticOperation LSH =new ArithmeticOperation("<<", (a, b) -> a << b);
    ArithmeticOperation RSH =new ArithmeticOperation(">>", (a, b) -> a >> b);
    ArithmeticOperation AND =new ArithmeticOperation("AND", (a, b) -> a & b);
    ArithmeticOperation OR = new ArithmeticOperation("OR", (a, b) -> a | b);
    ArithmeticOperation XOR =new ArithmeticOperation("XOR", (a, b) -> a ^ b);

    static ArithmeticOperation getOperation(String op) {
        return switch (op) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "*" -> MUL;
            case "/" -> DIV;
            case "%" -> MOD;
            case ">>" -> LSH;
            case "<<" -> RSH;
            case "|" -> OR;
            case "&" -> AND;
            case "^" -> XOR;
            default -> throw new UnsupportedOperationException("Unknown operation " + op);
        };
    }
}
