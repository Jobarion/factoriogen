package me.joba.factorio.lang.expr;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.CombinatorIn;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.entities.ArithmeticCombinator;
import me.joba.factorio.game.entities.ArithmeticOperator;
import me.joba.factorio.lang.*;
import me.joba.factorio.lang.types.FixedpType;
import me.joba.factorio.lang.types.PrimitiveType;

import java.util.Arrays;
import java.util.Optional;

public class FixedpExpressionResolver extends ExpressionResolver<LanguageParser.ExprContext, ArithmeticOperator> {

    public FixedpExpressionResolver() {
        super(null);//Doesn't make sense in this context, could be either PrimitiveType.UNASSIGNED_FIXEDP or FixedpType with precision
    }

    @Override
    public void parse(FunctionContext context, LanguageParser.ExprContext ruleContext) {
        var op = getOperation(ruleContext);
        Symbol[] symbols = new Symbol[2];
        boolean isConstant = true;
        for(int i = 0; i < symbols.length; i++) {
            var tmp = context.popTempVariable();
            if(tmp.getType() != PrimitiveType.UNASSIGNED_FIXEDP && !(tmp.getType() instanceof FixedpType)) throw new RuntimeException(tmp + " is not a fixedp type");
            symbols[symbols.length - 1 - i] = tmp;
            isConstant = isConstant && tmp instanceof Constant;
        }
        if(isConstant) {
            Constant[] consts = new Constant[symbols.length];
            System.arraycopy(symbols, 0, consts, 0, consts.length);
            var computedConstantOpt = computeConstExpr(consts, op);
            if(computedConstantOpt.isPresent()) {
                var computedConstant = computedConstantOpt.get();
                log(context, "Collapsed constants " + Arrays.toString(symbols) + " (using " + op + ") = " + computedConstant);
                context.pushTempVariable(computedConstant);
                return;
            }
        }

        int optimalFractBits = getOptimalFractBits(symbols, op);
        var targetType = new FixedpType(optimalFractBits);

        var outputContext = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        context.getFunctionGroup().getSubGroups().add(outputContext);

        int maxInputDelay = prepareSymbols(symbols, targetType, op, outputContext, context);
        var outSymbol = context.getFreeSignals(targetType.getSize());

        var bound = context.createBoundTempVariable(targetType, outSymbol, outputContext);
        int propagationDelay = generateCombinators(symbols, op, outSymbol, outputContext, context);
        bound.setDelay(maxInputDelay + propagationDelay);

        log(context, Arrays.toString(symbols) + " (using " + op + ") = " + bound + ", with delay " + bound.getTickDelay());
    }

    public static void main(String[] args) {
        System.out.println(constToFloat(new Constant(new FixedpType(16), 205863)));
        System.out.println(floatToFixedpInt("-14.0", 16));
        System.out.println(floatToFixedpInt("14.0", 16));
        System.out.println((floatToFixedpInt("-14.0", 16) >> 8) * (floatToFixedpInt("14.0", 16) >> 8));
        System.out.println(constToFloat((floatToFixedpInt("-13.0", 16) << 8) / (floatToFixedpInt("-2.0", 16) >> 8), 16));

    }

    private static String constToFloat(Constant c) {
        var c2 = longToConst(longFromConst(c));
        return c2.getVal()[0] + "." + String.valueOf((double)Integer.toUnsignedLong(c2.getVal()[1]) / (double)0xFFFFFFFFL).substring(2);
    }

    private static String constToFloat(int c, int fractionBits) {
        return constToFloat(new Constant(new FixedpType(fractionBits), c));
    }

    private static int floatToFixedpInt(String f, int fractBits) {
        var parts = f.split("\\.");
        int realPart = Integer.parseInt(parts[0]);
        int fractPart = (int)((long)(Double.parseDouble("0." + parts[1]) * 0xFFFFFFFFL));
        return unassignedConstToFixedpIntConst(new Constant(PrimitiveType.UNASSIGNED_FIXEDP, realPart, fractPart), fractBits).getVal()[0];
    }

    @Override
    public ArithmeticOperator getOperation(LanguageParser.ExprContext ruleContext) {
        return ArithmeticOperator.getOperator(ruleContext.op.getText());
    }

    @Override
    public Optional<Constant> computeConstExpr(Constant[] constants, ArithmeticOperator operation) {
        var c0 = longFromConst(constants[0]);
        var c1 = longFromConst(constants[1]);

        long result = switch (operation) {
            case MUL -> (c0 >> 16) * (c1 >> 16);
            case DIV -> (c0 << 16) / (c1 >> 16);
            case ADD -> c0 + c1;
            case SUB -> c0 - c1;
            case AND -> c0 & c1;
            case OR ->  c0 | c1;
            case XOR -> c0 ^ c1;
            default -> throw new UnsupportedOperationException(operation.getCodeSymbol() + " operator is not implemented for fixedp values");
        };
        return Optional.of(longToConst(result));
    }

    public static int[] operandRightShifts(int optimalFractionalBits, ArithmeticOperator operator) {
        if (operator == ArithmeticOperator.ADD || operator == ArithmeticOperator.SUB || operator == ArithmeticOperator.AND || operator == ArithmeticOperator.OR || operator == ArithmeticOperator.XOR) {
            return new int[]{0, 0};
        }
        else if(operator == ArithmeticOperator.MUL) {
            if (optimalFractionalBits % 2 == 0) {
                return new int[]{optimalFractionalBits / 2, optimalFractionalBits / 2};
            } else {
                return new int[]{optimalFractionalBits / 2, optimalFractionalBits / 2 + 1};
            }
        }
        else if(operator == ArithmeticOperator.DIV) {
            return new int[]{-optimalFractionalBits / 2, optimalFractionalBits / 2};
        }
        else {
            return new int[]{0, 0};
        }
    }

    @Override
    public int generateCombinators(Symbol[] symbols, ArithmeticOperator operator, FactorioSignal[] outSymbol, CombinatorGroup group, FunctionContext context) {
        //All required operands shifts are already done based on the operandRightShifts function
        var connected = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor()[0],  symbols[1].toAccessor()[0], outSymbol[0], operator);
        connected.setGreenIn(group.getInput());
        connected.setGreenOut(group.getOutput());
        group.getCombinators().add(connected);
        return 1;
    }

    private static int getOptimalFractBits(Symbol[] symbols, ArithmeticOperator operator) {
        int optimalFractBits = -1;
        if(operator == ArithmeticOperator.ADD || operator == ArithmeticOperator.SUB) {
            optimalFractBits = 32;
            for(Symbol s : symbols) {
                if(s.getType() instanceof FixedpType fpt) {
                    optimalFractBits = Math.min(optimalFractBits, fpt.getFractionBits());
                }
            }
        }
        else if(operator == ArithmeticOperator.AND || operator == ArithmeticOperator.OR || operator == ArithmeticOperator.XOR || operator == ArithmeticOperator.MUL || operator == ArithmeticOperator.DIV) {
            optimalFractBits = 0;
            int varCount = 0;
            for(Symbol s : symbols) {
                if(s.getType() instanceof FixedpType fpt) {
                    varCount++;
                    optimalFractBits += fpt.getFractionBits();
                }
            }
            optimalFractBits /= varCount;
        }
        return optimalFractBits;
    }

    private static int prepareSymbols(Symbol[] symbols, FixedpType outputType, ArithmeticOperator operator, CombinatorGroup outputContext, FunctionContext context) {
        int[] operationRequiredRightShifts = operandRightShifts(outputType.getFractionBits(), operator);
        int maxInputDelay = -1;
        for(int i = 0; i < symbols.length; i++) {
            Symbol s = symbols[i];
            if(s instanceof Variable && ((FixedpType)s.getType()).getFractionBits() != outputType.getFractionBits() - operationRequiredRightShifts[i]) {
                maxInputDelay = Math.max(maxInputDelay, s.getTickDelay() + 1);
            }
            else {
                maxInputDelay = Math.max(maxInputDelay, s.getTickDelay());
            }
            if(s instanceof Variable && !s.isBound()) {
                s.bind(context.getFreeSignal());
            }
        }

        CombinatorGroup shiftGroup = new CombinatorGroup(new NetworkGroup(), outputContext.getInput());

        context.getFunctionGroup().getSubGroups().add(outputContext);
        boolean isShifted = false;
        for(int i = 0; i < symbols.length; i++) {
            var targetFractBits = outputType.getFractionBits() - operationRequiredRightShifts[i];
            if(symbols[i] instanceof Constant c) {
                symbols[i] = unassignedConstToFixedpIntConst(c, targetFractBits);
            }
            if(symbols[i] instanceof Variable v) {
                var accessor = v.createVariableAccessor();
                var fractBits = ((FixedpType)v.getType()).getFractionBits();
                if(fractBits == targetFractBits) {
                    accessor.access(maxInputDelay).accept(outputContext);
                }
                else {
                    isShifted = true;
                    accessor.access(maxInputDelay - 1).accept(shiftGroup);
                    var shifted = context.createBoundTempVariable(new FixedpType(targetFractBits), context.getFreeSignals(1), outputContext);
                    context.popTempVariable();
                    shifted.setDelay(maxInputDelay);
                    var shiftCombinator = ArithmeticCombinator.withLeftRight(v.toAccessor()[0], CombinatorIn.constant(Math.abs(fractBits - targetFractBits)), shifted.getSignal()[0], fractBits > targetFractBits ? ArithmeticOperator.RSH : ArithmeticOperator.LSH);
                    shiftCombinator.setGreenIn(shiftGroup.getInput());
                    shiftCombinator.setGreenOut(shiftGroup.getOutput());
                    shiftGroup.getCombinators().add(shiftCombinator);
                    symbols[i] = shifted;
                }
                outputContext.getAccessors().add(accessor);
            }
        }
        if(isShifted) {
            outputContext.getSubGroups().add(shiftGroup);
        }
        return maxInputDelay;
    }

    public static Constant unassignedConstToFixedpIntConst(Constant c, int fractBits) {
        int val = c.getVal()[0] << fractBits | (c.getVal()[1] >>> (Integer.SIZE - fractBits));
        return new Constant(new FixedpType(fractBits), val);
    }

    private static long longFromConst(Constant c) {
        if(c.getType() instanceof FixedpType fpt) {
            int v = c.getVal()[0];
            return longFromConst(new Constant(PrimitiveType.UNASSIGNED_FIXEDP, v >>> fpt.getFractionBits(), v << (Integer.SIZE - fpt.getFractionBits())));
        }
        return  ((long) c.getVal()[0] << Integer.SIZE) | (c.getVal()[1] & 0xffffffffL);
    }

    private static Constant longToConst(long val) {
        return new Constant(PrimitiveType.UNASSIGNED_FIXEDP, (int)(val >> Integer.SIZE), (int)val);
    }
}
