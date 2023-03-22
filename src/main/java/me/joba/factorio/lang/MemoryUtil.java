package me.joba.factorio.lang;

import me.joba.factorio.CombinatorIn;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.CombinatorOut;
import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.game.entities.*;
import me.joba.factorio.graph.FunctionPlacer;
import me.joba.factorio.lang.types.PrimitiveType;

import java.util.*;
import java.util.stream.Collectors;

public class MemoryUtil {

    private static final int MEMORY_READ_DELAY_UNSAFE = 4;
//    private static final int MEMORY_READ_DELAY = MEMORY_READ_DELAY_UNSAFE + 4;

    private static final FactorioSignal ADDRESS_SIGNAL = FactorioSignal.SIGNAL_GREY;
    private static final FactorioSignal WRITE_VALUE_SIGNAL = FactorioSignal.SIGNAL_BLACK;

    public static final FunctionSignature MEMORY_READ_SIGNATURE;
//    public static final FunctionSignature MEMORY_UNSAFE_READ_SIGNATURE; //TODO
    public static final FunctionSignature MEMORY_WRITE_SIGNATURE;

    private static final int MEMORY_CELL_X_OFFSET = 0;
    private static final int MEMORY_CELL_Y_OFFSET = 8;

    static {
        MEMORY_READ_SIGNATURE = new FunctionSignature.Builder("__internal__arrayRead", new FunctionParameter[]{new FunctionParameter("address", PrimitiveType.INT, MemoryUtil.ADDRESS_SIGNAL)}, PrimitiveType.INT, new FactorioSignal[]{WRITE_VALUE_SIGNAL})
                .asNative(true)
                .asPipelined(true)
                .withDelay(MEMORY_READ_DELAY_UNSAFE)
                .withSideEffects(FunctionSignature.SideEffectsType.IDEMPOTENT_READ)
                .build();
//        MEMORY_UNSAFE_READ_SIGNATURE = new FunctionSignature.Builder("__internal__arrayUnsafeRead", new FunctionParameter[]{new FunctionParameter("address", PrimitiveType.INT, MemoryUtil.ADDRESS_SIGNAL)}, PrimitiveType.INT, new FactorioSignal[]{WRITE_VALUE_SIGNAL})
//                .asNative(true)
//                .asPipelined(true)
//                .withDelay(MEMORY_READ_DELAY_UNSAFE)
//                .build();
        MEMORY_WRITE_SIGNATURE = new FunctionSignature.Builder("__internal__arrayWrite", new FunctionParameter[]{new FunctionParameter("address", PrimitiveType.INT, MemoryUtil.ADDRESS_SIGNAL), new FunctionParameter("value", PrimitiveType.INT, MemoryUtil.WRITE_VALUE_SIGNAL)}, PrimitiveType.VOID, new FactorioSignal[0])
                .asNative(true)
                .asPipelined(true)
                .withSideEffects(FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE)
                .build();
    }

    public static EntityBlock generateMemoryController(int memorySize, NetworkGroup in, NetworkGroup out) {
        var writeIn = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.FUNCTION_IDENTIFIER), CombinatorIn.constant(MEMORY_WRITE_SIGNATURE.getFunctionId()), CombinatorOut.everything(false), DeciderOperator.EQ);
        var readIn = DeciderCombinator.withLeftRight(CombinatorIn.signal(Constants.FUNCTION_IDENTIFIER), CombinatorIn.constant(MEMORY_READ_SIGNATURE.getFunctionId()), CombinatorOut.everything(false), DeciderOperator.EQ);
//        var unsafeReadIn = DeciderCombinator.withLeftRight(Accessor.signal(Constants.FUNCTION_IDENTIFIER), Accessor.constant(MEMORY_UNSAFE_READ_SIGNATURE.getFunctionId()), Writer.everything(false), DeciderOperator.EQ);

        NetworkGroup writeInGroup = new NetworkGroup();
        NetworkGroup readInGroup = new NetworkGroup();
        writeIn.setGreenIn(in);
        readIn.setGreenIn(in);
        writeIn.setGreenOut(writeInGroup);
        readIn.setGreenOut(readInGroup);

        var cg = generateMemoryController(memorySize, writeInGroup, readInGroup, out);
        cg.getNetworks().add(writeInGroup);
        cg.getNetworks().add(readInGroup);
        cg.getCombinators().add(writeIn);
        cg.getCombinators().add(readIn);

        readIn.setPosition(MEMORY_CELL_X_OFFSET + 5, MEMORY_CELL_Y_OFFSET - 11, 2);
        writeIn.setPosition(MEMORY_CELL_X_OFFSET + 3, MEMORY_CELL_Y_OFFSET - 11, 2);

        var sub = new Substation();
        cg.getCombinators().add(sub);
        sub.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 5, 0);
        sub.setNetwork(0, WireColor.GREEN, in);
        sub.setNetwork(0, WireColor.RED, out);

        Set<CombinatorGroup> generatedGroups = new HashSet<>();
        Queue<CombinatorGroup> toExpand = new LinkedList<>();
        toExpand.add(cg);
        while(!toExpand.isEmpty()) {
            var group = toExpand.poll();
            generatedGroups.add(group);
            toExpand.addAll(group.getSubGroups());
        }

        generatedGroups.forEach(g -> g.getAccessors().forEach(VariableAccessor::generateAccessors));

        var combinators = generatedGroups.stream()
                .map(CombinatorGroup::getCombinators)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var networks = generatedGroups.stream()
                .map(CombinatorGroup::getNetworks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return FunctionPlacer.placeFunction(combinators, networks, in, out, true);
    }

    public static CombinatorGroup generateMemoryController(int memorySize, NetworkGroup writeIn, NetworkGroup readIn, NetworkGroup readOut) {
        FactorioSignal[] toUse = new FactorioSignal[FactorioSignal.values().length - 2];
        for(int i = 0, j = 0; i < toUse.length; i++, j++) {
            FactorioSignal signal = FactorioSignal.values()[j];
            if(signal == ADDRESS_SIGNAL || signal == WRITE_VALUE_SIGNAL) {
                i--;
                continue;
            }
            toUse[i] = signal;
        }
        int requiredCellCount = (memorySize + toUse.length) / toUse.length;
        return generate(requiredCellCount, toUse, writeIn, readIn, readOut);
    }

    private static CombinatorGroup generateWriteSignalTransformer(FactorioSignal[] signals, NetworkGroup constantSignalIdNetwork, NetworkGroup in, NetworkGroup out) {
        CombinatorGroup group = new CombinatorGroup(in, out);

        var in1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.MOD);
        var in2 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(ADDRESS_SIGNAL), DeciderOperator.GT);
        var in3 = ArithmeticCombinator.copying(WRITE_VALUE_SIGNAL);
        var in4 = DeciderCombinator.withLeftRight(CombinatorIn.signal(WRITE_VALUE_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(WRITE_VALUE_SIGNAL), DeciderOperator.EQ);
        var in5 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
        var in6 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(ADDRESS_SIGNAL), DeciderOperator.GT);

        group.getCombinators().add(in1);
        group.getCombinators().add(in2);
        group.getCombinators().add(in3);
        group.getCombinators().add(in4);
        group.getCombinators().add(in5);
        group.getCombinators().add(in6);

        in1.setPosition(MEMORY_CELL_X_OFFSET - 1, MEMORY_CELL_Y_OFFSET - 9, 4);
        in2.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET - 9, 4);
        in3.setPosition(MEMORY_CELL_X_OFFSET + 1, MEMORY_CELL_Y_OFFSET - 9, 4);
        in4.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET - 9, 4);
        in5.setPosition(MEMORY_CELL_X_OFFSET + 3, MEMORY_CELL_Y_OFFSET - 9, 4);
        in6.setPosition(MEMORY_CELL_X_OFFSET + 3, MEMORY_CELL_Y_OFFSET - 7, 4);

        in1.setGreenIn(in);
        in2.setGreenIn(in);
        in3.setGreenIn(in);
        in4.setGreenIn(in);
        in5.setGreenIn(in);
        in6.setGreenIn(in);

        var tmpIn = new NetworkGroup();
        group.getNetworks().add(tmpIn);

        in1.setRedOut(tmpIn);
        in2.setRedOut(tmpIn);

        var tmpOut = new NetworkGroup();
        group.getNetworks().add(tmpOut);

        var out1 = ArithmeticCombinator.withEach(CombinatorIn.signal(WRITE_VALUE_SIGNAL), ArithmeticOperator.MUL);
        var out2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(WRITE_VALUE_SIGNAL), CombinatorIn.constant(-1), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
        var out3 = ArithmeticCombinator.copying();

        out1.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET - 4, 2);
        out2.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET - 4, 2);
        out3.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET - 5, 4);

        group.getCombinators().add(out1);
        group.getCombinators().add(out2);
        group.getCombinators().add(out3);

        out1.setRedIn(tmpOut);

        out1.setGreenOut(out);
        out2.setGreenOut(out);
        out3.setGreenOut(out);

        var c1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(WRITE_VALUE_SIGNAL), CombinatorIn.signal(WRITE_VALUE_SIGNAL), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
        var c2 = ArithmeticCombinator.copying(WRITE_VALUE_SIGNAL);
        var c3 = ArithmeticCombinator.copying();
        group.getCombinators().add(c1);
        group.getCombinators().add(c2);
        group.getCombinators().add(c3);

        c1.setPosition(MEMORY_CELL_X_OFFSET - 1, MEMORY_CELL_Y_OFFSET - 5, 4);
        c2.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET - 5, 4);
        c3.setPosition(MEMORY_CELL_X_OFFSET + 1, MEMORY_CELL_Y_OFFSET - 5, 4);

        var tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        in3.setGreenOut(tmp);
        in4.setGreenOut(tmp);
        c1.setGreenIn(tmp);
        c2.setGreenIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c1.setRedOut(tmp);
        out2.setRedIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c2.setGreenOut(tmp);
        out1.setGreenIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        in4.setRedOut(tmp);
        in5.setRedOut(tmp);
        in6.setRedOut(tmp);
        c3.setRedIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c3.setGreenOut(tmp);
        out3.setGreenIn(tmp);

        group.getSubGroups().add(generateWriteSignalSelector(constantSignalIdNetwork, tmpIn, tmpOut, MEMORY_CELL_X_OFFSET - 2, MEMORY_CELL_Y_OFFSET - 10));

        return group;
    }

    private static CombinatorGroup generateMemoryCells(int cellCount, NetworkGroup writeIn, NetworkGroup readIn, NetworkGroup readOut) {
        CombinatorGroup group = new CombinatorGroup(null, null);

        NetworkGroup internalWriteIn1 = new NetworkGroup();
        NetworkGroup internalWriteIn2 = new NetworkGroup();
        NetworkGroup internalWriteIn3 = new NetworkGroup();

        group.getNetworks().add(internalWriteIn1);
        group.getNetworks().add(internalWriteIn2);
        group.getNetworks().add(internalWriteIn3);

        //Memory cell write controller
        {
            var in1 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.NEQ);
            var in2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);

            group.getCombinators().add(in1);
            group.getCombinators().add(in2);

            var c1 = DeciderCombinator.withLeftRight(CombinatorIn.signal(WRITE_VALUE_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
            var c2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
            var c3 = ArithmeticCombinator.withEach(CombinatorIn.constant(-1), ArithmeticOperator.MUL);
            var c4 = ArithmeticCombinator.withEach(CombinatorIn.constant(1073741824), ArithmeticOperator.ADD);
            var c5 = ArithmeticCombinator.withEach(CombinatorIn.constant(2147483647), ArithmeticOperator.AND);
            var c6 = ArithmeticCombinator.withEach(CombinatorIn.constant(-2147483648), ArithmeticOperator.AND);
            var c7 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.fromInput(ADDRESS_SIGNAL), DeciderOperator.NEQ);
            var c8 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.fromInput(ADDRESS_SIGNAL), DeciderOperator.NEQ);

            group.getCombinators().add(c1);
            group.getCombinators().add(c2);
            group.getCombinators().add(c3);
            group.getCombinators().add(c4);
            group.getCombinators().add(c5);
            group.getCombinators().add(c6);
            group.getCombinators().add(c7);
            group.getCombinators().add(c8);

            in1.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET - 3, 2);
            in2.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET - 2, 2);
            c1.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET - 3, 2);
            c2.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET - 2, 2);
            c3.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET - 1, 2);
            c4.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET - 1, 2);
            c5.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 4, 2);
            c6.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 3, 2);
            c7.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 2, 2);
            c8.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 1, 2);


            in1.setGreenIn(writeIn);
            in2.setGreenIn(writeIn);

            c5.setRedOut(internalWriteIn1);
            c8.setRedOut(internalWriteIn1);

            c6.setRedOut(internalWriteIn3);
            c7.setRedOut(internalWriteIn3);

            c2.setRedOut(internalWriteIn2);
            c3.setRedOut(internalWriteIn2);
            c4.setRedOut(internalWriteIn2);
            c7.setRedIn(internalWriteIn2);
            c8.setRedIn(internalWriteIn2);

            var tmp = new NetworkGroup();
            group.getNetworks().add(tmp);

            in1.setGreenOut(tmp);
            in2.setGreenOut(tmp);
            c1.setGreenIn(tmp);
            c3.setGreenIn(tmp);
            c4.setGreenIn(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            in2.setRedOut(tmp);
            c2.setRedIn(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            c1.setGreenOut(tmp);
            c5.setGreenIn(tmp);
            c6.setGreenIn(tmp);
        }

        //Memory cells
        NetworkGroup propagator = null;
        for(int cellId = 0; cellId < cellCount; cellId++) {

            var c1 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
            var c2 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
            var c3 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
            var c4 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);
            var c5 = DeciderCombinator.withEach(CombinatorIn.constant(0), false, DeciderOperator.GT);
            var c6 = DeciderCombinator.withEach(CombinatorIn.constant(-2147483648), false, DeciderOperator.EQ);
            var c7 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.everything(false), DeciderOperator.EQ);

            c1.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET + cellId, 2);
            c2.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET + cellId, 2);
            c3.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET + cellId, 2);
            c4.setPosition(MEMORY_CELL_X_OFFSET + 6, MEMORY_CELL_Y_OFFSET + cellId, 2);
            c5.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET + cellId, 2);
            c6.setPosition(MEMORY_CELL_X_OFFSET + 10, MEMORY_CELL_Y_OFFSET + cellId, 2);
            c7.setPosition(MEMORY_CELL_X_OFFSET + 12, MEMORY_CELL_Y_OFFSET + cellId, 2);

            group.getCombinators().add(c1);
            group.getCombinators().add(c2);
            group.getCombinators().add(c3);
            group.getCombinators().add(c4);
            group.getCombinators().add(c5);
            group.getCombinators().add(c6);
            group.getCombinators().add(c7);

            c1.setRedIn(internalWriteIn1);
            c2.setGreenIn(propagator);
            c3.setRedIn(internalWriteIn2);
            c4.setRedIn(internalWriteIn3);
            c7.setGreenIn(readIn);
            c7.setGreenOut(readOut);

            propagator = new NetworkGroup();
            group.getNetworks().add(propagator);
            c2.setGreenOut(propagator);

            c1.setGreenIn(propagator);
            c3.setGreenIn(propagator);
            c4.setGreenIn(propagator);

            var tmp = new NetworkGroup();
            group.getNetworks().add(tmp);

            c1.setRedOut(tmp);
            c2.setRedOut(tmp);
            c4.setRedOut(tmp);
            c5.setRedOut(tmp);
            c6.setRedOut(tmp);
            c7.setRedIn(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);

            c3.setRedOut(tmp);
            c5.setRedIn(tmp);
            c6.setRedIn(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);

            c1.setGreenOut(tmp);
            c3.setGreenOut(tmp);
            c5.setGreenIn(tmp);
            c5.setGreenOut(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);

            c4.setGreenOut(tmp);
            c6.setGreenIn(tmp);
            c6.setGreenOut(tmp);
        }

        return group;
    }

    private static CombinatorGroup generate(int cellCount, FactorioSignal[] signals, NetworkGroup writeIn, NetworkGroup readIn, NetworkGroup readOut) {
        CombinatorGroup group = new CombinatorGroup(null, null);

        NetworkGroup memWriteIn = new NetworkGroup();
        NetworkGroup memReadIn = new NetworkGroup();
        NetworkGroup memReadOut = new NetworkGroup();

        group.getNetworks().add(memWriteIn);
        group.getNetworks().add(memReadIn);
        group.getNetworks().add(memReadOut);

        var constantSignalIdGroup = generateConstantSignalIdNetwork(signals, MEMORY_CELL_X_OFFSET - 2, MEMORY_CELL_Y_OFFSET - 10);
        var constantSignalIdNetwork = constantSignalIdGroup.getOutput();
        group.getSubGroups().add(constantSignalIdGroup);
        group.getSubGroups().add(generateMemoryCells(cellCount, memWriteIn, memReadIn, memReadOut));
        group.getSubGroups().add(generateWriteSignalTransformer(signals, constantSignalIdNetwork, writeIn, memWriteIn));
        group.getSubGroups().add(generateRead(signals, constantSignalIdNetwork, readIn, readOut, memReadIn, memReadOut));

        return group;
    }

    private static CombinatorGroup generateRead(FactorioSignal[] signals, NetworkGroup constantSignalIdNetwork, NetworkGroup readIn, NetworkGroup readOut, NetworkGroup memReadIn, NetworkGroup memReadOut) {
        CombinatorGroup group = new CombinatorGroup(null, null);

        var in1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.MOD);
        var in2 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(ADDRESS_SIGNAL), DeciderOperator.GT);
        var in3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
        var in4 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(ADDRESS_SIGNAL), DeciderOperator.GT);

        group.getCombinators().add(in1);
        group.getCombinators().add(in2);
        group.getCombinators().add(in3);
        group.getCombinators().add(in4);

        in1.setPosition(MEMORY_CELL_X_OFFSET + 5, MEMORY_CELL_Y_OFFSET - 9, 4);
        in2.setPosition(MEMORY_CELL_X_OFFSET + 6, MEMORY_CELL_Y_OFFSET - 9, 4);
        in3.setPosition(MEMORY_CELL_X_OFFSET + 7, MEMORY_CELL_Y_OFFSET - 9, 4);
        in4.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET - 9, 4);

        in1.setGreenIn(readIn);
        in2.setGreenIn(readIn);
        in3.setGreenIn(readIn);
        in4.setGreenIn(readIn);

        var tmpIn = new NetworkGroup();
        group.getNetworks().add(tmpIn);

        in1.setRedOut(tmpIn);
        in2.setRedOut(tmpIn);
        in3.setGreenOut(memReadIn);
        in4.setGreenOut(memReadIn);

        var tmpOut = new NetworkGroup();
        group.getNetworks().add(tmpOut);

        group.getSubGroups().add(generateWriteSignalSelector(constantSignalIdNetwork, tmpIn, tmpOut, MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 10));

        var c1 = ArithmeticCombinator.withEach(CombinatorIn.constant(-1), ArithmeticOperator.MUL);
        var c2 = ArithmeticCombinator.withEach(CombinatorIn.constant(-2147483648), ArithmeticOperator.ADD);
        var c3 = ArithmeticCombinator.withEach(CombinatorIn.constant(2147483647), ArithmeticOperator.AND);
        var c4 = ArithmeticCombinator.withEach(CombinatorIn.constant(31), ArithmeticOperator.RSH);
        var c5 = DeciderCombinator.withEach(CombinatorIn.constant(0), WRITE_VALUE_SIGNAL, false, DeciderOperator.LT);
        var c6 = DeciderCombinator.withEach(CombinatorIn.constant(-2147483648), WRITE_VALUE_SIGNAL, false, DeciderOperator.EQ);

        c1.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET - 5, 2);
        c2.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET - 4, 2);
        c3.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET - 3, 2);
        c4.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET - 2, 2);
        c5.setPosition(MEMORY_CELL_X_OFFSET + 10, MEMORY_CELL_Y_OFFSET - 4, 2);
        c6.setPosition(MEMORY_CELL_X_OFFSET + 10, MEMORY_CELL_Y_OFFSET - 3, 2);

        group.getCombinators().add(c1);
        group.getCombinators().add(c2);
        group.getCombinators().add(c3);
        group.getCombinators().add(c4);
        group.getCombinators().add(c5);
        group.getCombinators().add(c6);

        c1.setRedIn(tmpOut);
        c2.setRedIn(tmpOut);
        c3.setGreenIn(memReadOut);
        c4.setGreenIn(memReadOut);
        c5.setRedOut(readOut);
        c6.setRedOut(readOut);

        var tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c1.setRedOut(tmp);
        c2.setRedOut(tmp);
        c5.setRedIn(tmp);
        c6.setRedIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c3.setGreenOut(tmp);
        c5.setGreenIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c4.setGreenOut(tmp);
        c6.setGreenIn(tmp);

        return group;
    }

    private static CombinatorGroup generateConstantSignalIdNetwork(FactorioSignal[] signals, int xOffset, int yOffset) {
        NetworkGroup constantNetwork = new NetworkGroup();
        CombinatorGroup group = new CombinatorGroup(null, constantNetwork);
        group.getNetworks().add(constantNetwork);
        for(int i = 0; i < (signals.length + 19) / 20; i++) {
            Map<FactorioSignal, Integer> values = new HashMap<>();
            for(int j = 0; j < Math.min(20, signals.length - i * 20); j++) {
                int id = i * 20 + j;
                values.put(signals[id], id + 1);
            }
            var cmb = new ConstantCombinator(values);
            cmb.setPosition(xOffset,yOffset + i);
            group.getCombinators().add(cmb);
            cmb.setGreenOut(constantNetwork);
        }
        return group;
    }

    private static CombinatorGroup generateWriteSignalSelector(NetworkGroup constantNetwork, NetworkGroup in, NetworkGroup out, int xOffset, int yOffset) {
        CombinatorGroup group = new CombinatorGroup(in, out);

        ArithmeticCombinator c1 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
        c1.setPosition(xOffset + 1, yOffset + 3, 4);
        DeciderCombinator c2 = DeciderCombinator.withEach(CombinatorIn.signal(ADDRESS_SIGNAL), true, DeciderOperator.EQ);
        c2.setPosition(xOffset + 2, yOffset + 3, 4);
        ArithmeticCombinator c3 = ArithmeticCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
        c3.setPosition(xOffset + 3, yOffset + 3, 4);
        DeciderCombinator c4 = DeciderCombinator.withLeftRight(CombinatorIn.signal(ADDRESS_SIGNAL), CombinatorIn.constant(0), CombinatorOut.one(ADDRESS_SIGNAL), DeciderOperator.EQ);
        c4.setPosition(xOffset + 4, yOffset + 3, 4);

        group.getCombinators().add(c1);
        group.getCombinators().add(c2);
        group.getCombinators().add(c3);
        group.getCombinators().add(c4);

        c1.setRedIn(in);
        c2.setRedIn(in);
        c3.setRedIn(in);
        c4.setRedIn(in);

        c1.setRedOut(out);
        c2.setRedOut(out);
        c3.setRedOut(out);
        c4.setRedOut(out);

        c2.setGreenIn(constantNetwork);

        return group;
    }
}
