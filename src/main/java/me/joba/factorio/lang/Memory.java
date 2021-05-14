package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.Writer;
import me.joba.factorio.game.entities.*;

import java.util.*;

public class Memory {

    private static final FactorioSignal ADDRESS_SIGNAL = FactorioSignal.SIGNAL_GREY;
    private static final FactorioSignal WRITE_VALUE_SIGNAL = FactorioSignal.SIGNAL_BLACK;

    private static final int MEMORY_CELL_X_OFFSET = 0;
    private static final int MEMORY_CELL_Y_OFFSET = 8;

    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
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

        var in1 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.MOD);
        var in2 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.GT);
        var in3 = ArithmeticCombinator.copying(WRITE_VALUE_SIGNAL);
        var in4 = DeciderCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.constant(0), Writer.one(WRITE_VALUE_SIGNAL), DeciderOperator.EQ);
        var in5 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
        var in6 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.GT);

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

        var out1 = ArithmeticCombinator.withEach(Accessor.signal(WRITE_VALUE_SIGNAL), ArithmeticOperator.MUL);
        var out2 = ArithmeticCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.constant(-1), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
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

        var c1 = ArithmeticCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.signal(WRITE_VALUE_SIGNAL), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
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
            var in1 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
            var in2 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);

            group.getCombinators().add(in1);
            group.getCombinators().add(in2);

            var c1 = DeciderCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
            var c2 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
            var c3 = ArithmeticCombinator.withEach(Accessor.constant(-1), ArithmeticOperator.MUL);
            var c4 = ArithmeticCombinator.withEach(Accessor.constant(1073741824), ArithmeticOperator.ADD);
            var c5 = ArithmeticCombinator.withEach(Accessor.constant(2147483647), ArithmeticOperator.AND);
            var c6 = ArithmeticCombinator.withEach(Accessor.constant(-2147483648), ArithmeticOperator.AND);
            var c7 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.fromInput(ADDRESS_SIGNAL), DeciderOperator.NEQ);
            var c8 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.fromInput(ADDRESS_SIGNAL), DeciderOperator.NEQ);

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

            var c1 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
            var c2 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
            var c3 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
            var c4 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
            var c5 = DeciderCombinator.withEach(Accessor.constant(0), false, DeciderOperator.GT);
            var c6 = DeciderCombinator.withEach(Accessor.constant(-2147483648), false, DeciderOperator.EQ);
            var c7 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);

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

        var in1 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.MOD);
        var in2 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.GT);
        var in3 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
        var in4 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.GT);

        group.getCombinators().add(in1);
        group.getCombinators().add(in2);
        group.getCombinators().add(in3);
        group.getCombinators().add(in4);

        in1.setPosition(MEMORY_CELL_X_OFFSET + 5, MEMORY_CELL_Y_OFFSET - 9, 4);
        in2.setPosition(MEMORY_CELL_X_OFFSET + 6, MEMORY_CELL_Y_OFFSET - 9, 4);
        in3.setPosition(MEMORY_CELL_X_OFFSET + 7, MEMORY_CELL_Y_OFFSET - 9, 4);
        in4.setPosition(MEMORY_CELL_X_OFFSET + 8, MEMORY_CELL_Y_OFFSET - 9, 4);

        in1.setGreenIn(readIn);
        in1.setGreenIn(readIn);
        in1.setGreenIn(readIn);
        in1.setGreenIn(readIn);

        var tmpIn = new NetworkGroup();
        group.getNetworks().add(tmpIn);

        in1.setRedOut(tmpIn);
        in2.setRedOut(tmpIn);
        in3.setGreenOut(memReadIn);
        in4.setGreenOut(memReadIn);

        var tmpOut = new NetworkGroup();
        group.getNetworks().add(tmpOut);

        group.getSubGroups().add(generateWriteSignalSelector(constantSignalIdNetwork, tmpIn, tmpOut, MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET - 10));

        var c1 = ArithmeticCombinator.withEach(Accessor.constant(-1), ArithmeticOperator.MUL);
        var c2 = ArithmeticCombinator.withEach(Accessor.constant(-2147483648), ArithmeticOperator.ADD);
        var c3 = ArithmeticCombinator.withEach(Accessor.constant(2147483647), ArithmeticOperator.AND);
        var c4 = ArithmeticCombinator.withEach(Accessor.constant(31), ArithmeticOperator.RSH);
        var c5 = DeciderCombinator.withEach(Accessor.constant(0), WRITE_VALUE_SIGNAL, false, DeciderOperator.LT);
        var c6 = DeciderCombinator.withEach(Accessor.constant(-2147483648), WRITE_VALUE_SIGNAL, false, DeciderOperator.EQ);

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
        c5.setGreenOut(readOut);
        c6.setGreenOut(readOut);

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

        ArithmeticCombinator c1 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
        c1.setPosition(xOffset + 1, yOffset + 3, 4);
        DeciderCombinator c2 = DeciderCombinator.withEach(Accessor.signal(ADDRESS_SIGNAL), true, DeciderOperator.EQ);
        c2.setPosition(xOffset + 2, yOffset + 3, 4);
        ArithmeticCombinator c3 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
        c3.setPosition(xOffset + 3, yOffset + 3, 4);
        DeciderCombinator c4 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.EQ);
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
