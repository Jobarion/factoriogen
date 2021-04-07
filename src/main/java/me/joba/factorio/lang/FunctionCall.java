package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

public class FunctionCall {

    private static int currentFunctionCallId = 1;

    private final FunctionContext called;
    private final NetworkGroup callerOut;
    private final CombinatorGroup callerReturn;
    private final int functionCallId = currentFunctionCallId++;

    public FunctionCall(FunctionContext called, NetworkGroup callerOut, CombinatorGroup callerReturn) {
        this.called = called;
        this.callerOut = callerOut;
        this.callerReturn = callerReturn;
    }

    public int getFunctionCallId() {
        return functionCallId;
    }

    public FunctionContext getCalled() {
        return called;
    }

    public NetworkGroup getCallerOut() {
        return callerOut;
    }

    public CombinatorGroup getCallerReturn() {
        return callerReturn;
    }
}
