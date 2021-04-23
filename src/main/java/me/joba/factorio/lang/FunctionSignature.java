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

    public FunctionSignature(String name, FunctionParameter[] parameters, Type returnType, FactorioSignal[] returnSignals) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.returnSignals = returnSignals;
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
        return name + "(" + paramJoiner + ") -> " + returnType;
    }
}
