package me.joba.factorio.game.entities;

@FunctionalInterface
public interface IntBinaryPredicate {
    boolean test(int a, int b);
}
