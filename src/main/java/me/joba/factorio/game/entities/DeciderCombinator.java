package me.joba.factorio.game.entities;

import me.joba.factorio.CombinatorIn;
import me.joba.factorio.CombinatorOut;
import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeciderCombinator extends IOCircuitNetworkEntity {

    private final JSONObject controlBehavior;

    private DeciderCombinator(JSONObject controlBehavior, Function<Map<FactorioSignal, Integer>, Map<FactorioSignal, Integer>> function) {
        super("decider-combinator", function);
        this.controlBehavior = controlBehavior;
    }

    @Override
    protected void extendJson(JSONObject json) {
        super.extendJson(json);
        json.put("control_behavior", controlBehavior);
    }

    public static DeciderCombinator withLeftRight(CombinatorIn left, CombinatorIn right, CombinatorOut out, DeciderOperator operator) {
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
        conds.put("comparator", operator.getBlueprintCode());
        conds.put("output_signal", out.toJson());
        conds.put("copy_count_from_input", !out.isConstant());
        return new DeciderCombinator(cbehavior, in -> operator.test(left.sample(in), right.sample(in)) ? out.sample(in) : Map.of());
    }

    public static DeciderCombinator withEach(CombinatorIn right, FactorioSignal outSignal, boolean one, DeciderOperator operator) {
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
        conds.put("comparator", operator.getBlueprintCode());
        JSONObject out = new JSONObject();
        out.put("type", outSignal.getType().getFactorioName());
        out.put("name", outSignal.getFactorioName());
        conds.put("output_signal", out);
        conds.put("copy_count_from_input", !one);
        return new DeciderCombinator(cbehavior, in -> Map.of(outSignal, in.values().stream()
                .filter(integer -> operator.test(integer, right.sample(in)))
                .mapToInt(integer -> one ? 1 : integer)
                .sum())
        );
    }

    public static DeciderCombinator withEach(CombinatorIn right, boolean one, DeciderOperator operator) {
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
        conds.put("comparator", operator.getBlueprintCode());
        JSONObject out = new JSONObject();
        out.put("type", "virtual");
        out.put("name", "signal-each");
        conds.put("output_signal", out);
        conds.put("copy_count_from_input", !one);
        return new DeciderCombinator(cbehavior, in -> in.entrySet().stream()
                .filter(e -> operator.test(e.getValue(), right.sample(in)))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> one ? 1 : e.getValue()))
        );
    }

    public static DeciderCombinator withAny(CombinatorIn right, CombinatorOut out, DeciderOperator operator) {
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
        conds.put("comparator", operator.getBlueprintCode());
        conds.put("output_signal", out.toJson());
        conds.put("copy_count_from_input", !out.isConstant());
        return new DeciderCombinator(cbehavior, in -> in.entrySet().stream()
                .filter(e -> e.getValue() != 0)
                .anyMatch(e -> operator.test(e.getValue(), right.sample(in))) ? out.sample(in) : Map.of() //TODO this breaks for a combinator like ANY > 0 return ANY' (instead of 'ANY > 0 return EVERY')
        );
    }

    public static DeciderCombinator withEvery(CombinatorIn right, CombinatorOut out, DeciderOperator operator) {
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
        conds.put("operation", operator.getBlueprintCode());
        conds.put("output_signal", out.toJson());
        conds.put("copy_count_from_input", !out.isConstant());
        return new DeciderCombinator(cbehavior, in -> in.entrySet().stream()
                .filter(e -> e.getValue() != 0)
                .allMatch(e -> operator.test(e.getValue(), right.sample(in))) ? out.sample(in) : Map.of()
        );
    }

}
