package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.lang.types.PrimitiveType;
import me.joba.factorio.lang.types.Type;

import java.util.*;

import static me.joba.factorio.lang.Generator.CONSTANT_DELAY_FUNCTION_OVERHEAD;

public class FunctionContext {

    public static final String CONTROL_FLOW_VAR_NAME = "__internal__CONTROL_FLOW";

    private int variableIdCounter = 0;
    private Set<FactorioSignal> freeBindings;
    private Stack<Stack<Symbol>> tempVariables;
    private Stack<VariableScope> variables;
    private Stack<ConditionContext> conditionContexts;
    private CombinatorGroup functionHeader;
    private CombinatorGroup functionReturn;
    private final FunctionSignature functionSignature;
    private final Set<Integer> takenFunctionCallSendSlots = new HashSet<>();
    private final Set<Integer> takenFunctionCallReturnSlots = new HashSet<>();
    private final NetworkGroup functionCallOutput;
    private final NetworkGroup functionCallReturn;
    private final Map<FunctionSignature.SideEffectsType, Integer> firstAvailableSlotBySideEffectType;
    private String code;

    public FunctionContext(FunctionSignature functionSignature, NetworkGroup functionCallOutput, NetworkGroup functionCallReturn) {
        this.functionSignature = functionSignature;
        tempVariables = new Stack<>();
        variables = new Stack<>();
        variables.push(new VariableScope(null));
        freeBindings = new HashSet<>();
        freeBindings.addAll(Arrays.asList(FactorioSignal.values()));
        freeBindings.removeIf(FactorioSignal::isReserved);
        conditionContexts = new Stack<>();
        firstAvailableSlotBySideEffectType = new HashMap<>();
        this.functionCallOutput = functionCallOutput;
        this.functionCallReturn = functionCallReturn;
        for(var type : FunctionSignature.SideEffectsType.values()) {
            firstAvailableSlotBySideEffectType.put(type, 0);
        }
//        functionCallOutput = new NetworkGroup("Function call out of group " + functionSignature);
//        functionCallReturn = new NetworkGroup("Function call return of group " + functionSignature);

        bindParameterSignals();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    private void bindParameterSignals() {
        for(var param : functionSignature.getParameters()) {
            if(param.getSignal() != null) {
                this.claimSymbol(param.getSignal());
            }
        }
        for(var param : functionSignature.getParameters()) {
            if(param.getSignal() == null) {
                param.setSignal(getFreeSymbols(param.getType().getSize()));
            }
            else {
                for(FactorioSignal signal : param.getSignal()) {
                    if(signal.isReserved()) throw new IllegalArgumentException("Signal " + Arrays.toString(param.getSignal()) + " is reserved");
                }
            }
        }
    }

    public void clearFunctionCallSlotReservations() {
        takenFunctionCallSendSlots.clear();
        takenFunctionCallReturnSlots.clear();
        for(var type : FunctionSignature.SideEffectsType.values()) {
            firstAvailableSlotBySideEffectType.put(type, 0);
        }
    }

    public int getCurrentSideEffectsDelay(FunctionSignature.SideEffectsType type) {
        return firstAvailableSlotBySideEffectType.get(type);
    }

    public int reserveFunctionCallSlot(FunctionSignature signature, int earliestStartTime) {
        if(signature.getReturnType() != PrimitiveType.VOID && !signature.isConstantDelay()) throw new IllegalArgumentException(signature + " is not constant delay");
        earliestStartTime = Math.max(earliestStartTime, firstAvailableSlotBySideEffectType.get(signature.getSideEffectsType()));
        if(signature.getReturnType() != PrimitiveType.VOID) {
            for(;;earliestStartTime++) {
                if(!takenFunctionCallSendSlots.contains(earliestStartTime) && !takenFunctionCallReturnSlots.contains(earliestStartTime + signature.getConstantDelay() + CONSTANT_DELAY_FUNCTION_OVERHEAD)) {
                    break;
                }
            }
            takenFunctionCallSendSlots.add(earliestStartTime);
            takenFunctionCallReturnSlots.add(earliestStartTime + signature.getConstantDelay() + CONSTANT_DELAY_FUNCTION_OVERHEAD);
        }
        else {
            for(;;earliestStartTime++) {
                if(!takenFunctionCallSendSlots.contains(earliestStartTime)) {
                    break;
                }
            }
            takenFunctionCallSendSlots.add(earliestStartTime);
        }
        updateSideEffectOrdering(signature, earliestStartTime);
        return earliestStartTime;
    }

    private void updateSideEffectOrdering(FunctionSignature signature, int callTime) {
        //TODO this just hard codes array read after write delay :(
        if(signature == MemoryUtil.MEMORY_READ_SIGNATURE || signature == MemoryUtil.MEMORY_WRITE_SIGNATURE) {
            switch (signature.getSideEffectsType()) {
                case ANY -> {
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.ANY, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_READ, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE, callTime);
                }
                case IDEMPOTENT_WRITE -> {
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.ANY, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_READ, callTime + 6);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE, callTime);
                }
                case IDEMPOTENT_READ -> {
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.ANY, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE, Math.max(0, callTime - 5));
                }
            }
        }
        else {
            switch (signature.getSideEffectsType()) {
                case ANY -> {
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.ANY, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_READ, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE, callTime);
                }
                case IDEMPOTENT_WRITE -> {
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.ANY, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_READ, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE, callTime);
                }
                case IDEMPOTENT_READ -> {
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.ANY, callTime);
                    setMax(firstAvailableSlotBySideEffectType, FunctionSignature.SideEffectsType.IDEMPOTENT_WRITE, callTime);
                }
            }
        }
    }

    private  <T> int setMax(Map<T, Integer> map, T key, int newVal) {
        return map.compute(key, (k, v) -> v == null ? newVal : Math.max(v, newVal));
    }

    public void setFunctionHeader(CombinatorGroup functionHeader) {
        if(this.functionHeader != null) throw new RuntimeException("Already set");
        this.functionHeader = functionHeader;
        this.functionHeader.getNetworks().add(functionCallOutput);
        this.functionHeader.getNetworks().add(functionCallReturn);
        functionReturn = new CombinatorGroup(null, functionCallReturn);
        functionHeader.getSubGroups().add(functionReturn);
        for(var param : functionSignature.getParameters()) {
            this.createNamedVariable(param.getName(), param.getType(), param.getSignal(), functionHeader).setDelay(0);
        }
    }

    public NetworkGroup getFunctionCallOutputGroup() {
        return functionCallOutput;
    }

    public NetworkGroup getFunctionCallReturnGroup() {
        return functionCallReturn;
    }

    public FunctionSignature getSignature() {
        return functionSignature;
    }

    public String getName() {
        return functionSignature.getName();
    }

    public Variable getControlFlowVariable() {
        return getNamedVariable(CONTROL_FLOW_VAR_NAME);
    }

    public Variable overwriteControlFlowVariable(CombinatorGroup producer) {
        return createNamedVariable(CONTROL_FLOW_VAR_NAME, PrimitiveType.INT, new FactorioSignal[]{Constants.CONTROL_FLOW_SIGNAL}, producer);
    }

    public void pushTempVariable(Symbol var) {
        tempVariables.peek().push(var);
    }

    public Variable createBoundTempVariable(Type type, FactorioSignal[] signal, CombinatorGroup producer) {
        var var = new Variable(type, variableIdCounter++, signal, producer);
        pushTempVariable(var);
        return var;
    }

    public Symbol popTempVariable() {
        return tempVariables.peek().pop();
    }

    public Stack<Symbol> getTempVariables() {
        return tempVariables.peek();
    }

    //TODO create named constant
    public Variable createNamedVariable(String name, Type type, FactorioSignal[] signal, CombinatorGroup producer) {
        return variables.peek().createNamedVariable(name, type, signal, producer);
    }

    public Variable getNamedVariable(String name) {
        return variables.peek().getNamedVariable(name);
    }

    public void enterLoop() {
        var preConditionGroup = new CombinatorGroup(null, new NetworkGroup("while pre condition out"));
        var postConditionGroup = new CombinatorGroup(null, new NetworkGroup("while post condition out"));
        variables.push(new WhileVariableScope(variables.peek(), preConditionGroup, postConditionGroup));
    }

    public int getDepth() {
        return variables.size();
    }

    public VariableScope getVariableScope() {
        return variables.peek();
    }

    public void leaveLoop() {
        variables.pop();
    }

    public void enterIfStatement() {
        variables.push(conditionContexts.peek().getIfScope());
    }

    public void enterElseStatement() {
        variables.pop(); //The if scope
        variables.push(conditionContexts.peek().getElseScope());
    }

    public void enterConditional() {
        VariableScope ifScope = new VariableScope(getVariableScope());
        VariableScope elseScope = new VariableScope(getVariableScope());

        CombinatorGroup ifVarProvider = new CombinatorGroup(null, new NetworkGroup("if var supply"));
        CombinatorGroup elseVarProvider = new CombinatorGroup(null, new NetworkGroup("else var supply"));

        //Make all currently existing variables available to if and else scopes
        for(var e : getVariableScope().getAllVariables().entrySet()) {
            ifScope.createNamedVariable(e.getKey(), e.getValue().getType(), e.getValue().getSignal(), ifVarProvider).setDelay(0);
            elseScope.createNamedVariable(e.getKey(), e.getValue().getType(), e.getValue().getSignal(), elseVarProvider).setDelay(0);
        }

        conditionContexts.push(new ConditionContext(ifScope, elseScope, ifVarProvider, elseVarProvider));
    }

    public ConditionContext leaveConditional() {
        variables.pop(); //the last if/else/elseif block
        return conditionContexts.pop();
    }

    public ConditionContext getConditionContext() {
        return conditionContexts.peek();
    }

    public FactorioSignal getFreeSymbol() {
        var signal = freeBindings.iterator().next();
        freeBindings.remove(signal);
        return signal;
    }

    public FactorioSignal[] getFreeSymbols(int count) {
        FactorioSignal[] signals = new FactorioSignal[count];
        var iter = freeBindings.iterator();
        for(int i = 0; i < count; i++) {
            signals[i] = iter.next();
            iter.remove();
        }
        return signals;
    }

    public boolean isSymbolFree(FactorioSignal... signals) {
        for(var s : signals) {
            if(!freeBindings.contains(s)) return false;
        }
        return true;
    }

    public void claimSymbol(FactorioSignal... signals) {
        for(FactorioSignal signal : signals) {
            if(!freeBindings.remove(signal)) {
                throw new IllegalArgumentException("Attempted to claim unavailable symbol " + signal);
            }
        }
    }

    public void unclaimSymbols(FactorioSignal[] tempSymbols) {
        freeBindings.addAll(Arrays.asList(tempSymbols));
    }

    public CombinatorGroup getFunctionGroup() {
        return functionHeader;
    }

    public CombinatorGroup getFunctionReturnGroup() {
        return functionReturn;
    }

    public void enterStatement() {
        tempVariables.push(new Stack<>());
    }

    public void exitStatement() {
        var previous = tempVariables.pop();
        if(!previous.isEmpty()) throw new RuntimeException("Expression stack not empty");
    }

    public class ConditionContext {

        private final VariableScope ifScope;
        private final VariableScope elseScope;
        private final CombinatorGroup ifProvider;
        private final CombinatorGroup elseProvider;


        public ConditionContext(VariableScope ifScope, VariableScope elseScope, CombinatorGroup ifProvider, CombinatorGroup elseProvider) {
            this.ifScope = ifScope;
            this.elseScope = elseScope;
            this.ifProvider = ifProvider;
            this.elseProvider = elseProvider;
        }

        public VariableScope getIfScope() {
            return ifScope;
        }
        public VariableScope getElseScope() {
            return elseScope;
        }

        public CombinatorGroup getIfProvider() {
            return ifProvider;
        }

        public CombinatorGroup getElseProvider() {
            return elseProvider;
        }
    }
}
