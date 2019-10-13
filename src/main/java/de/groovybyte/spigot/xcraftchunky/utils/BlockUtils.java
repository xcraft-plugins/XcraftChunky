package de.groovybyte.spigot.xcraftchunky.utils;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;

public class BlockUtils {
    public static boolean isLeaves(Block block) {
        return block.getBlockData() instanceof Leaves;
    }

    public static boolean isGround(Block block) {
        return !block.isEmpty() && !block.isPassable() && !isLeaves(block);
    }
}
