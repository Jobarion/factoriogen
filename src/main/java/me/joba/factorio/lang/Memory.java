package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.Writer;
import me.joba.factorio.game.entities.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Memory {

    private static final FactorioSignal ADDRESS_SIGNAL = FactorioSignal.SIGNAL_BLACK;
    private static final FactorioSignal WRITE_VALUE_SIGNAL = FactorioSignal.SIGNAL_GREY;

    private static final int MEMORY_CELL_X_OFFSET = 0;
    private static final int MEMORY_CELL_Y_OFFSET = 8;

    private static final int COST_PER_CELL = 4;
    private static final int COST_PER_SIGNAL = 2;//actually 2.05 but whatever


    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
    public static CombinatorGroup generateMemoryController(int memorySize, NetworkGroup readIn, NetworkGroup readOut, NetworkGroup writeIn) {
        int sqrt = (int)Math.ceil(Math.sqrt(memorySize + 1));
        FactorioSignal[] toUse = new FactorioSignal[Math.min(FactorioSignal.values().length - 2, sqrt)];
        for(int i = 0, j = 0; i < toUse.length; i++, j++) {
            FactorioSignal signal = FactorioSignal.values()[j];
            if(signal == ADDRESS_SIGNAL || signal == WRITE_VALUE_SIGNAL) i--;
            toUse[i] = signal;
        }
        int requiredCellCount = (memorySize + toUse.length) / toUse.length;
        return generateMemoryController(requiredCellCount, toUse, readIn, readOut, writeIn);
    }

    private static CombinatorGroup generateMemoryController(int cellCount, FactorioSignal[] signals, NetworkGroup readIn, NetworkGroup readOut, NetworkGroup writeIn) {
        CombinatorGroup group = new CombinatorGroup(null, null);

        NetworkGroup memoryBankWriteCellIn = new NetworkGroup();
        NetworkGroup memoryBankWriteDataIn = new NetworkGroup();
        NetworkGroup memoryBankReadIn = new NetworkGroup();
        NetworkGroup memoryBankReadOut = new NetworkGroup();
        group.getNetworks().add(memoryBankReadIn);
        group.getNetworks().add(memoryBankReadOut);
        group.getNetworks().add(memoryBankWriteCellIn);
        group.getNetworks().add(memoryBankWriteDataIn);

        group.getCombinators().add(new Substation(5, 7));

        for(int i = 0; i < cellCount; i++) {
            group.getSubGroups().add(generateMemoryCell(i + 1, memoryBankWriteCellIn, memoryBankWriteDataIn, memoryBankReadIn, memoryBankReadOut));
        }
        group.getSubGroups().add(generateWrite(signals, writeIn, memoryBankReadIn, memoryBankReadOut, memoryBankWriteCellIn, memoryBankWriteDataIn));

        Queue<CombinatorGroup> toIterate = new LinkedList<>();
        toIterate.add(group);
        while(!toIterate.isEmpty()) {
            var cg = toIterate.poll();
            toIterate.addAll(cg.getSubGroups());
            for(var entity : cg.getCombinators()) {
                if(entity.getX() == 0 && entity.getY() == 0) System.out.println("0 0 entity");
                entity.setFixedLocation(true);
            }
        }

        return group;
    }

    private static CombinatorGroup generateWrite(FactorioSignal[] signals, NetworkGroup writeIn, NetworkGroup memoryBankReadIn, NetworkGroup memoryBankReadOut, NetworkGroup memoryBankWriteCellIn, NetworkGroup memoryBankWriteDataIn) {
        CombinatorGroup group = new CombinatorGroup(null, null);

        group.getNetworks().add(memoryBankWriteCellIn);
        group.getNetworks().add(memoryBankWriteDataIn);

        var tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        {
            //Read current data
            var c1 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
            c1.setPosition(4, 1, 4);
            var c2 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.GT);
            c2.setPosition(5, 1, 4);
            group.getCombinators().add(c1);
            group.getCombinators().add(c2);
            c1.setGreenIn(writeIn);
            c2.setGreenIn(writeIn);
            c1.setGreenOut(memoryBankReadIn);
            c2.setGreenOut(memoryBankReadIn);

            //Write new data
            var c3 = ArithmeticCombinator.copying(WRITE_VALUE_SIGNAL);
            c3.setPosition(6, 1, 4);
            var c4 = ArithmeticCombinator.copying(ADDRESS_SIGNAL);
            c4.setPosition(7, 1, 4);
            var c5 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
            c5.setPosition(8, 1, 4);
            var c6 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
            c6.setPosition(9, 1, 4);
            var c7 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
            c7.setPosition(10, 1, 4);

            group.getCombinators().add(c3);
            group.getCombinators().add(c4);
            group.getCombinators().add(c5);
            group.getCombinators().add(c6);
            group.getCombinators().add(c7);

            c3.setGreenIn(writeIn);
            c4.setGreenIn(writeIn);
            c5.setGreenIn(writeIn);
            c6.setGreenIn(writeIn);
            c7.setGreenIn(writeIn);

            c3.setGreenOut(tmp);
            c4.setGreenOut(tmp);
            c5.setGreenOut(tmp);
            c6.setGreenOut(tmp);
            c7.setGreenOut(tmp);
        }

        NetworkGroup writeSignalSelectorIn = new NetworkGroup();
        NetworkGroup writeSignalSelectorOut = new NetworkGroup();

        group.getNetworks().add(writeSignalSelectorIn);
        group.getNetworks().add(writeSignalSelectorOut);

        //Weird delay circuits incoming
        var cmb = ArithmeticCombinator.copying();
        cmb.setPosition(3, 0, 4);
        group.getCombinators().add(cmb);
        cmb.setGreenIn(tmp);

        cmb.setGreenOut(memoryBankReadOut);

        cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.MOD);
        group.getCombinators().add(cmb);
        cmb.setPosition(1, 0, 4);
        cmb.setGreenIn(memoryBankReadOut);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        cmb.setRedOut(tmp);

        cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.ADD);
        group.getCombinators().add(cmb);
        cmb.setPosition(0, 0, 4);
        cmb.setRedIn(tmp);
        cmb.setRedOut(writeSignalSelectorIn);

        //Write signal branch
        cmb = ArithmeticCombinator.copying(ADDRESS_SIGNAL);
        cmb.setPosition(0, 2, 4);
        group.getCombinators().add(cmb);
        cmb.setGreenIn(memoryBankReadOut);


        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        cmb.setRedOut(tmp);

        group.getSubGroups().add(generateWriteSignalSelector(signals, writeSignalSelectorIn, writeSignalSelectorOut));

        cmb = ArithmeticCombinator.copying(ADDRESS_SIGNAL);
        cmb.setPosition(1, 2, 4);
        group.getCombinators().add(cmb);
        cmb.setRedIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        cmb.setRedOut(tmp);

        cmb = ArithmeticCombinator.copying(WRITE_VALUE_SIGNAL);
        cmb.setPosition(2, 0, 4);
        group.getCombinators().add(cmb);
        cmb.setGreenIn(memoryBankReadOut);
        cmb.setRedOut(tmp);

        cmb = ArithmeticCombinator.copying();
        cmb.setPosition(2, 2, 4);
        group.getCombinators().add(cmb);
        cmb.setRedIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        cmb.setRedOut(tmp);

        cmb = ArithmeticCombinator.copying();
        cmb.setPosition(3, 2, 4);
        group.getCombinators().add(cmb);
        cmb.setRedIn(tmp);

        NetworkGroup memoryBusSignalEncoderIn = new NetworkGroup();
        group.getNetworks().add(memoryBusSignalEncoderIn);
        cmb.setRedOut(memoryBusSignalEncoderIn);

        {
            var c1 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(signals.length), ADDRESS_SIGNAL, ArithmeticOperator.DIV);
            c1.setPosition(0, 4, 4);
            var c2 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.GT);
            c2.setPosition(1, 4, 4);
            var c3 = ArithmeticCombinator.copying(WRITE_VALUE_SIGNAL);
            c3.setPosition(2, 4, 4);
            var c4 = ArithmeticCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.signal(WRITE_VALUE_SIGNAL), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
            c4.setPosition(3, 4, 4);
            group.getCombinators().add(c1);
            group.getCombinators().add(c2);
            group.getCombinators().add(c3);
            group.getCombinators().add(c4);

            c1.setRedIn(memoryBusSignalEncoderIn);
            c2.setRedIn(memoryBusSignalEncoderIn);
            c3.setRedIn(memoryBusSignalEncoderIn);
            c4.setRedIn(memoryBusSignalEncoderIn);

            c1.setRedOut(memoryBankWriteCellIn);
            c2.setRedOut(memoryBankWriteCellIn);

            var c5 = ArithmeticCombinator.withLeftRight(Accessor.constant(-1), Accessor.signal(ADDRESS_SIGNAL), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
            c5.setPosition(0, 6, 4);
            var c6 = DeciderCombinator.withAny(Accessor.constant(0), Writer.everything(false), DeciderOperator.NEQ);
            c6.setPosition(1, 6, 4);
            var c7 = ArithmeticCombinator.withEach(Accessor.signal(WRITE_VALUE_SIGNAL), ArithmeticOperator.MUL);
            c7.setPosition(2, 6, 4);
            var c8 = ArithmeticCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.constant(-1), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
            c8.setPosition(3, 6, 4);
            group.getCombinators().add(c5);
            group.getCombinators().add(c6);
            group.getCombinators().add(c7);
            group.getCombinators().add(c8);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            c3.setGreenOut(tmp);
            c7.setGreenIn(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            c4.setGreenOut(tmp);
            c8.setGreenIn(tmp);

            c5.setRedIn(memoryBankWriteCellIn);
            c6.setRedIn(memoryBankWriteCellIn);
            c7.setRedOut(memoryBankWriteCellIn);
            c8.setRedOut(memoryBankWriteCellIn);

            c5.setRedOut(memoryBankWriteDataIn);
            c6.setRedOut(memoryBankWriteDataIn);

            var c9 = ArithmeticCombinator.copying();
            c9.setPosition(1, 7, 2);
            group.getCombinators().add(c9);

            c9.setRedIn(writeSignalSelectorOut);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            c9.setRedOut(tmp);
            c7.setRedIn(tmp);
        }

        group.getSubGroups().add(generateWriteReadParser(signals, memoryBankReadOut, writeSignalSelectorOut, memoryBankWriteCellIn));

        return group;
    }

    private static CombinatorGroup generateWriteReadParser(FactorioSignal[] signals, NetworkGroup readOut, NetworkGroup writeSignalSelectorOut, NetworkGroup out) {
        var group = new CombinatorGroup(null, null);

        var c1 = ArithmeticCombinator.copying();
        c1.setPosition(8, 4, 0);
        var c2 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
        c2.setPosition(9, 4, 0);
        var c3 = ArithmeticCombinator.withLeftRight(Accessor.signal(WRITE_VALUE_SIGNAL), Accessor.constant(-1), WRITE_VALUE_SIGNAL, ArithmeticOperator.MUL);
        c3.setPosition(10, 4, 0);
        var c4 = ArithmeticCombinator.copying();
        c4.setPosition(7, 3, 6);
        var c5 = ArithmeticCombinator.copying();
        c5.setPosition(7, 4, 6);
        var c6 = ArithmeticCombinator.copying();
        c6.setPosition(7, 5, 6);
        var c7 = ArithmeticCombinator.copying();
        c7.setPosition(7, 6, 6);
        group.getCombinators().add(c1);
        group.getCombinators().add(c2);
        group.getCombinators().add(c3);
        group.getCombinators().add(c4);
        group.getCombinators().add(c5);
        group.getCombinators().add(c6);
        group.getCombinators().add(c7);

        c1.setGreenIn(readOut);
        c2.setGreenIn(readOut);
        c3.setGreenIn(readOut);

        var tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        c1.setRedOut(tmp);
        c2.setRedOut(tmp);
        c3.setRedOut(tmp);
        c4.setRedIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c4.setRedOut(tmp);
        c5.setRedIn(tmp);


        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        var writeReadDecoderDataIn = new NetworkGroup();
        group.getNetworks().add(writeReadDecoderDataIn);
        c5.setRedOut(tmp);
        c6.setRedIn(tmp);

        c6.setRedOut(writeReadDecoderDataIn);
        c7.setRedIn(writeReadDecoderDataIn);
        c7.setRedOut(out);

        var internal = new NetworkGroup();
        group.getNetworks().add(internal);
        var cc = new ConstantCombinator(Map.of(WRITE_VALUE_SIGNAL, -1));
        cc.setPosition(MEMORY_CELL_X_OFFSET + 6, MEMORY_CELL_Y_OFFSET - 1);
        group.getCombinators().add(cc);
        cc.setGreenOut(internal);
        for(int i = 0; i < signals.length; i++) {
            var signal = signals[i];
            var inCmb = DeciderCombinator.withLeftRight(Accessor.signal(signal), Accessor.constant(0), Writer.fromInput(WRITE_VALUE_SIGNAL), DeciderOperator.NEQ);
            inCmb.setPosition(MEMORY_CELL_X_OFFSET + 9, MEMORY_CELL_Y_OFFSET + i, 2);
            var outCmb = ArithmeticCombinator.withLeftRight(Accessor.signal(signal), Accessor.signal(WRITE_VALUE_SIGNAL), signal, ArithmeticOperator.MUL);
            outCmb.setPosition(MEMORY_CELL_X_OFFSET + 11, MEMORY_CELL_Y_OFFSET + i, 2);
            group.getCombinators().add(inCmb);
            group.getCombinators().add(outCmb);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);

            inCmb.setGreenIn(internal);
            inCmb.setRedIn(writeSignalSelectorOut);
            inCmb.setGreenOut(tmp);
            outCmb.setGreenIn(tmp);
            outCmb.setRedIn(writeReadDecoderDataIn);
            outCmb.setRedOut(out);
        }

        return group;
    }

    private static CombinatorGroup generateWriteSignalSelector(FactorioSignal[] signals, NetworkGroup in, NetworkGroup out) {
        CombinatorGroup group = new CombinatorGroup(in, out);
        NetworkGroup constantNetwork = new NetworkGroup();
        group.getNetworks().add(constantNetwork);
        for(int i = 0; i < (signals.length + 19) / 20; i++) {
            Map<FactorioSignal, Integer> values = new HashMap<>();
            for(int j = 0; j < Math.min(20, signals.length - i * 20); j++) {
                int id = i * 20 + j;
                values.put(signals[id], id + 1);
            }
            var cmb = new ConstantCombinator(values);
            cmb.setPosition(7,8 + i);
            group.getCombinators().add(cmb);
            cmb.setGreenOut(constantNetwork);
        }

        ArithmeticCombinator c1 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(1), ADDRESS_SIGNAL, ArithmeticOperator.SUB);
        c1.setPosition(5, 5, 2);
        DeciderCombinator c2 = DeciderCombinator.withEach(Accessor.signal(ADDRESS_SIGNAL), true, DeciderOperator.EQ);
        c2.setPosition(5, 4, 2);
        ArithmeticCombinator c3 = ArithmeticCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(-1), ADDRESS_SIGNAL, ArithmeticOperator.MUL);
        c3.setPosition(5, 3, 2);
        DeciderCombinator c4 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.one(ADDRESS_SIGNAL), DeciderOperator.EQ);
        c4.setPosition(5, 2, 2);

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

    private static CombinatorGroup generateMemoryCell(int cellId, NetworkGroup writeCellIn, NetworkGroup writeDataIn, NetworkGroup readIn, NetworkGroup readOut) {
        CombinatorGroup group = new CombinatorGroup(null, null);
        var c1 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(cellId), Writer.one(ADDRESS_SIGNAL), DeciderOperator.EQ);
        c1.setPosition(MEMORY_CELL_X_OFFSET, MEMORY_CELL_Y_OFFSET + cellId - 1, 2);
        var c2 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        c2.setPosition(MEMORY_CELL_X_OFFSET + 2, MEMORY_CELL_Y_OFFSET + cellId - 1, 2);
        var c3 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(0), Writer.everything(false), DeciderOperator.EQ);
        c3.setPosition(MEMORY_CELL_X_OFFSET + 4, MEMORY_CELL_Y_OFFSET + cellId - 1, 2);
        var c4 = DeciderCombinator.withLeftRight(Accessor.signal(ADDRESS_SIGNAL), Accessor.constant(cellId), Writer.everything(false), DeciderOperator.EQ);
        c4.setPosition(MEMORY_CELL_X_OFFSET + 6, MEMORY_CELL_Y_OFFSET + cellId - 1, 2);

        group.getCombinators().add(c1);
        group.getCombinators().add(c2);
        group.getCombinators().add(c3);
        group.getCombinators().add(c4);

        c1.setRedIn(writeCellIn);
        c2.setRedIn(writeDataIn);
        c4.setGreenIn(readIn);
        c4.setGreenOut(readOut);

        NetworkGroup tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c1.setGreenOut(tmp);
        c2.setGreenIn(tmp);
        c3.setGreenIn(tmp);

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);
        c2.setRedOut(tmp);
        c3.setRedIn(tmp);
        c3.setRedOut(tmp);
        c4.setRedIn(tmp);

        return group;
    }
}
