package de.groovybyte.spigot.xcraftchunky;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.math.BlockVector2;
import de.groovybyte.spigot.xcraftchunky.utils.Corner;
import de.groovybyte.spigot.xcraftchunky.utils.Direction;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ChunkCoordinate {

    public final int x, z;

    public static ChunkCoordinate atBlock(Location loc) {
        return atBlock(
            loc.getBlockX(),
            loc.getBlockZ()
        );
    }

    public static ChunkCoordinate atBlock(BlockVector2 vec) {
        return atBlock(
            vec.getBlockX(),
            vec.getBlockZ()
        );
    }

    public static ChunkCoordinate atBlock(int x, int z) {
        return new ChunkCoordinate(
            (int) Math.floor(x / 16.),
            (int) Math.floor(z / 16.)
        );
    }

    public static ChunkCoordinate atChunk(int x, int z) {
        return new ChunkCoordinate(x, z);
    }

    public static ChunkCoordinate forChunk(Chunk chunk) {
        return ChunkCoordinate.atChunk(chunk.getX(), chunk.getZ());
    }

    private ChunkCoordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean contains(BlockVector2 position) {
        return position.containedWithin(
            toVec2(Corner.MINIMUM_COORDINATE),
            toVec2(Corner.MAXIMUM_COORDINATE)
        );
    }

    public BlockVector2 toVec2() {
        return toVec2(Corner.TOP_LEFT);
    }

    public BlockVector2 toVec2(Corner corner) {
        switch (corner) {
            case TOP_LEFT:
                return BlockVector2.at(x * 16, z * 16);
            case TOP_RIGHT:
                return BlockVector2.at(x * 16 + 15, z * 16);
            case BOTTOM_LEFT:
                return BlockVector2.at(x * 16, z * 16 + 15);
            default:
                return BlockVector2.at(x * 16 + 15, z * 16 + 15);
        }
    }

    public Stream<BlockVector2> getCornerCoordinates() {
        return Stream.of(
            toVec2(Corner.TOP_LEFT),
            toVec2(Corner.BOTTOM_LEFT),
            toVec2(Corner.BOTTOM_RIGHT),
            toVec2(Corner.TOP_RIGHT)
        );
    }

    public List<BlockVector2> asVec2Rectangle() {
        return getCornerCoordinates().collect(ImmutableList.toImmutableList());
    }

    public ChunkCoordinate getNextChunk(Direction direc) {
        return new ChunkCoordinate(
            this.x + direc.x,
            this.z + direc.z
        );
    }

    public Stream<ChunkCoordinate> getDirectNeighbors() {
        return Arrays.stream(Direction.values())
            .map(this::getNextChunk);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.x;
        hash = 83 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChunkCoordinate other = (ChunkCoordinate) obj;
        if (this.x != other.x) {
            return false;
        }
		return this.z == other.z;
	}

    public String getPrintableString() {
        return x + "@" + z;
    }

    @Override
    public String toString() {
        return "ChunkCoordinate{" + getPrintableString() + '}';
    }
}
