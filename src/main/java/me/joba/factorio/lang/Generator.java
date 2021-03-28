package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;
import java.util.stream.Collectors;

public class Generator extends LanguageBaseListener {

    private static final String SIMPLE_FUNCTION_ADD =
            "function add(a=red, b=green) {\n" +
                    "c = a * b;\n" +
                    "return(a, b, c, a + b * c, 1);\n" +
            "}";

    private static final String SIMPLE_FUNCTION_IF =
            "function addIf(a=red, b=green) {\n" +
                    "  if(a < 0) {\n" +
                    "    a = a * -1;\n" +
                    "  }\n" +
                    "  return(a + b, 1);\n" +
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

    private static final String PYTH_TRIPLE = "function nested(max: red) {\n" +
            "  a = 0;\n" +
            "  b = 0;\n" +
            "  c = 0;\n" +
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
            "  return(a, b, c);\n" +
            "}";

    private static final String NESTED_SMALLER = "function nested1(max= red) {\n" +
            "  a = 3;\n" +
            "  b = 0;\n" +
            "  c = 0;\n" +
            "  terminate = 0;\n" +
            "      while(terminate == 0 && b <= max) {\n" +
            "        c = 0;\n" +
            "        b = b + 1;\n" +
            "        while(terminate == 0 && c <= max) {\n" +
            "          c = c + 1;\n" +
            "          if(a * a + b * b == c * c) {\n" +
            "            terminate = 1;\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "  return(b, c);\n" +
            "}";

    private static final String COMPLEX_CONDITION = "function complexLoopCondition(max= red) {\n" +
            "  terminate = 0;\n" +
            "  b = 0;\n" +
            "  while(terminate == 0 && b <= max) {\n" +
            "    b = b + 1;\n" +
            "    if(1 + b * b == 1 + b + b) {\n" +
            "      terminate = 1;\n" +
            "    }\n" +
            "  }\n" +
            "  return(b);\n" +
            "}";

    private static final String TEST = PYTH_TRIPLE;

    private FunctionContext context;

    @Override
    public void enterFunction(LanguageParser.FunctionContext ctx) {
        NetworkGroup in = new NetworkGroup();
        NetworkGroup out = new NetworkGroup();
        CombinatorGroup functionHeader = new CombinatorGroup(in, out);
        var inputCombinator = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        inputCombinator.setName("Input function");
        functionHeader.getCombinators().add(inputCombinator);
        inputCombinator.setGreenIn(in);
        inputCombinator.setGreenOut(out);
        context = new FunctionContext(functionHeader);
        context.overwriteControlFlowVariable(functionHeader).setDelay(0);
    }

    @Override
    public void exitFunctionParams(LanguageParser.FunctionParamsContext ctx) {
        for(var param : ctx.functionParam()) {
            FactorioSignal signal = FactorioSignal.valueOf("SIGNAL_" + param.signal.getText().toUpperCase().replace('-', '_'));
            log("Param " + param.name.getText() + " supplied as " + signal);
            if(signal.isReserved()) {
                throw new IllegalArgumentException("Signal " + signal + " is reserved");
            }
            context.claimSymbol(signal);
            context.createNamedVariable(param.name.getText(), VarType.INT, signal, context.getFunctionGroup()).setDelay(0);
        }
    }

    @Override
    public void exitReturnStatement(LanguageParser.ReturnStatementContext ctx) {
        var returnGroup = context.getFunctionReturnGroup();

        NetworkGroup gateInput = new NetworkGroup();
        returnGroup.getNetworks().add(gateInput);

        var outputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        returnGroup.getCombinators().add(outputGate);
        outputGate.setGreenOut(returnGroup.getOutput());
        outputGate.setGreenIn(gateInput);
        outputGate.setName("Output " + ctx.getText());

        int maxDelay = context.getControlFlowVariable().getTickDelay();
        List<Symbol> valuesToReturn = new ArrayList<>();
        for(int i = 0; i < ctx.returnValues().completeExpression().size(); i++) {
            var returnVal = context.popTempVariable();
            valuesToReturn.add(returnVal);
            maxDelay = Math.max(maxDelay, returnVal.getTickDelay());
        }
        valuesToReturn.add(context.getControlFlowVariable());
        for(var returnVal : valuesToReturn) {
            if(!returnVal.isBound()) {
                returnVal.bind(context.getFreeSymbol());
            }
            log("Returning " + returnVal + " with delay " + returnVal.getTickDelay());
            if(returnVal instanceof Constant) {
                if(((Constant) returnVal).getVal() == 0) continue;
                ConnectedCombinator constant = new ConnectedCombinator(Combinator.constant(Signal.singleValue(returnVal.getSignal().ordinal(), ((Constant) returnVal).getVal())));
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
        context.enterIfStatement();
    }

    @Override
    public void enterElseStatement(LanguageParser.ElseStatementContext ctx) {
        context.enterElseStatement();
    }

    @Override
    public void enterIfExpr(LanguageParser.IfExprContext ctx) {
        context.startExpressionContext();
        context.enterConditional();
    }

    @Override
    public void enterWhileExpr(LanguageParser.WhileExprContext ctx) {
        context.startExpressionContext();
        context.enterLoop();
    }

    @Override
    public void enterLoopBody(LanguageParser.LoopBodyContext ctx) {
        ((WhileVariableScope)context.getVariableScope()).enterLoopBody();
    }

    /*
    The idea here is the following:
        1. Wire up all variables that we need in the loop to an input buffer. That buffer stores the first state it gets.
        2. Once the buffer receives a signal from the outside, it emits the stored variables.
        3. Using them, the loop condition is evaluated
        4. If true, let all variables into the loop + the loop timing pulse.
        5. Do normal expression evaluation inside the loop
        6. The loop is evaluated. All signals end up at the loop output gate that only lets stuff pass if the timing pulse is present (don't let intermediary signals pass)
        7. Back to 3.
        8. If the loop condition is false, emit the signals + loop pulse that can be used by future circuits (= more loops) to sync with.
        9. (TODO: Move all other variables into a buffer and write them out together with whatever comes out of 8.)
     */
    @Override
    public void exitWhileExpr(LanguageParser.WhileExprContext ctx) {
        WhileVariableScope whileScope = (WhileVariableScope) context.getVariableScope();
        context.leaveLoop();

        CombinatorGroup whileGroup = new CombinatorGroup(new NetworkGroup("while in"), new NetworkGroup("while out"));
        context.getFunctionGroup().getSubGroups().add(whileGroup);
        whileGroup.getSubGroups().add(whileScope.getPreConditionProvider());
        whileGroup.getSubGroups().add(whileScope.getPostConditionProvider());

        ConnectedCombinator inputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(inputGate);

        inputGate.setGreenIn(whileGroup.getInput());

        ConnectedCombinator dedupInput = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.TEMP_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ));
        whileGroup.getCombinators().add(dedupInput);

        ConnectedCombinator dedupStore = new ConnectedCombinator(DeciderCombinator.withAny(Accessor.constant(0), Writer.constant(CombinatorUtil.TEMP_SIGNAL.ordinal(), 1), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(dedupStore);

        ConnectedCombinator dedupReset = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.fromInput(CombinatorUtil.TEMP_SIGNAL.ordinal()), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(dedupReset);

        ConnectedCombinator dedupConstants = new ConnectedCombinator(Combinator.constant(Signal.singleValue(CombinatorUtil.TEMP_SIGNAL.ordinal(), -1)));
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

        ConnectedCombinator loopFeedbackGateInitial = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
        whileGroup.getCombinators().add(loopFeedbackGateInitial);

        ConnectedCombinator loopFeedbackGateSubsequent = new ConnectedCombinator(DeciderCombinator.withLeftRight(Accessor.signal(CombinatorUtil.CONTROL_FLOW_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderCombinator.NEQ));
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
        var condition = context.popTempVariable();

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
            ConnectedCombinator connected = new ConnectedCombinator(Combinator.constant(Signal.singleValue(condition.getSignal().ordinal(), ((Constant) condition).getVal())));
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
            var rebound = context.createNamedVariable(defined.getKey(), v.getType(), v.getSignal(), whileGroup);
            rebound.setDelay(0);
        }
    }

    /*
        The idea:
        1. Track which variables are assigned in the if/else blocks.
        2. In parallel, calculate the result of the if & else block + the condition.
        3. Add variables that exist in if but not in else to the if block result and vice versa.
            e.g. in the if block a & b are assigned, in the else block b & c. The if block now emits its own a & b + the original value of c.
            The else block emits its b & c + the original value of a.
        4. Sync up the condition, results of if and results of else.
        5. Wire to appropriate combinators that either let the if or the else results pass.
     */
    @Override
    public void exitIfExpr(LanguageParser.IfExprContext ctx) {
        var condition = context.popTempVariable();

        var assignedIf = context.getConditionContext().getIfScope().getDefinedVariables();
        var assignedElse = context.getConditionContext().getElseScope().map(VariableScope::getDefinedVariables).orElse(Collections.emptyMap());

        context.leaveConditional();

        log("Assigned in if: " + assignedIf);
        log("Assigned in else: " + assignedElse);

        //Remove if variables only existed in if/else block and are now out of scope
        assignedIf.keySet().removeIf(key -> context.getNamedVariable(key) == null);
        assignedElse.keySet().removeIf(key -> context.getNamedVariable(key) == null);

        Set<String> allVariables = new HashSet<>();
        allVariables.addAll(assignedIf.keySet());
        allVariables.addAll(assignedElse.keySet());
        context.getVariableScope().getAllVariables();
        allVariables.add(FunctionContext.CONTROL_FLOW_VAR_NAME);

        if(allVariables.isEmpty()) {
            return;
        }

        CombinatorGroup group = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        context.getFunctionGroup().getSubGroups().add(group);


        var ifCmb = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ);
        var elseCmb = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ);

        var ifConnected = new ConnectedCombinator(ifCmb);
        var elseConnected = new ConnectedCombinator(elseCmb);

        NetworkGroup outputNetwork = new NetworkGroup();

        CombinatorGroup ifGroup = new CombinatorGroup(new NetworkGroup(), outputNetwork);
        CombinatorGroup elseGroup = new CombinatorGroup(new NetworkGroup(), outputNetwork);

        ifConnected.setRedIn(group.getOutput());
        ifConnected.setGreenIn(ifGroup.getInput());
        ifConnected.setGreenOut(ifGroup.getOutput());
        elseConnected.setRedIn(group.getOutput());
        elseConnected.setGreenIn(elseGroup.getInput());
        elseConnected.setGreenOut(elseGroup.getOutput());

        ifGroup.getCombinators().add(ifConnected);
        elseGroup.getCombinators().add(elseConnected);

        CombinatorGroup combinedGroup = new CombinatorGroup(outputNetwork, new NetworkGroup());

        var passThroughCmb = ArithmeticCombinator.withEach(Accessor.constant(0), ArithmeticCombinator.ADD);
        var removeSignalCmb = ArithmeticCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(-1), condition.getSignal().ordinal(), ArithmeticCombinator.MUL);

        var passThroughConnected = new ConnectedCombinator(passThroughCmb);
        var removeSignalConnected = new ConnectedCombinator(removeSignalCmb);

        passThroughConnected.setGreenIn(combinedGroup.getInput());
        removeSignalConnected.setGreenIn(combinedGroup.getInput());
        passThroughConnected.setGreenOut(combinedGroup.getOutput());
        removeSignalConnected.setGreenOut(combinedGroup.getOutput());

        combinedGroup.getCombinators().add(passThroughConnected);
        combinedGroup.getCombinators().add(removeSignalConnected);

        context.getFunctionGroup().getSubGroups().add(ifGroup);
        context.getFunctionGroup().getSubGroups().add(elseGroup);
        context.getFunctionGroup().getSubGroups().add(combinedGroup);

        int delay = condition.getTickDelay() + 1;//Condition signal has to be first

        List<Variable> createdVariables = new ArrayList<>();

        List<VariableAccessor> ifAccessors = new ArrayList<>();
        List<VariableAccessor> elseAccessors = new ArrayList<>();

        for(String name : allVariables) {
            var original = context.getNamedVariable(name);
            VariableAccessor ifAccessor;
            if(assignedIf.containsKey(name)) {
                var ifVar = assignedIf.get(name);
                ifAccessor = ifVar.createVariableAccessor();
                delay = Math.max(delay, ifVar.getTickDelay());
            }
            else {
                ifAccessor = original.createVariableAccessor();
                delay = Math.max(delay, original.getTickDelay());
            }
            VariableAccessor elseAccessor;
            if(assignedElse.containsKey(name)) {
                var elseVar = assignedElse.get(name);
                elseAccessor = elseVar.createVariableAccessor();
                delay = Math.max(delay, elseVar.getTickDelay());
            }
            else {
                elseAccessor = original.createVariableAccessor();
                delay = Math.max(delay, original.getTickDelay());
            }
            ifAccessors.add(ifAccessor);
            elseAccessors.add(elseAccessor);
            createdVariables.add(context.createNamedVariable(name, original.getType(), original.getSignal(), combinedGroup));
        }

        ConnectedCombinator connected;
        if(condition instanceof Constant) {
            connected = new ConnectedCombinator(Combinator.constant(Signal.singleValue(condition.getSignal().ordinal(), ((Constant) condition).getVal())));
            connected.setRedOut(group.getOutput());
            group.getCombinators().add(connected);
        }
        else {
            connected = new ConnectedCombinator(ArithmeticCombinator.copying(condition.getSignal()));
            connected.setGreenIn(group.getInput());
            connected.setRedOut(group.getOutput());
            group.getCombinators().add(connected);
            var accessor = ((Variable)condition).createVariableAccessor();
            group.getAccessors().add(accessor);
            accessor.access(delay - 1).accept(group);
        }

        ConnectedCombinator currentConditionCombinator = connected;
        for(int i = delay; i > condition.getTickDelay() + 2; i--) {
            log("Added manual if condition delay");
            ConnectedCombinator next = new ConnectedCombinator(ArithmeticCombinator.copying(condition.getSignal()));
            var connection = new NetworkGroup();
            currentConditionCombinator.setGreenOut(connection);
            next.setGreenIn(connection);
            currentConditionCombinator = next;
            group.getNetworks().add(connection);
            group.getCombinators().add(next);
        }
        currentConditionCombinator.setRedOut(group.getOutput());

        for(var accessor : ifAccessors) {
            accessor.access(delay).accept(ifGroup);
            group.getAccessors().add(accessor);
        }

        for(var accessor : elseAccessors) {
            accessor.access(delay).accept(elseGroup);
            group.getAccessors().add(accessor);
        }

        for(var v : createdVariables) {
            v.setDelay(delay + 2); //1 for the if, 1 for filtering the condition signal out of the passed through one
        }
    }

//    @Override
//    public void enterCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
//        context.startExpressionContext();
//    }
//
//    @Override
//    public void exitCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
//
//    }

    @Override
    public void exitBoolExpr(LanguageParser.BoolExprContext ctx) {
        if(ctx.leftComponent != null) {
            log("Exit " + ctx.getText());
            BOOL_EXPR_COMPONENT_PARSER.parse(context, ctx);
        }
        else if(ctx.left != null) {log("Exit " + ctx.getText());
            BOOL_EXPR_PARSER.parse(context, ctx);
        }
        else if(ctx.negated != null) {
            NOT_EXPR_PARSER.parse(context, ctx);
        }
    }

    /*
        Get the expression components from the stack, and either calculate the result (if constant) or produce
        combinators to calculate the result. Put result back on stack.
     */
    @Override
    public void exitExpr(LanguageParser.ExprContext ctx) {
        if(ctx.left != null) {
            EXPR_PARSER.parse(context, ctx);
        }
        else if(ctx.numberLit != null) {
            int val = Integer.parseInt(ctx.getText());
            context.pushTempVariable(new Constant(val));
        }
        else if(ctx.var != null) {
            var named = context.getNamedVariable(ctx.var.getText());
            if(named == null) throw new RuntimeException("Variable " + ctx.var.getText() + " is not defined");
            context.pushTempVariable(named);
        }
    }

    /*
        Assignments either create a new named variable, or override an existing one with a new provider group (and delay)
        Provider group = a group of combinators with a defined output NetworkGroup that produce a certain variable.
        Anyone who wants that variable can get it out of the provider group.
     */
    @Override
    public void exitAssignment(LanguageParser.AssignmentContext ctx) {
        var value = context.popTempVariable();
        String varName = ctx.var.getText();

        FactorioSignal variableSymbol;
        //We don't care about new variables inside our if block, they go out of scope anyway
        if(context.getNamedVariable(varName) != null) {
            var existing = context.getNamedVariable(varName);
            variableSymbol = existing.getSignal();
        }
        else {
            variableSymbol = context.getFreeSymbol();
        }

        CombinatorGroup group = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        context.getFunctionGroup().getSubGroups().add(group);

        ConnectedCombinator connected;
        if(value instanceof Constant) {
            Combinator cmb = Combinator.constant(Signal.singleValue(variableSymbol.ordinal(), ((Constant) value).getVal()));
            connected = new ConnectedCombinator(cmb);
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
        }
        else {
            //TODO remove this and use the value directly
            Combinator cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(value.getSignal().ordinal()), Accessor.constant(0), variableSymbol.ordinal(), ArithmeticCombinator.ADD);
            connected = new ConnectedCombinator(cmb);
            connected.setGreenOut(group.getOutput());
            connected.setGreenIn(group.getInput());
            group.getCombinators().add(connected);
        }
        var named= context.createNamedVariable(varName, value.getType(), variableSymbol, group);
        named.setDelay(value.getTickDelay() + 1); //Aliasing
        if(value instanceof Variable) {
            var accessor = ((Variable) value).createVariableAccessor();
            group.getAccessors().add(accessor);
            accessor.access(value.getTickDelay()).accept(group);
        }
        log("Creating named " + varName + " = " + named + ", with delay " + named.getTickDelay());
    }

    @Override
    public void enterBlock(LanguageParser.BlockContext ctx) {
        context.enterScope();
    }

    @Override
    public void exitBlock(LanguageParser.BlockContext ctx) {
        context.leaveScope();
    }

    private final Combiner<LanguageParser.ExprContext, ArithmeticOperation> EXPR_PARSER = new Combiner<>(2, VarType.INT, VarType.INT) {

        @Override
        public ArithmeticOperation getOperation(LanguageParser.ExprContext ruleContext) {
            return ArithmeticCombinator.getOperation(ruleContext.op.getText());
        }

        @Override
        public Constant computeConstExpr(Constant[] constants, ArithmeticOperation operation) {
            int result = operation.applyAsInt(constants[0].getVal(), constants[1].getVal());
            return new Constant(result, VarType.INT);
        }

        @Override
        public int generateCombinators(Symbol[] symbols, ArithmeticOperation operation, FactorioSignal outSymbol, CombinatorGroup group) {
            var cmb = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor(context),  symbols[1].toAccessor(context), outSymbol.ordinal(), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, DeciderOperation> BOOL_EXPR_COMPONENT_PARSER = new Combiner<>(2, VarType.INT, VarType.BOOLEAN) {

        @Override
        public DeciderOperation getOperation(LanguageParser.BoolExprContext ruleContext) {
            return DeciderCombinator.getOperation(ruleContext.op.getText());
        }

        @Override
        public Constant computeConstExpr(Constant[] constants, DeciderOperation operation) {
            boolean result = operation.test(constants[0].getVal(), constants[1].getVal());
            return new Constant(result ? 1 : 0, VarType.BOOLEAN);
        }

        @Override
        public int generateCombinators(Symbol[] symbols, DeciderOperation operation, FactorioSignal outSymbol, CombinatorGroup group) {
            if(symbols[0] instanceof Constant) {
                var tmp = symbols[1];
                symbols[1] = symbols[0];
                symbols[0] = tmp;
                operation = DeciderCombinator.getInvertedOperation(operation);
            }
            var cmb = DeciderCombinator.withLeftRight(symbols[0].toAccessor(context),  symbols[1].toAccessor(context), Writer.constant(outSymbol.ordinal(), 1), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, ArithmeticOperation> BOOL_EXPR_PARSER = new Combiner<>(2, VarType.BOOLEAN, VarType.BOOLEAN) {

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
        public Constant computeConstExpr(Constant[] constants, ArithmeticOperation operation) {
            int result = operation.applyAsInt(constants[0].getVal(), constants[1].getVal());
            return new Constant(result, VarType.BOOLEAN);
        }

        @Override
        public int generateCombinators(Symbol[] symbols, ArithmeticOperation operation, FactorioSignal outSymbol, CombinatorGroup group) {
            var cmb = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor(context),  symbols[1].toAccessor(context), outSymbol.ordinal(), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, Void> NOT_EXPR_PARSER = new Combiner<>(1, VarType.BOOLEAN, VarType.BOOLEAN) {

        @Override
        public Void getOperation(LanguageParser.BoolExprContext ruleContext) {
            return null;
        }

        @Override
        public Constant computeConstExpr(Constant[] constants, Void operation) {
            return new Constant(constants[0].getVal() == 0 ? 1 : 0, VarType.BOOLEAN);
        }

        @Override
        public int generateCombinators(Symbol[] symbols, Void operation, FactorioSignal outSymbol, CombinatorGroup group) {
            var cmb = DeciderCombinator.withLeftRight(symbols[0].toAccessor(context), Accessor.constant(0), Writer.constant(outSymbol.ordinal(), 1), DeciderCombinator.EQ);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(group.getInput());
            connected.setGreenOut(group.getOutput());
            group.getCombinators().add(connected);
            return 1;
        }
    };

    private void log(String msg) {
        System.out.println("\t".repeat(context.getDepth()) + msg);
    }

    public static void main(String[] args) {
        LanguageLexer lexer = new LanguageLexer(CharStreams.fromString(TEST));
        LanguageParser parser = new LanguageParser(new CommonTokenStream(lexer));

        var generator = new Generator();

        parser.addParseListener(generator);

        var func = parser.function();
        Signal.SIGNAL_TYPES.set(FactorioSignal.values().length);

        Set<CombinatorGroup> generatedGroups = new HashSet<>();
        Queue<CombinatorGroup> toExpand = new LinkedList<>();
        toExpand.add(generator.context.getFunctionGroup());
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
                .map(CombinatorGroup::getNetworks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        System.out.println(BlueprintWriter.writeBlueprint(combinators, networks));
    }
}
