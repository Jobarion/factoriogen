package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;
import java.util.stream.Collectors;

public class Generator extends LanguageBaseListener {

    private static final String TEST_ORIGINAL = "{\n" +
            "  a = 3 + 2;\n" +
            "  b = a * a - 1;\n" +
            "  d = 0;\n" +
            "  if(b > 4) {\n" +
            "    a = 1;\n" +
            "  }\n" +
            "  else {\n" +
            "    a = a / 2;\n" +
            "    b = a;\n" +
            "  }\n" +
            "  c = a + b + d;\n" +
            "}";

    private static final String TEST_NESTED_IF = "{\n" +
            "  a = 30;\n" +
            "  if(a > 10) {\n" +
            "    a = a / 2;\n" +
            "    if(a > 10) {\n" +
            "      a = a * a;\n" +
            "    }\n" +
            "  }\n" +
            "  b = a;\n" +
            "}";

    private static final String TEST_LOOP = "{\n" +
            "  a = 10;\n" +
//            "  c = 10;\n" +
            "  while(a > 5) {\n" +
            "    if(a % 2 == 0) {\n" +
            "      a = a / 2;\n" +
            "    }\n" +
            "    a = a - 1;\n" +
//            "    c = c - a;\n" +
            "  }\n" +
            "  b = a;\n" +
            "}";

    private static final String IF_TIMING = "{ \n" +
            "  b = 4;\n" +
            "  x = 10;\n" +
            "  a = x;\n" +
            "  a = a / 3;\n" +
            "  a = a + 2;\n" +
            "  a = a - 5;\n" +
            "  if(b == 4) {\n" +
            "    a = 10;\n" +
            "  }\n" +
            "  c = a;\n" +
            "}";

    private static final String COLLATZ_LOOP = "{\n" +
            "  a = 27;\n" +
            "  i = 10;\n" +
            "  while(i > 0) {\n" +
            "    i = i - 1;\n" +
            "    if(a % 2 == 0) {\n" +
            "      a = a / 2;\n" +
            "    }\n" +
            "    else {\n" +
            "      a = a * 3 + 1;\n" +
            "    }\n" +
            "  }\n" +
            "  b = a;\n" +
            "}";

    private static final String NESTED_LOOP = "{\n" +
            "  a = 0;\n" +
            "  while(a < 10) {\n" +
            "    c = 0;\n" +
            "    while(c < 10) {\n" +
            "      a = a + 1;\n" +
            "    }\n" +
            "  }\n" +
            "  b = a;\n" +
            "}";

    private static final String TEST = NESTED_LOOP;

    private static final boolean AUTO_RUN_LOOP = true;

    private Context context = new Context();
    private List<CombinatorGroup> generatedGroups = new ArrayList<>();

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

    @Override
    public void exitWhileExpr(LanguageParser.WhileExprContext ctx) {
        WhileVariableScope whileScope = (WhileVariableScope) context.getVariableScope();
        context.leaveLoop();

        if(whileScope.getAccessedOutside().isEmpty()) {//The loop has no side effect and can be eliminated
            return;
        }

        this.generatedGroups.add(context.getExpressionContext());
        this.generatedGroups.add(whileScope.getVariableProviderGroup());
        this.generatedGroups.add(whileScope.getConditionVariableProviderGroup());

        //Build the variable buffer
        var loopPulseSymbol = context.getFreeSymbol();
        var storeCmb = DeciderCombinator.withLeftRight(Accessor.signal(loopPulseSymbol), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ);
        var inputGateCmb = storeCmb;
        var outputGateCmb = DeciderCombinator.withLeftRight(Accessor.signal(loopPulseSymbol), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ);
        var feedbackCmb = ArithmeticCombinator.withLeftRight(Accessor.signal(loopPulseSymbol), Accessor.constant(1), loopPulseSymbol.ordinal(), ArithmeticCombinator.SUB);

        var storeConnected = new ConnectedCombinator(storeCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(storeConnected);

        var inputGateConnected = new ConnectedCombinator(inputGateCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(inputGateConnected);

        var feedbackConnected = new ConnectedCombinator(feedbackCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(feedbackConnected);

        var outputGateConnected = new ConnectedCombinator(outputGateCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(outputGateConnected);

        var feedbackWire = new NetworkGroup();
        whileScope.getVariableProviderGroup().getNetworks().add(feedbackWire);
        feedbackConnected.setRedOut(feedbackWire);
        inputGateConnected.setRedIn(feedbackWire);

        var internalNetwork = new NetworkGroup();
        whileScope.getVariableProviderGroup().getNetworks().add(internalNetwork);
        storeConnected.setRedIn(internalNetwork);
        storeConnected.setRedOut(internalNetwork);
        inputGateConnected.setRedOut(internalNetwork);
        inputGateConnected.setGreenIn(whileScope.getVariableProviderGroup().getInput());
        feedbackConnected.setRedIn(internalNetwork);
        outputGateConnected.setRedIn(internalNetwork);

        NetworkGroup loopInputGroup = whileScope.getConditionVariableProviderGroup().getOutput();

        //Connect the variable buffer input
        System.out.println("Variables relevant for while: " + whileScope.getAccessedOutside());
        int maxDelay = 0;
        for(var varName : whileScope.getAccessedOutside().keySet()) {
            maxDelay = Math.max(maxDelay, whileScope.getParentScope().getNamedVariable(varName).getTickDelay());
        }
        for(var varName : whileScope.getAccessedOutside().keySet()) {
            var accessor = whileScope.getParentScope().getNamedVariable(varName).createVariableAccessor();
            accessor.access(maxDelay + 1).accept(whileScope.getVariableProviderGroup());
            whileScope.getVariableProviderGroup().getAccessors().add(accessor);
        }

        //Loop condition decider
        var condition = context.popTempVariable();
        ConnectedCombinator connectedCondition;
        if(condition instanceof Constant) {
            Combinator cmb = Combinator.constant(Signal.singleValue(condition.getSignal().ordinal(), ((Constant) condition).getVal()));
            connectedCondition = new ConnectedCombinator(cmb);
            whileScope.getVariableProviderGroup().getCombinators().add(connectedCondition);
        }

        else {
            Combinator cmb = ArithmeticCombinator.copying(condition.getSignal());
            connectedCondition = new ConnectedCombinator(cmb);
            connectedCondition.setGreenIn(context.getCurrentNetworkGroup());
            whileScope.getVariableProviderGroup().getCombinators().add(connectedCondition);
        }

        var loopFeedbackCmb = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ);
        var loopExitCmb = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ);

        var bufferDelayInput = loopInputGroup;
        ConnectedCombinator bufferDelayConnected = null;
        int signalDelay = condition.getTickDelay() + 2;
        for(int i = 0; i < signalDelay; i++) {//TODO why +3?
            var bufferDelayCmb = ArithmeticCombinator.withEach(Accessor.constant(0), ArithmeticCombinator.ADD);
            bufferDelayConnected = new ConnectedCombinator(bufferDelayCmb);
            bufferDelayConnected.setGreenIn(bufferDelayInput);
            whileScope.getVariableProviderGroup().getCombinators().add(bufferDelayConnected);
            if(i < signalDelay - 1) {
                bufferDelayInput = new NetworkGroup();
                whileScope.getVariableProviderGroup().getNetworks().add(bufferDelayInput);
                bufferDelayConnected.setGreenOut(bufferDelayInput);
            }
        }

        var loopFeedbackConnected = new ConnectedCombinator(loopFeedbackCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(loopFeedbackConnected);

        var loopExitConnected = new ConnectedCombinator(loopExitCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(loopExitConnected);

        NetworkGroup conditionSignalGroup = new NetworkGroup();
        whileScope.getVariableProviderGroup().getNetworks().add(conditionSignalGroup);
        connectedCondition.setRedOut(conditionSignalGroup);
        loopFeedbackConnected.setRedIn(conditionSignalGroup);
        loopExitConnected.setRedIn(conditionSignalGroup);
        bufferDelayConnected.setRedOut(conditionSignalGroup);

        outputGateConnected.setGreenOut(loopInputGroup);

        loopFeedbackConnected.setGreenOut(whileScope.getVariableProviderGroup().getOutput());

        var loopFeedbackFilterCmb = DeciderCombinator.withLeftRight(Accessor.signal(loopPulseSymbol.ordinal()), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ);
        var loopFeedbackFilterConnected = new ConnectedCombinator(loopFeedbackFilterCmb);
        whileScope.getVariableProviderGroup().getCombinators().add(loopFeedbackFilterConnected);

        NetworkGroup loopFeedbackInput = new NetworkGroup();
        whileScope.getVariableProviderGroup().getNetworks().add(loopFeedbackInput);

        loopFeedbackFilterConnected.setGreenIn(loopFeedbackInput);
        loopFeedbackFilterConnected.setGreenOut(loopInputGroup);

        maxDelay = 0;

        for(var accessedVar : whileScope.getAccessedOutside().values()) {
            maxDelay = Math.max(maxDelay, accessedVar.getTickDelay());
        }

        maxDelay++;

        for(var accessedVar : whileScope.getAccessedOutside().values()) {
            var accessor = accessedVar.createVariableAccessor();
            whileScope.getVariableProviderGroup().getAccessors().add(accessor);
            accessor.access(maxDelay).accept(loopFeedbackInput, whileScope.getVariableProviderGroup());
        }

        //Optimized delay circuit with constant amount of combinators
        // 0eNrVV+2OmzAQfJf9WZnr8RV6SO2LVCfkwJKsBAYZExVFvHttOJIcgSQkuUr9E8ledtidWY/JHtZZjaUkoSDcA8WFqCD8vYeKNoJnZk81JUIIpDAHBoLnZpVgTAlKKy7yNQmuCgktAxIJ/oHQbtlVAC5JbXNUFE9jOO07AxSKFGFfUbdoIlHna5T6JVegGJRFpbMLYWrQiL7HoIHQsl9XL75+UUIS4z7uMNCNK1lk0Rq3fEc6XycdgSMdTjqwygRSkpWKzjrckVS13jlU1j9hIY+3prUKDYzBqhQ3hFu6iaJEyfsy4JtOLWpV1ovB27ZrQfQddUXa5mcjEcUpfZRA6Lbv+nHHxCUm46ghg2Rck+qWnZij5PZkb9DDuTQb52KslorxgfocJY4SvJpFXnLZ1RnCr7slMEBlo+urhYpSWeQRCQ0DoZI1zukzwb/9mX/nKNakmP5Yrml53EPRQ++X9XFf/EEhf6yQN61QSplCOWMfc9yRSIueu9rIYZ8awCOcsWm27NvY8haby4EtZ8zW6mvNZSDww1wW5536z/c7hr/HWeQ//uyET0h743z7y+znIJf7b+wHrQS5uuI9P+/xnh3KRm1JbJ7nQN6tp+lMSHbpJhmHVw8aHbtU9Dgc3DZGq2VjFMye+q+8xY6eOUyS/9At9mk4Jwco5VmFi874BN9PuMWCO+Vx/2d5xqhPUshfdDp7RbQRdJ/x4cnfBgbagqqe1R+2F7w5get5ged4bfsXF5VBdQ==
//        if(maxDelay > 5) {
//
//        }
        var previousInput = whileScope.getVariableProviderGroup().getOutput();
        while(maxDelay > 0) {
            maxDelay--;
            var copyCmb = ArithmeticCombinator.copying(loopPulseSymbol);
            var connected = new ConnectedCombinator(copyCmb);
            connected.setGreenIn(previousInput);
            whileScope.getVariableProviderGroup().getNetworks().add(previousInput);
            whileScope.getVariableProviderGroup().getCombinators().add(connected);
            if(maxDelay == 0) {
                connected.setGreenOut(loopFeedbackInput);
            }
            else {
                previousInput = new NetworkGroup();
                connected.setGreenOut(previousInput);
            }
        }

        CombinatorGroup loopOutputGroup = new CombinatorGroup(null, new NetworkGroup());
        generatedGroups.add(loopOutputGroup);
        loopExitConnected.setGreenOut(loopOutputGroup.getOutput());

        if(AUTO_RUN_LOOP) {//TODO only for the first loop
            var loopPulseLoopback = new ConnectedCombinator(ArithmeticCombinator.copying(loopPulseSymbol));
            loopOutputGroup.getCombinators().add(loopPulseLoopback);
            loopPulseLoopback.setGreenIn(loopOutputGroup.getOutput());
            loopPulseLoopback.setRedOut(internalNetwork);
        }

        for(var defined : context.getVariableScope().getAllVariables().entrySet()) {
            var v = defined.getValue();
            var rebound = context.createNamedVariable(defined.getKey(), v.getType(), v.getSignal(), loopOutputGroup);
            rebound.setDelay(0);
        }

        //TODO
        //1. Loop back modified variables to loop input
        //2. Move all untouched variables to separate buffer
        //3. Loop condition decider that
        //  1. Either outputs the current loop state into the loop
        //  2. The current loop state + other variables to outside world
        //4. Wire up loop propagation/readiness pulse
    }

    @Override
    public void exitIfExpr(LanguageParser.IfExprContext ctx) {
        var condition = context.popTempVariable();

        var assignedIf = context.getConditionContext().getIfScope().getDefinedVariables();
        var assignedElse = context.getConditionContext().getElseScope().map(VariableScope::getDefinedVariables).orElse(Collections.emptyMap());

        context.leaveConditional();

        System.out.println("Assigned in if: " + assignedIf);
        System.out.println("Assigned in else: " + assignedElse);

        //Remove if variables only existed in if/else block and are now out of scope
        assignedIf.keySet().removeIf(key -> context.getNamedVariable(key) == null);
        assignedElse.keySet().removeIf(key -> context.getNamedVariable(key) == null);

        Set<String> allVariables = new HashSet<>();
        allVariables.addAll(assignedIf.keySet());
        allVariables.addAll(assignedElse.keySet());

        if(allVariables.isEmpty()) {
            return;
        }

        ConnectedCombinator connected;
        if(condition instanceof Constant) {
            Combinator cmb = Combinator.constant(Signal.singleValue(condition.getSignal().ordinal(), ((Constant) condition).getVal()));
            connected = new ConnectedCombinator(cmb);
            connected.setRedOut(context.getExpressionContext().getOutput());
            context.getExpressionContext().getCombinators().add(connected);
        }
        else {
            Combinator cmb = ArithmeticCombinator.copying(condition.getSignal());
            connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(context.getCurrentNetworkGroup());
            context.getExpressionContext().getCombinators().add(connected);
        }

        var ifCmb = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(1), Writer.everything(false), DeciderCombinator.EQ);
        var elseCmb = DeciderCombinator.withLeftRight(Accessor.signal(condition.getSignal()), Accessor.constant(0), Writer.everything(false), DeciderCombinator.EQ);

        var ifConnected = new ConnectedCombinator(ifCmb);
        var elseConnected = new ConnectedCombinator(elseCmb);

        NetworkGroup outputNetwork = new NetworkGroup();

        CombinatorGroup ifGroup = new CombinatorGroup(new NetworkGroup(), outputNetwork);
        CombinatorGroup elseGroup = new CombinatorGroup(new NetworkGroup(), outputNetwork);

        ifConnected.setRedIn(context.getExpressionContext().getOutput());
        ifConnected.setGreenIn(ifGroup.getInput());
        ifConnected.setGreenOut(ifGroup.getOutput());
        elseConnected.setRedIn(context.getExpressionContext().getOutput());
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

        generatedGroups.add(ifGroup);
        generatedGroups.add(elseGroup);
        generatedGroups.add(combinedGroup);

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
        delay++; //Accessors :(

        ConnectedCombinator currentConditionCombinator = connected;
        for(int i = delay; i > condition.getTickDelay() + 2; i--) {
            System.out.println("Added manual if condition delay");
            ConnectedCombinator next = new ConnectedCombinator(ArithmeticCombinator.copying(condition.getSignal()));
            var connection = new NetworkGroup();
            currentConditionCombinator.setGreenOut(connection);
            next.setGreenIn(connection);
            currentConditionCombinator = next;
            context.getExpressionContext().getNetworks().add(connection);
            context.getExpressionContext().getCombinators().add(next);
        }
        currentConditionCombinator.setRedOut(context.getExpressionContext().getOutput());

        for(var accessor : ifAccessors) {
            accessor.access(delay).accept(ifGroup);
            context.getExpressionContext().getAccessors().add(accessor);
        }

        for(var accessor : elseAccessors) {
            accessor.access(delay).accept(elseGroup);
            context.getExpressionContext().getAccessors().add(accessor);
        }

        for(var v : createdVariables) {
            v.setDelay(delay + 2); //1 for the if, 1 for filtering the condition signal out of the passed through one
        }

        generatedGroups.add(context.getExpressionContext());
    }

    @Override
    public void enterCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
        context.startExpressionContext();
    }

    @Override
    public void exitCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
        generatedGroups.add(context.getExpressionContext());
        context.getExpressionContext().setCorrespondingCode(ctx.getText());
    }

    @Override
    public void exitBoolExpr(LanguageParser.BoolExprContext ctx) {
        if(ctx.leftComponent != null) {
            System.out.println("Exit " + ctx.getText());
            BOOL_EXPR_COMPONENT_PARSER.parse(ctx);
        }
        else if(ctx.left != null) {System.out.println("Exit " + ctx.getText());
            BOOL_EXPR_PARSER.parse(ctx);
        }
        else if(ctx.negated != null) {
            NOT_EXPR_PARSER.parse(ctx);
        }
    }

    @Override
    public void exitExpr(LanguageParser.ExprContext ctx) {
        if(ctx.left != null) {
            EXPR_PARSER.parse(ctx);
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

        ConnectedCombinator connected;
        if(value instanceof Constant) {
            Combinator cmb = Combinator.constant(Signal.singleValue(variableSymbol.ordinal(), ((Constant) value).getVal()));
            connected = new ConnectedCombinator(cmb);
            connected.setGreenOut(context.getExpressionContext().getOutput());
            context.getExpressionContext().getCombinators().add(connected);
        }
        else {
            Combinator cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(value.getSignal().ordinal()), Accessor.constant(0), variableSymbol.ordinal(), ArithmeticCombinator.ADD);
            connected = new ConnectedCombinator(cmb);
            connected.setGreenOut(context.getExpressionContext().getOutput());
            connected.setGreenIn(context.getCurrentNetworkGroup());
            context.getExpressionContext().getCombinators().add(connected);
        }
        var named= context.createNamedVariable(varName, value.getType(), variableSymbol, context.getExpressionContext());
        named.setDelay(value.getTickDelay() + 2); //Aliasing, accessor
        if(value instanceof NamedVariable) {
            var accessor = ((NamedVariable) value).createVariableAccessor();
            context.getExpressionContext().getAccessors().add(accessor);
            accessor.access(value.getTickDelay() + 1).accept(context.getExpressionContext());
        }
        System.out.println("Creating named " + varName + " = " + named + ", with delay " + named.getTickDelay());
    }

    @Override
    public void enterBlock(LanguageParser.BlockContext ctx) {
        context.enterScope();
    }

    @Override
    public void exitBlock(LanguageParser.BlockContext ctx) {
        context.leaveScope();
    }

    private final Combiner<LanguageParser.ExprContext, ArithmeticOperation> EXPR_PARSER = new Combiner<>(context, 2, VarType.INT, VarType.INT) {

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
        public void generateCombinators(Symbol[] symbols, ArithmeticOperation operation, FactorioSignal outSymbol) {
            var cmb = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor(context),  symbols[1].toAccessor(context), outSymbol.ordinal(), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(context.getCurrentNetworkGroup());
            connected.setGreenOut(context.getCurrentNetworkGroup());
            context.getExpressionContext().getCombinators().add(connected);
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, DeciderOperation> BOOL_EXPR_COMPONENT_PARSER = new Combiner<>(context, 2, VarType.INT, VarType.INT) {

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
        public void generateCombinators(Symbol[] symbols, DeciderOperation operation, FactorioSignal outSymbol) {
            if(symbols[0] instanceof Constant) {
                var tmp = symbols[1];
                symbols[1] = symbols[0];
                symbols[0] = tmp;
                operation = DeciderCombinator.getInvertedOperation(operation);
            }
            var cmb = DeciderCombinator.withLeftRight(symbols[0].toAccessor(context),  symbols[1].toAccessor(context), Writer.constant(outSymbol.ordinal(), 1), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(context.getCurrentNetworkGroup());
            connected.setGreenOut(context.getCurrentNetworkGroup());
            context.getExpressionContext().getCombinators().add(connected);
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, ArithmeticOperation> BOOL_EXPR_PARSER = new Combiner<>(context, 2, VarType.BOOLEAN, VarType.BOOLEAN) {

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
        public void generateCombinators(Symbol[] symbols, ArithmeticOperation operation, FactorioSignal outSymbol) {
            var cmb = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor(context),  symbols[1].toAccessor(context), outSymbol.ordinal(), operation);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(context.getCurrentNetworkGroup());
            connected.setGreenOut(context.getCurrentNetworkGroup());
            context.getExpressionContext().getCombinators().add(connected);
        }
    };

    private final Combiner<LanguageParser.BoolExprContext, Void> NOT_EXPR_PARSER = new Combiner<>(context, 1, VarType.BOOLEAN, VarType.BOOLEAN) {

        @Override
        public Void getOperation(LanguageParser.BoolExprContext ruleContext) {
            return null;
        }

        @Override
        public Constant computeConstExpr(Constant[] constants, Void operation) {
            return new Constant(constants[0].getVal() == 0 ? 1 : 0, VarType.BOOLEAN);
        }

        @Override
        public void generateCombinators(Symbol[] symbols, Void operation, FactorioSignal outSymbol) {
            var cmb = DeciderCombinator.withLeftRight(symbols[0].toAccessor(context), Accessor.constant(0), Writer.constant(outSymbol.ordinal(), 1), DeciderCombinator.EQ);
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(context.getCurrentNetworkGroup());
            connected.setGreenOut(context.getCurrentNetworkGroup());
            context.getExpressionContext().getCombinators().add(connected);
        }
    };

    public static void main(String[] args) {
        LanguageLexer lexer = new LanguageLexer(CharStreams.fromString(TEST));
        LanguageParser parser = new LanguageParser(new CommonTokenStream(lexer));

        var generator = new Generator();

        parser.addParseListener(generator);

        parser.block();
        Signal.SIGNAL_TYPES.set(FactorioSignal.values().length);

        System.out.println(generator.generatedGroups);

//        generator.accessors.forEach(VariableAccessor::generateAccessors);
        generator.generatedGroups.forEach(g -> {
            g.getAccessors().forEach(VariableAccessor::generateAccessors);
        });

        var combinators = generator.generatedGroups.stream()
                .map(CombinatorGroup::getCombinators)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var networks = generator.generatedGroups.stream()
                .map(CombinatorGroup::getNetworks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        System.out.println(combinators);
        System.out.println(networks);

        System.out.println(Simulator.writeBlueprint(combinators, networks));
    }
}
