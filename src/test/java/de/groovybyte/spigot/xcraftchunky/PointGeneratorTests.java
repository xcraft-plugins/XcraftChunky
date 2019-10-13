package de.groovybyte.spigot.xcraftchunky;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.math.BlockVector2;
import de.groovybyte.spigot.xcraftchunky.utils.PointGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Maximilian Stiede
 */
public class PointGeneratorTests {

    ImmutableList<BlockVector2> generatePoints(ChunkCoordinate... dataSet) {
        return PointGenerator.generate(new ConnectedChunkRegion(
            ImmutableSet.<ChunkCoordinate>builder()
                .add(dataSet)
                .build())
        ).collect(ImmutableList.toImmutableList());
    }

    @Test
    void testCorrectGenerationSingleChunk() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 15),
                BlockVector2.at(15, 15),
                BlockVector2.at(15, 0)
            ),
            generatePoints(
                ChunkCoordinate.atChunk(0, 0)
            )
        );
    }

    @Test
    void testCorrectGenerationTwoChunksHorizontal() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 15),
                BlockVector2.at(31, 15),
                BlockVector2.at(31, 0)
            ),
            generatePoints(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(1, 0)
            )
        );
    }

    @Test
    void testCorrectGenerationTwoChunksVertical() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 31),
                BlockVector2.at(15, 31),
                BlockVector2.at(15, 0)
            ),
            generatePoints(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(0, 1)
            )
        );
    }

    @Test
    void testCorrectGenerationLChunks() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 31),
                BlockVector2.at(15, 31),
                BlockVector2.at(15, 15),
                BlockVector2.at(31, 15),
                BlockVector2.at(31, 0)
            ),
            generatePoints(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(1, 0),
                ChunkCoordinate.atChunk(0, 1)
            )
        );
    }

    @Test
    void testCorrectGeneration4ChunkSquare() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 31),
                BlockVector2.at(31, 31),
                BlockVector2.at(31, 0)
            ),
            generatePoints(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(1, 0),
                ChunkCoordinate.atChunk(0, 1),
                ChunkCoordinate.atChunk(1, 1)
            )
        );
    }

    @Test
    void testCorrectGenerationManyChunk() {
        assertEquals(
            ImmutableList.of(
                BlockVector2.at(0, 0),
                BlockVector2.at(0, 31),
                BlockVector2.at(16, 31),
                BlockVector2.at(16, 48),
                BlockVector2.at(0, 48),
                BlockVector2.at(0, 63),
                BlockVector2.at(32, 63),
                BlockVector2.at(32, 79),
                BlockVector2.at(47, 79),
                BlockVector2.at(47, 47),
                BlockVector2.at(64, 47),
                BlockVector2.at(64, 79),
                BlockVector2.at(79, 79),
                BlockVector2.at(79, 0),
                BlockVector2.at(48, 0),
                BlockVector2.at(48, 15),
                BlockVector2.at(64, 15),
                BlockVector2.at(64, 32),
                BlockVector2.at(31, 32),
                BlockVector2.at(31, 16),
                BlockVector2.at(15, 16),
                BlockVector2.at(15, 0)
            ),
            generatePoints(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(0, 1),
                ChunkCoordinate.atChunk(0, 3),
                ChunkCoordinate.atChunk(1, 1),
                ChunkCoordinate.atChunk(1, 2),
                ChunkCoordinate.atChunk(1, 3),
                ChunkCoordinate.atChunk(2, 2),
                ChunkCoordinate.atChunk(2, 3),
                ChunkCoordinate.atChunk(2, 4),
                ChunkCoordinate.atChunk(3, 0),
                ChunkCoordinate.atChunk(3, 2),
                ChunkCoordinate.atChunk(4, 0),
                ChunkCoordinate.atChunk(4, 1),
                ChunkCoordinate.atChunk(4, 2),
                ChunkCoordinate.atChunk(4, 3),
                ChunkCoordinate.atChunk(4, 4)
            )
        );
    }
}
