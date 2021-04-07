package me.joba.factorio.lang;

import java.util.StringJoiner;

public class FunctionSignature {

    private static int currentFunctionId = 1;

    private final String name;
    private final FunctionParameter[] parameters;
    private final Type returnType;
    private final int functionId = currentFunctionId++;

    public FunctionSignature(String name, FunctionParameter[] parameters, Type returnType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
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

    public int getFunctionId() {
        return functionId;
    }

    @Override
    public String toString() {
        StringJoiner paramJoiner = new StringJoiner(", ");
        for(var param : parameters) {
            paramJoiner.add(param.getName() + ": " + param.getType() + "<" + param.getSignal() + ">");
        }
        return name + "(" + paramJoiner + ") -> " + returnType;
    }
}
