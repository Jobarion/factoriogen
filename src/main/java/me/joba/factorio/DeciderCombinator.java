package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.function.IntBinaryOperator;

public interface DeciderCombinator extends Combinator {

    @Override
    default boolean isOutputOnly() {
        return false;
    }

    @Override
    default String getFactorioName() {
        return "decider-combinator";
    }

    static DeciderCombinator withLeftRight(Accessor left, Accessor right, Writer out, DeciderOperation operator) {
        return new DeciderCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                int a = left.getValue(red, green);
                int b = right.getValue(red, green);
                if(operator.test(a, b)) {
                    return out.writeValue(red, green);
                }
                return Signal.EMPTY;
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                JSONObject conds = new JSONObject();
                cbehavior.put("decider_conditions", conds);
                conds.put("first_signal", left.toJson());
                if(right.isConstant()) {
                    conds.put("constant", right.toJson());
                }
                else {
                    conds.put("second_signal", right.toJson());
                }
                conds.put("comparator", operator.getCode());
                conds.put("output_signal", out.toJson());
                conds.put("copy_count_from_input", !out.isConstant());
                return cbehavior;
            }
        };
    }

    static DeciderCombinator withEach(Accessor right, int outId, boolean one, DeciderOperation operator) {
        return new DeciderCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                int sum = 0;
                int rval = right.getValue(red, green);
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int in = red.get(i) + green.get(i);
                    if(operator.test(in, rval)) {
                        sum += one ? 1 : in;
                    }
                }
                return Signal.singleValue(outId, sum);
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                JSONObject conds = new JSONObject();
                cbehavior.put("decider_conditions", conds);
                JSONObject each = new JSONObject();
                each.put("type", "virtual");
                each.put("name", "signal-each");
                conds.put("first_signal", each);
                if(right.isConstant()) {
                    conds.put("constant", right.toJson());
                }
                else {
                    conds.put("second_signal", right.toJson());
                }
                conds.put("comparator", operator.getCode());
                JSONObject out = new JSONObject();
                out.put("type", "virtual");
                out.put("name", FactorioSignal.values()[outId].getFactorioName());
                conds.put("output_signal", out);
                conds.put("copy_count_from_input", !one);
                return cbehavior;
            }
        };
    }

    static DeciderCombinator withEach(Accessor right, boolean one, DeciderOperation operator) {
        return new DeciderCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                int rval = right.getValue(red, green);
                int[] values = new int[Signal.SIGNAL_TYPES.get()];
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int in = red.get(i) + green.get(i);
                    if(operator.test(in, rval)) {
                        values[i] = one ? 1 : in;
                    }
                }
                return Signal.multiValue(values);
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                JSONObject conds = new JSONObject();
                cbehavior.put("decider_conditions", conds);
                JSONObject each = new JSONObject();
                each.put("type", "virtual");
                each.put("name", "signal-each");
                conds.put("first_signal", each);
                if(right.isConstant()) {
                    conds.put("constant", right.toJson());
                }
                else {
                    conds.put("second_signal", right.toJson());
                }
                conds.put("comparator", operator.getCode());
                JSONObject out = new JSONObject();
                out.put("type", "virtual");
                out.put("name", "signal-each");
                conds.put("output_signal", out);
                conds.put("copy_count_from_input", !one);
                return cbehavior;
            }
        };
    }

    static DeciderCombinator withAny(Accessor right, Writer out, DeciderOperation operator) {
        return new DeciderCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                boolean anyMatch = false;
                int rval = right.getValue(red, green);
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int in = red.get(i) + green.get(i);
                    if(operator.test(in, rval)) {
                        anyMatch = true;
                        break;
                    }
                }
                if(anyMatch) out.writeValue(red, green);
                return Signal.EMPTY;
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                JSONObject conds = new JSONObject();
                cbehavior.put("decider_conditions", conds);
                JSONObject each = new JSONObject();
                each.put("type", "virtual");
                each.put("name", "signal-anything");
                conds.put("first_signal", each);
                if(right.isConstant()) {
                    conds.put("constant", right.toJson());
                }
                else {
                    conds.put("second_signal", right.toJson());
                }
                conds.put("comparator", operator.getCode());
                conds.put("output_signal", out.toJson());
                conds.put("copy_count_from_input", !out.isConstant());
                return cbehavior;
            }
        };
    }

    static DeciderCombinator withEvery(Accessor right, Writer out, DeciderOperation operator) {
        return new DeciderCombinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                boolean anyNotMatch = true;
                int rval = right.getValue(red, green);
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int in = red.get(i) + green.get(i);
                    if(in == 0) continue;
                    if(!operator.test(in, rval)) {
                        anyNotMatch = false;
                        break;
                    }
                }
                if(anyNotMatch) return Signal.EMPTY;
                return out.writeValue(red, green);
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                JSONObject conds = new JSONObject();
                cbehavior.put("decider_conditions", conds);
                JSONObject each = new JSONObject();
                each.put("type", "virtual");
                each.put("name", "signal-everything");
                conds.put("first_signal", each);
                if(right.isConstant()) {
                    conds.put("constant", right.toJson());
                }
                else {
                    conds.put("second_signal", right.toJson());
                }
                conds.put("operation", operator.getCode());
                conds.put("output_signal", out.toJson());
                conds.put("copy_count_from_input", !out.isConstant());
                return cbehavior;
            }
        };
    }


    DeciderOperation GT  = new DeciderOperation(">", (a, b) -> a > b);
    DeciderOperation GEQ = new DeciderOperation(">=", (a, b) -> a >= b);
    DeciderOperation LT  = new DeciderOperation("<", (a, b) -> a < b);
    DeciderOperation LEQ = new DeciderOperation("<=", (a, b) -> a <= b);
    DeciderOperation EQ  = new DeciderOperation("=", (a, b) -> a == b);
    DeciderOperation NEQ = new DeciderOperation("!=", (a, b) -> a != b);

    static DeciderOperation getOperationForFlippedOperands(DeciderOperation op) {
        return switch (op.getCode()) {
            case ">" -> LT;
            case ">=" -> LEQ;
            case "<" -> GT;
            case "<=" -> GEQ;
            case "=" -> EQ;
            case "!=" -> NEQ;
            default -> throw new UnsupportedOperationException("Unknown operation " + op);
        };
    }

    static DeciderOperation getInvertedOperation(DeciderOperation op) {
        return switch (op.getCode()) {
            case ">" -> LEQ;
            case ">=" -> LT;
            case "<" -> GEQ;
            case "<=" -> GT;
            case "=" -> NEQ;
            case "!=" -> EQ;
            default -> throw new UnsupportedOperationException("Unknown operation " + op);
        };
    }

    static DeciderOperation getOperation(String op) {
        return switch (op) {
            case ">" -> GT;
            case ">=" -> GEQ;
            case "<" -> LT;
            case "<=" -> LEQ;
            case "==" -> EQ;
            case "!=" -> NEQ;
            default -> throw new UnsupportedOperationException("Unknown operation " + op);
        };
    }
}
