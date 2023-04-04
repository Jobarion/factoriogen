package me.joba.factorio;

import me.joba.factorio.game.entities.CircuitNetworkEntity;
import me.joba.factorio.game.entities.CircuitNetworkOutput;
import me.joba.factorio.game.entities.ConstantCombinator;
import me.joba.factorio.lang.FactorioSignal;
import me.joba.factorio.lang.Generator;
import me.joba.factorio.lang.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Simulator {

    public static void main(String[] args) throws IOException {
        Path file = Path.of("examples/array_race_condition_while.fcl");
        var code = Files.readAllLines(file)
                .stream()
                .collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();
        var result = simulate(Generator.generateProgram(code, false),  Map.of(FactorioSignal.SIGNAL_RED, 14082375, FactorioSignal.SIGNAL_GREEN, 54702112), 1000);
        //var result = simulate(Generator.generateProgram(code, false),  Map.of(FactorioSignal.SIGNAL_RED, 880148, FactorioSignal.SIGNAL_GREEN, 854720), 100);
        System.out.println(result);
    }

    public static Optional<ProgramResult> simulate(Program program, Map<FactorioSignal, Integer> input, int maxSteps) {
        var entities = program.entities().stream()
                .flatMap(eb -> eb.getEntities().stream())
                .filter(e -> e instanceof CircuitNetworkEntity)
                .map(e -> (CircuitNetworkEntity)e)
                .toList();
//        System.out.println("Entity count: " + entities.size());
        for(var e : entities) {
            if(e instanceof CircuitNetworkOutput cno) {
                if(cno.getRedOut() != null) cno.getRedOut().addEntity(e);
                if(cno.getGreenOut() != null) cno.getGreenOut().addEntity(e);
            }
        }
        Map<FactorioSignal, Integer> programIn = new HashMap<>(input);
        programIn.put(FactorioSignal.SIGNAL_CHECK, 105231);
        for(int i = 0; i < maxSteps; i++) {
//            System.out.println("\nSTEP " + (i + 1) + "\n");
            var inputEntity = new ConstantCombinator(input) {
                @Override
                public Map<FactorioSignal, Integer> getOutput() {
                    return programIn;
                }
            };
            inputEntity.setGreenOut(program.mainIn());
            program.mainIn().addEntity(inputEntity);
            for(var e : entities) {
                e.gatherSignals();
            }
            for(var e : entities) {
                e.update();
//                System.out.println(e + ": " + e.getOutput());
            }
            if(program.mainOut().getValues().getOrDefault(FactorioSignal.SIGNAL_CHECK, 0) == 105231) {
                return Optional.of(new ProgramResult(program.mainOut().getValues(), i + 1));
            }
            programIn.clear();
        }
        return Optional.empty();
    }

    public record ProgramResult(Map<FactorioSignal, Integer> returnValue, int cycles) {

    }
}
