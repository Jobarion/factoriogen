package me.joba.factorio.lang;

import me.joba.factorio.*;

public class CombinatorUtil {

    public static final FactorioSignal CONTROL_FLOW_SIGNAL = FactorioSignal.SIGNAL_CHECK;
    public static final FactorioSignal TEMP_SIGNAL = FactorioSignal.SIGNAL_DOT;

    //More complicated one that buffers the last legit input signal, and returns signals only if controlSignalIn is set
//    public static CombinatorGroup generateIOGate(NetworkGroup signalIn, NetworkGroup signalOut, NetworkGroup controlSignalIn, boolean requireCleanOutput) {
//        CombinatorGroup group = new CombinatorGroup(signalIn, signalOut);
//        group.getNetworks().add(controlSignalIn);
//
//        ConnectedCombinator inputData = new ConnectedCombinator(DeciderCombinator.withLeftRight(
//                Accessor.signal(CONTROL_FLOW_SIGNAL),
//                Accessor.constant(0),
//                Writer.everything(false),
//                DeciderCombinator.NEQ));
//        group.getCombinators().add(inputData);
//        inputData.setGreenIn(signalIn);
//
//        ConnectedCombinator inputDataDelay = new ConnectedCombinator(ArithmeticCombinator.copying());
//        group.getCombinators().add(inputDataDelay);
//
//        NetworkGroup tmp = new NetworkGroup();
//        group.getNetworks().add(tmp);
//        inputData.setGreenOut(tmp);
//        inputDataDelay.setGreenIn(tmp);
//
//        ConnectedCombinator inputDataReset = new ConnectedCombinator(DeciderCombinator.withLeftRight(
//                Accessor.signal(CONTROL_FLOW_SIGNAL),
//                Accessor.constant(0),
//                Writer.constant(TEMP_SIGNAL.ordinal(), 1),
//                DeciderCombinator.NEQ));
//        group.getCombinators().add(inputDataReset);
//        inputDataReset.setGreenIn(signalIn);
//
//        ConnectedCombinator memory = new ConnectedCombinator(DeciderCombinator.withLeftRight(
//                Accessor.signal(TEMP_SIGNAL),
//                Accessor.constant(0),
//                Writer.everything(false),
//                DeciderCombinator.EQ));
//        group.getCombinators().add(memory);
//
//        NetworkGroup memoryData = new NetworkGroup();
//        group.getNetworks().add(memoryData);
//        inputDataDelay.setGreenOut(memoryData);
//        memory.setGreenIn(memoryData);
//        memory.setGreenOut(memoryData);
//
//        ConnectedCombinator inputControl = new ConnectedCombinator(ArithmeticCombinator.withLeftRight(
//                Accessor.signal(CONTROL_FLOW_SIGNAL),
//                Accessor.constant(0),
//                TEMP_SIGNAL.ordinal(),
//                ArithmeticCombinator.ADD
//        ));
//        group.getCombinators().add(inputControl);
//        inputControl.setGreenIn(controlSignalIn);
//
//        NetworkGroup resetSignal = new NetworkGroup();
//        group.getNetworks().add(resetSignal);
//        inputControl.setRedOut(resetSignal);
//        inputDataReset.setRedOut(resetSignal);
//        memory.setRedIn(resetSignal);
//
//        ConnectedCombinator outputGate = new ConnectedCombinator(DeciderCombinator.withLeftRight(
//                Accessor.signal(TEMP_SIGNAL),
//                Accessor.constant(0),
//                Writer.everything(false),
//                DeciderCombinator.NEQ));
//        group.getCombinators().add(outputGate);
//
//        tmp = new NetworkGroup();
//        group.getNetworks().add(tmp);
//        outputGate.setGreenIn(tmp);
//        inputControl.setGreenOut(tmp);
//
//        tmp = new NetworkGroup();
//        group.getNetworks().add(tmp);
//        inputDataDelay.setRedOut(tmp);
//        memory.setRedOut(tmp);
//        outputGate.setRedIn(tmp);
//
//        if(requireCleanOutput) {
//            ConnectedCombinator copySignal = new ConnectedCombinator(ArithmeticCombinator.copying());
//            group.getCombinators().add(copySignal);
//
//            ConnectedCombinator subtractSignal = new ConnectedCombinator(ArithmeticCombinator.withLeftRight(
//                    Accessor.signal(TEMP_SIGNAL),
//                    Accessor.constant(-1),
//                    TEMP_SIGNAL.ordinal(),
//                    ArithmeticCombinator.MUL
//            ));
//            group.getCombinators().add(copySignal);
//
//            tmp = new NetworkGroup();
//            group.getNetworks().add(tmp);
//            copySignal.setGreenIn(tmp);
//            subtractSignal.setGreenIn(tmp);
//            outputGate.setGreenOut(tmp);
//
//            copySignal.setGreenOut(signalOut);
//            subtractSignal.setGreenOut(signalOut);
//        }
//        else {
//            outputGate.setGreenOut(signalOut);
//        }
//        return group;
//    }
}
