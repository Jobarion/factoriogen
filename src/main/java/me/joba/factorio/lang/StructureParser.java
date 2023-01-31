package me.joba.factorio.lang;

import me.joba.factorio.lang.types.ArrayType;
import me.joba.factorio.lang.types.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StructureParser extends LanguageBaseListener {

    private Map<String, FunctionContext> functions = new HashMap<>();
    private Map<String, ArrayDeclaration> declaredArrays = new HashMap<>();
    private int currentArrayAddress = 1;

    public Map<String, FunctionContext> getFunctions() {
        return functions;
    }

    public Map<String, ArrayDeclaration> getDeclaredArrays() {
        return declaredArrays;
    }

    @Override
    public void exitArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx) {
        var type = Type.parseType(ctx.type());
        var name = ctx.varName().getText();
        var size = Integer.parseInt(ctx.intLiteral().getText());

        var declaration = new ArrayDeclaration(new ArrayType(type), currentArrayAddress, size);
        currentArrayAddress += size * type.getSize();
        declaredArrays.put(name, declaration);
        System.out.println("Declared array " + name + " of type " + type + "[" + size + "] at with address range " + declaration.getAddress() + " - " + (currentArrayAddress - 1));
    }

    @Override
    public void exitFunction(LanguageParser.FunctionContext ctx) {
        String name = ctx.functionHeader().functionName().getText();
        Type returnType = Type.parseType(ctx.functionHeader().returnType);
        FunctionParameter[] paramTypes = new FunctionParameter[ctx.functionHeader().functionParams().functionParam().size()];
        for(int i = 0; i < paramTypes.length; i++) {
            var param = ctx.functionHeader().functionParams().functionParam(i);
            var type = Type.parseType(param.type());
            FactorioSignal signal = null;
            if(param.signalName() != null) {
                signal = FactorioSignal.valueOf("SIGNAL_" + param.signalName().getText().toUpperCase().replace('-', '_'));
            }
            paramTypes[i] = new FunctionParameter(param.varName().getText(), type, signal == null ? null : new FactorioSignal[]{signal});
        }
        var modifiers = ctx.functionHeader().functionModifiers().functionModifier();
        if(modifiers == null) modifiers = Collections.emptyList();

        var signatureBuilder = new FunctionSignature.Builder(name, paramTypes, returnType, getFunctionReturnSignals(returnType));

        for(var modifier : modifiers) {
            switch (modifier.key.getText()) {
                case "native" -> signatureBuilder.asNative(true);
                case "pipelined" -> signatureBuilder.asPipelined(true);
                case "delay" -> signatureBuilder.withDelay(Integer.parseInt(modifier.intLiteral().getText()));
            }
        }
        FunctionContext context = new FunctionContext(signatureBuilder.build());
        functions.put(name, context);
    }

    private static FactorioSignal[] getFunctionReturnSignals(Type type) {
        var returnSignals = new FactorioSignal[type.getSize()];
        var allSignals = FactorioSignal.values();
        int cursor = 0;
        for(int i = 0; i < returnSignals.length; i++) {
            FactorioSignal signal;
            while((signal = allSignals[cursor++]).isReserved());
            returnSignals[i] = signal;
        }
        return returnSignals;
    }
}
