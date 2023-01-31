package me.joba.factorio.lang;

import me.joba.factorio.*;
import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.game.entities.*;
import me.joba.factorio.graph.FunctionPlacer;
import me.joba.factorio.lang.expr.BooleanExpressionResolver;
import me.joba.factorio.lang.expr.BooleanNotExpressionResolver;
import me.joba.factorio.lang.expr.ComparisonExpressionResolver;
import me.joba.factorio.lang.expr.IntExpressionResolver;
import me.joba.factorio.lang.types.ArrayType;
import me.joba.factorio.lang.types.PrimitiveType;
import me.joba.factorio.lang.types.TupleType;
import me.joba.factorio.lang.types.Type;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;
import java.util.stream.Collectors;

public class Generator extends LanguageBaseListener {

    private static final String ARRAY_SORT_TEST = """

            int[10] ARRAY_1;

            function main() -> void {
                sort(ARRAY_1, 10);
            }

            function sort(arr: int[], size: int) -> void {
                start = 0;
                while(start < size - 1) {
                    index = start + 1;
                    smallestIndex = start;
                    smallest = arr[start];
                    while(index < size) {
                        x = arr[index];
                        if(x < smallest) {
                            smallest = x;
                            smallestIndex = index;
                        }
                        index = index + 1;
                    }
                    tmp = arr[start];
                    arr[start] = smallest;
                    arr[smallestIndex] = tmp;
                    start = start + 1;
                }
            }
            """;

    private static final String FUNCTION_FUCKING_COMPLEX =
            """
                    function main(start: int<red>, end: int<green>, iterations: int<i>) -> int {
                      max = -1;
                      while(start <= end) {
                        max = max(collatz(start, iterations), max);
                        start = start + 1;
                      }
                      return max;
                    }

                    function collatz(currentVal: int, iterations: int) -> int {
                      max = currentVal;
                      while(iterations != 0) {
                        iterations = iterations - 1;
                        if(currentVal % 2 == 0) {
                          currentVal = currentVal / 2;
                        }
                        else {
                          currentVal = currentVal * 3 + 1;
                        }
                        max = max(currentVal, max);
                      }
                      return max;
                    }

                    function max(a: int, b: int) -> int {
                      if(a < b) {
                        a = b;
                      }
                      return a;
                    }""";

    private static final String TEST = FUNCTION_FUCKING_COMPLEX;

    private static final ExpressionResolver<LanguageParser.ExprContext, ArithmeticOperator> EXPR_PARSER = new IntExpressionResolver();
    private static final ExpressionResolver<LanguageParser.BoolExprContext, DeciderOperator> BOOL_EXPR_COMPONENT_PARSER = new ComparisonExpressionResolver();
    private static final ExpressionResolver<LanguageParser.BoolExprContext, ArithmeticOperator> BOOL_EXPR_PARSER = new BooleanExpressionResolver();
    private static final ExpressionResolver<LanguageParser.BoolExprContext, Void> NOT_EXPR_PARSER = new BooleanNotExpressionResolver();

    private static final int CONSTANT_DELAY_FUNCTION_OVERHEAD = 2;

    private FunctionContext currentFunctionContext;
    private final Map<String, FunctionContext> definedFunctions;
    private final Map<String, ArrayDeclaration> declaredArrays;
    private FunctionContext arrayReadFunction;
    private FunctionContext arrayWriteFunction;
    private int currentFunctionCallId = 1;
    private int indentationLevel = 0;

    public Generator(Map<String, FunctionContext> definedFunctions, Map<String, ArrayDeclaration> declaredArrays) {
        this.definedFunctions = definedFunctions;
        this.declaredArrays = declaredArrays;
        arrayReadFunction = new FunctionContext(MemoryUtil.MEMORY_READ_SIGNATURE);
        arrayWriteFunction = new FunctionContext(MemoryUtil.MEMORY_WRITE_SIGNATURE);
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
            inputCombinator = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
            inputCombinator.setFixedLocation(true);
            inputCombinator.setX(0);
            inputCombinator.setY(0);
            inputCombinator.setOrientation(2);
        }
        else {
            inputCombinator = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.FUNCTION_IDENTIFIER), CombinatorIn.constant(currentFunctionContext.getSignature().getFunctionId()), CombinatorOut.everything(false), DeciderOperator.EQ);
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
            throw new RuntimeException("Invalid return type " + returnVal.getType() + ", expected " + Arrays.toString(currentFunctionContext.getSignature().getReturnSignals()));
        }

        var returnGroup = currentFunctionContext.getFunctionReturnGroup();

        var outputGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
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

        var inputGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(inputGate);

        inputGate.setGreenIn(whileGroup.getInput());

        var dedupInput = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.TEMP_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
        whileGroup.getCombinators().add(dedupInput);

        var dedupStore = DeciderCombinator.withAny(CombinatorIn.constant(0), CombinatorOut.one(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(dedupStore);

        var dedupReset = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.fromInput(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
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

        var loopFeedbackGateInitial = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
        whileGroup.getCombinators().add(loopFeedbackGateInitial);

        var loopFeedbackGateSubsequent = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
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

        var loopFeedback = DeciderCombinator.withLeftRight(CombinatorIn.signal(condition.getSignal()[0]), CombinatorIn.constant(1), CombinatorOut.everything(false), DeciderOperator.EQ);
        var loopExit = DeciderCombinator.withLeftRight(CombinatorIn.signal(condition.getSignal()[0]), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
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
            bufferDelayConnected = ArithmeticCombinator.withEach(CombinatorIn.constant(0), ArithmeticOperator.ADD);
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
        currentFunctionContext.clearFunctionCallSlotReservations();
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

        var ifInput = DeciderCombinator.withLeftRight(CombinatorIn.signal(condition.getSignal()[0]), CombinatorIn.constant(1), CombinatorOut.everything(false), DeciderOperator.EQ);
        var elseInput = DeciderCombinator.withLeftRight(CombinatorIn.signal(condition.getSignal()[0]), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
        conditionGroup.getCombinators().add(ifInput);
        conditionGroup.getCombinators().add(elseInput);

        ifInput.setGreenIn(conditionGroup.getInput());
        ifInput.setGreenOut(conditionContext.getIfProvider().getOutput());
        elseInput.setGreenIn(conditionGroup.getInput());
        elseInput.setGreenOut(conditionContext.getElseProvider().getOutput());

        int outsideVariableDelay = condition.getTickDelay() + 1;
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

        DeciderCombinator ifOutGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
        DeciderCombinator elseOutGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
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

        if(targetFunction.getSignature().isConstantDelay()) {
            generateConstantTimeFunctionCall(targetFunction);
            return;
        }

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
        var inputGateFunctionArguments = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(inputGateFunctionArguments);

        var inputGateVariableScope = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(inputGateVariableScope);

        inputGateVariableScope.setGreenIn(functionCallInput.getInput());

        var dedupInputFunctionArguments = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.TEMP_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(dedupInputFunctionArguments);

        var dedupInputVariableScope = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.TEMP_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(dedupInputVariableScope);

        var dedupStore = DeciderCombinator.withAny(CombinatorIn.constant(0), CombinatorOut.one(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(dedupStore);

        var dedupReset = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.fromInput(Constants.TEMP_SIGNAL), DeciderOperator.NEQ);
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
        var c1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);
        var c2 = ArithmeticCombinator.copying();
        var c3 = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(Constants.CONTROL_FLOW_SIGNAL), DeciderOperator.NEQ);
        functionCallInput.getCombinators().add(c1);
        functionCallInput.getCombinators().add(c2);
        functionCallInput.getCombinators().add(c3);

        NetworkGroup forwardIn = new NetworkGroup();
        functionCallInput.getNetworks().add(forwardIn);

        c1.setRedIn(forwardIn);
        c2.setRedIn(forwardIn);
        c3.setRedIn(forwardIn);
        dedupInputFunctionArguments.setRedOut(forwardIn);

        var c4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);
        var c5 = ArithmeticCombinator.copying();

        int functionCallId = currentFunctionCallId++;
        var c6 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(functionCallId), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);

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

        var preCallStateStore = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.TEMP_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(preCallStateStore);

        NetworkGroup storeIn = new NetworkGroup();
        functionCallInput.getNetworks().add(storeIn);

        dedupInputVariableScope.setRedOut(storeIn);
        preCallStateStore.setRedIn(storeIn);
        dedupReset.setRedOut(storeIn);
        preCallStateStore.setGreenIn(stateStoreOut);
        preCallStateStore.setGreenOut(stateStoreOut);

        var preCallStateOutputGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.TEMP_SIGNAL), CombinatorIn.constant(-1), CombinatorOut.everything(false), DeciderOperator.EQ);
        functionCallInput.getCombinators().add(preCallStateOutputGate);

        preCallStateOutputGate.setGreenIn(stateStoreOut);
        preCallStateOutputGate.setRedIn(storeIn);

        //Function call data return. We might be able to ditch this one
        var returnGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(functionCallId), CombinatorOut.everything(false), DeciderOperator.EQ);
        functionCallReturn.getCombinators().add(returnGate);
        returnGate.setRedIn(currentFunctionContext.getFunctionCallReturnGroup());

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);

        dedupReset.setRedIn(tmp);
        returnGate.setRedOut(tmp);

        var filter1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticOperator.MUL);
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

        var outputGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
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

        for (var variable : currentFunctionContext.getVariableScope().getAllVariables().values()) {
            var accessor = variable.createVariableAccessor();
            functionCallInput.getAccessors().add(accessor);
            accessor.access(totalDelay).accept(functionCallInput);
        }

        for (int i = 0; i < arguments.length; i++) {
            FactorioSignal[] targetSignal = targetFunction.getSignature().getParameters()[i].getSignal();

            Symbol argument = arguments[i];
            if (argument instanceof Constant) {
                int[] vals = ((Constant) argument).getVal();
                Map<FactorioSignal, Integer> constants = new HashMap<>();
                for (int j = 0; j < vals.length; j++) {
                    constants.put(targetSignal[j], vals[j]);
                }
                ConstantCombinator combinator = new ConstantCombinator(constants);
                functionCallInput.getCombinators().add(combinator);
                combinator.setGreenOut(argumentsIn);
            } else {
                Variable var = (Variable) argument;
                for (int j = 0; j < var.getSignal().length; j++) {
                    if (var.getSignal()[j] == targetSignal[j]) {
                        var accessor = var.createVariableAccessor();
                        functionCallInput.getAccessors().add(accessor);
                        accessor.access(totalDelay).accept(argumentsIn, functionCallInput);
                    } else {
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

        for (var defined : currentFunctionContext.getVariableScope().getAllVariables().entrySet()) {
            var v = defined.getValue();
            var rebound = currentFunctionContext.createNamedVariable(defined.getKey(), v.getType(), v.getSignal(), functionCallReturn);
            rebound.setDelay(0);
            log("Rebinding " + v + " " + defined.getKey() + " as " + rebound);
        }
        currentFunctionContext.clearFunctionCallSlotReservations();

        var returnType = targetFunction.getSignature().getReturnType();

        if(returnType != PrimitiveType.VOID) {
            var functionReturnVal = currentFunctionContext.createBoundTempVariable(returnType, targetFunction.getSignature().getReturnSignals(), functionCallReturn);
            functionReturnVal.setDelay(0);
            log("Function return value: " + functionReturnVal);
        }

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
        if(ctx.tuple != null) {//Tuple access
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
        else if(ctx.tupleValues != null) {//Tuple creation
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
                    for (int val : vals) {
                        constants.put(remappedSignals[offset++], val);
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
        else if(ctx.left != null) {//Arithmetic
            EXPR_PARSER.parse(currentFunctionContext, ctx);
        }
        else if(ctx.numberLit != null) {//Literal int
            int val = Integer.parseInt(ctx.getText());
            currentFunctionContext.pushTempVariable(new Constant(val));
        }
        else if(ctx.var != null) {//Variable access
            var named = currentFunctionContext.getNamedVariable(ctx.var.getText());
            if(named == null) {
                var declaredArray = declaredArrays.get(ctx.var.getText());
                if(declaredArray == null) {
                    throw new RuntimeException("Variable " + ctx.var.getText() + " is not defined");
                }
                currentFunctionContext.pushTempVariable(new Constant(declaredArray.getType(), declaredArray.getAddress()));
            }
            else {
                currentFunctionContext.pushTempVariable(named);
            }
        }
        else if(ctx.array != null) {//Array access
            var index = currentFunctionContext.popTempVariable();
            if(index.getType() != PrimitiveType.INT) throw new RuntimeException("Invalid array index type " + index.getType() + ", expected INT");
            var array = currentFunctionContext.popTempVariable();
            if(!(array.getType() instanceof ArrayType)) throw new RuntimeException("Invalid array type " + array.getType());
            var arraySubtype = ((ArrayType)array.getType()).getSubType();


            CombinatorGroup arrayLoaderGroup = new CombinatorGroup(new NetworkGroup(), null);
            currentFunctionContext.getFunctionGroup().getSubGroups().add(arrayLoaderGroup);
            if(array instanceof Constant && index instanceof Constant) {
                int addressBegin = ((Constant) array).getVal()[0] * arraySubtype.getSize() + ((Constant) index).getVal()[0];
                for(int i = 0; i < array.getType().getSize(); i++) {
                    var address = new Constant(PrimitiveType.INT, addressBegin + i);
                    generateConstantTimeFunctionCall(arrayReadFunction, new Symbol[]{address});
                }
            }
            else {
                var arrayReadFunctionCallParamSignal = arrayReadFunction.getSignature().getParameters()[0].getSignal()[0];//Not strictly necessary to use this, but saves us a tick

                CombinatorGroup addressOffsetGroup = new CombinatorGroup(null, new NetworkGroup());
                arrayLoaderGroup.getSubGroups().add(addressOffsetGroup);

                var addressOffsetCalculator = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(index.getSignal()[0]), CombinatorIn.constant(arraySubtype.getSize()), arrayReadFunctionCallParamSignal, ArithmeticOperator.MUL);
                addressOffsetGroup.getCombinators().add(addressOffsetCalculator);
                addressOffsetCalculator.setGreenIn(arrayLoaderGroup.getInput());
                addressOffsetCalculator.setGreenOut(addressOffsetGroup.getOutput());

                var indexOffsetVar = currentFunctionContext.createBoundTempVariable(PrimitiveType.INT, new FactorioSignal[]{arrayReadFunctionCallParamSignal}, addressOffsetGroup);
                indexOffsetVar.setDelay(index.getTickDelay() + 1);
                currentFunctionContext.popTempVariable();//createBoundTempVariable puts it on the stack. We don't want that

                var accessor = ((Variable)index).createVariableAccessor();
                arrayLoaderGroup.getAccessors().add(accessor);
                accessor.access(index.getTickDelay()).accept(arrayLoaderGroup);

                for(int i = 0; i < arraySubtype.getSize(); i++) {
                    var indexOffsetCombinator = new ConstantCombinator(Map.of(index.getSignal()[0], i));
                    arrayLoaderGroup.getCombinators().add(indexOffsetCombinator);
                    Constant c = new Constant(PrimitiveType.INT, i + 1);
                    c.bind(arrayReadFunctionCallParamSignal);
                    generateConstantTimeFunctionCall(arrayReadFunction, new Symbol[]{indexOffsetVar}, c);
                }

                if(arraySubtype instanceof TupleType) {
                    Variable[] tupleComponents = new Variable[arraySubtype.getSize()];
                    FactorioSignal[] signals = currentFunctionContext.getFreeSymbols(tupleComponents.length);
                    int maxDelay = -1;

                    for(int i = tupleComponents.length - 1; i >= 0; i--) {
                        tupleComponents[i] = (Variable) currentFunctionContext.popTempVariable();
                        maxDelay = Math.max(tupleComponents[i].getTickDelay(), maxDelay);
                    }

                    int sharedChainDelay = tupleComponents[0].getTickDelay() + 1;
                    NetworkGroup current = new NetworkGroup();
                    arrayLoaderGroup.getNetworks().add(current);

                    for(int i = 0; i < tupleComponents.length; i++) {
                        var tupleComponent = tupleComponents[i];
                        var remapping = ArithmeticCombinator.remapping(tupleComponent.getSignal()[0], signals[i]);
                        arrayLoaderGroup.getCombinators().add(remapping);
                        remapping.setGreenIn(tupleComponent.getProducer().getOutput());
                        if(tupleComponent.getTickDelay() + 1 < sharedChainDelay) throw new RuntimeException("Unsorted tuple components");
                        while(sharedChainDelay < tupleComponent.getTickDelay() + 1) {
                            sharedChainDelay++;
                            var copy = ArithmeticCombinator.copying();
                            arrayLoaderGroup.getCombinators().add(copy);
                            copy.setGreenIn(current);
                            current = new NetworkGroup();
                            arrayLoaderGroup.getNetworks().add(current);
                            copy.setGreenOut(current);
                        }
                        remapping.setGreenOut(current);
                    }

                    var tupleOut = new CombinatorGroup(null, current);
                    arrayLoaderGroup.getSubGroups().add(tupleOut);
                    currentFunctionContext.createBoundTempVariable(arraySubtype, signals, tupleOut).setDelay(sharedChainDelay);
                }
            }
        }
    }

    @Override
    public void exitAssignment(LanguageParser.AssignmentContext ctx) {
        var value = currentFunctionContext.popTempVariable();
        String varName = ctx.var.getText();

        FactorioSignal[] variableSymbols;

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
                var connected = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(value.getSignal()[i]), CombinatorIn.constant(0), variableSymbols[i], ArithmeticOperator.ADD);
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

    @Override
    public void exitArrayAssignment(LanguageParser.ArrayAssignmentContext ctx) {
        var value = currentFunctionContext.popTempVariable();
        var index = currentFunctionContext.popTempVariable();
        if(index.getType() != PrimitiveType.INT) throw new RuntimeException("Invalid array index type " + index.getType() + ", expected INT");

        String varName = ctx.var.getText();
        Symbol array = currentFunctionContext.getNamedVariable(varName);
        if(array == null) {
            var declaredArray = declaredArrays.get(ctx.var.getText());
            if(declaredArray == null) {
                throw new RuntimeException("Variable " + ctx.var.getText() + " is not defined");
            }
            array = new Constant(declaredArray.getType(), declaredArray.getAddress());
        }

        if(!(array.getType() instanceof ArrayType)) throw new RuntimeException(varName + " is not an array. Expected " + new ArrayType(value.getType()) + ", found " + array.getType());
        var arraySubtype = ((ArrayType)array.getType()).getSubType();

        if(!arraySubtype.equals(value.getType())) throw new RuntimeException("Type mismatch " + value + " to array " + array);

        CombinatorGroup arrayWriterGroup = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        currentFunctionContext.getFunctionGroup().getSubGroups().add(arrayWriterGroup);

        if(array instanceof Constant && index instanceof Constant) {
            int addressBegin = ((Constant) array).getVal()[0] * arraySubtype.getSize() + ((Constant) index).getVal()[0];
            for(int i = 0; i < array.getType().getSize(); i++) {
                var address = new Constant(PrimitiveType.INT, addressBegin + i);
                generateConstantTimeFunctionCall(arrayWriteFunction, new Symbol[]{address, value});
            }
        }
        else {
            var arrayWriteFunctionCallParamSignal = arrayReadFunction.getSignature().getParameters()[0].getSignal()[0];//Not strictly necessary to use this, but saves us a tick

            CombinatorGroup addressOffsetGroup = new CombinatorGroup(null, new NetworkGroup());
            arrayWriterGroup.getSubGroups().add(addressOffsetGroup);

            var addressOffsetCalculator = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(index.getSignal()[0]), CombinatorIn.constant(arraySubtype.getSize()), arrayWriteFunctionCallParamSignal, ArithmeticOperator.MUL);
            addressOffsetGroup.getCombinators().add(addressOffsetCalculator);
            addressOffsetCalculator.setGreenIn(arrayWriterGroup.getInput());
            addressOffsetCalculator.setGreenOut(addressOffsetGroup.getOutput());

            var indexOffsetVar = currentFunctionContext.createBoundTempVariable(PrimitiveType.INT, new FactorioSignal[]{arrayWriteFunctionCallParamSignal}, addressOffsetGroup);
            indexOffsetVar.setDelay(index.getTickDelay() + 1);
            currentFunctionContext.popTempVariable();//createBoundTempVariable puts it on the stack. We don't want that

            var accessor = ((Variable)index).createVariableAccessor();
            arrayWriterGroup.getAccessors().add(accessor);
            accessor.access(index.getTickDelay()).accept(arrayWriterGroup);

            for(int i = 0; i < arraySubtype.getSize(); i++) {
                //This is only a virtual variable we create that represents one int of the larger value.
                Symbol subValue;
                if(value instanceof Constant) {
                    subValue = new Constant(PrimitiveType.INT, ((Constant) value).getVal()[i]);
                    subValue.bind(value.getSignal()[i]);
                }
                else {
                    subValue = currentFunctionContext.createBoundTempVariable(PrimitiveType.INT, new FactorioSignal[]{value.getSignal()[i]}, ((Variable)value).getProducer());
                    currentFunctionContext.popTempVariable();
                    ((Variable)subValue).setDelay(value.getTickDelay());
                }
                var indexOffsetCombinator = new ConstantCombinator(Map.of(index.getSignal()[0], i));
                arrayWriterGroup.getCombinators().add(indexOffsetCombinator);
                Constant c = new Constant(PrimitiveType.INT, i + 1);
                c.bind(arrayWriteFunctionCallParamSignal);
                generateConstantTimeFunctionCall(arrayWriteFunction, new Symbol[]{indexOffsetVar, subValue}, c);
            }
        }
    }

    private void generateConstantTimeFunctionCall(FunctionContext targetFunction, Constant... redConstants) {
        Symbol[] arguments = new Symbol[targetFunction.getSignature().getParameters().length];
        for(int i = arguments.length - 1; i >= 0; i--) {
            var tmpVar = currentFunctionContext.popTempVariable();
            arguments[i] = tmpVar;
        }
        generateConstantTimeFunctionCall(targetFunction, arguments, redConstants);
    }

    private void generateConstantTimeFunctionCall(FunctionContext targetFunction, Symbol[] arguments, Constant... redConstants) {
        int argumentDelay = 0;
        for(int i = 0; i < arguments.length; i++) {
            int delay = arguments[i].getTickDelay();
            log("Param " + Arrays.toString(targetFunction.getSignature().getParameters()[i].getSignal()) + " as " + arguments[i]);
            if(!Arrays.equals(targetFunction.getSignature().getParameters()[i].getSignal(), arguments[i].getSignal())) {
                delay++; //Mapping to new signal type can be skipped if they are identical
            }
            argumentDelay = Math.max(argumentDelay, delay);
        }

        var controlFlowVar = currentFunctionContext.getControlFlowVariable();

        int functionCallTime;
        if(targetFunction.getSignature().getReturnType() == PrimitiveType.VOID) {
            functionCallTime = currentFunctionContext.reserveVoidFunctionCallSlot(Math.max(argumentDelay, controlFlowVar.getTickDelay()) + 1);
            log("Reserved slot " + functionCallTime);
        }
        else {
            functionCallTime = currentFunctionContext.reserveFunctionCallSlot(Math.max(argumentDelay, controlFlowVar.getTickDelay()) + 1, targetFunction.getSignature().getConstantDelay() + CONSTANT_DELAY_FUNCTION_OVERHEAD);
            log("Reserved slot " + functionCallTime + ", " + (functionCallTime + targetFunction.getSignature().getConstantDelay() + CONSTANT_DELAY_FUNCTION_OVERHEAD));
        }

        CombinatorGroup functionCallGroup = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        currentFunctionContext.getFunctionGroup().getSubGroups().add(functionCallGroup);

        var functionOutGate = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.CONTROL_FLOW_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
        var functionIdConstant = new ConstantCombinator(Map.of(Constants.FUNCTION_IDENTIFIER, targetFunction.getSignature().getFunctionId()));
        functionCallGroup.getCombinators().add(functionOutGate);
        functionCallGroup.getCombinators().add(functionIdConstant);

        NetworkGroup tmp = new NetworkGroup();

        functionCallGroup.getNetworks().add(tmp);
        functionIdConstant.setRedOut(tmp);
        functionOutGate.setRedIn(tmp);

        if(redConstants.length > 0) {
            Map<FactorioSignal, Integer> constantData = new HashMap<>();
            for(Constant c : redConstants) {
                for(int i = 0; i < c.getType().getSize(); i++) {
                    int index = i;
                    constantData.compute(c.getSignal()[i], (k, v) -> {
                        if(v == null) {
                            v = 0;
                        }
                        v += c.getVal()[index];
                        return v;
                    });
                }
            }
            var otherConstants = new ConstantCombinator(constantData);
            functionCallGroup.getCombinators().add(otherConstants);
            otherConstants.setRedOut(tmp);
        }

        functionOutGate.setGreenIn(functionCallGroup.getInput());

        functionOutGate.setGreenOut(currentFunctionContext.getFunctionCallOutputGroup());

        var cfAccessor = controlFlowVar.createVariableAccessor();
        currentFunctionContext.getFunctionGroup().getAccessors().add(cfAccessor);
        cfAccessor.access(functionCallTime - 1).accept(functionCallGroup);

        for (int i = 0; i < arguments.length; i++) {
            FactorioSignal[] targetSignal = targetFunction.getSignature().getParameters()[i].getSignal();

            Symbol argument = arguments[i];
            if (argument instanceof Constant) {
                int[] vals = ((Constant) argument).getVal();
                Map<FactorioSignal, Integer> constants = new HashMap<>();
                for (int j = 0; j < vals.length; j++) {
                    constants.put(targetSignal[j], vals[j]);
                }
                ConstantCombinator combinator = new ConstantCombinator(constants);
                functionCallGroup.getCombinators().add(combinator);
                combinator.setGreenOut(functionCallGroup.getInput());
            } else {
                Variable var = (Variable) argument;
                for (int j = 0; j < var.getSignal().length; j++) {
                    if (var.getSignal()[j] == targetSignal[j]) {
                        var accessor = var.createVariableAccessor();
                        functionCallGroup.getAccessors().add(accessor);
                        accessor.access(functionCallTime - 1).accept(functionCallGroup);
                    } else {
                        log("Remapping combinator for " + var.getSignal()[j] + " -> " + targetSignal[j]);
                        NetworkGroup paramRemapIn = new NetworkGroup();
                        functionCallGroup.getNetworks().add(paramRemapIn);
                        var accessor = var.createVariableAccessor();
                        functionCallGroup.getAccessors().add(accessor);
                        accessor.access(functionCallTime - 2).accept(paramRemapIn, functionCallGroup);
                        ArithmeticCombinator arithmeticCombinator = ArithmeticCombinator.remapping(argument.getSignal()[j], targetSignal[j]);
                        functionCallGroup.getCombinators().add(arithmeticCombinator);
                        arithmeticCombinator.setGreenIn(paramRemapIn);
                        arithmeticCombinator.setGreenOut(functionCallGroup.getInput());
                    }
                }
            }
        }

        var returnType = targetFunction.getSignature().getReturnType();
        if(returnType != PrimitiveType.VOID) {
            var returnSignalColorMapper = ArithmeticCombinator.copying();
            functionCallGroup.getCombinators().add(returnSignalColorMapper);
            returnSignalColorMapper.setRedIn(currentFunctionContext.getFunctionCallReturnGroup());
            returnSignalColorMapper.setGreenOut(functionCallGroup.getOutput());

            currentFunctionContext.createBoundTempVariable(returnType, targetFunction.getSignature().getReturnSignals(), functionCallGroup).setDelay(functionCallTime + targetFunction.getSignature().getConstantDelay() + CONSTANT_DELAY_FUNCTION_OVERHEAD);
        }
    }

    private void castTopOfStack(Type newType) {
        var symbol = currentFunctionContext.popTempVariable();
        if(symbol.getType().getSize() != newType.getSize()) throw new IllegalArgumentException("Cannot cast between types of different size");
        if(symbol instanceof Constant) {
            currentFunctionContext.pushTempVariable(new Constant(newType, ((Constant) symbol).getVal()));
        }
        else if(symbol instanceof Variable) {
            currentFunctionContext.createBoundTempVariable(newType, symbol.getSignal(), ((Variable) symbol).getProducer());
        }
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

        var functionMap = new HashMap<>(structureParser.getFunctions());

        var generator = new Generator(functionMap, structureParser.getDeclaredArrays());

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

            generatedGroups.forEach(g -> g.getAccessors().forEach(VariableAccessor::generateAccessors));

            var combinators = generatedGroups.stream()
                    .map(CombinatorGroup::getCombinators)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            var networks = generatedGroups.stream()
                    .map(CombinatorGroup::getNetworks)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            entityBlocks.add(FunctionPlacer.placeFunction(combinators, networks, function.getFunctionCallOutputGroup(), function.getFunctionCallReturnGroup()));
        }

        if(!structureParser.getDeclaredArrays().isEmpty()) {
            int arraySize = structureParser.getDeclaredArrays().values()
                    .stream()
                    .mapToInt(dc -> dc.getSize() * ((ArrayType)dc.getType()).getSubType().getSize())
                    .sum();
            System.out.println("Generating memory controller with " + arraySize * 4 + "B capacity");
            entityBlocks.add(MemoryUtil.generateMemoryController(arraySize, new NetworkGroup(), new NetworkGroup()));
        }

        int currentX = 0;
        for(var block : entityBlocks) {
            block.applyOffset(currentX - block.getMinX(), -block.getMinY());//Align top left corner to (currentX, 0)
            currentX += block.getMaxX() - block.getMinX() + 2;
        }
        var poles = FunctionPlacer.generateFunctionConnectors(entityBlocks);
        entityBlocks.add(poles);
        return BlueprintWriter.writeBlueprint(entityBlocks);
    }

    public static void main(String[] args) {
        System.out.println(generateBlueprint(TEST));
    }
}
