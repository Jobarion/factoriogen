package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

public class Generator extends LanguageBaseListener {

    private static final String SIMPLE_FUNCTION_ADD =
            "function add(a: int<red>, b: int<green>) -> int {\n" +
                    "return(a + b);\n" +
            "}";

    private static final String FUNCTION_CALL_SIMPLE =
            "function mul(a: int<red>, b: int<green>) -> int {\n" +
                    "if(a < 0) {a = 0 - a;}\n" +
                    "sum = 0;\n" +
                    "while(a > 0) { sum = add(sum, b); }\n" +
                    "return(sum);\n" +
            "}\n" +
            "function add(a: int<red>, b: int) -> int {\n" +
                    "return(a + b);\n" +
            "}";

    private static final String COLLATZ =
            "function collatz(currentVal=red, iterations=green) {\n" +
                    "  while(iterations != 0) {\n" +
                    "    iterations = iterations - 1;\n" +
                    "    if(currentVal % 2 == 0) {\n" +
                    "      currentVal = currentVal / 2;\n" +
                    "    }\n" +
                    "    else {\n" +
                    "      currentVal = currentVal * 3 + 1;\n" +
                    "    }\n" +
                    "  }\n" +
                    "  return(currentVal);\n" +
                    "}";

    private static final String WHILE_OUTSIDE_VAR =
            "function simple(a=red) {\n" +
                    "  b = a * a;\n" +
                    "  while(a > 10) {\n" +
                    "    a = a / 2;\n" +
                    "  }\n" +
                    "  return(a, b);\n" +
                    "}";

    private static final String FUNCTION_NESTED_WHILE =
            "function nested(a=red) {\n" +
            "  while(a > 8) {\n" +
            "    while(a > 10) {\n" +
            "      a = a / 2;\n" +
            "    }\n" +
            "    a = a - 1;\n" +
            "  }\n" +
            "  return(a);\n" +
            "}";

    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
    private static final String PYTH_TRIPLE =
            "function nested(max: int) -> int {\n" +
            "  a = 0;\n" +
            "  b = 0;\n" +
            "  c = 0;\n" +
            "  if(max >= 5) {\n" +
            "  terminate = 0;\n" +
            "    while(terminate == 0 && a <= max) {\n" +
            "      b = a;\n" +
            "      a = a + 1;\n" +
            "      while(terminate == 0 && b <= max) {\n" +
            "        c = b;\n" +
            "        b = b + 1;\n" +
            "        while(terminate == 0 && c <= max) {\n" +
            "          c = c + 1;\n" +
            "          if(a * a + b * b == c * c) {\n" +
            "            terminate = 1;\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  return(a, b, c);\n" +
            "}";

    private static final String TEST = FUNCTION_CALL_SIMPLE;

    private FunctionContext currentFunctionContext;
    private final Map<String, FunctionContext> definedFunctions;

    public Generator(Map<String, FunctionContext> definedFunctions) {
        this.definedFunctions = definedFunctions;
    }

    //TODO: This must only be called during _function_ definitions
    @Override
    public void exitFunctionHeader(LanguageParser.FunctionHeaderContext ctx) {
        NetworkGroup in = new NetworkGroup();
        NetworkGroup out = new NetworkGroup();
        CombinatorGroup functionHeader = new CombinatorGroup(in, out);
        var inputCombinator = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        inputCombinator.setName("Input function");
        functionHeader.getCombinators().add(inputCombinator);
        inputCombinator.setGreenIn(in);
        inputCombinator.setGreenOut(out);
        currentFunctionContext = definedFunctions.get(ctx.functionName().getText());
        currentFunctionContext.setFunctionHeader(functionHeader);
        currentFunctionContext.overwriteControlFlowVariable(functionHeader).setDelay(0);
        log("Defining function " + currentFunctionContext.getSignature());
    }

    @Override
    public void exitReturnStatement(LanguageParser.ReturnStatementContext ctx) {
        var returnGroup = currentFunctionContext.getFunctionReturnGroup();

        NetworkGroup gateInput = new NetworkGroup();
        returnGroup.getNetworks().add(gateInput);

        var outputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        returnGroup.getCombinators().add(outputGate);
        outputGate.setGreenOut(returnGroup.getOutput());
        outputGate.setGreenIn(gateInput);
        outputGate.setName("Output " + ctx.getText());

        int maxDelay = currentFunctionContext.getControlFlowVariable().getTickDelay();
        List<Symbol> valuesToReturn = new ArrayList<>();
        for(int i = 0; i < ctx.returnValues().expr().size(); i++) {
            var returnVal = currentFunctionContext.popTempVariable();
            valuesToReturn.add(returnVal);
            maxDelay = Math.max(maxDelay, returnVal.getTickDelay());
        }
        valuesToReturn.add(currentFunctionContext.getControlFlowVariable());
        for(var returnVal : valuesToReturn) {
            if(!returnVal.isBound()) {
                returnVal.bind(currentFunctionContext.getFreeSymbol());
            }
            log("Returning " + returnVal + " with delay " + returnVal.getTickDelay());
            if(returnVal instanceof Constant) {
                if(((Constant) returnVal).getVal() == 0) continue;
                ConnectedCombinator constant = new ConnectedCombinator(Combinator.constant(Map.of(returnVal.getSignal(), ((Constant) returnVal).getVal())));
                constant.setGreenOut(gateInput);
                returnGroup.getCombinators().add(constant);
            }
            else {
                var accessor = ((Variable)returnVal).createVariableAccessor();
                accessor.access(maxDelay + 1).accept(gateInput, returnGroup);//Ensure our output is clean
                returnGroup.getAccessors().add(accessor);
            }
        }
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

        ConnectedCombinator inputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(inputGate);

        inputGate.setGreenIn(whileGroup.getInput());

        ConnectedCombinator dedupInput = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ));
        whileGroup.getCombinators().add(dedupInput);

        ConnectedCombinator dedupStore = new ConnectedCombinator(DeciderCombinator.withAny(Accessor.constant(0), Writer.one(Constants.TEMP_SIGNAL), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(dedupStore);

        ConnectedCombinator dedupReset = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.fromInput(Constants.TEMP_SIGNAL), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(dedupReset);

        ConnectedCombinator dedupConstants = new ConnectedCombinator(Combinator.constant(Map.of(Constants.TEMP_SIGNAL, -1)));
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

        ConnectedCombinator loopFeedbackGateInitial = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(loopFeedbackGateInitial);

        ConnectedCombinator loopFeedbackGateSubsequent = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
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

        NetworkGroup conditionSignal = new NetworkGroup("loop condition");
        whileGroup.getNetworks().add(conditionSignal);

        var loopFeedback = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ));
        var loopExit = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ));
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
            ConnectedCombinator connected = new ConnectedCombinator(Combinator.constant(Map.of(condition.getSignal(), ((Constant) condition).getVal())));
            connected.setRedOut(conditionSignal);
            whileGroup.getCombinators().add(connected);
        }
        else {
            NetworkGroup inner = new NetworkGroup("condition inner");
            whileGroup.getNetworks().add(inner);
            ConnectedCombinator connected = new ConnectedCombinator(ArithmeticCombinator.copying(condition.getSignal()));
            connected.setRedOut(conditionSignal);
            connected.setGreenIn(inner);
            whileGroup.getCombinators().add(connected);
            var accessor = ((Variable)condition).createVariableAccessor();
            whileGroup.getAccessors().add(accessor);
            accessor.access(condition.getTickDelay()).accept(inner, whileGroup);
        }

        var bufferDelayInput = loopDataPreCondition;
        ConnectedCombinator bufferDelayConnected = null;
        for(int i = condition.getTickDelay(); i >= 0; i--) {
            var bufferDelayCmb = ArithmeticCombinator.withEach(Accessor.constant(0), ArithmeticCombinator.ADD);
            bufferDelayConnected = new ConnectedCombinator(bufferDelayCmb);
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

        var conditionContext = currentFunctionContext.leaveConditional();

        CombinatorGroup conditionGroup = new CombinatorGroup(new NetworkGroup("condition input"), null);
        currentFunctionContext.getFunctionGroup().getSubGroups().add(conditionGroup);

        conditionGroup.getSubGroups().add(conditionContext.getIfProvider());
        conditionGroup.getSubGroups().add(conditionContext.getElseProvider());

        var ifInput = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ));
        var elseInput = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ));
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

        ConnectedCombinator ifOutGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        ConnectedCombinator elseOutGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
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

        ConnectedCombinator connected;
        if(condition instanceof Constant) {
            connected = new ConnectedCombinator(Combinator.constant(Map.of(condition.getSignal(), ((Constant) condition).getVal())));
            connected.setRedOut(conditionWire);
            conditionGroup.getCombinators().add(connected);
        }
        else {
            connected = new ConnectedCombinator(ArithmeticCombinator.copying(condition.getSignal()));
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
        Symbol[] arguments = new Symbol[targetFunction.getSignature().getParameters().length];
        int argumentDelay = 0;
        for(int i = arguments.length - 1; i >= 0; i--) {
            var tmpVar = currentFunctionContext.popTempVariable();
            arguments[i] = tmpVar;
            int delay = tmpVar.getTickDelay();
            if(targetFunction.getSignature().getParameters()[i].getSignal() != tmpVar.getSignal()) {
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

        for(var variable : currentFunctionContext.getVariableScope().getAllVariables().values()) {
            var accessor = variable.createVariableAccessor();
            functionCallInput.getAccessors().add(accessor);
            accessor.access(totalDelay).accept(functionCallInput);
        }

        CombinatorGroup functionCallReturn = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        functionCallInput.getSubGroups().add(functionCallReturn);

        FunctionCall functionCall = new FunctionCall(targetFunction, functionCallInput.getOutput(), functionCallReturn);
        currentFunctionContext.registerFunctionCall(functionCall);

        //Deduplication of outside signals
        ConnectedCombinator inputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        functionCallInput.getCombinators().add(inputGate);

        inputGate.setGreenIn(functionCallInput.getInput());

        ConnectedCombinator dedupInput = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ));
        functionCallInput.getCombinators().add(dedupInput);

        ConnectedCombinator dedupStore = new ConnectedCombinator(DeciderCombinator.withAny(Accessor.constant(0), Writer.one(Constants.TEMP_SIGNAL), DeciderCombinator.NEQ));
        functionCallInput.getCombinators().add(dedupStore);

        ConnectedCombinator dedupReset = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.fromInput(Constants.TEMP_SIGNAL), DeciderCombinator.NEQ));
        functionCallInput.getCombinators().add(dedupReset);

        ConnectedCombinator dedupConstants = new ConnectedCombinator(Combinator.constant(Map.of(Constants.TEMP_SIGNAL, -1)));
        functionCallInput.getCombinators().add(dedupConstants);

        NetworkGroup tmp = new NetworkGroup("dedup network");
        functionCallInput.getNetworks().add(tmp);
        inputGate.setGreenOut(tmp);
        dedupInput.setGreenIn(tmp);
        dedupStore.setGreenIn(tmp);
        dedupStore.setGreenOut(tmp);
        dedupReset.setGreenOut(tmp);

        tmp = new NetworkGroup("dedup reset constant");
        functionCallInput.getNetworks().add(tmp);
        dedupConstants.setGreenOut(tmp);
        dedupReset.setGreenIn(tmp);

        //Forward signals, replacing control flow with predetermined value
        ConnectedCombinator c1 = new ConnectedCombinator(ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticCombinator.MUL));
        ConnectedCombinator c2 = new ConnectedCombinator(ArithmeticCombinator.copying());
        ConnectedCombinator c3 = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.one(Constants.CONTROL_FLOW_SIGNAL), DeciderCombinator.NEQ));
        functionCallInput.getCombinators().add(c1);
        functionCallInput.getCombinators().add(c2);
        functionCallInput.getCombinators().add(c3);

        NetworkGroup forwardIn = new NetworkGroup();
        functionCallInput.getNetworks().add(forwardIn);

        c1.setRedIn(forwardIn);
        c2.setRedIn(forwardIn);
        c3.setRedIn(forwardIn);
        dedupInput.setRedOut(forwardIn);

        ConnectedCombinator c4 = new ConnectedCombinator(ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticCombinator.MUL));
        ConnectedCombinator c5 = new ConnectedCombinator(ArithmeticCombinator.copying());
        //Accessor.constant(function call id)
        int functionCallId = functionCall.getFunctionCallId();
        ConnectedCombinator c6 = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(functionCallId), Writer.one(Constants.CONTROL_FLOW_SIGNAL), DeciderCombinator.NEQ));
        ConnectedCombinator c7 = new ConnectedCombinator(Combinator.constant(Map.of(Constants.FUNCTION_IDENTIFIER, targetFunction.getSignature().getFunctionId())));

        functionCallInput.getCombinators().add(c4);
        functionCallInput.getCombinators().add(c5);
        functionCallInput.getCombinators().add(c6);
        functionCallInput.getCombinators().add(c7);

        NetworkGroup forwardInternal = new NetworkGroup();
        functionCallInput.getNetworks().add(forwardInternal);

        c1.setRedOut(forwardInternal);
        c2.setRedOut(forwardInternal);
        c3.setRedOut(forwardInternal);

        c4.setRedIn(forwardInternal);
        c5.setRedIn(forwardInternal);
        c6.setRedIn(forwardInternal);
        c7.setRedOut(forwardInternal);

        c4.setGreenOut(functionCallInput.getOutput());
        c5.setGreenOut(functionCallInput.getOutput());
        c6.setGreenOut(functionCallInput.getOutput());

        //Store previous state, forward when function call was completed

        NetworkGroup stateStoreOut = new NetworkGroup();
        functionCallInput.getNetworks().add(stateStoreOut);

        ConnectedCombinator preCallStateStore = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ));
        functionCallInput.getCombinators().add(preCallStateStore);

        preCallStateStore.setRedIn(forwardIn);
        dedupReset.setRedOut(forwardIn);
        preCallStateStore.setGreenIn(stateStoreOut);
        preCallStateStore.setGreenOut(stateStoreOut);

        ConnectedCombinator preCallStateOutputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.TEMP_SIGNAL), Accessor.constant(-1), Writer.everything(false), DeciderCombinator.EQ));
        functionCallInput.getCombinators().add(preCallStateOutputGate);

        preCallStateOutputGate.setGreenIn(stateStoreOut);
        preCallStateOutputGate.setRedIn(forwardIn);

        //Function call data return
        ConnectedCombinator returnGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(functionCallId), Writer.everything(false), DeciderCombinator.NEQ));
        functionCallReturn.getCombinators().add(returnGate);
        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);

        dedupReset.setRedIn(tmp);
        returnGate.setRedOut(tmp);

        ConnectedCombinator filter1 = new ConnectedCombinator(ArithmeticCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(-1), Constants.CONTROL_FLOW_SIGNAL, ArithmeticCombinator.MUL));
        ConnectedCombinator filter2 = new ConnectedCombinator(ArithmeticCombinator.copying());
        functionCallReturn.getCombinators().add(filter1);
        functionCallReturn.getCombinators().add(filter2);

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        filter1.setGreenIn(tmp);
        filter2.setGreenIn(tmp);
        returnGate.setGreenOut(tmp);

        ConnectedCombinator filter3 = new ConnectedCombinator(ArithmeticCombinator.copying());
        functionCallReturn.getCombinators().add(filter3);

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        filter1.setGreenOut(tmp);
        filter2.setGreenOut(tmp);
        filter3.setGreenIn(tmp);

        ConnectedCombinator outputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(Constants.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        functionCallReturn.getCombinators().add(outputGate);

        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        outputGate.setGreenIn(tmp);
        preCallStateOutputGate.setGreenOut(tmp);
        filter3.setGreenOut(tmp);

        ConnectedCombinator c8 = new ConnectedCombinator(Combinator.constant(Map.of(Constants.TEMP_SIGNAL, 1)));
        functionCallReturn.getCombinators().add(c8);
        tmp = new NetworkGroup();
        functionCallReturn.getNetworks().add(tmp);
        c8.setRedOut(tmp);
        outputGate.setRedIn(tmp);
        outputGate.setGreenOut(functionCallReturn.getOutput());

        for(var defined : currentFunctionContext.getVariableScope().getAllVariables().entrySet()) {
            var v = defined.getValue();
            var rebound = currentFunctionContext.createNamedVariable(defined.getKey(), v.getType(), v.getSignal(), functionCallReturn);
            rebound.setDelay(0);
        }

        //Bind function return value
        //The currentFunctionContext.getFreeSymbol() is wrong
        var functionReturnVal = currentFunctionContext.createBoundTempVariable(targetFunction.getSignature().getReturnType(), currentFunctionContext.getFreeSymbol(), functionCallReturn);
        functionReturnVal.setDelay(0);

        //Example of "functions"
        //0eNrtWllu2zAQvQs/WyUQqc0S0I+kvUURGIpMx0RtyaCooEagA/QWPVtPUtJKbNlayJGXOHV/AjiSRuS8eW8W8QU9zgu65CwVKHpBLMnSHEXfX1DOntJ4rv4nVkuKIsQEXSALpfFC/Yo5E7MFFSy5SbLFI0tjkXFUWoilE/oTRbi0tDYmNGETytsNkPLBQjQVTDBarWj9YzVOi8Uj5fINmrVYaJnl8uksVQtQFn0LrVB0E/i3nnzPhHGaVJeJheS+Bc/m40c6i5+ZfFw+s7U7lpcna1u5ujBlPBfjxu6eGReF/M9mYdUdN3dqWzlVNswfulcPZUvK42qN6JO8JSvEsgC8+Ssqy3K9ubTa63r5WP154pSmdb+yCYoceS/jScHE+icuH+TTxPR2CZl6XQMpMhgp98xIJTOa/KihpdggYkUNexeMzwPAeDV+VkCsncte01obXs5gvLwPyawhYH4DAon3Xb+LDOm/7DaJ1hcGpD8MsBlt3T6hbsaA5Ooba8t21F/NmEFumG62DFXBnS2WMV9fitCXAajSZ8pXYsbSp8r2ciUXW6RiPOXZYsxSaQxFghcUhj2Qtl289MC8DHYwqfHSPS0vaZzMTiWjlW0QAKSTPpxOWtFqc78P48No43vvGHzQpqwdJ+8w4c+v3+/BhRbfBqqoa4VopJGs0Eyzgs0u3tzRD1K4AYncdsE0ZXNBeUdV3OU9lk6zym+FggTXCuMHc3/5ZsIwAgvDNjid8qqkAANTqSECITDu8Mb/+KRxd2MSeO2Oss12ru6DyCJ53be9Xyr6H1Ekt+4+hjziZqLqkMuwP4X5pnb2dbVRlxq2DHjbjeeLeC6pOZf75VKGltmctoSBtyGA/UYAEw95mjTRqKO7Nu727xsHZvkGE1j0bwsybFSQnSr61zL47tVyV/XVE9m2KcDYNYxceLcb7oB4xclT19e0JdtWDNwDCphTY9A9E1LJ9dAJ3ZChEHY04kVgNQ7WjI1GhkyC96fOG4p+eH0ojjSpFwNRHBmSDdjHEi1GR0lScVpPJWet0qbxPAflKL+fLwQ2b/A1bLYNcQ3A9PMugH5K+y+Dfhp6NXAx5Ru8O3fPhsulFRgerCzXgqbrF0zL/HDg7O+0mrnVtXoyu8i6nnjGLal9kgkNsYEjGnuDoX/cEc1dbT7jaD6YG7R0aztu7bs5xM59qw1HgcPysXJGlRwhw3ZTQPAB5WJwdeUi0XzUI+5h0og19jo/7pOh5WRwXmm0L1UZdcD6kGqSaL4kN6vNLh0m2JDHDiwAvPMEwMlGvjXD8G6i3dMufAjb+/nflLruAZVp8M9Wpm2sCgA02QMPeISn2fb32+sE1xs4mf6vy9CKlTjwDr5HwXU9UGioy4PnPFd6XqEdWxuYbjvAgB5M2AqtO6z7MD4/VbwenhpyPoG0fmRRzYN6e1Q7W2whCU9enQkcYTcISeD4rms7pCz/AvsiUOs=
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

    /*
        Get the expression components from the stack, and either calculate the result (if constant) or produce
        combinators to calculate the result. Put result back on stack.
     */
    @Override
    public void exitExpr(LanguageParser.ExprContext ctx) {
        if(ctx.left != null) {
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

    /*
                Assignments either create a new named variable, or override an existing one with a new provider group (and delay)
                Provider group = a group of combinators with a defined output NetworkGroup that produce a certain variable.
                Anyone who wants that variable can get it out of the provider group.
             */
    @Override
    public void exitAssignment(LanguageParser.AssignmentContext ctx) {
        var value = currentFunctionContext.popTempVariable();
        String varName = ctx.var.getText();

        FactorioSignal variableSymbol;
        //We don't care about new variables inside our if block, they go out of scope anyway
        if(currentFunctionContext.getNamedVariable(varName) != null) {
            var existing = currentFunctionContext.getNamedVariable(varName);
            variableSymbol = existing.getSignal();
        }
        else {
            variableSymbol = currentFunctionContext.getFreeSymbol();
        }

        CombinatorGroup group = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        currentFunctionContext.getFunctionGroup().getSubGroups().add(group);

        ConnectedCombinator connected;
        if(value instanceof Constant) {
            Combinator cmb = Combinator.constant(Map.of(variableSymbol, ((Constant) value).getVal()));
            connected = new ConnectedCombinator(cmb);
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
        }
        else {
            //TODO remove this and use the value directly (is this a good idea?)
            Combinator cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(value.getSignal()), Accessor.constant(0), variableSymbol, ArithmeticCombinator.ADD);
            connected = new ConnectedCombinator(cmb);
            connected.setGreenOut(group.getOutput());
            connected.setGreenIn(group.getInput());
            group.getCombinators().add(connected);
        }
        var named= currentFunctionContext.createNamedVariable(varName, value.getType(), variableSymbol, group);
        named.setDelay(value.getTickDelay() + 1); //Aliasing
        if(value instanceof Variable) {
            var accessor = ((Variable) value).createVariableAccessor();
            group.getAccessors().add(accessor);
            accessor.access(value.getTickDelay()).accept(group);
        }
        log("Creating named " + varName + " = " + named + ", with delay " + named.getTickDelay());
    }

    private final Combiner<LanguageParser.ExprContext, ArithmeticOperation> EXPR_PARSER = new Combiner<>(2, PrimitiveType.INT, PrimitiveType.INT) {

        @Override
        public ArithmeticOperation getOperation(LanguageParser.ExprContext ruleContext) {
            return ArithmeticCombinator.getOperation(ruleContext.op.getText());
        }

        @Override
        public Optional<Constant> computeConstExpr(Constant[] constants, ArithmeticOperation operation) {
            int result = operation.applyAsInt(constants[0].getVal(), constants[1].getVal());
            return Optional.of(new Constant(result, PrimitiveType.INT));
        }

        @Override
        public int generateCombinators(Symbol[] symbols, ArithmeticOperation operation, FactorioSignal outSymbol, CombinatorGroup group) {
            var cmb = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor(currentFunctionContext),  symbols[1].toAccessor(currentFunctionContext), outSymbol, operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, DeciderOperation> BOOL_EXPR_COMPONENT_PARSER = new Combiner<>(2, PrimitiveType.INT, PrimitiveType.BOOLEAN) {

        @Override
        public DeciderOperation getOperation(LanguageParser.BoolExprContext ruleContext) {
            return DeciderCombinator.getOperation(ruleContext.op.getText());
        }

        @Override
        public Optional<Constant> computeConstExpr(Constant[] constants, DeciderOperation operation) {
            boolean result = operation.test(constants[0].getVal(), constants[1].getVal());
            return Optional.of(new Constant(result ? 1 : 0, PrimitiveType.BOOLEAN));
        }

        @Override
        public int generateCombinators(Symbol[] symbols, DeciderOperation operation, FactorioSignal outSymbol, CombinatorGroup group) {
            if(symbols[0] instanceof Constant) {
                var tmp = symbols[1];
                symbols[1] = symbols[0];
                symbols[0] = tmp;
                operation = DeciderCombinator.getInvertedOperation(operation);
            }
            var cmb = DeciderCombinator.withLeftRight(symbols[0].toAccessor(currentFunctionContext),  symbols[1].toAccessor(currentFunctionContext), Writer.one(outSymbol), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, ArithmeticOperation> BOOL_EXPR_PARSER = new Combiner<>(2, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN) {

        @Override
        public ArithmeticOperation getOperation(LanguageParser.BoolExprContext ruleContext) {
            return switch(ruleContext.op.getText()) {
                case "&&" -> ArithmeticCombinator.AND;
                case "||" -> ArithmeticCombinator.OR;
                case "^" -> ArithmeticCombinator.XOR;
                default -> throw new UnsupportedOperationException("Unknown operation '" + ruleContext.op.getText() + "'");
            };
        }

        @Override
        public Optional<Constant> computeConstExpr(Constant[] constants, ArithmeticOperation operation) {
            int result = operation.applyAsInt(constants[0].getVal(), constants[1].getVal());
            return Optional.of(new Constant(result, PrimitiveType.BOOLEAN));
        }

        @Override
        public int generateCombinators(Symbol[] symbols, ArithmeticOperation operation, FactorioSignal outSymbol, CombinatorGroup group) {
            var cmb = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor(currentFunctionContext),  symbols[1].toAccessor(currentFunctionContext), outSymbol, operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, Void> NOT_EXPR_PARSER = new Combiner<>(1, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN) {

        @Override
        public Void getOperation(LanguageParser.BoolExprContext ruleContext) {
            return null;
        }

        @Override
        public Optional<Constant> computeConstExpr(Constant[] constants, Void operation) {
            return Optional.of(new Constant(constants[0].getVal() == 0 ? 1 : 0, PrimitiveType.BOOLEAN));
        }

        @Override
        public int generateCombinators(Symbol[] symbols, Void operation, FactorioSignal outSymbol, CombinatorGroup group) {
            var cmb = DeciderCombinator.withLeftRight(symbols[0].toAccessor(currentFunctionContext), Accessor.constant(0), Writer.one(outSymbol), DeciderCombinator.EQ);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private void log(String msg) {
        System.out.println("\t".repeat(currentFunctionContext.getDepth()) + msg);
    }

    public static void main(String[] args) {
        LanguageParser parser = new LanguageParser(new CommonTokenStream(new LanguageLexer(CharStreams.fromString(TEST))));

        var structureParser = new StructureParser();
        parser.addParseListener(structureParser);

        parser.file();

        parser = new LanguageParser(new CommonTokenStream(new LanguageLexer(CharStreams.fromString(TEST))));

        var generator = new Generator(structureParser.getFunctions());

        parser.addParseListener(generator);
        parser.file();

        Set<CombinatorGroup> generatedGroups = new HashSet<>();
        Queue<CombinatorGroup> toExpand = new LinkedList<>();
        toExpand.add(generator.currentFunctionContext.getFunctionGroup());
        while(!toExpand.isEmpty()) {
            var group = toExpand.poll();
            generatedGroups.add(group);
            toExpand.addAll(group.getSubGroups());
        }

//        generator.accessors.forEach(VariableAccessor::generateAccessors);
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


        System.out.println(BlueprintWriter.writeBlueprint(combinators, networks));
    }
}
