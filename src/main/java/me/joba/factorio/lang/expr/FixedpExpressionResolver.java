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

        var targetFixedp = symbols[0] instanceof Constant ? ((FixedpType)symbols[1].getType()).getFractionBits() : ((FixedpType)symbols[0].getType()).getFractionBits();
        var targetType = new FixedpType(targetFixedp);

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
        var tempSymbols = new FactorioSignal[]{
                FactorioSignal.SIGNAL_A,
                FactorioSignal.SIGNAL_B,
                FactorioSignal.SIGNAL_C,
                FactorioSignal.SIGNAL_D,
        };

        ArithmeticCombinator shiftA0 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[0].getSignal()[0]), CombinatorIn.constant(16), tempSymbols[0], ArithmeticOperator.RSH);
        ArithmeticCombinator shiftB0 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[1].getSignal()[0]), CombinatorIn.constant(16), tempSymbols[1], ArithmeticOperator.RSH);
        ArithmeticCombinator shiftA1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[0].getSignal()[0]), CombinatorIn.constant(0xFFFFFFFF >>> 16), tempSymbols[2], ArithmeticOperator.AND);
        ArithmeticCombinator shiftB1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[1].getSignal()[0]), CombinatorIn.constant(0xFFFFFFFF >>> 16), tempSymbols[3], ArithmeticOperator.AND);

        //This is incorrect if we want to support a * a
        shiftA0.setGreenIn(expressionGroup.getInput());
        shiftB0.setGreenIn(expressionGroup.getInput());
        shiftA1.setGreenIn(expressionGroup.getInput());
        shiftB1.setGreenIn(expressionGroup.getInput());

        expressionGroup.getCombinators().add(shiftA0);
        expressionGroup.getCombinators().add(shiftB0);
        expressionGroup.getCombinators().add(shiftA1);
        expressionGroup.getCombinators().add(shiftB1);

        var internalStage1 = new NetworkGroup();
        expressionGroup.getNetworks().add(internalStage1);

        shiftA0.setGreenOut(internalStage1);
        shiftA1.setGreenOut(internalStage1);
        shiftB0.setGreenOut(internalStage1);
        shiftB1.setGreenOut(internalStage1);

        ArithmeticCombinator mul1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.signal(tempSymbols[1]), tempSymbols[0], ArithmeticOperator.MUL);
        ArithmeticCombinator mul2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.signal(tempSymbols[3]), tempSymbols[1], ArithmeticOperator.MUL);
        ArithmeticCombinator mul3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[1]), CombinatorIn.signal(tempSymbols[2]), tempSymbols[2], ArithmeticOperator.MUL);
        ArithmeticCombinator mul4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[2]), CombinatorIn.signal(tempSymbols[3]), tempSymbols[1], ArithmeticOperator.MUL);
        expressionGroup.getCombinators().add(mul1);
        expressionGroup.getCombinators().add(mul2);
        expressionGroup.getCombinators().add(mul3);
        expressionGroup.getCombinators().add(mul4);

        mul1.setGreenIn(internalStage1);
        mul2.setGreenIn(internalStage1);
        mul3.setGreenIn(internalStage1);
        mul4.setGreenIn(internalStage1);

        var internalStage2_0 = new NetworkGroup();
        expressionGroup.getNetworks().add(internalStage2_0);
        mul1.setGreenOut(internalStage2_0);
        mul4.setGreenOut(internalStage2_0);

        var internalStage2_1 = new NetworkGroup();
        expressionGroup.getNetworks().add(internalStage2_1);
        mul2.setGreenOut(internalStage2_1);
        mul3.setGreenOut(internalStage2_1);

        int shiftConstant = outputType.getFractionBits() - t0.getFractionBits() - t1.getFractionBits();
        int lshift1 = 32 + shiftConstant;
        int lshift23 = 16 + shiftConstant;
        int lshift4 = shiftConstant;

        ArithmeticCombinator shift1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.constant(Math.abs(lshift1)), outSymbol, lshift1 > 0 ? ArithmeticOperator.LSH : ArithmeticOperator.RSH);
        ArithmeticCombinator shift23 = ArithmeticCombinator.withEachMerge(CombinatorIn.constant(Math.abs(lshift23)), outSymbol, lshift23 > 0 ? ArithmeticOperator.LSH : ArithmeticOperator.RSH);
        ArithmeticCombinator shift4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[3]), CombinatorIn.constant(Math.abs(lshift4)), outSymbol, lshift4 > 0 ? ArithmeticOperator.LSH : ArithmeticOperator.RSH);

        expressionGroup.getCombinators().add(shift1);
        expressionGroup.getCombinators().add(shift23);
        expressionGroup.getCombinators().add(shift4);

        shift1.setGreenIn(internalStage2_0);
        shift1.setGreenOut(expressionGroup.getOutput());
        shift4.setGreenIn(internalStage2_0);
        shift4.setGreenOut(expressionGroup.getOutput());
        shift23.setGreenIn(internalStage2_1);
        shift23.setGreenOut(expressionGroup.getOutput());

        return 3;
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
        String d1 = "13.43";
        String d2 = "-13.042";
        int manu = fixedpMulImpl(floatToFixedpInt(d1, 18), 18, floatToFixedpInt(d2, 12), 12, 21);
        System.out.println(manu);
        System.out.println(constToFloat(manu, 21));
        System.out.println("Expected: " + (Double.parseDouble(d1) * Double.parseDouble(d2)));
    }

    public static int fixedpMulImpl(int a, int aFractBits, int b, int bFractBits, int targetFractBits) {
        System.out.println("Multiplying " + a + "<" + aFractBits + "> * " + b + "<" + bFractBits + "> into ?<" + targetFractBits + ">");
        System.out.println("a: " + Integer.toBinaryString(a));
        System.out.println("b: " + Integer.toBinaryString(b));
        int a0 = a >> 16;
        int a1 = a & 0xFFFF;
        int b0 = b >> 16;
        int b1 = b & 0xFFFF;
        System.out.println("a0: " + Integer.toBinaryString(a0));
        System.out.println("b0: " + Integer.toBinaryString(b0));
        System.out.println("a1: " + Integer.toBinaryString(a1));
        System.out.println("b1: " + Integer.toBinaryString(b1));

        int shiftPartConstant = targetFractBits - aFractBits - bFractBits;
        int part1Shift =  32 + shiftPartConstant;
        int part23Shift = 16 + shiftPartConstant;
        int part4Shift =  shiftPartConstant;

        System.out.println("Shift a0*b0: " + part1Shift);
        System.out.println("Shift a1*b0/a0*b1: " + part23Shift);
        System.out.println("Shift a1*b1: " + part4Shift);

        int part0 = part1Shift > 0 ? (a0 * b0) << part1Shift :  (a0 * b0) >>> -part1Shift;
        int part1 = part23Shift > 0 ? (a0 * b1) << part23Shift : (a0 * b1) >>> -part23Shift;
        int part2 = part23Shift > 0 ? (a1 * b0) << part23Shift : (a1 * b0) >>> -part23Shift;
        int part3 = part4Shift > 0 ? (a1 * b1) << part4Shift :  (a1 * b1) >>> -part4Shift;

        System.out.println("a0*b0: " + part0 + " (" + Integer.toBinaryString(part0) + ")");
        System.out.println("a0*b1: " + part1 + " (" + Integer.toBinaryString(part1) + ")");
        System.out.println("a1*b0: " + part2 + " (" + Integer.toBinaryString(part2) + ")");
        System.out.println("a1*b1: " + part3 + " (" + Integer.toBinaryString(part3) + ")");

        int result = part0 + part1 + part2 + part3;
        System.out.println("Result: " + result + " (" + Integer.toBinaryString(result) + ")");
        return result;
    }

    //Something like this???
    public static int fixedp16div(int a, int b) {
        int a0 = a << 16;
        int a1 = (a & 0x000F0000) << 11;
        int a2 = (a & 0x00F00000) << 7;
        int a3 = (a & 0x0F000000) << 3;
        int a4 = (a & 0xF0000000);

        return  (a0 / b) +
                (a1 / (b >> 5)) +
                (a2 / (b >> 9)) +
                (a3 / (b >> 13)) +
                (a4 / (b >> 16));
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
            return longFromConst(new Constant(PrimitiveType.UNASSIGNED_FIXEDP, v >> fpt.getFractionBits(), v << (Integer.SIZE - fpt.getFractionBits())));
        }
        return  ((long) c.getVal()[0] << Integer.SIZE) | (c.getVal()[1] & 0xffffffffL);
    }

    private static Constant longToConst(long val) {
        return new Constant(PrimitiveType.UNASSIGNED_FIXEDP, (int)(val >> Integer.SIZE), (int)val);
    }
}
