package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.function.IntBinaryOperator;

public interface ArithmeticCombinator extends Combinator {

    @Override
    default String getFactorioName() {
        return "arithmetic-combinator";
    }

    @Override
    default boolean isOutputOnly() {
        return false;
    }

    static ArithmeticCombinator withLeftRight(Accessor left, Accessor right, int outId, ArithmeticOperation operator) {
        return new ArithmeticCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                int a = left.getValue(red, green);
                int b = right.getValue(red, green);
                return Signal.singleValue(outId, operator.applyAsInt(a, b));
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                JSONObject conds = new JSONObject();
                cbehavior.put("arithmetic_conditions", conds);
                conds.put("first_signal", left.toJson());
                if(right.isConstant()) {
                    conds.put("second_constant", right.toJson());
                }
                else {
                    conds.put("second_signal", right.toJson());
                }
                conds.put("operation", operator.getCode());
                JSONObject out = new JSONObject();
                out.put("type", "virtual");
                out.put("name", FactorioSignal.values()[outId].getFactorioName());
                conds.put("output_signal", out);
                return cbehavior;
            }
        };
    }

    static ArithmeticCombinator withEach(Accessor right, ArithmeticOperation operator) {
        return new ArithmeticCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                int[] values = new int[Signal.SIGNAL_TYPES.get()];
                int rval = right.getValue(red, green);
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int in = red.get(i) + green.get(i);
                    if(in == 0) continue;
                    values[i] = operator.applyAsInt(in, rval);
                }
                return Signal.multiValue(values);
            }

            @Override
            public JSONObject createControlBehaviorJson() {
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
            }
        };
    }

    static ArithmeticCombinator withEachMerge(Accessor right, int outId, ArithmeticOperation operator) {
        return new ArithmeticCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                int total = 0;
                int rval = right.getValue(red, green);
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int in = red.get(i) + green.get(i);
                    if(in == 0) continue;
                    total += operator.applyAsInt(in, rval);
                }
                return Signal.singleValue(outId, total);
            }

            @Override
            public JSONObject createControlBehaviorJson() {
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
                out.put("name", FactorioSignal.values()[outId].getFactorioName());
                conds.put("output_signal", out);
                return cbehavior;
            }
        };
    }

    ArithmeticOperation MUL =new ArithmeticOperation("*", (a, b) -> a * b);
    ArithmeticOperation ADD =new ArithmeticOperation("+", (a, b) -> a + b);
    ArithmeticOperation SUB =new ArithmeticOperation("-", (a, b) -> a - b);
    ArithmeticOperation DIV =new ArithmeticOperation("/", (a, b) -> a / b);
    ArithmeticOperation MOD =new ArithmeticOperation("%", (a, b) -> a * b);
    ArithmeticOperation LSH =new ArithmeticOperation("<<", (a, b) -> a << b);
    ArithmeticOperation RSH =new ArithmeticOperation(">>", (a, b) -> a >> b);
    ArithmeticOperation AND =new ArithmeticOperation("&", (a, b) -> a & b);
    ArithmeticOperation OR = new ArithmeticOperation("|", (a, b) -> a | b);
    ArithmeticOperation XOR =new ArithmeticOperation("^", (a, b) -> a ^ b);

    static ArithmeticOperation getOperation(String op) {
        return switch (op) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "*" -> MUL;
            case "/" -> DIV;
            default -> throw new UnsupportedOperationException("Unknown operation " + op);
        };
    }
}
