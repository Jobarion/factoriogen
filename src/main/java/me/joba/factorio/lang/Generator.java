package me.joba.factorio.lang;

import me.joba.factorio.*;
import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.game.entities.*;
import me.joba.factorio.graph.FunctionPlacer;
import me.joba.factorio.lang.expr.BooleanExpressionResolver;
import me.joba.factorio.lang.expr.BooleanNotExpressionResolver;
import me.joba.factorio.lang.expr.ComparisonExpressionResolver;
import me.joba.factorio.lang.expr.IntExpressionResolver;
import me.joba.factorio.lang.types.PrimitiveType;
import me.joba.factorio.lang.types.TupleType;
import me.joba.factorio.lang.types.Type;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;
import java.util.stream.Collectors;

public class Generator extends LanguageBaseListener {
//
//    private static final String SIMPLE_TUPLE = """
//            function main(a: int<red>, b: int<green>) -> (int, int, int) {
//                t = (a, b);
//                u = (t.0 * a, t.1 * b, t);
//                return (1, a, u.0 * u.1 + u.2.0 + u.2.1);
//            }
//            """;
//
//    private static final String SIMPLE_FUNCTION_ADD =
//            "function main(a: int<red>, b: int<green>) -> int {\n" +
//                    "sum = add(a, b);\n" +
//                    "return sum;\n" +
//                    "}\n" +
//                    "function add(a: int<red>, b: int) -> int {\n" +
//                    "return a + b;\n" +
//                    "}";
//
//    private static final String FUNCTION_CALL_LOOP =
//            "function main(a: int<red>, b: int<green>) -> int {\n" +
//                    "if(a < 0) {a = 0 - a;}\n" +
//                    "sum = 0;\n" +
//                    "while(a > 0) {\n" +
//                    "  sum = add(sum, b);\n" +
//                    "  a = a - 1;\n" +
//                    "}\n" +
//                    "return sum;\n" +
//            "}\n" +
//            "function add(a: int<red>, b: int) -> int {\n" +
//                    "return a + b;\n" +
//            "}";
//
    private static final String FUNCTION_FUCKING_COMPLEX =
            "function main(start: int<red>, end: int<green>, iterations: int<i>) -> int {\n" +
            "  max = -1;\n" +
            "  while(start <= end) {\n" +
            "    max = max(collatz(start, iterations), max);\n" +
            "    start = start + 1;\n" +
            "  }\n" +
            "  return max;\n" +
            "}\n" +
            "\n" +
            "function collatz(currentVal: int, iterations: int) -> int {\n" +
            "  max = currentVal;\n" +
            "  while(iterations != 0) {\n" +
            "    iterations = iterations - 1;\n" +
            "    if(currentVal % 2 == 0) {\n" +
            "      currentVal = currentVal / 2;\n" +
            "    }\n" +
            "    else {\n" +
            "      currentVal = currentVal * 3 + 1;\n" +
            "    }\n" +
            "    max = max(currentVal, max);\n" +
            "  }\n" +
            "  return max;\n" +
            "}\n" +
            "\n" +
            "function max(a: int, b: int) -> int {\n" +
            "  if(a < b) {\n" +
            "    a = b;\n" +
            "  }\n" +
            "  return a;\n" +
            "}";
//
//    private static final String COLLATZ =
//            "function collatz(currentVal: int<red>, iterations: int<green>) -> int {\n" +
//                    "  while(iterations != 0) {\n" +
//                    "    iterations = iterations - 1;\n" +
//                    "    if(currentVal % 2 == 0) {\n" +
//                    "      currentVal = currentVal / 2;\n" +
//                    "    }\n" +
//                    "    else {\n" +
//                    "      currentVal = currentVal * 3 + 1;\n" +
//                    "    }\n" +
//                    "  }\n" +
//                    "  return(currentVal);\n" +
//                    "}";
//
//    private static final String WHILE_OUTSIDE_VAR =
//            "function simple(a=red) {\n" +
//                    "  b = a * a;\n" +
//                    "  while(a > 10) {\n" +
//                    "    a = a / 2;\n" +
//                    "  }\n" +
//                    "  return(a, b);\n" +
//                    "}";
//
//    private static final String FUNCTION_NESTED_WHILE =
//            "function nested(a=red) {\n" +
//            "  while(a > 8) {\n" +
//            "    while(a > 10) {\n" +
//            "      a = a / 2;\n" +
//            "    }\n" +
//            "    a = a - 1;\n" +
//            "  }\n" +
//            "  return(a);\n" +
//            "}";
//
//    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
//    private static final String PYTH_TRIPLE =
//            "function nested(max: int) -> int {\n" +
//            "  a = 0;\n" +
//            "  b = 0;\n" +
//            "  c = 0;\n" +
//            "  if(max >= 5) {\n" +
//            "  terminate = 0;\n" +
//            "    while(terminate == 0 && a <= max) {\n" +
//            "      b = a;\n" +
//            "      a = a + 1;\n" +
//            "      while(terminate == 0 && b <= max) {\n" +
//            "        c = b;\n" +
//            "        b = b + 1;\n" +
//            "        while(terminate == 0 && c <= max) {\n" +
//            "          c = c + 1;\n" +
//            "          if(a * a + b * b == c * c) {\n" +
//            "            terminate = 1;\n" +
//            "          }\n" +
//            "        }\n" +
//            "      }\n" +
//            "    }\n" +
//            "  }\n" +
//            "  return a;\n" +
//            "}";

    //TODO: Arrays.
    //0eNrtnFlu8zYQx+9CoC+tUoiLNqN9SHuAAn3wSxEYss0kBGzJ0BLUCHSAHqQX60lKWfliWxs5lGWrcV8CeNGY5C/D+c9wpHe03OR8l4goQ7N3JFZxlKLZH+8oFS9RuCnfy/Y7jmZIZHyLLBSF2/LVmq/EmicPq3i7FFGYxQkqLCSiNf8TzXBhKQ2EichetzwTq3YbpHiyEI8ykQlejejwYr+I8u2SJ/JH+sZioV2cykvjqBxAOaTgR8dCezR7cG35I2uR8FX1MbOQnHSWxJvFkr+Gb0JeLq/5MLqQn60PhtLy3WeRpNmiMbU3kWS5fOdzSNU3Hh7LCZVLmoXl+jrli+0uTA5jnKGf5AVxnu1ygEn+xpN99iqil8r2bi+HmEfZ4jmJtwsRSWNoliU5L6qfjqp5HkaPyz8vCefR6YKKtVxt+V2RrHKRHV4eAJ58HBRP0hrpvJzWL38qihMT35AREDJiTwBZMFlkuLnmfYiYHiJq6FWOfzNEuIbon7/+niokooDk6EFihn50Q0jOhCERGCRXD5KjCHV9zuRqcTraHYaKh6vXcj1TXppZHKHZktCOS2SHUaAfTIgdbINw0DqO81jkwfY9rwm3jZYLpXXiVXdNi/XT8mG0fD1a3gDfYlel9diK6gGfs/regNUjEJQDI+H0c8W2Hil/gF/dKykXRspVkMJ6pILjsPOlnP1hwk1HIh9w6CCtej5Equn25T+dmTolxYhSZx1n52LHromdn6ejR+1OrZPwdePbzT3D0rOL+4N2J2Bsqmz/B9zt7T2AXW3AChfW9WBYCeBzt5Ggz4MBuV7qcgnYtIfxc7hJgZCBqgrr0gFm/04XHfd22b8JHfuiHqhQxDgAwgs04TGwKPaA/MaVWuxcaX13BaWFFSqXAItsmPbvkkRTi2HHVOjYxd0V3triWlPmdMQ1QmHFH8z6CWNXk7BrqnSuSdiZLmGsTZgBCTuaBD0jKcP8G0oZcoFgiUeWMrUd0xkqdfrtddL1zegGN6RLL0CXXJau4lwRA4u5mCi0lW6eGRgJ3SZd9+v4LlzoqsSOPUw8EYW9LrjENoPr3xDu2K4Lhxso4LpDs5h+e51wsWmWo+28X6+grBKthABhKlJcQjRhEmOY/pVhTuzUDZqY6iaexLT/g/nFf6uP6vGiHQQKB6PusByks6HKtBPkqriCyeFSKBfqAXFptoQQxyyp8G6YVIxdXzVIKjxFNIPS8/qjGSWadF0z3endUHeOfbYBd01g0tBMCmrwdHdScEvJMfQ5xdcrl7eU3Ajw4ILoLj24R6Qs/lUrr7kxjkviAhJwXBIUa5IIjEmwK5OYj0ViDiPhw2LRUKVBbc02bhtMkt4Ryfb9KgCy0NQFFBuzuIf9rZ2FD2SheQcKhVccnBv5Ra3iAPOnoRUmk7ZtG37yAkFat8d0t0JqjPxu3Y/iYVthV3mJGvSofMpsNo02+0sUcE3cC3gQqttTS+H3qbhTQ1LzkN9+N0DyCzRCKdquKQPen9cJyDUtyTpj1vh+vXyZaJyOWAo8Yya6UsIzrb1ekwuZLhe3PzViuLd1uSUHtnSL8x1AfWOdTu/7NIrZQLmgOD8MuggFxlKOXJnQfDTtMB8IRxmINNssmG0M487dhSq6mCi0vKforaC+5m3lx0LFtyXS1Oa00VvRIQWfxSbjSccTRhRnFvmHzuh9yIgiLNZsEJCN+YkN+mmDloFHpIty1tVh1kBx0ormWLdIt+FG/tttpP1EOtwu3vA+jf6BxjjPqNy/nGL5+JbZyeNiLCR1QVrlxT5mXkA86nkOtllR/AsB4ynE
    
    private static final String TEST = FUNCTION_FUCKING_COMPLEX;

    private static final ExpressionResolver<LanguageParser.ExprContext, ArithmeticOperator> EXPR_PARSER = new IntExpressionResolver();
    private static final ExpressionResolver<LanguageParser.BoolExprContext, DeciderOperator> BOOL_EXPR_COMPONENT_PARSER = new ComparisonExpressionResolver();
    private static final ExpressionResolver<LanguageParser.BoolExprContext, ArithmeticOperator> BOOL_EXPR_PARSER = new BooleanExpressionResolver();
    private static final ExpressionResolver<LanguageParser.BoolExprContext, Void> NOT_EXPR_PARSER = new BooleanNotExpressionResolver();

    private FunctionContext currentFunctionContext;
    private final Map<String, FunctionContext> definedFunctions;
    private int currentFunctionCallId = 1;
    private int indentationLevel = 0;

    public Generator(Map<String, FunctionContext> definedFunctions) {
        this.definedFunctions = definedFunctions;
    }

    @Override
    public void exitFunction(LanguageParser.FunctionContext ctx) {
        indentationLevel--;
    }

    @Override
    public void exitFunctionHeader(LanguageParser.FunctionHeaderContext ctx) {
        currentFunctionContext = definedFunctions.get(ctx.functionName().getText());
        NetworkGroup out = new NetworkGroup();
        CombinatorGroup functionHeader = new CombinatorGroup(currentFunctionContext.getFunctionCallOutputGroup(), out);
        DeciderCombinator inputCombinator;
        if(currentFunctionContext.getSignature().getName().equals("main")) {
            inputCombinator = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
            inputCombinator.setFixedLocation(true);
            inputCombinator.setX(0);
            inputCombinator.setY(0);
            inputCombinator.setOrientation(2);
        }
        else {
            inputCombinator = DeciderCombinator.withLeftRight(Accessor.signal(Constants.FUNCTION_IDENTIFIER), Accessor.constant(currentFunctionContext.getSignature().getFunctionId()), Writer.everything(false), DeciderOperator.EQ);
            inputCombinator.setGreenIn(functionHeader.getInput());
        }
        inputCombinator.setDescription("Input function");
        functionHeader.getCombinators().add(inputCombinator);
        inputCombinator.setGreenOut(out);
        currentFunctionContext.setFunctionHeader(functionHeader);
        currentFunctionContext.overwriteControlFlowVariable(functionHeader).setDelay(0);
        log("Defining function " + currentFunctionContext.getSignature());
        indentationLevel++;
    }

    @Override
    public void exitReturnStatement(LanguageParser.ReturnStatementContext ctx) {
        var returnVal = currentFunctionContext.popTempVariable();

        if(!returnVal.getType().equals(currentFunctionContext.getSignature().getReturnType())) {
            throw new RuntimeException("Invalid return type " + returnVal.getType() + ", expected " + currentFunctionContext.getSignature().getReturnSignals());
        }

        var returnGroup = currentFunctionContext.getFunctionReturnGroup();

        var outputGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        returnGroup.getCombinators().add(outputGate);
        if(!currentFunctionContext.getSignature().getName().equals("main")) {
            outputGate.setRedOut(returnGroup.getOutput());
        }
        else {
            outputGate.setFixedLocation(true);
            outputGate.setX(0);
            outputGate.setY(1);
            outputGate.setOrientation(6);
        }
        outputGate.setDescription("Output " + ctx.getText());

        NetworkGroup gateInput = new NetworkGroup();
        returnGroup.getNetworks().add(gateInput);

        if(!returnVal.isBound()) {
            returnVal.bind(currentFunctionContext.getSignature().getReturnSignals());
        }
        log("Returning " + returnVal + " with delay " + returnVal.getTickDelay());

        int delay = Math.max(returnVal.getTickDelay() + 1, currentFunctionContext.getControlFlowVariable().getTickDelay());

        if(returnVal instanceof Constant) {
            int[] vals = ((Constant) returnVal).getVal();
            Map<FactorioSignal, Integer> constants = new HashMap<>();
            for(int j = 0; j < vals.length; j++) {
                if(vals[j] == 0) continue;
                constants.put(returnVal.getSignal()[j], vals[j]);
            }
            ConstantCombinator constant = new ConstantCombinator(constants);
            constant.setGreenOut(gateInput);
            returnGroup.getCombinators().add(constant);
        }
        else {
            var accessor = ((Variable)returnVal).createVariableAccessor();
            accessor.access(delay).accept(gateInput, returnGroup);//Ensure our output is clean
            returnGroup.getAccessors().add(accessor);
        }

        boolean signalMappingRequired = false;
        for(int i = 0; i < returnVal.getSignal().length; i++) {
            if(!returnVal.getSignal()[i].equals(currentFunctionContext.getSignature().getReturnSignals()[i])) {
                signalMappingRequired = true;
                break;
            }
        }

        NetworkGroup gateOutput;

        if(signalMappingRequired) {
            gateOutput = new NetworkGroup();
            returnGroup.getNetworks().add(gateOutput);

            for(int i = 0; i < returnVal.getSignal().length; i++) {
                ArithmeticCombinator cmb;
                if(returnVal.getSignal()[i].equals(currentFunctionContext.getSignature().getReturnSignals()[i])) {
                    cmb = ArithmeticCombinator.copying(returnVal.getSignal()[i]);
                    log("Copying signal " + returnVal.getSignal()[i]);
                }
                else {
                    cmb = ArithmeticCombinator.remapping(returnVal.getSignal()[i], currentFunctionContext.getSignature().getReturnSignals()[i]);
                    log("Remapping signal " + returnVal.getSignal()[i] + " to " + currentFunctionContext.getSignature().getReturnSignals()[i]);
                }
                returnGroup.getCombinators().add(cmb);
                cmb.setGreenIn(gateInput);
                cmb.setGreenOut(gateOutput);
            }
        }
        else {
            gateOutput = gateInput;
        }

        var accessor = currentFunctionContext.getControlFlowVariable().createVariableAccessor();
        accessor.access(delay + (signalMappingRequired ? 1 : 0)).accept(gateOutput, returnGroup);//Ensure our output is clean
        returnGroup.getAccessors().add(accessor);

        outputGate.setGreenIn(gateOutput);
    }

    @Override
    public void enterIfStatement(LanguageParser.IfStatementContext ctx) {
        currentFunctionContext.enterIfStatement();
    }

    @Override
    public void enterElseStatement(LanguageParser.ElseStatementContext ctx) {
        currentFunctionContext.enterElseStatement();
    }

    @Override
    public void enterIfExpr(LanguageParser.IfExprContext ctx) {
        currentFunctionContext.enterConditional();
    }

    @Override
    public void enterWhileExpr(LanguageParser.WhileExprContext ctx) {
        currentFunctionContext.enterLoop();
    }

    @Override
    public void enterLoopBody(LanguageParser.LoopBodyContext ctx) {
        ((WhileVariableScope) currentFunctionContext.getVariableScope()).enterLoopBody();
    }

    @Override
    public void exitWhileExpr(LanguageParser.WhileExprContext ctx) {
        WhileVariableScope whileScope = (WhileVariableScope) currentFunctionContext.getVariableScope();
        currentFunctionContext.leaveLoop();

        CombinatorGroup whileGroup = new CombinatorGroup(new NetworkGroup("while in"), new NetworkGroup("while out"));
        currentFunctionContext.getFunctionGroup().getSubGroups().add(whileGroup);
        whileGroup.getSubGroups().add(whileScope.getPreConditionProvider());
        whileGroup.getSubGroups().add(whileScope.getPostConditionProvider());

        var inputGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(inputGate);

        inputGate.setGreenIn(whileGroup.getInput());

        var dedupInput = DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        whileGroup.getCombinators().add(dedupInput);

        var dedupStore = DeciderCombinator.withAny(Accessor.constant(0), Writer.one(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(dedupStore);

        var dedupReset = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.fromInput(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(dedupReset);

        var dedupConstants = new ConstantCombinator(Map.of(Constants.TEMP_SIGNAL, -1));
        whileGroup.getCombinators().add(dedupConstants);

        NetworkGroup tmp = new NetworkGroup("dedup network");
        whileGroup.getNetworks().add(tmp);
        inputGate.setGreenOut(tmp);
        dedupInput.setGreenIn(tmp);
        dedupStore.setGreenIn(tmp);
        dedupStore.setGreenOut(tmp);
        dedupReset.setGreenOut(tmp);

        tmp = new NetworkGroup("dedup reset constant");
        whileGroup.getNetworks().add(tmp);
        dedupConstants.setGreenOut(tmp);
        dedupReset.setGreenIn(tmp);

        NetworkGroup dedupResetInput = new NetworkGroup("dedup reset input");
        whileGroup.getNetworks().add(dedupResetInput);
        dedupReset.setRedIn(dedupResetInput);

        tmp = new NetworkGroup("dedup input blocker internal");
        whileGroup.getNetworks().add(tmp);
        dedupStore.setRedIn(tmp);
        dedupInput.setRedOut(tmp);

        var loopFeedbackGateInitial = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(loopFeedbackGateInitial);

        var loopFeedbackGateSubsequent = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(loopFeedbackGateSubsequent);

        loopFeedbackGateInitial.setRedIn(tmp);

        NetworkGroup loopDataPreCondition = whileScope.getPreConditionProvider().getOutput();
        whileGroup.getNetworks().add(loopDataPreCondition);

        loopFeedbackGateInitial.setGreenOut(loopDataPreCondition);
        loopFeedbackGateSubsequent.setGreenOut(loopDataPreCondition);

        NetworkGroup loopFeedbackWire = new NetworkGroup("loop feedback");
        whileGroup.getNetworks().add(loopFeedbackWire);
        loopFeedbackGateSubsequent.setGreenIn(loopFeedbackWire);

        //The loop condition
        var condition = currentFunctionContext.popTempVariable();
        if(condition.getType() != PrimitiveType.BOOLEAN) {
            throw new IllegalArgumentException("Loop condition type must be boolean");
        }

        NetworkGroup conditionSignal = new NetworkGroup("loop condition");
        whileGroup.getNetworks().add(conditionSignal);

        var loopFeedback = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()[0]), Accessor.constant(1), Writer.everything(false), DeciderOperator.EQ);
        var loopExit = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()[0]), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        whileGroup.getCombinators().add(loopFeedback);
        whileGroup.getCombinators().add(loopExit);

        loopFeedback.setRedIn(conditionSignal);
        loopExit.setRedIn(conditionSignal);
        loopExit.setRedOut(dedupResetInput);

        loopFeedback.setGreenOut(whileScope.getPostConditionProvider().getOutput());
        loopExit.setGreenOut(whileGroup.getOutput());

        int innerLoopDelay = condition.getTickDelay();
        for(var x : whileScope.getDefinedVariables().values()) {
            innerLoopDelay = Math.max(innerLoopDelay, x.getTickDelay());
        }

        if(condition instanceof Constant) {
            ConstantCombinator connected = new ConstantCombinator(Map.of(condition.getSignal()[0], ((Constant) condition).getVal()[0]));
            connected.setRedOut(conditionSignal);
            whileGroup.getCombinators().add(connected);
        }
        else {
            NetworkGroup inner = new NetworkGroup("condition inner");
            whileGroup.getNetworks().add(inner);
            ArithmeticCombinator connected = ArithmeticCombinator.copying(condition.getSignal()[0]);
            connected.setRedOut(conditionSignal);
            connected.setGreenIn(inner);
            whileGroup.getCombinators().add(connected);
            var accessor = ((Variable)condition).createVariableAccessor();
            whileGroup.getAccessors().add(accessor);
            accessor.access(condition.getTickDelay()).accept(inner, whileGroup);
        }

        var bufferDelayInput = loopDataPreCondition;
        ArithmeticCombinator bufferDelayConnected = null;
        for(int i = condition.getTickDelay(); i >= 0; i--) {
            bufferDelayConnected = ArithmeticCombinator.withEach(Accessor.constant(0), ArithmeticOperator.ADD);
            bufferDelayConnected.setGreenIn(bufferDelayInput);
            whileGroup.getCombinators().add(bufferDelayConnected);
            if(i > 0) {
                bufferDelayInput = new NetworkGroup("loop data delay internal " + i);
                whileGroup.getNetworks().add(bufferDelayInput);
                bufferDelayConnected.setGreenOut(bufferDelayInput);
            }
        }
        NetworkGroup delayedSignal = new NetworkGroup("loop data delay out");
        whileGroup.getNetworks().add(delayedSignal);
        bufferDelayConnected.setGreenOut(delayedSignal);
        loopExit.setGreenIn(delayedSignal);
        loopFeedback.setGreenIn(delayedSignal);

        int outsideVariableDelay = 0;
        for(var varName : whileScope.getParentScope().getAllVariables().keySet()) {
            outsideVariableDelay = Math.max(outsideVariableDelay, whileScope.getParentScope().getNamedVariable(varName).getTickDelay());
        }

        log("Delay before loop start: " + outsideVariableDelay);

        //Get outside variables into the while loop
        for(var varName : whileScope.getParentScope().getAllVariables().keySet()) {
            var accessor = whileScope.getParentScope().getNamedVariable(varName).createVariableAccessor();
            whileGroup.getAccessors().add(accessor);
            accessor.access(outsideVariableDelay).accept(whileGroup);
        }

        for(var accessedVar : whileScope.getAllVariables().values()) {
            var accessor = accessedVar.createVariableAccessor();
            whileGroup.getAccessors().add(accessor);
            accessor.access(innerLoopDelay).accept(loopFeedbackWire, whileGroup);
        }

        for(var defined : whileScope.getParentScope().getAllVariables().entrySet()) {
            var v = defined.getValue();
            var rebound = currentFunctionContext.createNamedVariable(defined.getKey(), v.getType(), v.getSignal(), whileGroup);
            rebound.setDelay(0);
        }
    }

    //This is a general implementation that can handle loops inside if statements.
    //There are better solutions if we know the if statement doesn't contain loops
    @Override
    public void exitIfExpr(LanguageParser.IfExprContext ctx) {
        var condition = currentFunctionContext.popTempVariable();

        if(condition.getType() != PrimitiveType.BOOLEAN) {
            throw new IllegalArgumentException("Condition type must be boolean");
        }

        var conditionContext = currentFunctionContext.leaveConditional();

        CombinatorGroup conditionGroup = new CombinatorGroup(new NetworkGroup("condition input"), null);
        currentFunctionContext.getFunctionGroup().getSubGroups().add(conditionGroup);

        conditionGroup.getSubGroups().add(conditionContext.getIfProvider());
        conditionGroup.getSubGroups().add(conditionContext.getElseProvider());

        var ifInput = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()[0]), Accessor.constant(1), Writer.everything(false), DeciderOperator.EQ);
        var elseInput = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()[0]), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        conditionGroup.getCombinators().add(ifInput);
        conditionGroup.getCombinators().add(elseInput);

        ifInput.setGreenIn(conditionGroup.getInput());
        ifInput.setGreenOut(conditionContext.getIfProvider().getOutput());
        elseInput.setGreenIn(conditionGroup.getInput());
        elseInput.setGreenOut(conditionContext.getElseProvider().getOutput());

        int conditionDelay = condition.getTickDelay() + 1;//green -> red conversion :( @TODO allow accessors to use red wires instead

        int outsideVariableDelay = conditionDelay;
        int ifDelay = 0;
        int elseDelay = 0;
        for(var varName : currentFunctionContext.getVariableScope().getAllVariables().keySet()) {
            outsideVariableDelay = Math.max(outsideVariableDelay, currentFunctionContext.getNamedVariable(varName).getTickDelay());
            ifDelay = Math.max(ifDelay, conditionContext.getIfScope().getNamedVariable(varName).getTickDelay());
            elseDelay = Math.max(elseDelay, conditionContext.getElseScope().getNamedVariable(varName).getTickDelay());
        }

        log("Outside delay " + outsideVariableDelay);
        log("If delay " + ifDelay);
        log("Else delay " + elseDelay);

        CombinatorGroup conditionOutputGroup = new CombinatorGroup(null, new NetworkGroup("if output (if/else merged)"));
        conditionGroup.getSubGroups().add(conditionOutputGroup);
        NetworkGroup ifDataOut = new NetworkGroup("if result output");
        NetworkGroup elseDataOut = new NetworkGroup("else result output");
        conditionOutputGroup.getNetworks().add(ifDataOut);
        conditionOutputGroup.getNetworks().add(elseDataOut);

        DeciderCombinator ifOutGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        DeciderCombinator elseOutGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        conditionOutputGroup.getCombinators().add(ifOutGate);
        conditionOutputGroup.getCombinators().add(elseOutGate);

        ifOutGate.setGreenIn(ifDataOut);
        elseOutGate.setGreenIn(elseDataOut);
        ifOutGate.setGreenOut(conditionOutputGroup.getOutput());
        elseOutGate.setGreenOut(conditionOutputGroup.getOutput());


        //Get outside variables into the if/else
        for(var e : currentFunctionContext.getVariableScope().getAllVariables().entrySet()) {
            var varName = e.getKey();
            var variable = e.getValue();

            //Outside into if/else
            var accessor = currentFunctionContext.getNamedVariable(varName).createVariableAccessor();
            conditionGroup.getAccessors().add(accessor);
            accessor.access(outsideVariableDelay).accept(conditionGroup);

            //If var out
            accessor = conditionContext.getIfScope().getNamedVariable(varName).createVariableAccessor();
            conditionGroup.getAccessors().add(accessor);
            accessor.access(ifDelay).accept(ifDataOut, conditionOutputGroup);

            //Else var out
            accessor = conditionContext.getElseScope().getNamedVariable(varName).createVariableAccessor();
            conditionGroup.getAccessors().add(accessor);
            accessor.access(elseDelay).accept(elseDataOut, conditionOutputGroup);

            var produced = currentFunctionContext.getVariableScope().createNamedVariable(varName, variable.getType(), variable.getSignal(), conditionOutputGroup);
            produced.setDelay(0);
        }

        NetworkGroup conditionWire = new NetworkGroup("condition wire");
        conditionGroup.getNetworks().add(conditionWire);
        ifInput.setRedIn(conditionWire);
        elseInput.setRedIn(conditionWire);

        if(condition instanceof Constant) {
            var connected = new ConstantCombinator(Map.of(condition.getSignal()[0], ((Constant) condition).getVal()[0]));
            connected.setRedOut(conditionWire);
            conditionGroup.getCombinators().add(connected);
        }
        else {
            var connected = ArithmeticCombinator.copying(condition.getSignal()[0]);
            NetworkGroup conditionWireGreen = new NetworkGroup("green -> red input wire");
            conditionGroup.getNetworks().add(conditionWireGreen);
            connected.setGreenIn(conditionWireGreen);
            connected.setRedOut(conditionWire);
            conditionGroup.getCombinators().add(connected);
            var accessor = ((Variable)condition).createVariableAccessor();
            conditionGroup.getAccessors().add(accessor);
            accessor.access(outsideVariableDelay - 1).accept(conditionWireGreen, conditionGroup);
        }
    }

    @Override
    public void exitFunctionCall(LanguageParser.FunctionCallContext ctx) {
        var targetFunction = definedFunctions.get(ctx.functionName().getText());
        log("Calling function " + targetFunction.getSignature());
        indentationLevel++;
        Symbol[] arguments = new Symbol[targetFunction.getSignature().getParameters().length];
        int argumentDelay = 0;
        for(int i = arguments.length - 1; i >= 0; i--) {
            var tmpVar = currentFunctionContext.popTempVariable();
            arguments[i] = tmpVar;
            log("Param " + Arrays.toString(targetFunction.getSignature().getParameters()[i].getSignal()) + " as " + tmpVar);
            int delay = tmpVar.getTickDelay();
            if(!Arrays.equals(targetFunction.getSignature().getParameters()[i].getSignal(), tmpVar.getSignal())) {
                delay++; //Mapping to new signal type can be skipped if they are identical
            }
            argumentDelay = Math.max(argumentDelay, delay);
        }
        int outsideDelay = 0;
        for(var variable : currentFunctionContext.getVariableScope().getAllVariables().values()) {
            outsideDelay = Math.max(outsideDelay, variable.getTickDelay());
        }

        int totalDelay = Math.max(outsideDelay, argumentDelay);

        CombinatorGroup functionCallInput = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        currentFunctionContext.getFunctionGroup().getSubGroups().add(functionCallInput);

        CombinatorGroup functionCallReturn = new CombinatorGroup(new NetworkGroup(), new NetworkGroup("Function call out (" + targetFunction.getSignature() + ")"));
        functionCallInput.getSubGroups().add(functionCallReturn);

        //Deduplication of outside signals
        var inputGateFunctionArguments = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(inputGateFunctionArguments);

        var inputGateVariableScope = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(inputGateVariableScope);

        inputGateVariableScope.setGreenIn(functionCallInput.getInput());

        var dedupInputFunctionArguments = DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(dedupInputFunctionArguments);

        var dedupInputVariableScope = DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(dedupInputVariableScope);

        var dedupStore = DeciderCombinator.withAny(Accessor.constant(0), Writer.one(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(dedupStore);

        var dedupReset = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.fromInput(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(dedupReset);

        var dedupConstants = new ConstantCombinator(Map.of(Constants.TEMP_SIGNAL, -1));
        functionCallInput.getCombinators().add(dedupConstants);

        NetworkGroup tmp = new NetworkGroup("dedup network");
        functionCallInput.getNetworks().add(tmp);
        dedupInputVariableScope.setGreenIn(tmp);
        dedupInputFunctionArguments.setGreenIn(tmp);
        dedupStore.setGreenIn(tmp);
        dedupStore.setGreenOut(tmp);
        dedupReset.setGreenOut(tmp);

        tmp = new NetworkGroup("arguments input forward");
        functionCallInput.getNetworks().add(tmp);
        inputGateFunctionArguments.setRedOut(tmp);
        dedupInputFunctionArguments.setRedIn(tmp);

        tmp = new NetworkGroup("state input forward");
        functionCallInput.getNetworks().add(tmp);
        inputGateVariableScope.setRedOut(tmp);
        dedupInputVariableScope.setRedIn(tmp);
        dedupStore.setRedIn(tmp);

        tmp = new NetworkGroup("dedup reset constant");
        functionCallInput.getNetworks().add(tmp);
        dedupConstants.setGreenOut(tmp);
        dedupReset.setGreenIn(tmp);

        //Forward signals, replacing control flow with predetermined value
        var c1 = ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);
        var c2 = ArithmeticCombinator.copying();
        var c3 = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.one(Constants.CONTROL_FLOW_SIGNAL), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(c1);
        functionCallInput.getCombinators().add(c2);
        functionCallInput.getCombinators().add(c3);

        NetworkGroup forwardIn = new NetworkGroup();
        functionCallInput.getNetworks().add(forwardIn);

        c1.setRedIn(forwardIn);
        c2.setRedIn(forwardIn);
        c3.setRedIn(forwardIn);
        dedupInputFunctionArguments.setRedOut(forwardIn);

        var c4 = ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);
        var c5 = ArithmeticCombinator.copying();

        int functionCallId = currentFunctionCallId++;
        var c6 = ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(functionCallId), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);

        functionCallInput.getCombinators().add(c4);
        functionCallInput.getCombinators().add(c5);
        functionCallInput.getCombinators().add(c6);

        NetworkGroup forwardInternal = new NetworkGroup();
        functionCallInput.getNetworks().add(forwardInternal);

        c1.setRedOut(forwardInternal);
        c2.setRedOut(forwardInternal);
        c3.setRedOut(forwardInternal);

        c4.setRedIn(forwardInternal);
        c5.setRedIn(forwardInternal);
        c6.setRedIn(forwardInternal);

        c4.setGreenOut(currentFunctionContext.getFunctionCallOutputGroup());
        c5.setGreenOut(currentFunctionContext.getFunctionCallOutputGroup());
        c6.setGreenOut(currentFunctionContext.getFunctionCallOutputGroup());

        //Store previous state, forward when function call was completed
        NetworkGroup stateStoreOut = new NetworkGroup();
        functionCallInput.getNetworks().add(stateStoreOut);

        var preCallStateStore = DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(preCallStateStore);

        NetworkGroup storeIn = new NetworkGroup();
        functionCallInput.getNetworks().add(storeIn);

        dedupInputVariableScope.setRedOut(storeIn);
        preCallStateStore.setRedIn(storeIn);
        dedupReset.setRedOut(storeIn);
        preCallStateStore.setGreenIn(stateStoreOut);
        preCallStateStore.setGreenOut(stateStoreOut);

        var preCallStateOutputGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(-1), Writer.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(preCallStateOutputGate);

        preCallStateOutputGate.setGreenIn(stateStoreOut);
        preCallStateOutputGate.setRedIn(storeIn);

        //Function call data return. We might be able to ditch this one
        var returnGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(functionCallId), Writer.everything(false), DeciderOperator.EQ);
        functionCallReturn.getCombinators().add(returnGate);
        returnGate.setRedIn(currentFunctionContext.getFunctionCallReturnGroup());

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);

        dedupReset.setRedIn(tmp);
        returnGate.setRedOut(tmp);

        var filter1 = ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);
        var filter2 = ArithmeticCombinator.copying();
        functionCallReturn.getCombinators().add(filter1);
        functionCallReturn.getCombinators().add(filter2);

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        filter1.setGreenIn(tmp);
        filter2.setGreenIn(tmp);
        returnGate.setGreenOut(tmp);

        var filter3 = ArithmeticCombinator.copying();
        functionCallReturn.getCombinators().add(filter3);

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        filter1.setGreenOut(tmp);
        filter2.setGreenOut(tmp);
        filter3.setGreenIn(tmp);

        var outputGate = DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
        functionCallReturn.getCombinators().add(outputGate);

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        outputGate.setGreenIn(tmp);
        preCallStateOutputGate.setGreenOut(tmp);
        filter3.setGreenOut(tmp);

        var c8 = new ConstantCombinator(Map.of(Constants.TEMP_SIGNAL, 1));
        functionCallReturn.getCombinators().add(c8);
        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        c8.setRedOut(tmp);
        outputGate.setRedIn(tmp);
        outputGate.setGreenOut(functionCallReturn.getOutput());

        NetworkGroup argumentsIn = new NetworkGroup();
        functionCallInput.getNetworks().add(argumentsIn);

        inputGateFunctionArguments.setGreenIn(argumentsIn);

        var functionCallIdCombinator = new ConstantCombinator(Map.of(Constants.FUNCTION_IDENTIFIER, targetFunction.getSignature().getFunctionId()));
        functionCallInput.getCombinators().add(functionCallIdCombinator);
        functionCallIdCombinator.setGreenOut(argumentsIn);

        var accessorTmp = currentFunctionContext.getControlFlowVariable().createVariableAccessor();
        functionCallInput.getAccessors().add(accessorTmp);
        accessorTmp.access(totalDelay).accept(argumentsIn, functionCallInput);

        for(var variable : currentFunctionContext.getVariableScope().getAllVariables().values()) {
            var accessor = variable.createVariableAccessor();
            functionCallInput.getAccessors().add(accessor);
            accessor.access(totalDelay).accept(functionCallInput);
        }

        for(int i = 0; i < arguments.length; i++) {
            FactorioSignal[] targetSignal = targetFunction.getSignature().getParameters()[i].getSignal();

            Symbol argument = arguments[i];
            if(argument instanceof Constant) {
                int[] vals = ((Constant) argument).getVal();
                Map<FactorioSignal, Integer> constants = new HashMap<>();
                for(int j = 0; j < vals.length; j++) {
                    constants.put(targetSignal[j], vals[j]);
                }
                ConstantCombinator combinator = new ConstantCombinator(constants);
                functionCallInput.getCombinators().add(combinator);
                combinator.setGreenOut(argumentsIn);
            }
            else {
                Variable var = (Variable) argument;
                for(int j = 0; j < var.getSignal().length; j++) {
                    if(var.getSignal()[j] == targetSignal[j]) {
                        var accessor = var.createVariableAccessor();
                        functionCallInput.getAccessors().add(accessor);
                        accessor.access(totalDelay).accept(argumentsIn, functionCallInput);
                    }
                    else {
                        log("Remapping combinator for " + var.getSignal()[j] + " -> " + targetSignal[j]);
                        NetworkGroup paramRemapIn = new NetworkGroup();
                        functionCallInput.getNetworks().add(paramRemapIn);
                        var accessor = var.createVariableAccessor();
                        functionCallInput.getAccessors().add(accessor);
                        accessor.access(totalDelay - 1).accept(paramRemapIn, functionCallInput);
                        ArithmeticCombinator arithmeticCombinator = ArithmeticCombinator.remapping(argument.getSignal()[j], targetSignal[j]);
                        functionCallInput.getCombinators().add(arithmeticCombinator);
                        arithmeticCombinator.setGreenIn(paramRemapIn);
                        arithmeticCombinator.setGreenOut(argumentsIn);
                    }
                }
            }
        }

        for(var defined : currentFunctionContext.getVariableScope().getAllVariables().entrySet()) {
            var v = defined.getValue();
            var rebound = currentFunctionContext.createNamedVariable(defined.getKey(), v.getType(), v.getSignal(), functionCallReturn);
            rebound.setDelay(0);
            log("Rebinding " + v + " " + defined.getKey() + " as " + rebound);
        }

        var returnType = targetFunction.getSignature().getReturnType();
        var functionReturnVal = currentFunctionContext.createBoundTempVariable(returnType, targetFunction.getSignature().getReturnSignals(), functionCallReturn);
        functionReturnVal.setDelay(0);
        log("Function return value: " + functionReturnVal);

        //Example of "functions"
        //0eNrtWllu2zAQvQs/WyUQqc0S0I+kvUURGIpMx0RtyaCooEagA/QWPVtPUtJKbNlayJGXOHV/AjiSRuS8eW8W8QU9zgu65CwVKHpBLMnSHEXfX1DOntJ4rv4nVkuKIsQEXSALpfFC/Yo5E7MFFSy5SbLFI0tjkXFUWoilE/oTRbi0tDYmNGETytsNkPLBQjQVTDBarWj9YzVOi8Uj5fINmrVYaJnl8uksVQtQFn0LrVB0E/i3nnzPhHGaVJeJheS+Bc/m40c6i5+ZfFw+s7U7lpcna1u5ujBlPBfjxu6eGReF/M9mYdUdN3dqWzlVNswfulcPZUvK42qN6JO8JSvEsgC8+Ssqy3K9ubTa63r5WP154pSmdb+yCYoceS/jScHE+icuH+TTxPR2CZl6XQMpMhgp98xIJTOa/KihpdggYkUNexeMzwPAeDV+VkCsncte01obXs5gvLwPyawhYH4DAon3Xb+LDOm/7DaJ1hcGpD8MsBlt3T6hbsaA5Ooba8t21F/NmEFumG62DFXBnS2WMV9fitCXAajSZ8pXYsbSp8r2ciUXW6RiPOXZYsxSaQxFghcUhj2Qtl289MC8DHYwqfHSPS0vaZzMTiWjlW0QAKSTPpxOWtFqc78P48No43vvGHzQpqwdJ+8w4c+v3+/BhRbfBqqoa4VopJGs0Eyzgs0u3tzRD1K4AYncdsE0ZXNBeUdV3OU9lk6zym+FggTXCuMHc3/5ZsIwAgvDNjid8qqkAANTqSECITDu8Mb/+KRxd2MSeO2Oss12ru6DyCJ53be9Xyr6H1Ekt+4+hjziZqLqkMuwP4X5pnb2dbVRlxq2DHjbjeeLeC6pOZf75VKGltmctoSBtyGA/UYAEw95mjTRqKO7Nu727xsHZvkGE1j0bwsybFSQnSr61zL47tVyV/XVE9m2KcDYNYxceLcb7oB4xclT19e0JdtWDNwDCphTY9A9E1LJ9dAJ3ZChEHY04kVgNQ7WjI1GhkyC96fOG4p+eH0ojjSpFwNRHBmSDdjHEi1GR0lScVpPJWet0qbxPAflKL+fLwQ2b/A1bLYNcQ3A9PMugH5K+y+Dfhp6NXAx5Ru8O3fPhsulFRgerCzXgqbrF0zL/HDg7O+0mrnVtXoyu8i6nnjGLal9kgkNsYEjGnuDoX/cEc1dbT7jaD6YG7R0aztu7bs5xM59qw1HgcPysXJGlRwhw3ZTQPAB5WJwdeUi0XzUI+5h0og19jo/7pOh5WRwXmm0L1UZdcD6kGqSaL4kN6vNLh0m2JDHDiwAvPMEwMlGvjXD8G6i3dMufAjb+/nflLruAZVp8M9Wpm2sCgA02QMPeISn2fb32+sE1xs4mf6vy9CKlTjwDr5HwXU9UGioy4PnPFd6XqEdWxuYbjvAgB5M2AqtO6z7MD4/VbwenhpyPoG0fmRRzYN6e1Q7W2whCU9enQkcYTcISeD4rms7pCz/AvsiUOs=
        indentationLevel--;
    }

    @Override
    public void exitBoolExpr(LanguageParser.BoolExprContext ctx) {
        if(ctx.leftComponent != null) {
            log("Exit " + ctx.getText());
            BOOL_EXPR_COMPONENT_PARSER.parse(currentFunctionContext, ctx);
        }
        else if(ctx.left != null) {log("Exit " + ctx.getText());
            BOOL_EXPR_PARSER.parse(currentFunctionContext, ctx);
        }
        else if(ctx.negated != null) {
            NOT_EXPR_PARSER.parse(currentFunctionContext, ctx);
        }
    }

    @Override
    public void exitExpr(LanguageParser.ExprContext ctx) {
        if(ctx.tuple != null) {
            var tupleVar = currentFunctionContext.popTempVariable();
            if(!(tupleVar.getType() instanceof TupleType)) {
                throw new UnsupportedOperationException("Expected tuple type, found " + tupleVar.getType());
            }
            var tupleType = (TupleType)tupleVar.getType();
            var propId = Integer.parseInt(ctx.propertyId.getText());
            if(tupleType.getSubtypes().length <= propId || propId < 0) {
                throw new IllegalArgumentException("Tried to access property " + propId + " of tuple with properties " + tupleType);
            }
            var subtype = tupleType.getSubtypes()[propId];
            int offset = 0;
            for(int i = 0; i < propId; i++) {
                offset += tupleType.getSubtypes()[i].getSize();
            }
            if(tupleVar instanceof Constant) {
                log("Accessing tuple " + tupleVar + " value " + propId);
                int[] constVal = new int[subtype.getSize()];
                System.arraycopy(((Constant)tupleVar).getVal(), offset, constVal, 0, subtype.getSize());
                currentFunctionContext.pushTempVariable(new Constant(subtype, constVal));
            }
            else {
                if(!tupleVar.isBound()) {
                    tupleVar.bind(currentFunctionContext.getFreeSymbols(tupleVar.getType().getSize()));
                }
                var oldSignals = new FactorioSignal[subtype.getSize()];
                System.arraycopy(tupleVar.getSignal(), offset, oldSignals, 0, oldSignals.length);
                var newSignals = currentFunctionContext.getFreeSymbols(subtype.getSize());
                log("Accessing tuple " + tupleVar + " value " + propId + " -> " + Arrays.toString(newSignals));
                CombinatorGroup group = new CombinatorGroup(((Variable)tupleVar).getProducer().getOutput(), new NetworkGroup());
                currentFunctionContext.getFunctionGroup().getSubGroups().add(group);
                for(int i = 0; i < oldSignals.length; i++) {
                    ArithmeticCombinator ac = ArithmeticCombinator.remapping(oldSignals[i], newSignals[i]);
                    group.getCombinators().add(ac);
                    ac.setGreenIn(group.getInput());
                    ac.setGreenOut(group.getOutput());
                }
                currentFunctionContext.createBoundTempVariable(subtype, newSignals, group).setDelay(tupleVar.getTickDelay() + 1);
            }
        }
        else if(ctx.tupleValues != null) {
            int delay = 0;
            Symbol[] symbols = new Symbol[ctx.tupleValues.expr().size()];
            Type[] types = new Type[symbols.length];
            for(int i = symbols.length - 1; i >= 0; i--) {
                var symbol = currentFunctionContext.popTempVariable();
                if(!symbol.isBound()) {
                    symbol.bind(currentFunctionContext.getFreeSymbols(symbol.getType().getSize()));
                }
                symbols[i] = symbol;
                types[i] = symbol.getType();
                delay = Math.max(delay, symbols[i].getTickDelay());
            }
            Type tupleType = new TupleType(types);
            //Do we really need new symbols here? Issue:
            // a = x
            // b = (a, x)
            // c = b.0 * a //b.0 and a need to have a different symbol in this case, but not _always_

            CombinatorGroup group = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
            currentFunctionContext.getFunctionGroup().getSubGroups().add(group);
            int offset = 0;
            FactorioSignal[] remappedSignals = currentFunctionContext.getFreeSymbols(tupleType.getSize());
            int tupleCreationDelay = 0;
            for(Symbol symbol : symbols) {
                if(symbol instanceof Constant) {
                    int[] vals = ((Constant) symbol).getVal();
                    Map<FactorioSignal, Integer> constants = new HashMap<>();
                    for(int j = 0; j < vals.length; j++) {
                        constants.put(remappedSignals[offset++], vals[j]);
                    }
                    ConstantCombinator cc = new ConstantCombinator(constants);
                    group.getCombinators().add(cc);
                    cc.setGreenOut(group.getOutput());
                }
                else {
                    tupleCreationDelay = 1;
                    var accessor = ((Variable)symbol).createVariableAccessor();
                    group.getAccessors().add(accessor);
                    accessor.access(delay).accept(group);
                    for(FactorioSignal signal : symbol.getSignal()) {
                        ArithmeticCombinator ac = ArithmeticCombinator.remapping(signal, remappedSignals[offset++]);
                        group.getCombinators().add(ac);
                        ac.setGreenIn(group.getInput());
                        ac.setGreenOut(group.getOutput());
                    }
                }
            }
            log("Combining " + Arrays.toString(symbols) + " into tuple, delay: " + (delay + tupleCreationDelay));
            currentFunctionContext.createBoundTempVariable(tupleType, remappedSignals, group).setDelay(delay + tupleCreationDelay);
        }
        else if(ctx.left != null) {
            EXPR_PARSER.parse(currentFunctionContext, ctx);
        }
        else if(ctx.numberLit != null) {
            int val = Integer.parseInt(ctx.getText());
            currentFunctionContext.pushTempVariable(new Constant(val));
        }
        else if(ctx.var != null) {
            var named = currentFunctionContext.getNamedVariable(ctx.var.getText());
            if(named == null) throw new RuntimeException("Variable " + ctx.var.getText() + " is not defined");
            currentFunctionContext.pushTempVariable(named);
        }
    }

    @Override
    public void exitAssignment(LanguageParser.AssignmentContext ctx) {
        var value = currentFunctionContext.popTempVariable();
        String varName = ctx.var.getText();

        FactorioSignal[] variableSymbols;
        //We don't care about new variables inside our if block, they go out of scope anyway
        if(currentFunctionContext.getNamedVariable(varName) != null) {
            var existing = currentFunctionContext.getNamedVariable(varName);
            variableSymbols = existing.getSignal();
        }
        else {
            variableSymbols = currentFunctionContext.getFreeSymbols(value.getType().getSize());
        }

        CombinatorGroup group = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        currentFunctionContext.getFunctionGroup().getSubGroups().add(group);

        if(value instanceof Constant) {
            int[] vals = ((Constant) value).getVal();
            Map<FactorioSignal, Integer> constants = new HashMap<>();
            for(int j = 0; j < vals.length; j++) {
                constants.put(variableSymbols[j], vals[j]);
            }
            var connected = new ConstantCombinator(constants);
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
        }
        else {
            //TODO remove this and use the value directly (is this a good idea?)
            for(int i = 0; i < variableSymbols.length; i++) {
                var connected = ArithmeticCombinator.withLeftRight(Accessor.signal(value.getSignal()[i]), Accessor.constant(0), variableSymbols[i], ArithmeticOperator.ADD);
                connected.setGreenOut(group.getOutput());
                connected.setGreenIn(group.getInput());
                group.getCombinators().add(connected);
            }
        }
        var named= currentFunctionContext.createNamedVariable(varName, value.getType(), variableSymbols, group);
        named.setDelay(value.getTickDelay() + 1); //Aliasing
        if(value instanceof Variable) {
            var accessor = ((Variable) value).createVariableAccessor();
            group.getAccessors().add(accessor);
            accessor.access(value.getTickDelay()).accept(group);
        }
        log("Creating named " + varName + " = " + named + ", with delay " + named.getTickDelay());
    }

    private void log(String msg) {
        System.out.println("\t".repeat(currentFunctionContext.getDepth() + indentationLevel) + msg);
    }

    public static String generateBlueprint(String code) {
        LanguageParser parser = new LanguageParser(new CommonTokenStream(new LanguageLexer(CharStreams.fromString(code))));

        var structureParser = new StructureParser();
        parser.addParseListener(structureParser);

        parser.file();

        parser = new LanguageParser(new CommonTokenStream(new LanguageLexer(CharStreams.fromString(code))));

        var generator = new Generator(structureParser.getFunctions());

        parser.addParseListener(generator);
        parser.file();

        List<EntityBlock> entityBlocks = new ArrayList<>();

        var functions = new ArrayList<>(generator.definedFunctions.values());
        boolean mainFound = false;
        for(int i = 0; i < functions.size(); i++) {
            if(functions.get(i).getSignature().getName().equals("main")) {
                var tmp = functions.get(i);
                functions.set(i, functions.get(0));
                functions.set(0, tmp);
                mainFound = true;
                break;
            }
        }
        if(!mainFound) {
            System.out.println("Warning: No main method defined");
        }
        for(var function : functions) {
            System.out.println("Generating function " + function.getSignature());
            Set<CombinatorGroup> generatedGroups = new HashSet<>();
            Queue<CombinatorGroup> toExpand = new LinkedList<>();
            toExpand.add(function.getFunctionGroup());
            while(!toExpand.isEmpty()) {
                var group = toExpand.poll();
                generatedGroups.add(group);
                toExpand.addAll(group.getSubGroups());
            }

            generatedGroups.forEach(g -> {
                g.getAccessors().forEach(VariableAccessor::generateAccessors);
            });

            var combinators = generatedGroups.stream()
                    .map(CombinatorGroup::getCombinators)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            var networks = generatedGroups.stream()
                    .peek(x -> {
                        if(x.getNetworks().contains(null)) {
                            System.out.println(x);
                        }
                    })
                    .map(CombinatorGroup::getNetworks)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            entityBlocks.add(FunctionPlacer.placeFunction(combinators, networks, function.getFunctionCallOutputGroup(), function.getFunctionCallReturnGroup()));
        }

        int currentX = 0;
        for(var block : entityBlocks) {
            block.applyOffset(currentX, 0);
            currentX += block.getMaxX() - block.getMinX() + 3;
        }
        var poles = FunctionPlacer.generateFunctionConnectors(entityBlocks);
        entityBlocks.add(poles);
        return BlueprintWriter.writeBlueprint(entityBlocks);
    }

    public static void main(String[] args) {
        generateBlueprint(TEST);
    }
}
