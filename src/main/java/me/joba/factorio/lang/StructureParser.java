package me.joba.factorio.lang;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.lang.types.ArrayType;
import me.joba.factorio.lang.types.Type;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Map<String, List<String>> functionCallDependencies = new HashMap<>();
    private String currentFunction;

    private final NetworkGroup FUNCTION_CALL_IN = new NetworkGroup("Global function call out");
    private final NetworkGroup FUNCTION_CALL_RETURN = new NetworkGroup("Global function call return");

    public NetworkGroup getFunctionCallIn() {
        return FUNCTION_CALL_IN;
    }

    public NetworkGroup getFunctionCallReturn() {
        return FUNCTION_CALL_RETURN;
    }

    public List<List<String>> getCompileOrder() {
        var list = new ArrayList<List<String>>();
        var localMap = functionCallDependencies.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> new ArrayList<>(v.getValue())));

        while(!localMap.isEmpty()) {
            var currentGroup = new ArrayList<String>();
            for(var e : localMap.entrySet()) {
                if(e.getValue().isEmpty()) {
                    currentGroup.add(e.getKey());
                }
            }
            if(!localMap.values().removeIf(List::isEmpty)) {
                throw new IllegalStateException("Cyclic dependencies between functions " + localMap.keySet());
            }
            list.add(currentGroup);
            for(var v : localMap.values()) {
                v.removeAll(currentGroup);
            }
        }
        return list;
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
    public void exitFunctionCall(LanguageParser.FunctionCallContext ctx) {
        functionCallDependencies.get(currentFunction).add(ctx.functionName().getText());
    }

    @Override
    public void exitFunction(LanguageParser.FunctionContext ctx) {
        functions.get(ctx.functionHeader().functionName().getText()).setCode(ctx.getText());
    }

    @Override
    public void exitFunctionHeader(LanguageParser.FunctionHeaderContext ctx) {
        String name = ctx.functionName().getText();
        currentFunction = name;
        functionCallDependencies.put(name, new ArrayList<>());
        Type returnType = Type.parseType(ctx.returnType);
        FunctionParameter[] paramTypes = new FunctionParameter[ctx.functionParams().functionParam().size()];
        for(int i = 0; i < paramTypes.length; i++) {
            var param = ctx.functionParams().functionParam(i);
            var type = Type.parseType(param.type());
            FactorioSignal signal = null;
            if(param.signalName() != null) {
                signal = FactorioSignal.valueOf("SIGNAL_" + param.signalName().getText().toUpperCase().replace('-', '_'));
            }
            paramTypes[i] = new FunctionParameter(param.varName().getText(), type, signal == null ? null : new FactorioSignal[]{signal});
        }
        var modifiers = ctx.functionModifiers().functionModifier();
        if(modifiers == null) modifiers = Collections.emptyList();

        var signatureBuilder = new FunctionSignature.Builder(name, paramTypes, returnType, getFunctionReturnSignals(returnType));

        for(var modifier : modifiers) {
            switch (modifier.key.getText()) {
                case "native" -> signatureBuilder.asNative(true);
                case "fixed_delay" -> {
                    if(modifier.intLiteral() != null) {
                        signatureBuilder.withConstantDelay(Integer.parseInt(modifier.intLiteral().getText()));
                    }
                    else {
                        signatureBuilder.withConstantDelay(true);//Delay is derived
                    }
                }
            }
        }
        FunctionContext context = new FunctionContext(signatureBuilder.build(), FUNCTION_CALL_IN, FUNCTION_CALL_RETURN);
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
