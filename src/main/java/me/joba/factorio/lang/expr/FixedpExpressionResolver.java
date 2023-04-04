package me.joba.factorio.lang.expr;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.CombinatorIn;
import me.joba.factorio.CombinatorOut;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.entities.*;
import me.joba.factorio.lang.*;
import me.joba.factorio.lang.types.FixedpType;
import me.joba.factorio.lang.types.PrimitiveType;

import java.util.*;
import java.util.stream.Stream;

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

//    public int generateMulCombinators(Symbol[] symbols, FactorioSignal outSymbol, FixedpType outputType, CombinatorGroup expressionGroup) {
//        if(symbols[0] == symbols[1]) {
//            //a*a requires fewer combinators
//        }
//        var t0 = (FixedpType)symbols[0].getType();
//        var t1 = (FixedpType)symbols[1].getType();
//        var tempSymbols = new FactorioSignal[]{
//                FactorioSignal.SIGNAL_A,
//                FactorioSignal.SIGNAL_B,
//                FactorioSignal.SIGNAL_C,
//                FactorioSignal.SIGNAL_D,
//        };
//
//        ArithmeticCombinator shiftA0 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[0].getSignal()[0]), CombinatorIn.constant(16), tempSymbols[0], ArithmeticOperator.RSH);
//        ArithmeticCombinator shiftB0 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[1].getSignal()[0]), CombinatorIn.constant(16), tempSymbols[1], ArithmeticOperator.RSH);
//        ArithmeticCombinator shiftA1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[0].getSignal()[0]), CombinatorIn.constant(0xFFFFFFFF >>> 16), tempSymbols[2], ArithmeticOperator.AND);
//        ArithmeticCombinator shiftB1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(symbols[1].getSignal()[0]), CombinatorIn.constant(0xFFFFFFFF >>> 16), tempSymbols[3], ArithmeticOperator.AND);
//
//        //This is incorrect if we want to support a * a
//        shiftA0.setGreenIn(expressionGroup.getInput());
//        shiftB0.setGreenIn(expressionGroup.getInput());
//        shiftA1.setGreenIn(expressionGroup.getInput());
//        shiftB1.setGreenIn(expressionGroup.getInput());
//
//        expressionGroup.getCombinators().add(shiftA0);
//        expressionGroup.getCombinators().add(shiftB0);
//        expressionGroup.getCombinators().add(shiftA1);
//        expressionGroup.getCombinators().add(shiftB1);
//
//        var internalStage1 = new NetworkGroup();
//        expressionGroup.getNetworks().add(internalStage1);
//
//        shiftA0.setGreenOut(internalStage1);
//        shiftA1.setGreenOut(internalStage1);
//        shiftB0.setGreenOut(internalStage1);
//        shiftB1.setGreenOut(internalStage1);
//
//        ArithmeticCombinator mul1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.signal(tempSymbols[1]), tempSymbols[0], ArithmeticOperator.MUL);
//        ArithmeticCombinator mul2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.signal(tempSymbols[3]), tempSymbols[1], ArithmeticOperator.MUL);
//        ArithmeticCombinator mul3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[1]), CombinatorIn.signal(tempSymbols[2]), tempSymbols[2], ArithmeticOperator.MUL);
//        ArithmeticCombinator mul4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[2]), CombinatorIn.signal(tempSymbols[3]), tempSymbols[1], ArithmeticOperator.MUL);
//        expressionGroup.getCombinators().add(mul1);
//        expressionGroup.getCombinators().add(mul2);
//        expressionGroup.getCombinators().add(mul3);
//        expressionGroup.getCombinators().add(mul4);
//
//        mul1.setGreenIn(internalStage1);
//        mul2.setGreenIn(internalStage1);
//        mul3.setGreenIn(internalStage1);
//        mul4.setGreenIn(internalStage1);
//
//        var internalStage2_0 = new NetworkGroup();
//        expressionGroup.getNetworks().add(internalStage2_0);
//        mul1.setGreenOut(internalStage2_0);
//        mul4.setGreenOut(internalStage2_0);
//
//        var internalStage2_1 = new NetworkGroup();
//        expressionGroup.getNetworks().add(internalStage2_1);
//        mul2.setGreenOut(internalStage2_1);
//        mul3.setGreenOut(internalStage2_1);
//
//        int shiftConstant = outputType.getFractionBits() - t0.getFractionBits() - t1.getFractionBits();
//        int lshift1 = 32 + shiftConstant;
//        int lshift23 = 16 + shiftConstant;
//        int lshift4 = shiftConstant;
//
//        ArithmeticCombinator shift1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[0]), CombinatorIn.constant(Math.abs(lshift1)), outSymbol, lshift1 > 0 ? ArithmeticOperator.LSH : ArithmeticOperator.RSH);
//        ArithmeticCombinator shift23 = ArithmeticCombinator.withEachMerge(CombinatorIn.constant(Math.abs(lshift23)), outSymbol, lshift23 > 0 ? ArithmeticOperator.LSH : ArithmeticOperator.RSH);
//        ArithmeticCombinator shift4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(tempSymbols[3]), CombinatorIn.constant(Math.abs(lshift4)), outSymbol, lshift4 > 0 ? ArithmeticOperator.LSH : ArithmeticOperator.RSH);
//
//        expressionGroup.getCombinators().add(shift1);
//        expressionGroup.getCombinators().add(shift23);
//        expressionGroup.getCombinators().add(shift4);
//
//        shift1.setGreenIn(internalStage2_0);
//        shift1.setGreenOut(expressionGroup.getOutput());
//        shift4.setGreenIn(internalStage2_0);
//        shift4.setGreenOut(expressionGroup.getOutput());
//        shift23.setGreenIn(internalStage2_1);
//        shift23.setGreenOut(expressionGroup.getOutput());
//
//        return 3;
//    }

    public int generateMulCombinators(Symbol[] symbols, FactorioSignal outSymbol, FixedpType outputType, CombinatorGroup expressionGroup) {
        var t0 = (FixedpType)symbols[0].getType();
        var t1 = (FixedpType)symbols[1].getType();
        var s0 = symbols[0].getSignal()[0];
        var s1 = symbols[1].getSignal()[0];

        var ts = new FactorioSignal[]{
                FactorioSignal.SIGNAL_A,
                FactorioSignal.SIGNAL_B,
                FactorioSignal.SIGNAL_C,
                FactorioSignal.SIGNAL_D,
                FactorioSignal.SIGNAL_E,
                FactorioSignal.SIGNAL_F,
                FactorioSignal.SIGNAL_G,
                FactorioSignal.SIGNAL_H,
        };

        var constants = new ConstantCombinator(Map.of(ts[0], 0xFFFF, ts[1], 0xFFFF, ts[4], 0xFFFF, ts[5], 0xFFFF));
        expressionGroup.getCombinators().add(constants);

        var stage1network = new NetworkGroup();
        expressionGroup.getNetworks().add(stage1network);

        var shift2group = new CombinatorGroup(expressionGroup.getInput(), stage1network);
        expressionGroup.getSubGroups().add(shift2group);

        var shiftA0 = DeciderCombinator.withLeftRight(CombinatorIn.signal(s0), CombinatorIn.constant(0), CombinatorOut.fromInput(ts[0]), DeciderOperator.LT);
        var shiftA1 = DeciderCombinator.withLeftRight(CombinatorIn.signal(s0), CombinatorIn.constant(0), CombinatorOut.fromInput(ts[1]), DeciderOperator.LT);
        generateLogicalRightShift(s0, 16, ts[2], shift2group);
        var shiftA3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(s0), CombinatorIn.constant(0xFFFF), ts[3], ArithmeticOperator.AND);

        var shiftB0 = DeciderCombinator.withLeftRight(CombinatorIn.signal(s1), CombinatorIn.constant(0), CombinatorOut.fromInput(ts[4]), DeciderOperator.LT);
        var shiftB1 = DeciderCombinator.withLeftRight(CombinatorIn.signal(s1), CombinatorIn.constant(0), CombinatorOut.fromInput(ts[5]), DeciderOperator.LT);
        generateLogicalRightShift(s1, 16, ts[6], shift2group);
        var shiftB3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(s1), CombinatorIn.constant(0xFFFF), ts[7], ArithmeticOperator.AND);

        var shiftA = new ArrayList<>(List.of(shiftA0, shiftA1, shiftA3));

        var shiftB = new ArrayList<>(List.of(shiftB0, shiftB1, shiftB3));

        expressionGroup.getCombinators().addAll(shiftA);
        expressionGroup.getCombinators().addAll(shiftB);

        constants.setGreenOut(expressionGroup.getInput());
        shiftA.forEach(c -> c.setGreenIn(expressionGroup.getInput()));
        shiftB.forEach(c -> c.setGreenIn(expressionGroup.getInput()));

        shiftA.forEach(c -> c.setGreenOut(stage1network));
        shiftB.forEach(c -> c.setGreenOut(stage1network));

        //result of ts[a] * ts[b] is at matrixMulCombinators[a] as ts[b]
        var mmcs = Stream.of(ts[0], ts[1], ts[2], ts[3])
                .map(s -> ArithmeticCombinator.withEach(CombinatorIn.signal(s), ArithmeticOperator.MUL))
                .toList();

        expressionGroup.getCombinators().addAll(mmcs);
        mmcs.forEach(mc -> {
            var outNetwork = new NetworkGroup();
            expressionGroup.getNetworks().add(outNetwork);
            mc.setGreenIn(stage1network);
            mc.setGreenOut(outNetwork);
        });

        //lo
        var lo1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[7]), CombinatorIn.constant(16), ts[0], ArithmeticOperator.LSH);
        var lo2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[6]), CombinatorIn.constant(16), ts[0], ArithmeticOperator.LSH);
        var lo3 = ArithmeticCombinator.mapping(ts[7], ts[0]);
        var loDelay = ArithmeticCombinator.copying(ts[0]);
        expressionGroup.getCombinators().add(lo1);
        expressionGroup.getCombinators().add(lo2);
        expressionGroup.getCombinators().add(lo3);
        expressionGroup.getCombinators().add(loDelay);


        lo1.setGreenIn(mmcs.get(2).getGreenOut());
        lo2.setGreenIn(mmcs.get(3).getGreenOut());
        lo3.setGreenIn(mmcs.get(3).getGreenOut());


        var loTmp = new NetworkGroup();
        expressionGroup.getNetworks().add(loTmp);

        lo1.setGreenOut(loTmp);
        lo2.setGreenOut(loTmp);
        lo3.setGreenOut(loTmp);
        loDelay.setGreenIn(loTmp);

        var loOut = new NetworkGroup();
        expressionGroup.getNetworks().add(loOut);
        loDelay.setGreenOut(loOut);


        var loCarryTmp = new NetworkGroup();
        expressionGroup.getNetworks().add(loCarryTmp);

        //lo_carry
        var lo1Carry = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[7]), CombinatorIn.constant(0xFFFF), ts[2], ArithmeticOperator.AND);
        var lo2Carry = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[6]), CombinatorIn.constant(0xFFFF), ts[2], ArithmeticOperator.AND);
        var lo2CarryGroup = new CombinatorGroup(lo3.getGreenIn(), loCarryTmp);
        expressionGroup.getSubGroups().add(lo2CarryGroup);
        generateLogicalRightShift(ts[7], 16, ts[2], lo2CarryGroup);
        var loCarryShift = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[2]), CombinatorIn.constant(16), ts[1], ArithmeticOperator.RSH);

        expressionGroup.getCombinators().add(lo1Carry);
        expressionGroup.getCombinators().add(lo2Carry);
        expressionGroup.getCombinators().add(loCarryShift);



        lo1Carry.setGreenIn(lo1.getGreenIn());
        lo2Carry.setGreenIn(lo2.getGreenIn());

        lo1Carry.setGreenOut(loCarryTmp);
        lo2Carry.setGreenOut(loCarryTmp);
        loCarryShift.setGreenIn(loCarryTmp);

        //hi
        var hiTmp = new NetworkGroup();
        expressionGroup.getNetworks().add(hiTmp);

        var hi = new ArrayList<ArithmeticCombinator>();
        var hi1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[7]), CombinatorIn.constant(16), ts[1], ArithmeticOperator.LSH);
        var hi2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[6]), CombinatorIn.constant(16), ts[1], ArithmeticOperator.LSH);
        var hi3 = ArithmeticCombinator.mapping(ts[7], ts[1]);
        var hi4 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[5]), CombinatorIn.constant(16), ts[1], ArithmeticOperator.LSH);
        var hi5 = ArithmeticCombinator.mapping(ts[6], ts[1]);
        var hi6group = new CombinatorGroup(mmcs.get(2).getGreenOut(), hiTmp);
        expressionGroup.getSubGroups().add(hi6group);
        generateLogicalRightShift(ts[7], 16, ts[1], hi6group); //hi6
        var hi7 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[4]), CombinatorIn.constant(16), ts[1], ArithmeticOperator.LSH);
        var hi8 = ArithmeticCombinator.mapping(ts[5], ts[1]);

        var hi9group = new CombinatorGroup(mmcs.get(3).getGreenOut(), hiTmp);
        expressionGroup.getSubGroups().add(hi9group);
        generateLogicalRightShift(ts[6], 16, ts[1], hi9group); //hi9

        hi.addAll(List.of(hi1, hi2, hi3, hi4, hi5, hi7, hi8));
        expressionGroup.getCombinators().addAll(hi);


        hi1.setGreenIn(mmcs.get(0).getGreenOut());
        hi2.setGreenIn(mmcs.get(1).getGreenOut());
        hi3.setGreenIn(mmcs.get(1).getGreenOut());
        hi4.setGreenIn(mmcs.get(2).getGreenOut());
        hi5.setGreenIn(mmcs.get(2).getGreenOut());
        hi7.setGreenIn(mmcs.get(3).getGreenOut());
        hi8.setGreenIn(mmcs.get(3).getGreenOut());
        hi.forEach(c -> c.setGreenOut(hiTmp));

        var hiDelay = ArithmeticCombinator.copying(ts[1]);
        hiDelay.setGreenIn(hiTmp);
        expressionGroup.getCombinators().add(hiDelay);

        var hiOut = new NetworkGroup();
        expressionGroup.getNetworks().add(hiOut);
        hiDelay.setGreenOut(hiOut);
        loCarryShift.setGreenOut(hiOut);

        int shiftConstant = outputType.getFractionBits() - t0.getFractionBits() - t1.getFractionBits();

        var finalHiShift = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ts[1]), CombinatorIn.constant(32 + shiftConstant), outSymbol, ArithmeticOperator.LSH);
        var finalLoShiftGroup = new CombinatorGroup(loOut, expressionGroup.getOutput());
        expressionGroup.getSubGroups().add(finalLoShiftGroup);
        generateLogicalRightShift(ts[0], -shiftConstant, outSymbol, finalLoShiftGroup);

        expressionGroup.getCombinators().add(finalHiShift);

        finalHiShift.setGreenIn(hiOut);
        finalHiShift.setGreenOut(expressionGroup.getOutput());

        return 5;
    }

    //a >>> b = ((a + (int)0x80000000) >> b) + (0x40000000 >> b) * 2 + (b >> b) + (b == 31 ? 1 : 0)
    private static void generateLogicalRightShift(FactorioSignal signalIn, int shiftValue, FactorioSignal outSignal, CombinatorGroup combinatorGroup) {
        if(shiftValue >= 31) {
            throw new IllegalArgumentException("Unsupported shift");
        }
        var tmp = new NetworkGroup();
        combinatorGroup.getNetworks().add(tmp);
        var c = new ConstantCombinator(Map.of(signalIn, 0x80000000));
        var a1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(signalIn), CombinatorIn.constant(shiftValue), outSignal, ArithmeticOperator.RSH);
        var a2 = ArithmeticCombinator.withLeftRight(CombinatorIn.constant(0x40000000), CombinatorIn.constant(shiftValue - 1), outSignal, ArithmeticOperator.RSH);
        c.setRedOut(tmp);
        a1.setRedIn(tmp);
        a1.setGreenIn(combinatorGroup.getInput());
        a2.setGreenOut(combinatorGroup.getInput());
        a1.setGreenOut(combinatorGroup.getOutput());
        a2.setGreenOut(combinatorGroup.getOutput());
        combinatorGroup.getCombinators().add(c);
        combinatorGroup.getCombinators().add(a1);
        combinatorGroup.getCombinators().add(a2);
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
        String d1 = "-17.293";
        String d2 = "9.459";
        System.out.println(floatToFixedpInt(d1, 16));
        System.out.println(floatToFixedpInt(d2, 16));
//        System.out.println(constToFloat(new Constant(new FixedpType(18), floatToFixedpInt(d1, 18))));
//        int manu2 = fixedpMulImpl(floatToFixedpInt(d1, 16), 16, floatToFixedpInt(d2, 16), 16, 16)
        int manu = signedFixedpMul(floatToFixedpInt(d1, 16), 16, floatToFixedpInt(d2, 16), 16, 16);
//        int manu2 = fixedpMulImpl(floatToFixedpInt(d1, 16), 16, floatToFixedpInt(d2, 16), 16, 16);
        int manu3 = test(floatToFixedpInt(d1, 16), floatToFixedpInt(d2, 16));
//        System.out.println(manu);
        System.out.println(constToFloat(manu, 16));
//        System.out.println(manu2);
//        System.out.println(constToFloat(manu2, 16));
        System.out.println(constToFloat(manu3, 16));
        System.out.println("Expected: " + (Double.parseDouble(d1) * Double.parseDouble(d2)));





        double maxError = 0;
        for(int i = 0; i < 0; i++) {
            double f1 = Math.random() * 200 - 100;
            double f2 = Math.random() * 200 - 100;
            int fp1 = floatToFixedpInt(Double.toString(f1), 16);
            int fp2 = floatToFixedpInt(Double.toString(f2), 16);
            int res1 = test(fp1, fp2);
            int res2 = signedFixedpMul(fp1, 16, fp2, 16, 16);
            double resFp1 = Double.parseDouble(constToFloat(res1, 16));
            double resFp2 = Double.parseDouble(constToFloat(res2, 16));
            double actual = f1 * f2;

            if(Math.abs(resFp2 - actual) > 0.001) {
                maxError = Math.max(maxError, Math.abs(resFp2 - actual));
                System.out.println("[2] Difference: " + f1 + " * " + f2 + ". Expected " + actual + " got " + resFp2);
            }
            if(Math.abs(resFp2 - resFp1) > 0.001) {
                System.out.println("[3] Difference: " + f1 + " * " + f2 + ". Expected " + resFp1 + " got " + resFp2);
            }
        }
    }

    public static int test(int a, int b) {
        long r = (long)a * (long)b;
        return (int)(r >> 16);
    }

    public static int fixedpMulImpl(int a, int aFractBits, int b, int bFractBits, int targetFractBits) {
        System.out.println("Multiplying " + a + "<" + aFractBits + "> * " + b + "<" + bFractBits + "> into ?<" + targetFractBits + ">");
        System.out.println("a:  " + Integer.toBinaryString(a).substring(0, 16) + "." + Integer.toBinaryString(a).substring(16));
        System.out.println("b:  " + Integer.toBinaryString(b).substring(0, 16) + "." + Integer.toBinaryString(b).substring(16));
        int a0 = a >>> 16;
        int a1 = a & 0xFFFF;
        int b0 = b >>> 16;
        int b1 = b & 0xFFFF;
        System.out.println("a0: " + Integer.toBinaryString(a0));
        System.out.println("b0: " + Integer.toBinaryString(b0));
        System.out.println("a1: " + Integer.toBinaryString(a1));
        System.out.println("b1: " + Integer.toBinaryString(b1));

        int shiftPartConstant = targetFractBits - aFractBits - bFractBits;
        int part1Shift =  32 + shiftPartConstant;
        int part23Shift = 16 + shiftPartConstant;
        int part4Shift =  shiftPartConstant;

        System.out.println("a0*b0: " + (a0 * b0) + " (" + Integer.toBinaryString(a0 * b0) + ")");
        System.out.println("a0*b1: " + (a0 * b1) + " (" + Integer.toBinaryString(a0 * b1) + ")");
        System.out.println("a1*b0: " + (a1 * b0) + " (" + Integer.toBinaryString(a1 * b0) + ")");
        System.out.println("a1*b1: " + (a1 * b1) + " (" + Integer.toBinaryString(a1 * b1) + ")");

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

    public static int signedFixedpMul(int a, int aFractBits, int b, int bFractBits, int targetFractBits) {
        int a0 = a < 0 ? 0xFFFF : 0;
        int a1 = a0;
        int a2 = a >>> 16;
        int a3 = a & 0xFFFF;
        int b0 = b < 0 ? 0xFFFF : 0;
        int b1 = b0;
        int b2 = b >>> 16;
        int b3 = b & 0xFFFF;

        int shiftPartConstant = targetFractBits - aFractBits - bFractBits;

        int lo = ((a2 * b3) << 16) +
                ((a3 * b2) << 16) +
                (a3 * b3);

        int lo_carry = (((a2 * b3) & 0xFFFF) +
                ((a3 * b2)  & 0xFFFF) +
                ((a3 * b3) >>> 16)) >> 16;

        int hi = ((a0 * b3) << 16) +
                 ((a1 * b2) << 16) +
                 (a1 * b3) +
                 ((a2 * b1) << 16) +
                 (a2 * b2) +
                 ((a2 * b3) >>> 16) +
                 ((a3 * b0) << 16) +
                 (a3 * b1) +
                 ((a3 * b2) >>> 16) +
                 lo_carry;

        return (hi << (32 + shiftPartConstant)) + (lo >>> -shiftPartConstant);
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
        return String.valueOf((double)longFromConst(c) / (double)(1L << 32));
    }

    private static String constToFloat(int c, int fractionBits) {
        return constToFloat(new Constant(new FixedpType(fractionBits), c));
    }

    private static int floatToFixedpInt(String f, int fractBits) {
        return (int)Math.round(Double.parseDouble(f) * (1 << fractBits));
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
