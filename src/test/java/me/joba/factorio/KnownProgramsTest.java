package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import me.joba.factorio.lang.Generator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnownProgramsTest {

    @ParameterizedTest
    @EnumSource(KnownProgram.class)
    public void knownProgramTest(KnownProgram program) throws IOException {
        var code = Files.readAllLines(program.getFile())
                .stream()
                .collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();

        var result = Simulator.simulate(Generator.generateProgram(code, false),  program.getInput(), program.getMaxCycles());
        //Program completed within timeout
        assertTrue(result.isPresent(), "Program timed out");
        var returnValues = result.get().returnValue();
        returnValues.remove(FactorioSignal.SIGNAL_CHECK);
        //No additional signals returned
        assertTrue(program.getReturnKnownSignals().size() + program.getReturnUnknownSignals().size() >= returnValues.size(), "Superfluous signals returned");
        for(var expected : program.getReturnKnownSignals().entrySet()) {
            assertEquals(returnValues.getOrDefault(expected.getKey(), 0), expected.getValue(), "Signal " + expected.getKey() + " with unexpected value");
            returnValues.remove(expected.getKey());
        }
        assertTrue(returnValues.values().containsAll(program.getReturnUnknownSignals()), "Expected signals " + program.getReturnUnknownSignals() + " but found " + returnValues.values());
        System.out.println("Completed in " + result.get().cycles() + " cycles");
    }

    public enum KnownProgram {
        ARRAY_DELAY("examples/array_delay_test.fcl", Map.of(), 50, Map.of(), List.of(2)),
        ARRAY_RACE_CONDITION_WHILE("examples/array_race_condition_while.fcl", Map.of(), 500, Map.of(), List.of(45)),
        MAX_COLLATZ("examples/max_collatz.fcl", Map.of(FactorioSignal.SIGNAL_RED, 1, FactorioSignal.SIGNAL_GREEN, 10, FactorioSignal.SIGNAL_I, 10), 10000, Map.of(), List.of(52)),
        FUNCTION_CALL_MINIMAL("examples/function_call_minimal.fcl", Map.of(FactorioSignal.SIGNAL_RED, 5, FactorioSignal.SIGNAL_GREEN, 13), 50, Map.of(), List.of(30)),
        FUNCTION_CALL_TREE("examples/function_call_tree.fcl", Map.of(), 100, Map.of(), List.of(3)),
        FUNCTION_CALL_CHAIN("examples/function_chain.fcl", Map.of(), 100, Map.of(), List.of(1)),
        IF_IN_WHILE("examples/if_in_while.fcl", Map.of(FactorioSignal.SIGNAL_RED, 1, FactorioSignal.SIGNAL_GREEN, 10), 500, Map.of(), List.of(15)),
        IF_NO_ELSE_A("examples/if_no_else.fcl", Map.of(FactorioSignal.SIGNAL_RED, 5, FactorioSignal.SIGNAL_GREEN, 6), 50, Map.of(), List.of()),
        IF_NO_ELSE_B("examples/if_no_else.fcl", Map.of(FactorioSignal.SIGNAL_RED, 6, FactorioSignal.SIGNAL_GREEN, 5), 50, Map.of(), List.of(7)),
        IF_RUNTIME_EQUAL_A("examples/if_runtime_equal.fcl", Map.of(FactorioSignal.SIGNAL_RED, 5, FactorioSignal.SIGNAL_GREEN, 6), 50, Map.of(), List.of(6)),
        IF_RUNTIME_EQUAL_B("examples/if_runtime_equal.fcl", Map.of(FactorioSignal.SIGNAL_RED, 6, FactorioSignal.SIGNAL_GREEN, 5), 50, Map.of(), List.of(6)),
        IF_RUNTIME_UNEQUAL_A("examples/if_runtime_unequal.fcl", Map.of(FactorioSignal.SIGNAL_RED, 5, FactorioSignal.SIGNAL_GREEN, 6), 50, Map.of(), List.of(6)),
        IF_RUNTIME_UNEQUAL_B("examples/if_runtime_unequal.fcl", Map.of(FactorioSignal.SIGNAL_RED, 6, FactorioSignal.SIGNAL_GREEN, 5), 50, Map.of(), List.of(7)),
        SAME_VAR_EXPRESSION("examples/same_var_expression.fcl", Map.of(FactorioSignal.SIGNAL_RED, 125, FactorioSignal.SIGNAL_GREEN, 8), 20, Map.of(), List.of(976)),
        WHILE_IN_IF_A("examples/while_in_if.fcl", Map.of(FactorioSignal.SIGNAL_RED, 1, FactorioSignal.SIGNAL_GREEN, 5), 500, Map.of(), List.of(10)),
        WHILE_IN_IF_B("examples/while_in_if.fcl", Map.of(FactorioSignal.SIGNAL_RED, 5, FactorioSignal.SIGNAL_GREEN, 1), 50, Map.of(), List.of()),
        WHILE_MINIMAL("examples/while_minimal.fcl", Map.of(FactorioSignal.SIGNAL_RED, 1, FactorioSignal.SIGNAL_GREEN, 5), 50, Map.of(), List.of(15)),
        FUNCTION_CALL_IN_WHILE("examples/function_call_in_while.fcl", Map.of(), 500, Map.of(), List.of(10));

        private final Path file;
        private final Map<FactorioSignal, Integer> input;
        private final int maxCycles;
        private final Map<FactorioSignal, Integer> returnKnownSignals;
        private final List<Integer> returnUnknownSignals;

        KnownProgram(String name, Map<FactorioSignal, Integer> input, int maxCycles, Map<FactorioSignal, Integer> returnKnownSignals, List<Integer> returnUnknownSignals) {
            this.file = Path.of(name);
            this.input = input;
            this.maxCycles = maxCycles;
            this.returnKnownSignals = returnKnownSignals;
            this.returnUnknownSignals = returnUnknownSignals;
        }

        public Path getFile() {
            return file;
        }

        public Map<FactorioSignal, Integer> getInput() {
            return input;
        }

        public int getMaxCycles() {
            return maxCycles;
        }

        public Map<FactorioSignal, Integer> getReturnKnownSignals() {
            return returnKnownSignals;
        }

        public List<Integer> getReturnUnknownSignals() {
            return returnUnknownSignals;
        }
    }
}
