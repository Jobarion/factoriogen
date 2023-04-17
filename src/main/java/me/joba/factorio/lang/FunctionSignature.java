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
    private int constantDelay;
    private final boolean isNative;
    private final boolean isConstantDelay;
    private final SideEffectsType sideEffectsType;

    public FunctionSignature(String name, FunctionParameter[] parameters, Type returnType, FactorioSignal[] returnSignals, int delay, boolean constDelay, boolean isNative, SideEffectsType sideEffectsType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.returnSignals = returnSignals;
        this.constantDelay = delay;
        this.isConstantDelay = constDelay;
        this.isNative = isNative;
        this.sideEffectsType = sideEffectsType;
    }

    public boolean isConstantDelay() {
        return isConstantDelay;
    }

    public boolean isNative() {
        return isNative;
    }

    public SideEffectsType getSideEffectsType() {
        return sideEffectsType;
    }

    public void setConstantDelay(int constantDelay) {
        this.constantDelay = constantDelay;
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
        private boolean isConstantDelay = false, isNative = false;
        private SideEffectsType sideEffectsType = SideEffectsType.ANY;

        public Builder(String name, FunctionParameter[] parameters, Type returnType, FactorioSignal[] returnSignals) {
            this.name = name;
            this.parameters = parameters;
            this.returnType = returnType;
            this.returnSignals = returnSignals;
        }

        public Builder withConstantDelay(int delay) {
            this.isConstantDelay = true;
            this.constantDelay = delay;
            return this;
        }

        public Builder withConstantDelay(boolean isConstantDelay) {
            this.isConstantDelay = isConstantDelay;
            return this;
        }

        public Builder asNative(boolean isNative) {
            this.isNative = isNative;
            return this;
        }

        public Builder withSideEffects(SideEffectsType sideEffectsType) {
            this.sideEffectsType = sideEffectsType;
            return this;
        }

        public FunctionSignature build() {
            if(isNative && isConstantDelay && constantDelay == -1) {
                throw new IllegalArgumentException("Native constant delay functions cannot derive the delay value");
            }
            else if(!isNative && isConstantDelay && constantDelay != -1) {
                throw new IllegalArgumentException("Non native constant delay functions must not specify the delay value");
            }
            return new FunctionSignature(name, parameters, returnType, returnSignals, constantDelay, isConstantDelay, isNative, sideEffectsType);
        }
    }

    public enum SideEffectsType {
        PURE,
        IDEMPOTENT_READ,
        IDEMPOTENT_WRITE,
        ANY
    }
}
