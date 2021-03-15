package me.joba.factorio.lang;

import me.joba.factorio.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private static final String TEST = "{ a = 1; b = a + 2; c = a + b; }";
    private static Context variableContext = new Context();
    private static int entityIdCounter = 0;

    public static void main(String[] args) throws IOException {
        InputStream stream = new ByteArrayInputStream(TEST.getBytes(StandardCharsets.UTF_8));
        LanguageLexer lexer = new LanguageLexer(CharStreams.fromString(TEST));
        LanguageParser parser = new LanguageParser(new CommonTokenStream(lexer));
        List<ConnectedCombinator> combinators = new ArrayList<>();
        List<NetworkGroup> networkGroups = new ArrayList<>();

        parser.addParseListener(new LanguageBaseListener() {

            @Override
            public void exitIfExpr(LanguageParser.IfExprContext ctx) {

            }

            @Override
            public void exitBoolExpr(LanguageParser.BoolExprContext ctx) {
            }

            @Override
            public void enterExpr(LanguageParser.ExprContext ctx) {
                System.out.println("Enter " + ctx.getText());
            }

            @Override
            public void enterStatement(LanguageParser.StatementContext ctx) {
                System.out.println("Enter statement");
            }

            @Override
            public void enterBlockStatement(LanguageParser.BlockStatementContext ctx) {

            }

            @Override
            public void exitCompleteExpression(LanguageParser.CompleteExpressionContext ctx) {
                networkGroups.add(variableContext.getCurrentNetworkGroup());
                variableContext.startNewContext();
            }

            @Override
            public void exitExpr(LanguageParser.ExprContext ctx) {
                System.out.println("Exit " + ctx.getText());
                if(ctx.left != null) {
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

                    System.out.println(leftVar + ctx.op.getText() + rightVar);

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

                    var connected = new ConnectedCombinator(entityIdCounter++, cmb);
                    connected.setGreenIn(variableContext.getCurrentNetworkGroup());
                    connected.setGreenOut(variableContext.getCurrentNetworkGroup());
                    combinators.add(connected);
                }
                else if(ctx.numberLit != null) {
                    int val = Integer.parseInt(ctx.getText());
                    variableContext.pushTempVariable(new Constant(val));
                }
                else if(ctx.var != null) {
                    variableContext.pushTempVariable(variableContext.getNamedVariable(ctx.var.getText()));
                }
//                else if(ctx

            }

            @Override
            public void exitAssignment(LanguageParser.AssignmentContext ctx) {
                var value = variableContext.popTempVariable();
                var named = variableContext.createNamedVariable(ctx.var.getText(), value.getType());

                if(!named.isBound()) {
                    named.bind(variableContext.getFreeSymbol(), variableContext.getCurrentNetworkGroup());
                }
                else {
                    named.bind(named.getSignal(), variableContext.getCurrentNetworkGroup());
                }
                ConnectedCombinator connected;
                if(value instanceof Constant) {
                    Combinator cmb = Combinator.constant(Signal.singleValue(named.getSignal().ordinal(), ((Constant) value).getVal()));
                    connected = new ConnectedCombinator(entityIdCounter++, cmb);
                    connected.setGreenOut(variableContext.getCurrentNetworkGroup());
                }
                else {
                    Combinator cmb = ArithmeticCombinator.withLeftRight(value.toAccessor(variableContext), Accessor.constant(0), named.getSignal().ordinal(), ArithmeticCombinator.ADD);
                    connected = new ConnectedCombinator(entityIdCounter++, cmb);
                    connected.setGreenIn(value.getOutputGroup());
                    connected.setGreenOut(variableContext.getCurrentNetworkGroup());
                }
                combinators.add(connected);
            }

            @Override
            public void enterBlock(LanguageParser.BlockContext ctx) {
                variableContext.enterScope();
            }

            @Override
            public void exitBlock(LanguageParser.BlockContext ctx) {
                networkGroups.add(variableContext.getCurrentNetworkGroup());
                variableContext.leaveScope();
            }
        });
        parser.block();
        Signal.SIGNAL_TYPES.set(FactorioSignal.values().length);

        System.out.println(combinators);
        System.out.println(networkGroups);

        System.out.println(Simulator.writeBlueprint(combinators, networkGroups));

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
