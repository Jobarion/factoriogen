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

    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
    public static CombinatorGroup generateMemoryController(int cellCount, FactorioSignal[] signals, NetworkGroup readIn, NetworkGroup readOut, NetworkGroup writeIn) {
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

        //Weird delay circuits incoming
        var cmb = ArithmeticCombinator.copying();
        cmb.setPosition(3, 0, 4);
        group.getCombinators().add(cmb);
        cmb.setGreenIn(tmp);

        cmb.setGreenOut(memoryBankReadOut);
        //Write signal branch
        cmb = ArithmeticCombinator.copying(ADDRESS_SIGNAL);
        cmb.setPosition(0, 2, 4);
        group.getCombinators().add(cmb);
        cmb.setGreenIn(memoryBankReadOut);

        NetworkGroup writeSignalSelectorIn = new NetworkGroup();
        NetworkGroup writeSignalSelectorOut = new NetworkGroup();
        group.getNetworks().add(writeSignalSelectorIn);
        group.getNetworks().add(writeSignalSelectorOut);

        cmb.setRedOut(writeSignalSelectorIn);

        group.getSubGroups().add(generateWriteSignalSelector(signals, writeSignalSelectorIn, writeSignalSelectorOut));

        tmp = new NetworkGroup();
        group.getNetworks().add(tmp);

        cmb = ArithmeticCombinator.copying(ADDRESS_SIGNAL);
        cmb.setPosition(1, 2, 4);
        group.getCombinators().add(cmb);
        cmb.setRedIn(writeSignalSelectorIn);
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
            var c10 = ArithmeticCombinator.copying();
            c10.setPosition(3, 7, 2);
            group.getCombinators().add(c9);
            group.getCombinators().add(c10);

            c9.setRedIn(writeSignalSelectorOut);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            c9.setRedOut(tmp);
            c10.setRedIn(tmp);

            tmp = new NetworkGroup();
            group.getNetworks().add(tmp);
            c10.setRedOut(tmp);
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

        var writeReadDecoderDataIn = new NetworkGroup();
        group.getNetworks().add(writeReadDecoderDataIn);
        c5.setRedOut(writeReadDecoderDataIn);
        c6.setRedIn(writeReadDecoderDataIn);

        var writeReadDecoderDataOut = new NetworkGroup();
        group.getNetworks().add(writeReadDecoderDataOut);
        c6.setRedOut(writeReadDecoderDataOut);
        c7.setRedIn(writeReadDecoderDataOut);

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
            outCmb.setRedOut(writeReadDecoderDataOut);
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
