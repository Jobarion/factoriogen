package me.joba.factorio;

import me.joba.factorio.lang.Generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            args = new String[]{"examples/pi.fcl"};
        }

        Path file = Path.of(args[0]);
        var code = Files.readAllLines(file)
                .stream()
                .collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();
        System.out.println(Generator.generateBlueprint(Generator.generateProgram(code, true).entities()));
    }

//    public static int[] ARRAY_1 = new int[20];
//
//    public static void main(String[] args) {
//        int start = 0;
//        int seed = 598223;
//        while(start < 20) {
//            ARRAY_1[start] = seed % 100;
//            seed = (8121 * seed + 28411) % 134456;
//            start = start + 1;
//        }
//        sort(ARRAY_1, 20);
//        System.out.println(Arrays.toString(ARRAY_1));
//    }
//
//    public static void sort(int[] arr, int size) {
//        int start = 0;
//        while(start < size - 1) {
//            int index = start + 1;
//            int smallestIndex = start;
//            int smallest = arr[start];
//            while(index < size) {
//                int x = arr[index];
//                if(x < smallest) {
//                    smallest = x;
//                    smallestIndex = index;
//                }
//                index = index + 1;
//            }
//            int tmp = arr[start];
//            arr[start] = smallest;
//            arr[smallestIndex] = tmp;
//            start = start + 1;
//        }
//    }
}
