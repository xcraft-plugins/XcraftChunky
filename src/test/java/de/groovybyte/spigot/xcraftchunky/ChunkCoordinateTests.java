package de.groovybyte.spigot.xcraftchunky;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.math.BlockVector2;
import de.groovybyte.spigot.xcraftchunky.utils.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Maximilian Stiede
 */
public class ChunkCoordinateTests {

    @Test
    void testNextChunk() {
        assertEquals(
            ChunkCoordinate.atChunk(0, -1),
            ChunkCoordinate.atChunk(0, 0).getNextChunk(Direction.NORTH)
        );
        assertEquals(
            ChunkCoordinate.atChunk(1, 0),
            ChunkCoordinate.atChunk(0, 0).getNextChunk(Direction.EAST)
        );
        assertEquals(
            ChunkCoordinate.atChunk(0, 1),
            ChunkCoordinate.atChunk(0, 0).getNextChunk(Direction.SOUTH)
        );
        assertEquals(
            ChunkCoordinate.atChunk(-1, 0),
            ChunkCoordinate.atChunk(0, 0).getNextChunk(Direction.WEST)
        );
    }

    @Test
    void testGetDirectNeightbors() {
        assertEquals(
            ImmutableSet.of(
                ChunkCoordinate.atChunk(0, -1),
                ChunkCoordinate.atChunk(-1, 0),
                ChunkCoordinate.atChunk(0, 1),
                ChunkCoordinate.atChunk(1, 0)
            ),
            ChunkCoordinate.atChunk(0, 0)
                .getDirectNeighbors()
                .collect(ImmutableSet.toImmutableSet())
        );
    }

    @Test
    void testAsVec2Rectangle() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 15),
                BlockVector2.at(15, 15),
                BlockVector2.at(15, 0)
            ),
            ChunkCoordinate.atChunk(0, 0)
                .asVec2Rectangle()
        );
    }
}
