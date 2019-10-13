package de.groovybyte.spigot.xcraftchunky.utils;

import com.sk89q.worldedit.math.BlockVector2;
import de.groovybyte.spigot.xcraftchunky.ChunkCoordinate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class ChunkUtils {

    public static Location getGroundLocation(Chunk chunk, BlockVector2 vec) {
        int x = Math.floorMod(vec.getBlockX(), 16);
        int y = 256;
        int z = Math.floorMod(vec.getBlockZ(), 16);
        Block block;
        while (--y >= 0) {
            block = chunk.getBlock(x, y, z);
            if (BlockUtils.isGround(block)) {
                break;
            }
        }
        return new Location(chunk.getWorld(), vec.getBlockX(), y, vec.getBlockZ());
    }

    public static void highlightChunkBounds(Chunk chunk) {
        FireworkEffect effect = FireworkEffect.builder()
            .with(FireworkEffect.Type.BURST)
            .withColor(Color.RED)
            .withTrail()
            .build();
        ChunkCoordinate cc = ChunkCoordinate.forChunk(chunk);
        cc.getCornerCoordinates()
            .map(vec -> ChunkUtils.getGroundLocation(chunk, vec))
            .map(loc -> loc.add(0.5, 0, 0.5))
            .forEach(loc -> {
                chunk.getWorld().spawn(loc, Firework.class, fe -> {
                    FireworkMeta meta = fe.getFireworkMeta();
                    meta.addEffect(effect);
                    meta.setPower(0);
                    fe.setFireworkMeta(meta);
                    fe.setVelocity(new Vector());
                    fe.setSilent(true);
                    fe.setGlowing(true);
                });
            });
    }

    public static void markChunkBounds(Chunk chunk) {
        ChunkCoordinate.forChunk(chunk)
            .getCornerCoordinates()
            .map(vec -> getGroundLocation(chunk, vec).add(0, 1, 0))
            .map(Location::getBlock)
            .forEach(b -> b.setType(Material.RED_WOOL));
    }
}
