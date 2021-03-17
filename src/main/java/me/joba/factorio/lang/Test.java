package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;
import java.util.stream.Collectors;

public class Test {

    private static final String TEST_INCORRECT = "{\n" +
            "  a = 2;\n" +
            "  b = 3;\n" +
            "  if(b > 4) {\n" +
            "    a = 1;\n" +
            "  };\n" +
            "  else {\n" +
            "    a = a / 2;\n" +
            "    b = a;\n" +
            "  };\n" +
            "  c = a + b;\n" +
            "}";


    private static final String TEST_MINIMAL = "{\n" +
            "  b = 4;\n" +
            "  if(b > 4) {\n" +
            "    b = 1;\n" +
            "  };\n" +
            "  else {\n" +
            "    b = b / 2;\n" +
            "  };\n" +
            "  c = b;\n" +
            "}";

    private static final String TEST_ORIGINAL = "{\n" +
            "  a = 3;\n" +
            "  b = a * a - 1;\n" +
            "  if(b > 4) {\n" +
            "    a = 1;\n" +
            "  };\n" +
            "  else {\n" +
            "    a = a / 2;\n" +
            "    b = a;\n" +
            "  };\n" +
            "  c = a + b;\n" +
            "}";

    private static final String TEST = TEST_ORIGINAL;

    private static Context context = new Context();

    public static void main(String[] args) {
        LanguageLexer lexer = new LanguageLexer(CharStreams.fromString(TEST));
        LanguageParser parser = new LanguageParser(new CommonTokenStream(lexer));
        List<CombinatorGroup> generatedGroups = new ArrayList<>();
        Set<VariableAccessor> accessors = new HashSet<>();

        parser.addParseListener(new LanguageBaseListener() {

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

                for(String name : allVariables) {
                    var original = assignedOriginal.get(name);
                    VariableAccessor ifAccessor;
                    if(assignedIf.containsKey(name)) {
                        ifAccessor = assignedIf.get(name).createVariableAccessor();
                    }
                    else {
                        ifAccessor = original.createVariableAccessor();
                    }
                    VariableAccessor elseAccessor;
                    if(assignedElse.containsKey(name)) {
                        elseAccessor = assignedElse.get(name).createVariableAccessor();
                    }
                    else {
                        elseAccessor = original.createVariableAccessor();
                    }
                    ifAccessor.access().accept(ifGroup);
                    elseAccessor.access().accept(elseGroup);
                    accessors.add(ifAccessor);
                    accessors.add(elseAccessor);
                    context.createNamedVariable(name, original.getType(), original.getSignal(), combinedGroup);
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
                    var rightVar = context.popTempVariable();
                    var leftVar = context.popTempVariable();
                    var op = DeciderCombinator.getOperation(ctx.op.getText());

                    if(leftVar.getType() != VarType.INT) throw new RuntimeException(leftVar + " is not of type boolean");
                    if(rightVar.getType() != VarType.INT) throw new RuntimeException(leftVar + " is not of type boolean");

                    if(leftVar instanceof Constant) {
                        if(rightVar instanceof Constant) {
                            //Calculate constant expressions immediately
                            boolean boolVal = op.test(((Constant) leftVar).getVal(), ((Constant) rightVar).getVal());
                            System.out.println("Folded const expr '" + leftVar + ctx.op.getText() + rightVar + " -> " + boolVal);
                            context.pushTempVariable(new Constant(boolVal ? 1 : 0, VarType.BOOLEAN));
                            return;
                        }
                        else {
                            //Constants are only allowed on the right side
                            var tmp = rightVar;
                            rightVar = leftVar;
                            leftVar = tmp;
                        }
                    }
                    if(!leftVar.isBound()) {
                        leftVar.bind(context.getFreeSymbol());
                    }
                    if(!rightVar.isBound()) {
                        rightVar.bind(context.getFreeSymbol());
                    }

                    var outSymbol = context.getFreeSymbol();

                    var bound = context.createBoundVariable(VarType.BOOLEAN, outSymbol);
                    System.out.println(bound + " = " + leftVar + " " + ctx.op.getText() + " " + rightVar);

                    var cmb = DeciderCombinator.withLeftRight(leftVar.toAccessor(context),  rightVar.toAccessor(context), Writer.constant(outSymbol.ordinal(), 1), op);

                    var connected = new ConnectedCombinator(cmb);
                    connected.setGreenIn(context.getCurrentNetworkGroup());
                    connected.setGreenOut(context.getCurrentNetworkGroup());
                    context.getExpressionContext().getCombinators().add(connected);
                }
                else if(ctx.left != null) {System.out.println("Exit " + ctx.getText());
                    var rightVar = context.popTempVariable();
                    var leftVar = context.popTempVariable();
                    var op = switch(ctx.op.getText()) {
                        case "&&" -> ArithmeticCombinator.AND;
                        case "||" -> ArithmeticCombinator.OR;
                        case "^" -> ArithmeticCombinator.XOR;
                        default -> throw new UnsupportedOperationException("Unknown operation '" + ctx.op.getText() + "'");
                    };

                    if(leftVar.getType() != VarType.BOOLEAN) throw new RuntimeException(leftVar + " is not of type boolean");
                    if(rightVar.getType() != VarType.BOOLEAN) throw new RuntimeException(leftVar + " is not of type boolean");

                    if(leftVar instanceof Constant) {
                        if(rightVar instanceof Constant) {
                            //Calculate constant expressions immediately
                            int newVal = op.applyAsInt(((Constant) leftVar).getVal(), ((Constant) rightVar).getVal());
                            System.out.println("Folded const expr '" + leftVar + ctx.op.getText() + rightVar + " -> " + newVal);
                            context.pushTempVariable(new Constant(newVal, VarType.BOOLEAN));
                            return;
                        }
                        else {
                            //Constants are only allowed on the right side
                            var tmp = rightVar;
                            rightVar = leftVar;
                            leftVar = tmp;
                        }
                    }
                    if(!leftVar.isBound()) {
                        leftVar.bind(context.getFreeSymbol());
                    }
                    if(!rightVar.isBound()) {
                        rightVar.bind(context.getFreeSymbol());
                    }

                    System.out.println(leftVar + ctx.op.getText() + rightVar);

                    var outSymbol = context.getFreeSymbol();

                    context.createBoundVariable(VarType.BOOLEAN, outSymbol);
                    var cmb = ArithmeticCombinator.withLeftRight(leftVar.toAccessor(context),  rightVar.toAccessor(context), outSymbol.ordinal(), op);

                    var connected = new ConnectedCombinator(cmb);
                    connected.setGreenIn(context.getCurrentNetworkGroup());
                    connected.setGreenOut(context.getCurrentNetworkGroup());
                    context.getExpressionContext().getCombinators().add(connected);
                }
                else if(ctx.negated != null) {
                    System.out.println("Exit " + ctx.getText());
                    var toNegate = context.popTempVariable();

                    if(toNegate.getType() != VarType.BOOLEAN) throw new RuntimeException(toNegate + " is not of type boolean");

                    if(toNegate instanceof Constant) {
                        //Calculate constant expressions immediately
                        int val = ((Constant) toNegate).getVal();
                        context.pushTempVariable(new Constant(val == 0 ? 1 : 0, VarType.BOOLEAN));
                        return;
                    }
                    if(!toNegate.isBound()) {
                        toNegate.bind(context.getFreeSymbol());
                    }

                    var outSymbol = context.getFreeSymbol();

                    context.createBoundVariable(VarType.BOOLEAN, outSymbol);
                    var cmb = DeciderCombinator.withLeftRight(toNegate.toAccessor(context),  Accessor.constant(0), Writer.constant(outSymbol.ordinal(), 1), DeciderCombinator.EQ);

                    var connected = new ConnectedCombinator(cmb);
                    connected.setGreenIn(context.getCurrentNetworkGroup());
                    connected.setGreenOut(context.getCurrentNetworkGroup());
                    context.getExpressionContext().getCombinators().add(connected);
                }
            }

            @Override
            public void exitExpr(LanguageParser.ExprContext ctx) {
                if(ctx.left != null) {
                    System.out.println("Exit " + ctx.getText());
                    var rightVar = context.popTempVariable();
                    var leftVar = context.popTempVariable();
                    var op = ArithmeticCombinator.getOperation(ctx.op.getText());
                    if(leftVar instanceof Constant) {
                        if(rightVar instanceof Constant) {
                            //Calculate constant expressions immediately
                            int newVal = op.applyAsInt(((Constant) leftVar).getVal(), ((Constant) rightVar).getVal());
                            System.out.println("Folded const expr '" + leftVar + ctx.op.getText() + rightVar + " -> " + newVal);
                            context.pushTempVariable(new Constant(newVal));
                            return;
                        }
                        else {
                            //Constants are only allowed on the right side
                            var tmp = rightVar;
                            rightVar = leftVar;
                            leftVar = tmp;
                        }
                    }

                    if(!leftVar.isBound()) {
                        leftVar.bind(context.getFreeSymbol());
                    }
                    if(!rightVar.isBound()) {
                        rightVar.bind(context.getFreeSymbol());
                    }

                    System.out.println(leftVar + ctx.op.getText() + rightVar);

                    var outSymbol = context.getFreeSymbol();

                    context.createBoundVariable(VarType.INT, outSymbol);
                    var cmb = ArithmeticCombinator.withLeftRight(leftVar.toAccessor(context),  rightVar.toAccessor(context), outSymbol.ordinal(), op);

                    var connected = new ConnectedCombinator(cmb);
                    connected.setGreenIn(context.getCurrentNetworkGroup());
                    connected.setGreenOut(context.getCurrentNetworkGroup());
                    context.getExpressionContext().getCombinators().add(connected);
                }
                else if(ctx.numberLit != null) {
                    int val = Integer.parseInt(ctx.getText());
                    context.pushTempVariable(new Constant(val));
                }
                else if(ctx.var != null) {
                    var named = context.getNamedVariable(ctx.var.getText());
                    if(named == null) throw new RuntimeException("Variable " + ctx.var.getText() + " is not defined");
                    context.pushTempVariable(named);
                    var accessor = named.createVariableAccessor();
                    accessors.add(accessor);
                    accessor.access().accept(context.getExpressionContext());
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
                if(preexisting) {
                    context.getConditionContext().registerAssignment(varName, named);
                }
                System.out.println("Creating named " + varName + " = " + named);
            }

            @Override
            public void enterBlock(LanguageParser.BlockContext ctx) {
                context.enterScope();
            }

            @Override
            public void exitBlock(LanguageParser.BlockContext ctx) {
                context.leaveScope();
            }
        });
        parser.block();
        Signal.SIGNAL_TYPES.set(FactorioSignal.values().length);

        System.out.println(generatedGroups);

        accessors.forEach(VariableAccessor::generateAccessors);

        var combinators = generatedGroups.stream()
                .map(CombinatorGroup::getCombinators)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var networks = generatedGroups.stream()
                .map(CombinatorGroup::getNetworks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        System.out.println(combinators);
        System.out.println(networks);

        System.out.println(Simulator.writeBlueprint(combinators, networks));
    }
}
