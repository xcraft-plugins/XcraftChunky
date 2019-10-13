package de.groovybyte.spigot.xcraftchunky;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkBoundingRect;
import de.groovybyte.spigot.xcraftchunky.utils.Corner;
import de.groovybyte.spigot.xcraftchunky.utils.Direction;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Maximilian Stiede
 */
public class ChunkBoundingRectTests {

    @Test
    void testConstructorWithRegion() {
        ProtectedRegion region = mock(ProtectedRegion.class);
        when(region.getMinimumPoint())
            .thenReturn(BlockVector2.at(-1, -1).toBlockVector3());
        when(region.getMaximumPoint())
            .thenReturn(BlockVector2.at(30, 30).toBlockVector3());

        ChunkBoundingRect cbr = new ChunkBoundingRect(region);
        assertEquals(9, cbr.getChunkCount());

        assertEquals(3, cbr.getWidth());
        assertEquals(3, cbr.getHeight());
        assertEquals(16 * cbr.getWidth(), cbr.getBlockWidth());
        assertEquals(16 * cbr.getHeight(), cbr.getBlockHeight());
        assertEquals(cbr.getWidth() * cbr.getHeight(), cbr.getChunkCount());
    }

    @Test
    void testConstructorWithSingleChunk() {
        ChunkCoordinate cc = ChunkCoordinate.atChunk(Integer.MIN_VALUE, Integer.MAX_VALUE);

        ChunkBoundingRect cbr = new ChunkBoundingRect(cc);
        assertEquals(1, cbr.getChunkCount());

        assertEquals(cc, cbr.getAllChunks().findAny().get());

        assertEquals(1, cbr.getWidth());
        assertEquals(1, cbr.getHeight());
        assertEquals(16, cbr.getBlockWidth());
        assertEquals(16, cbr.getBlockHeight());
        assertTrue(cbr.isSingleChunk());
    }

    @Test
    void testConstructorWithMinAndMaxChunk() {
        ChunkCoordinate cc1 = ChunkCoordinate.atChunk(-100, 100);
        ChunkCoordinate cc2 = ChunkCoordinate.atChunk(100, -100);

        ChunkBoundingRect cbr = new ChunkBoundingRect(cc1, cc2);

        assertTrue(cbr.contains(cc1));
        assertTrue(cbr.contains(cc2));
        assertTrue(cbr.contains(ChunkCoordinate.atChunk(100, 100)));
        assertTrue(cbr.contains(ChunkCoordinate.atChunk(-100, -100)));

        assertEquals(201, cbr.getWidth());
        assertEquals(201, cbr.getHeight());
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void testConstructorWithoutChunks() {
        assertThrows(IllegalArgumentException.class, ()
            -> new ChunkBoundingRect(Collections.emptySet())
        );
    }

    @Test
    void testConstructorWithMultipleChunks() {
        Set<ChunkCoordinate> ccs = ImmutableSet.of(
            ChunkCoordinate.atChunk(-100, 200),
            ChunkCoordinate.atChunk(-200, 200),
            ChunkCoordinate.atChunk(100, -100),
            ChunkCoordinate.atChunk(200, -200)
        );

        ChunkBoundingRect cbr = new ChunkBoundingRect(ccs);

        assertTrue(ccs.stream().allMatch(cc -> cbr.contains(cc)));
        assertTrue(cbr.contains(ChunkCoordinate.atChunk(-200, 200)));
        assertTrue(cbr.contains(ChunkCoordinate.atChunk(100, 200)));

        assertEquals(401, cbr.getWidth());
        assertEquals(401, cbr.getHeight());
    }

    @Test
    void testGetCornerChunk() {
        ChunkCoordinate cc1 = ChunkCoordinate.atChunk(0, 0);
        ChunkCoordinate cc2 = ChunkCoordinate.atChunk(1, 1);

        ChunkBoundingRect cbr = new ChunkBoundingRect(cc1, cc2);

        assertEquals(ChunkCoordinate.atChunk(0, 0), cbr.getCornerChunk(Corner.TOP_LEFT));
        assertEquals(ChunkCoordinate.atChunk(1, 0), cbr.getCornerChunk(Corner.TOP_RIGHT));
        assertEquals(ChunkCoordinate.atChunk(0, 1), cbr.getCornerChunk(Corner.BOTTOM_LEFT));
        assertEquals(ChunkCoordinate.atChunk(1, 1), cbr.getCornerChunk(Corner.BOTTOM_RIGHT));
    }

    @Test
    void testExpandedBy() {
        ChunkBoundingRect cbr;

        cbr = new ChunkBoundingRect(ChunkCoordinate.atChunk(0, 0));
        assertEquals(1, cbr.getChunkCount());
        cbr = cbr.expandedBy(1);
        assertEquals(3 * 3, cbr.getChunkCount());

        cbr = new ChunkBoundingRect(ChunkCoordinate.atChunk(0, 0), ChunkCoordinate.atChunk(1, 1));
        assertEquals(2 * 2, cbr.getChunkCount());
        cbr = cbr.expandedBy(2, 1);
        assertEquals(6, cbr.getWidth());
        assertEquals(4, cbr.getHeight());
    }

    @Test
    void testGetEdgeChunksWithDirection() {
        ChunkBoundingRect cbr = new ChunkBoundingRect(ChunkCoordinate.atChunk(0, 0),
            ChunkCoordinate.atChunk(2, 2));

        assertEquals(
            ImmutableSet.of(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(1, 0),
                ChunkCoordinate.atChunk(2, 0)
            ),
            cbr.getEdgeChunks(Direction.NORTH).collect(Collectors.toSet())
        );
        assertEquals(
            ImmutableSet.of(
                ChunkCoordinate.atChunk(0, 2),
                ChunkCoordinate.atChunk(1, 2),
                ChunkCoordinate.atChunk(2, 2)
            ),
            cbr.getEdgeChunks(Direction.SOUTH).collect(Collectors.toSet())
        );
        assertEquals(
            ImmutableSet.of(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(0, 1),
                ChunkCoordinate.atChunk(0, 2)
            ),
            cbr.getEdgeChunks(Direction.WEST).collect(Collectors.toSet())
        );
        assertEquals(
            ImmutableSet.of(
                ChunkCoordinate.atChunk(2, 0),
                ChunkCoordinate.atChunk(2, 1),
                ChunkCoordinate.atChunk(2, 2)
            ),
            cbr.getEdgeChunks(Direction.EAST).collect(Collectors.toSet())
        );
    }

    @Test
    void testGetEdgeChunks() {
        ChunkBoundingRect cbr = new ChunkBoundingRect(ChunkCoordinate.atChunk(0, 0),
            ChunkCoordinate.atChunk(2, 2));

        assertEquals(
            ImmutableSet.of(
                ChunkCoordinate.atChunk(0, 0),
                ChunkCoordinate.atChunk(1, 0),
                ChunkCoordinate.atChunk(2, 0),
                ChunkCoordinate.atChunk(0, 1),
                ChunkCoordinate.atChunk(2, 1),
                ChunkCoordinate.atChunk(0, 2),
                ChunkCoordinate.atChunk(1, 2),
                ChunkCoordinate.atChunk(2, 2)
            ),
            cbr.getEdgeChunks().collect(Collectors.toSet())
        );
    }
}
