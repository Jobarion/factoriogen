package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Generator extends LanguageBaseListener {

    private static final String TEST_ORIGINAL = "{\n" +
            "  a = 3 + 2;\n" +
            "  b = a * a - 1;\n" +
            "  d = 0;\n" +
            "  if(b > 4) {\n" +
            "    a = 1;\n" +
            "  };\n" +
            "  else {\n" +
            "    a = a / 2;\n" +
            "    b = a;\n" +
            "  };\n" +
            "  c = a + b + d;\n" +
            "}";

    private static final String TEST = TEST_ORIGINAL;

    private Context context = new Context();
    private List<CombinatorGroup> generatedGroups = new ArrayList<>();
//    private Set<VariableAccessor> accessors = new HashSet<>();

    @Override
    public void enterElseExpr(LanguageParser.ElseExprContext ctx) {
        context.getConditionContext().enterElse();
    }

    @Override
    public void enterIfExpr(LanguageParser.IfExprContext ctx) {
        context.startExpressionContext();
        context.enterIf();
    }

    @Override
    public void exitIfExpr(LanguageParser.IfExprContext ctx) {
        System.out.println("Assigned in if: " + context.getConditionContext().getAssignedIf());
        System.out.println("Assigned in else: " + context.getConditionContext().getAssignedElse());

        var condition = context.popTempVariable();

        var assignedIf = context.getConditionContext().getAssignedIf();
        var assignedElse = context.getConditionContext().getAssignedElse();
        var assignedOriginal = context.getConditionContext().getOriginalBindings();

        Set<String> allVariables = new HashSet<>();
        allVariables.addAll(assignedIf.keySet());
        allVariables.addAll(assignedElse.keySet());

        if(allVariables.isEmpty()) {
            context.leaveIf();
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
            connected.setRedOut(context.getExpressionContext().getOutput());
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
            var original = assignedOriginal.get(name);
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

        context.leaveIf();
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
        boolean preexisting;
        //We don't care about new variables inside our if block, they go out of scope anyway
        if(context.getNamedVariable(varName) != null) {
            var existing = context.getNamedVariable(varName);
            variableSymbol = existing.getSignal();
            preexisting = true;
        }
        else {
            preexisting = false;
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
        if(preexisting) {
            context.getConditionContext().registerAssignment(varName, named);
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
