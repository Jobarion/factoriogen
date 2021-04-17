package me.joba.factorio.game.combinators;

import me.joba.factorio.Accessor;
import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public class ArithmeticCombinator extends IOCircuitNetworkEntity {

    private final JSONObject controlBehavior;

    private ArithmeticCombinator(JSONObject controlBehavior) {
        super("arithmetic-combinator");
        this.controlBehavior = controlBehavior;
    }

    @Override
    protected void extendJson(JSONObject json) {
        super.extendJson(json);
        json.put("control_behavior", controlBehavior);
    }

    public static ArithmeticCombinator copying() {
        return withEach(Accessor.constant(0), ArithmeticOperator.ADD);
    }

    public static ArithmeticCombinator copying(FactorioSignal signal) {
        return withLeftRight(Accessor.signal(signal), Accessor.constant(0), signal, ArithmeticOperator.ADD);
    }

    public static ArithmeticCombinator remapping(FactorioSignal in, FactorioSignal out) {
        return withLeftRight(Accessor.signal(in), Accessor.constant(0), out, ArithmeticOperator.ADD);
    }

    public static ArithmeticCombinator withLeftRight(Accessor left, Accessor right, FactorioSignal outSignal, ArithmeticOperator operator) {
        JSONObject cbehavior = new JSONObject();
        JSONObject conds = new JSONObject();
        cbehavior.put("arithmetic_conditions", conds);
        if (left.isConstant()) {
            conds.put("first_constant", left.toJson());
        } else {
            conds.put("first_signal", left.toJson());
        }
        if (right.isConstant()) {
            conds.put("second_constant", right.toJson());
        } else {
            conds.put("second_signal", right.toJson());
        }
        conds.put("operation", operator.getBlueprintCode());
        JSONObject out = new JSONObject();
        out.put("type", "virtual");
        out.put("name", outSignal.getFactorioName());
        conds.put("output_signal", out);
        return new ArithmeticCombinator(cbehavior);
    }

    public static ArithmeticCombinator withEach(Accessor right, ArithmeticOperator operator) {
        JSONObject cbehavior = new JSONObject();
        JSONObject conds = new JSONObject();
        cbehavior.put("arithmetic_conditions", conds);
        JSONObject each = new JSONObject();
        each.put("type", "virtual");
        each.put("name", "signal-each");
        conds.put("first_signal", each);
        if (right.isConstant()) {
            conds.put("second_constant", right.toJson());
        } else {
            conds.put("second_signal", right.toJson());
        }
        conds.put("operation", operator.getBlueprintCode());
        JSONObject out = new JSONObject();
        out.put("type", "virtual");
        out.put("name", "signal-each");
        conds.put("output_signal", out);
        return new ArithmeticCombinator(cbehavior);
    }

    public static ArithmeticCombinator withEachMerge(Accessor right, FactorioSignal outSignal, ArithmeticOperator operator) {

        JSONObject cbehavior = new JSONObject();
        JSONObject conds = new JSONObject();
        cbehavior.put("arithmetic_conditions", conds);
        JSONObject each = new JSONObject();
        each.put("type", "virtual");
        each.put("name", "signal-each");
        conds.put("first_signal", each);
        if (right.isConstant()) {
            conds.put("second_constant", right.toJson());
        } else {
            conds.put("second_signal", right.toJson());
        }
        conds.put("operation", operator.getBlueprintCode());
        JSONObject out = new JSONObject();
        out.put("type", "virtual");
        out.put("name", outSignal.getFactorioName());
        conds.put("output_signal", out);
        return new ArithmeticCombinator(cbehavior);
    }

}
