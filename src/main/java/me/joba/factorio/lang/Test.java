package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

    private static final String TEST = "{ a = 1; b = a + 2; c = a + b; b = b / 2; d = a + b + c; }";
    private static Context variableContext = new Context();

    public static void main(String[] args) throws IOException {
        InputStream stream = new ByteArrayInputStream(TEST.getBytes(StandardCharsets.UTF_8));
        LanguageLexer lexer = new LanguageLexer(CharStreams.fromString(TEST));
        LanguageParser parser = new LanguageParser(new CommonTokenStream(lexer));
        List<CombinatorGroup> generatedGroups = new ArrayList<>();
//        List<ConnectedCombinator> combinators = new ArrayList<>();
//        List<NetworkGroup> networkGroups = new ArrayList<>();

        parser.addParseListener(new LanguageBaseListener() {

            @Override
            public void exitIfExpr(LanguageParser.IfExprContext ctx) {

            }

            @Override
            public void exitBoolExpr(LanguageParser.BoolExprContext ctx) {
            }

            @Override
            public void enterExpr(LanguageParser.ExprContext ctx) {

            }

            @Override
            public void enterStatement(LanguageParser.StatementContext ctx) {
                System.out.println("Enter statement");
            }

            @Override
            public void enterBlockStatement(LanguageParser.BlockStatementContext ctx) {

            }

            @Override
            public void enterCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
                variableContext.startExpressionContext();
            }

            @Override
            public void exitCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
                generatedGroups.add(variableContext.getExpressionContext());
                variableContext.getExpressionContext().setCorrespondingCode(ctx.getText());
            }

            @Override
            public void exitExpr(LanguageParser.ExprContext ctx) {
                if(ctx.left != null) {
                    System.out.println("Exit " + ctx.getText());
                    var rightVar = variableContext.popTempVariable();
                    var leftVar = variableContext.popTempVariable();
                    var op = switch (ctx.op.getText()) {
                        case "+" -> ArithmeticCombinator.ADD;
                        case "-" -> ArithmeticCombinator.SUB;
                        case "*" -> ArithmeticCombinator.MUL;
                        case "/" -> ArithmeticCombinator.DIV;
                        default -> throw new UnsupportedOperationException("Unknown op " + ctx.op.getText());
                    };
                    if(leftVar instanceof Constant) {
                        if(rightVar instanceof Constant) {
                            //Calculate constant expressions immediately
                            int newVal = op.applyAsInt(((Constant) leftVar).getVal(), ((Constant) rightVar).getVal());
                            System.out.println("Folded const expr '" + leftVar + ctx.op.getText() + rightVar + " -> " + newVal);
                            variableContext.pushTempVariable(new Constant(newVal));
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
                        leftVar.bind(variableContext.getFreeSymbol(), variableContext.getCurrentNetworkGroup());
                    }
                    if(!rightVar.isBound()) {
                        rightVar.bind(variableContext.getFreeSymbol(), variableContext.getCurrentNetworkGroup());
                    }

                    System.out.println(leftVar + ctx.op.getText() + rightVar);

                    var outSymbol = variableContext.getFreeSymbol();

                    variableContext.createBoundVariable(VarType.INT, outSymbol);
                    var cmb = ArithmeticCombinator.withLeftRight(leftVar.toAccessor(variableContext),  rightVar.toAccessor(variableContext), outSymbol.ordinal(), op);

                    var connected = new ConnectedCombinator(cmb);
                    connected.setGreenIn(variableContext.getCurrentNetworkGroup());
                    connected.setGreenOut(variableContext.getCurrentNetworkGroup());
                    variableContext.getExpressionContext().getCombinators().add(connected);
                }
                else if(ctx.numberLit != null) {
                    int val = Integer.parseInt(ctx.getText());
                    variableContext.pushTempVariable(new Constant(val));
                }
                else if(ctx.var != null) {
                    var named = variableContext.getNamedVariable(ctx.var.getText());
                    if(named == null) throw new RuntimeException("Variable " + ctx.var.getText() + " is not defined");
                    variableContext.pushTempVariable(named);
                    var accessor = named.createVariableAccessor();
                    accessor.setGreenOut(variableContext.getCurrentNetworkGroup());
                    variableContext.getExpressionContext().getCombinators().add(accessor);
                }
            }

            @Override
            public void exitAssignment(LanguageParser.AssignmentContext ctx) {
                var value = variableContext.popTempVariable();

                FactorioSignal variableSymbol;
                if(variableContext.getNamedVariable(ctx.var.getText()) != null) {
                    variableSymbol = variableContext.getNamedVariable(ctx.var.getText()).getSignal();
                }
                else {
                    variableSymbol = variableContext.getFreeSymbol();
                }

                ConnectedCombinator connected;
                if(value instanceof Constant) {
                    Combinator cmb = Combinator.constant(Signal.singleValue(variableSymbol.ordinal(), ((Constant) value).getVal()));
                    connected = new ConnectedCombinator(cmb);
                    connected.setGreenOut(variableContext.getExpressionContext().getOutput());
                    variableContext.getExpressionContext().getCombinators().add(connected);
                }
                else {
                    Combinator cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(value.getSignal().ordinal()), Accessor.constant(0), variableSymbol.ordinal(), ArithmeticCombinator.ADD);
                    connected = new ConnectedCombinator(cmb);
                    connected.setGreenOut(variableContext.getExpressionContext().getOutput());
                    connected.setGreenIn(variableContext.getCurrentNetworkGroup());
                    variableContext.getExpressionContext().getCombinators().add(connected);
                }
                var named= variableContext.createNamedVariable(ctx.var.getText(), value.getType(), variableSymbol, variableContext.getExpressionContext());

                System.out.println("Creating named " + ctx.var.getText() + " = " + named);
            }

            @Override
            public void enterBlock(LanguageParser.BlockContext ctx) {
                variableContext.enterScope();
            }

            @Override
            public void exitBlock(LanguageParser.BlockContext ctx) {
                variableContext.leaveScope();
            }
        });
        parser.block();
        Signal.SIGNAL_TYPES.set(FactorioSignal.values().length);

        System.out.println(generatedGroups);

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

//        while(true) {
//            networkGroups.forEach(NetworkGroup::aggregateInputs);
//            combinators.forEach(ConnectedCombinator::tick);
//            networkGroups.forEach(ng -> System.out.println(Simulator.toString(ng.getState())));
//        }
    }


//    public static void generateExpr(LanguageParser.ExprContext expr) {
//        expr.
//    }
}
