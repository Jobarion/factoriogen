package me.joba.factorio.lang;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.EntityBlock;

import java.util.List;

public record Program(NetworkGroup mainIn, NetworkGroup mainOut,
                      List<EntityBlock> entities) {

}
