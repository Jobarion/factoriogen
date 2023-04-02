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

        var targetType = new FixedpType(getOptimalFractBits(symbols, op));

        var outputGroup = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        context.getFunctionGroup().getSubGroups().add(outputGroup);

        int maxInputDelay = prepareSymbols(symbols, targetType, op, outputGroup, context);
        var outSymbol = context.getFreeSymbols(targetType.getSize());

        var bound = context.createBoundTempVariable(targetType, outSymbol, outputGroup);
        int propagationDelay;
        if(op == ArithmeticOperator.MUL) {
            propagationDelay = generateMulCombinators(symbols, outSymbol[0], targetType, outputGroup);
        }
        else {
            propagationDelay = generateCombinators(symbols, op, outSymbol, outputGroup, context);
        }
        bound.setDelay(maxInputDelay + propagationDelay);

        log(context, Arrays.toString(symbols) + " (using " + op + ") = " + bound + ", with delay " + bound.getTickDelay());
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

    @Override
    public int generateCombinators(Symbol[] symbols, ArithmeticOperator operator, FactorioSignal[] outSymbol, CombinatorGroup group, FunctionContext context) {
        //All required operands shifts are already done based on the operandRightShifts function
        var connected = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor()[0],  symbols[1].toAccessor()[0], outSymbol[0], operator);
        connected.setGreenIn(group.getInput());
        connected.setGreenOut(group.getOutput());
        group.getCombinators().add(connected);
        return 1;
    }

    public int generateMulCombinators(Symbol[] symbols, FactorioSignal outSymbol, FixedpType outputType, CombinatorGroup expressionGroup) {
        if(symbols[0] == symbols[1]) {
            //a*a requires fewer combinators
        }
        var t0 = (FixedpType)symbols[0].getType();
        var t1 = (FixedpType)symbols[1].getType();
        boolean preAdjustFractPart = t0.getFractionBits() != t1.getFractionBits();
        //These symbols never leak, so we might as well hardcode something that's easier for debugging.
        //We could also use fewer signals here (2 are enough), but 4 form a larger NetworkGroup and are maybe easier for combinator placement?
        var tempSymbols = new FactorioSignal[]{
                FactorioSignal.SIGNAL_A,
                FactorioSignal.SIGNAL_B,
                FactorioSignal.SIGNAL_C,
                FactorioSignal.SIGNAL_D,
        };

        ArithmeticCombinator shiftA0 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[0].getSignal()[0]), CombinatorIn.constant(t0.getFractionBits()), tempSymbols[0], ArithmeticOperator.RSH);
        ArithmeticCombinator shiftB0 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[1].getSignal()[0]), CombinatorIn.constant(t1.getFractionBits()), tempSymbols[1], ArithmeticOperator.RSH);
        ArithmeticCombinator shiftA1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[0].getSignal()[0]), CombinatorIn.constant(0xFFFFFFFF >>> (32 - t0.getFractionBits())), tempSymbols[2], ArithmeticOperator.AND);
        ArithmeticCombinator shiftB1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[1].getSignal()[0]), CombinatorIn.constant(0xFFFFFFFF >>> (32 - t1.getFractionBits())), tempSymbols[3], ArithmeticOperator.AND);

        shiftA0.setGreenIn(expressionGroup.getInput());
        shiftB0.setGreenIn(expressionGroup.getInput());
        shiftA1.setGreenIn(expressionGroup.getInput());
        shiftB1.setGreenIn(expressionGroup.getInput());

        //This is incorrect if we want to support a * a
        expressionGroup.getCombinators().add(shiftA0);
        expressionGroup.getCombinators().add(shiftB0);
        expressionGroup.getCombinators().add(shiftA1);
        expressionGroup.getCombinators().add(shiftB1);

        var internalStage1 = new NetworkGroup();
        expressionGroup.getNetworks().add(internalStage1);

        //There are two different paths here.
        //If a and b have different fract part sizes, we need to make them match. In this case, we adjust their fract parts
        if(!preAdjustFractPart) {
            shiftA0.setGreenOut(internalStage1);
            shiftA1.setGreenOut(internalStage1);
            shiftB0.setGreenOut(internalStage1);
            shiftB1.setGreenOut(internalStage1);
        }
        else {
            ArithmeticCombinator adjustA1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[2]), CombinatorIn.constant(Math.abs(outputType.getFractionBits()) - t0.getFractionBits()), tempSymbols[2], outputType.getFractionBits() > t0.getFractionBits() ? ArithmeticOperator.RSH : ArithmeticOperator.LSH);
            ArithmeticCombinator adjustB1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[3]), CombinatorIn.constant(Math.abs(outputType.getFractionBits()) - t1.getFractionBits()), tempSymbols[3], outputType.getFractionBits() > t1.getFractionBits() ? ArithmeticOperator.RSH : ArithmeticOperator.LSH);
            ArithmeticCombinator copy = ArithmeticCombinator.copying();
            expressionGroup.getCombinators().add(adjustA1);
            expressionGroup.getCombinators().add(adjustB1);
            expressionGroup.getCombinators().add(copy);

            var copyNetwork = new NetworkGroup();
            expressionGroup.getNetworks().add(copyNetwork);
            shiftA0.setGreenOut(copyNetwork);
            shiftB0.setGreenOut(copyNetwork);
            copy.setGreenIn(copyNetwork);
            copy.setGreenOut(internalStage1);
            var adjustGroup = new NetworkGroup();
            expressionGroup.getNetworks().add(adjustGroup);
            shiftA1.setGreenOut(adjustGroup);
            shiftB1.setGreenOut(adjustGroup);
            adjustA1.setGreenIn(adjustGroup);
            adjustB1.setGreenIn(adjustGroup);
            adjustA1.setGreenOut(internalStage1);
            adjustB1.setGreenOut(internalStage1);
        }

        ArithmeticCombinator mul1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.signal(tempSymbols[1]), tempSymbols[0], ArithmeticOperator.MUL);
        ArithmeticCombinator mul2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.signal(tempSymbols[3]), outSymbol, ArithmeticOperator.MUL);
        ArithmeticCombinator mul3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[1]), CombinatorIn.signal(tempSymbols[2]), outSymbol, ArithmeticOperator.MUL);
        ArithmeticCombinator mul4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[2]), CombinatorIn.signal(tempSymbols[3]), tempSymbols[1], ArithmeticOperator.MUL);
        expressionGroup.getCombinators().add(mul1);
        expressionGroup.getCombinators().add(mul2);
        expressionGroup.getCombinators().add(mul3);
        expressionGroup.getCombinators().add(mul4);

        mul1.setGreenIn(internalStage1);
        mul2.setGreenIn(internalStage1);
        mul3.setGreenIn(internalStage1);
        mul4.setGreenIn(internalStage1);

        var internalStage2 = new NetworkGroup();
        expressionGroup.getNetworks().add(internalStage2);
        mul1.setGreenOut(internalStage2);
        mul2.setGreenOut(internalStage2);
        mul3.setGreenOut(internalStage2);
        mul4.setGreenOut(internalStage2);

        ArithmeticCombinator shiftA = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.constant(outputType.getFractionBits()), outSymbol, ArithmeticOperator.LSH);
        ArithmeticCombinator shiftB = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[1]), CombinatorIn.constant(outputType.getFractionBits() - (outputType.getFractionBits() - t0.getFractionBits())), outSymbol, ArithmeticOperator.RSH);
        expressionGroup.getCombinators().add(shiftA);
        expressionGroup.getCombinators().add(shiftB);

        shiftA.setGreenIn(internalStage2);
        shiftA.setGreenOut(expressionGroup.getOutput());
        shiftB.setGreenIn(internalStage2);
        shiftB.setGreenOut(expressionGroup.getOutput());

        //t0 and t1 are equal
        if(!preAdjustFractPart && t0.getFractionBits() != outputType.getFractionBits()) {
            int difference = (outputType.getFractionBits() - t0.getFractionBits());
            ArithmeticCombinator shiftRest = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(outSymbol), CombinatorIn.constant(Math.abs(difference)), outSymbol, difference < 0 ? ArithmeticOperator.RSH : ArithmeticOperator.LSH);
            expressionGroup.getCombinators().add(shiftRest);
            shiftRest.setGreenIn(internalStage2);
            shiftRest.setGreenOut(expressionGroup.getOutput());
        }
        else {
            ArithmeticCombinator copy = ArithmeticCombinator.copying(outSymbol);
            expressionGroup.getCombinators().add(copy);
            copy.setGreenIn(internalStage2);
            copy.setGreenOut(expressionGroup.getOutput());
        }

        return preAdjustFractPart ? 4 : 3;
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
        else {
            throw new IllegalArgumentException("Unsupported fixedp operator " + operator);
        }
        return optimalFractBits;
    }

    private static int prepareSymbols(Symbol[] symbols, FixedpType outputType, ArithmeticOperator operator, CombinatorGroup outputContext, FunctionContext context) {
        int maxInputDelay = -1;
        for(int i = 0; i < symbols.length; i++) {
            Symbol s = symbols[i];
            if(s instanceof Variable && ((FixedpType)s.getType()).getFractionBits() != outputType.getFractionBits()) {
                maxInputDelay = Math.max(maxInputDelay, s.getTickDelay() + 1);
            }
            else {
                maxInputDelay = Math.max(maxInputDelay, s.getTickDelay());
            }
            if(s instanceof Variable && !s.isBound()) {
                s.bind(context.getFreeSymbol());
            }
        }

        CombinatorGroup shiftGroup = new CombinatorGroup(new NetworkGroup(), outputContext.getInput());

        context.getFunctionGroup().getSubGroups().add(outputContext);
        boolean isShifted = false;
        for(int i = 0; i < symbols.length; i++) {
            if(symbols[i] instanceof Constant c) {
                symbols[i] = unassignedConstToFixedpIntConst(c, outputType.getFractionBits());
            }
            if(symbols[i] instanceof Variable v) {
                var accessor = v.createVariableAccessor();
                var fractBits = ((FixedpType)v.getType()).getFractionBits();
                var targetFractBits = outputType.getFractionBits();
                //MUL and DIV handle this differently
                if(operator == ArithmeticOperator.MUL || operator == ArithmeticOperator.DIV || fractBits == targetFractBits) {
                    accessor.access(maxInputDelay).accept(outputContext);
                }
                else {
                    isShifted = true;
                    accessor.access(maxInputDelay - 1).accept(shiftGroup);
                    var shifted = context.createBoundTempVariable(new FixedpType(targetFractBits), v.getSignal(), outputContext);
                    shifted.setDelay(maxInputDelay);
                    context.popTempVariable();
                    var shiftCombinator = ArithmeticCombinator.withLeftRight(v.toAccessor()[0], CombinatorIn.constant(Math.abs(fractBits - targetFractBits)), symbols[i].getSignal()[0], fractBits > targetFractBits ? ArithmeticOperator.RSH : ArithmeticOperator.LSH);
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

    public static void main(String[] args) {
        System.out.println(constToFloat(11075584, 16));
        System.out.println(fixedp16mul(floatToFixedpInt("13.43", 16), floatToFixedpInt("13.042", 16)));
        System.out.println(fixedpmul(floatToFixedpInt("13.43", 20), 20, floatToFixedpInt("13.042", 22), 22, 16));
        System.out.println(constToFloat(new Constant(new FixedpType(16), fixedp16mul(floatToFixedpInt("13.43", 16), floatToFixedpInt("13.042", 16)))));
        System.out.println(constToFloat(new Constant(new FixedpType(16), fixedpmul(floatToFixedpInt("13.43", 20), 20, floatToFixedpInt("13.042", 21), 21, 16))));

        System.out.println(floatToFixedpInt("13.43", 20));
        System.out.println(floatToFixedpInt("13.042", 22));
    }

    public static int fixedp16mul(int a, int b) {
        int a0 = a >> 16;
        int a1 = a & 0xFFFF;
        int b0 = b >> 16;
        int b1 = b & 0xFFFF;
        return (a0 * a0 << 16) + a0 * b1 + a1 * b0 + (a1 * b1 >>> 16);
    }

    public static int fixedpmul(int a, int aFractBits, int b, int bFractBits, int targetFractBits) {
        int a0 = a >> aFractBits;
        int a1 = a & (0xFFFFFFFF >>> (32 - aFractBits));
        int b0 = b >> bFractBits;
        int b1 = b & (0xFFFFFFFF >>> (32 - bFractBits));
        if(aFractBits < targetFractBits) {
            a1 <<= targetFractBits - aFractBits;
        }
        else {
            a1 >>>= aFractBits - targetFractBits;
        }
        if(bFractBits < targetFractBits) {
            b1 <<= targetFractBits - bFractBits;
        }
        else {
            b1 >>>= bFractBits - targetFractBits;
        }
        return (a0 * a0 << targetFractBits) + a0 * b1 + a1 * b0 + (a1 * b1 >>> targetFractBits);
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
