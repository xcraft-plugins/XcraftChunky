package de.groovybyte.spigot.xcraftchunky.utils;

import com.google.common.base.Strings;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.groovybyte.spigot.xcraftchunky.ChunkCoordinate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ChunkBoundingRect {

    public final ChunkCoordinate min, max;

    public ChunkBoundingRect(ProtectedRegion region) {
        this.min = ChunkCoordinate.atBlock(region.getMinimumPoint().toBlockVector2());
        this.max = ChunkCoordinate.atBlock(region.getMaximumPoint().toBlockVector2());
    }

    public ChunkBoundingRect(ChunkCoordinate cc) {
        this.min = cc;
        this.max = cc;
    }

    public ChunkBoundingRect(ChunkCoordinate p1, ChunkCoordinate p2) {
        this(p1.x, p1.z, p2.x, p2.z);
    }

    public ChunkBoundingRect(int x1, int z1, int x2, int z2) {
        this.min = ChunkCoordinate.atChunk(Math.min(x1, x2), Math.min(z1, z2));
        this.max = ChunkCoordinate.atChunk(Math.max(x1, x2), Math.max(z1, z2));
    }

    public ChunkBoundingRect(Collection<ChunkCoordinate> chunks) {
        if (chunks.isEmpty()) {
            throw new IllegalArgumentException("empty collection - requires atleast 1 chunk");
        }
        this.min = ChunkCoordinate.atChunk(
            chunks.stream().mapToInt(ChunkCoordinate::getX).min().getAsInt(),
            chunks.stream().mapToInt(ChunkCoordinate::getZ).min().getAsInt()
        );
        this.max = ChunkCoordinate.atChunk(
            chunks.stream().mapToInt(ChunkCoordinate::getX).max().getAsInt(),
            chunks.stream().mapToInt(ChunkCoordinate::getZ).max().getAsInt()
        );
    }

    public long getChunkCount() {
        return getWidth() * getHeight();
    }

    /**
     * @return width in chunks
     */
    public int getWidth() {
        return max.x - min.x + 1;
    }

    /**
     * @return height in chunks
     */
    public int getHeight() {
        return max.z - min.z + 1;
    }

    /**
     * @return width in blocks
     */
    public int getBlockWidth() {
        return getMaximumCoordinate().getBlockX() - getMinimumCoordinate().getBlockX() + 1;
    }

    /**
     * @return height in blocks
     */
    public int getBlockHeight() {
        return getMaximumCoordinate().getBlockZ() - getMinimumCoordinate().getBlockZ() + 1;
    }

    public BlockVector2 getMinimumCoordinate() {
        return min.toVec2(Corner.MINIMUM_COORDINATE);
    }

    public BlockVector2 getMaximumCoordinate() {
        return max.toVec2(Corner.MAXIMUM_COORDINATE);
    }

    public boolean isSingleChunk() {
        return min.equals(max);
    }

    public boolean contains(ChunkCoordinate cc) {
        return min.x <= cc.x && cc.x <= max.x
            && min.z <= cc.z && cc.z <= max.z;
    }

    public ChunkBoundingRect expandedBy(int delta) {
        return ChunkBoundingRect.this.expandedBy(delta, delta);
    }

    public ChunkBoundingRect expandedBy(int horizontal, int vertical) {
        return new ChunkBoundingRect(
            min.x - horizontal,
            min.z - vertical,
            max.x + horizontal,
            max.z + vertical
        );
    }

    public ChunkCoordinate getCornerChunk(Corner corner) {
        switch (corner) {
            case TOP_LEFT:
                return min;
            case TOP_RIGHT:
                return ChunkCoordinate.atChunk(max.x, min.z);
            case BOTTOM_LEFT:
                return ChunkCoordinate.atChunk(min.x, max.z);
            case BOTTOM_RIGHT:
                return max;
        }
        throw new IllegalStateException("?!");
    }

    public Stream<ChunkCoordinate> getAllChunks() {
        Stream.Builder<ChunkCoordinate> builder = Stream.builder();
        for (long z = min.z; z <= max.z; z++) {
            for (long x = min.x; x <= max.x; x++) {
                builder.accept(ChunkCoordinate.atChunk((int) x, (int) z));
            }
        }
        return builder.build();
    }

    public Stream<ChunkCoordinate> getEdgeChunks(Direction direction) {
        switch (direction) {
            case NORTH:
                return allChunksUpToEdge(getCornerChunk(Corner.TOP_LEFT), Direction.EAST);
            case SOUTH:
                return allChunksUpToEdge(getCornerChunk(Corner.BOTTOM_LEFT), Direction.EAST);
            case WEST:
                return allChunksUpToEdge(getCornerChunk(Corner.TOP_LEFT), Direction.SOUTH);
            case EAST:
                return allChunksUpToEdge(getCornerChunk(Corner.TOP_RIGHT), Direction.SOUTH);
        }
        throw new IllegalStateException();
    }

    public Stream<ChunkCoordinate> getEdgeChunks() {
        return Arrays.stream(Direction.values())
            .flatMap(this::getEdgeChunks)
            .unordered()
            .distinct();
    }

    public Stream<ChunkCoordinate> allChunksUpToEdge(ChunkCoordinate cc, Direction direction) {
        Stream.Builder<ChunkCoordinate> builder = Stream.builder();
        ChunkCoordinate next = cc;
        while (contains(next)) {
            builder.accept(next);
            next = next.getNextChunk(direction);
        }
        return builder.build();
    }

    public String generateMap(Function<ChunkCoordinate, String> mapping) {
        return generateMap(mapping, false);
    }

    public String generateMap(Function<ChunkCoordinate, String> mapping, boolean border) {
        StringBuilder sb = new StringBuilder();
        if (border) {
            sb.append('┌'); //▗,
            sb.append(Strings.repeat("─", getWidth())); //▁
            sb.append("┐\n"); //▖,
        }
        for (int z = min.z; z <= max.z; z++) {
            if (border) {
                sb.append("│ "); //▕
            }
            for (int x = min.x; x <= max.x; x++) {
                sb.append(mapping.apply(ChunkCoordinate.atChunk(x, z)));
            }
            if (border) {
                sb.append("§r│"); //▏
            }
            sb.append('\n');
        }
        if (border) {
            sb.append('└'); //▝\'
            sb.append(Strings.repeat("─", getWidth())); //▔
            sb.append("┘\n"); //▘'
        }
        return sb.substring(0, sb.length() - 1);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.min);
        hash = 61 * hash + Objects.hashCode(this.max);
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
        final ChunkBoundingRect other = (ChunkBoundingRect) obj;
        if (!Objects.equals(this.min, other.min)) {
            return false;
        }
		return Objects.equals(this.max, other.max);
	}

    public String getPrintableString() {
        return min.getPrintableString() + " to " + max.getPrintableString();
    }

    @Override
    public String toString() {
        return "ChunkBoundingRect{" + getPrintableString() + '}';
    }
}
