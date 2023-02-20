package me.joba.factorio;

import me.joba.factorio.lang.Generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            args = new String[]{"examples/fixedp.fcl"};
        }

        Path file = Path.of(args[0]);
        var code = Files.readAllLines(file)
                .stream()
                .collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();
        System.out.println(Generator.generateBlueprint(code));
    }
}
