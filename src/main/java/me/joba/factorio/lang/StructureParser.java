package me.joba.factorio.lang;

import java.util.HashMap;
import java.util.Map;

public class StructureParser extends LanguageBaseListener {

    private Map<String, FunctionContext> functions = new HashMap<>();

    public Map<String, FunctionContext> getFunctions() {
        return functions;
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
            paramTypes[i] = new FunctionParameter(param.varName().getText(), type, signal);
        }
        FunctionSignature signature = new FunctionSignature(name, paramTypes, returnType);
        FunctionContext context = new FunctionContext(signature);
        functions.put(name, context);
    }
}
