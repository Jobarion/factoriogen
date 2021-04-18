package me.joba.factorio.game.entities;

import me.joba.factorio.Accessor;
import me.joba.factorio.Writer;
import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public class DeciderCombinator extends IOCircuitNetworkEntity {

    private final JSONObject controlBehavior;

    private DeciderCombinator(JSONObject controlBehavior) {
        super("decider-combinator");
        this.controlBehavior = controlBehavior;
    }

    @Override
    protected void extendJson(JSONObject json) {
        super.extendJson(json);
        json.put("control_behavior", controlBehavior);
    }

    public static DeciderCombinator withLeftRight(Accessor left, Accessor right, Writer out, DeciderOperator operator) {
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
        return new DeciderCombinator(cbehavior);
    }

    public static DeciderCombinator withEach(Accessor right, int outId, boolean one, DeciderOperator operator) {
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
        out.put("name", FactorioSignal.values()[outId].getFactorioName());
        conds.put("output_signal", out);
        conds.put("copy_count_from_input", !one);
        return new DeciderCombinator(cbehavior);
    }

    public static DeciderCombinator withEach(Accessor right, boolean one, DeciderOperator operator) {
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
        return new DeciderCombinator(cbehavior);
    }

    public static DeciderCombinator withAny(Accessor right, Writer out, DeciderOperator operator) {
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
        return new DeciderCombinator(cbehavior);
    }

    public static DeciderCombinator withEvery(Accessor right, Writer out, DeciderOperator operator) {
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
        return new DeciderCombinator(cbehavior);
    }

}
