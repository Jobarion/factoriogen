package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.lang.types.PrimitiveType;
import me.joba.factorio.lang.types.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class FunctionContext {

    public static final String CONTROL_FLOW_VAR_NAME = "__internal__CONTROL_FLOW";

    private int variableIdCounter = 0;
    private Set<FactorioSignal> freeBindings;
    private Stack<Symbol> tempVariables;
    private Stack<VariableScope> variables;
    private Stack<ConditionContext> conditionContexts;
    private CombinatorGroup functionHeader;
    private CombinatorGroup functionReturn;
    private final FunctionSignature functionSignature;
    private final NetworkGroup functionCallOutput;
    private final NetworkGroup functionCallReturn;
    private final Set<Integer> takenFunctionCallSendSlots = new HashSet<>();
    private final Set<Integer> takenFunctionCallReturnSlots = new HashSet<>();

    public FunctionContext(FunctionSignature functionSignature) {
        this.functionSignature = functionSignature;
        tempVariables = new Stack<>();
        variables = new Stack<>();
        variables.push(new VariableScope(null));
        freeBindings = new HashSet<>();
        freeBindings.addAll(Arrays.asList(FactorioSignal.values()));
        freeBindings.removeIf(FactorioSignal::isReserved);
        conditionContexts = new Stack<>();

        functionCallOutput = new NetworkGroup("Function call out of group " + functionSignature);
        functionCallReturn = new NetworkGroup("Function call return of group " + functionSignature);

        bindParameterSignals();
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
    }

    public int reserveVoidFunctionCallSlot(int earliestStartTime) {
        for(;;earliestStartTime++) {
            if(!takenFunctionCallSendSlots.contains(earliestStartTime)) {
                break;
            }
        }
        takenFunctionCallSendSlots.add(earliestStartTime);
        return earliestStartTime;
    }

    public int reserveFunctionCallSlot(int earliestStartTime, int functionCallDuration) {
        for(;;earliestStartTime++) {
            if(!takenFunctionCallSendSlots.contains(earliestStartTime) && !takenFunctionCallReturnSlots.contains(earliestStartTime + functionCallDuration)) {
                break;
            }
        }
        takenFunctionCallSendSlots.add(earliestStartTime);
        takenFunctionCallReturnSlots.add(earliestStartTime + functionCallDuration);
        return earliestStartTime;
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
        tempVariables.push(var);
    }

    public Variable createBoundTempVariable(Type type, FactorioSignal[] signal, CombinatorGroup producer) {
        var var = new Variable(type, variableIdCounter++, signal, producer);
        tempVariables.push(var);
        return var;
    }

    public Symbol popTempVariable() {
        return tempVariables.pop();
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


    public void claimSymbol(FactorioSignal... signals) {
        for(FactorioSignal signal : signals) {
            if(!freeBindings.remove(signal)) {
                throw new IllegalArgumentException("Attempted to claim unavailable symbol " + signal);
            }
        }
    }

    public CombinatorGroup getFunctionGroup() {
        return functionHeader;
    }

    public CombinatorGroup getFunctionReturnGroup() {
        return functionReturn;
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
