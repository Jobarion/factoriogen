package me.joba.factorio.lang;

import me.joba.factorio.lang.types.Type;

import java.util.Arrays;
import java.util.StringJoiner;

public class FunctionSignature {

    private static int currentFunctionId = 1;

    private final String name;
    private final FunctionParameter[] parameters;
    private final Type returnType;
    private final FactorioSignal[] returnSignals;
    private final int functionId = currentFunctionId++;
    private final int constantDelay;
    private final boolean pipelined, isNative;

    public FunctionSignature(String name, FunctionParameter[] parameters, Type returnType, FactorioSignal[] returnSignals, int delay, boolean pipelined, boolean isNative) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.returnSignals = returnSignals;
        this.constantDelay = delay;
        this.pipelined = pipelined;
        this.isNative = isNative;
    }

    public boolean isPipelined() {
        return pipelined;
    }

    public boolean isNative() {
        return isNative;
    }

    public boolean isConstantDelay() {
        return constantDelay >= 0;
    }

    public int getConstantDelay() {
        return constantDelay;
    }

    public String getName() {
        return name;
    }

    public FunctionParameter[] getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public FactorioSignal[] getReturnSignals() {
        return returnSignals;
    }

    public int getFunctionId() {
        return functionId;
    }

    @Override
    public String toString() {
        StringJoiner paramJoiner = new StringJoiner(", ");
        for(var param : parameters) {
            paramJoiner.add(param.getName() + ": " + param.getType() + "<" + Arrays.toString(param.getSignal()) + ">");
        }

        return name + "(" + paramJoiner + ") -> " + returnType + "<" + Arrays.toString(returnSignals) + ">";
    }

    public static class Builder {

        private final String name;
        private final FunctionParameter[] parameters;
        private final Type returnType;
        private final FactorioSignal[] returnSignals;
        private int constantDelay = -1;
        private boolean pipelined = false, isNative = false;

        public Builder(String name, FunctionParameter[] parameters, Type returnType, FactorioSignal[] returnSignals) {
            this.name = name;
            this.parameters = parameters;
            if(returnType.getSize() != returnSignals.length) throw new IllegalArgumentException(returnType + " has different size than " + Arrays.toString(returnSignals));
            this.returnType = returnType;
            this.returnSignals = returnSignals;
        }

        public Builder withDelay(int delay) {
            this.constantDelay = delay;
            return this;
        }

        public Builder asPipelined(boolean isPipelined) {
            this.pipelined = isPipelined;
            return this;
        }

        public Builder asNative(boolean isNative) {
            this.isNative = isNative;
            return this;
        }

        public FunctionSignature build() {
            return new FunctionSignature(name, parameters, returnType, returnSignals, constantDelay, pipelined, isNative);
        }
    }
}
