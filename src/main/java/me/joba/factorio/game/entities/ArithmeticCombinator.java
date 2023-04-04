package me.joba.factorio.game.entities;

import me.joba.factorio.CombinatorIn;
import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArithmeticCombinator extends IOCircuitNetworkEntity {

    private final JSONObject controlBehavior;
    private ArithmeticCombinator(JSONObject controlBehavior, Function<Map<FactorioSignal, Integer>, Map<FactorioSignal, Integer>> function) {
        super("arithmetic-combinator", function);
        this.controlBehavior = controlBehavior;
    }

    @Override
    protected void extendJson(JSONObject json) {
        super.extendJson(json);
        json.put("control_behavior", controlBehavior);
    }

    public static ArithmeticCombinator copying() {
        return withEach(CombinatorIn.constant(0), ArithmeticOperator.OR);
    }

    public static ArithmeticCombinator copying(FactorioSignal signal) {
        return withLeftRight(CombinatorIn.signal(signal), CombinatorIn.constant(0), signal, ArithmeticOperator.OR);
    }

    public static ArithmeticCombinator mapping(FactorioSignal in, FactorioSignal out) {
        return withLeftRight(CombinatorIn.signal(in), CombinatorIn.constant(0), out, ArithmeticOperator.OR);
    }

    public static ArithmeticCombinator withLeftRight(CombinatorIn left, CombinatorIn right, FactorioSignal outSignal, ArithmeticOperator operator) {
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
        out.put("type", outSignal.getType().getFactorioName());
        out.put("name", outSignal.getFactorioName());
        conds.put("output_signal", out);
        return new ArithmeticCombinator(cbehavior, in -> Map.of(outSignal, operator.applyAsInt(left.sample(in), right.sample(in))));
    }

    public static ArithmeticCombinator withEach(CombinatorIn right, ArithmeticOperator operator) {
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
        return new ArithmeticCombinator(cbehavior, in -> in.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> operator.applyAsInt(e.getValue(), right.sample(in))))
        );
    }

    public static ArithmeticCombinator withEachMerge(CombinatorIn right, FactorioSignal outSignal, ArithmeticOperator operator) {

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
        out.put("type", outSignal.getType().getFactorioName());
        out.put("name", outSignal.getFactorioName());
        conds.put("output_signal", out);
        return new ArithmeticCombinator(cbehavior, in -> Map.of(outSignal, in.values().stream()
                .mapToInt(i -> i)
                .map(i -> operator.applyAsInt(i, right.sample(in)))
                .sum())
        );
    }

}
