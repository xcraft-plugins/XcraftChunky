package de.groovybyte.spigot.xcraftchunky;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.groovybyte.spigot.xcraftchunky.exceptions.NotConnectedException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkBoundingRect;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Maximilian Stiede
 */
public class ConnectedChunkRegionTests {

    private ProtectedRegion mockedProtectedRegion(ConnectedChunkRegion ccr) {
        ArgumentMatcher<BlockVector2> inChunks = (BlockVector2 argument)
            -> ccr.getChunks().stream()
            .anyMatch(cc -> cc.contains(argument));

        ProtectedRegion region = mock(ProtectedRegion.class);
        when(region.contains(any(BlockVector2.class)))
            .thenReturn(false);
        when(region.contains(argThat(inChunks)))
            .thenReturn(true);
        when(region.getMinimumPoint())
            .thenReturn(ccr.getBounds().getMinimumCoordinate().toBlockVector3());
        when(region.getMaximumPoint())
            .thenReturn(ccr.getBounds().getMaximumCoordinate().toBlockVector3());
        return region;
    }

    @Test
    void testConstructorWithProtectedRegion() {
        ConnectedChunkRegion existingCCR = new ConnectedChunkRegion(ImmutableSet.of(
            ChunkCoordinate.atChunk(1, 0),
            ChunkCoordinate.atChunk(0, 1),
            ChunkCoordinate.atChunk(1, 1),
            ChunkCoordinate.atChunk(2, 1),
            ChunkCoordinate.atChunk(0, 2)
        ));

        ProtectedRegion region = mockedProtectedRegion(existingCCR);
        ConnectedChunkRegion ccr = new ConnectedChunkRegion(region);
        assertEquals(existingCCR.getBounds(), ccr.getBounds());
        assertEquals(existingCCR.getChunks(), ccr.getChunks());
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void testConstructorWithContainedChunksWithoutConnection() {
        assertThrows(NotConnectedException.class, ()
                -> new ConnectedChunkRegion(ImmutableSet.of(
            ChunkCoordinate.atChunk(1, 0),
            ChunkCoordinate.atChunk(0, 1)
            ))
        );
    }

    @Test
    void testConstructorWithExactChunks() {
        Set<ChunkCoordinate> chunks = ImmutableSet.of(
            ChunkCoordinate.atChunk(1, 0),
            ChunkCoordinate.atChunk(0, 1),
            ChunkCoordinate.atChunk(1, 1),
            ChunkCoordinate.atChunk(2, 1),
            ChunkCoordinate.atChunk(0, 2)
        );
        ConnectedChunkRegion ccr = new ConnectedChunkRegion(chunks);
        assertEquals(new ChunkBoundingRect(0, 0, 2, 2), ccr.getBounds());
        assertEquals(chunks, ccr.getChunks());
    }

    @Test
    void testConstructorWithEnclosedChunks() {
        Set<ChunkCoordinate> chunks = ImmutableSet.of(
            ChunkCoordinate.atChunk(0, 0),
            ChunkCoordinate.atChunk(1, 0),
            ChunkCoordinate.atChunk(2, 0),
            ChunkCoordinate.atChunk(0, 1),
            ChunkCoordinate.atChunk(2, 1),
            ChunkCoordinate.atChunk(0, 2),
            ChunkCoordinate.atChunk(1, 2)
        );
        Set<ChunkCoordinate> enclosedChunks = Sets.union(chunks, Collections.singleton(
            ChunkCoordinate.atChunk(1, 1)
        ));
        ConnectedChunkRegion ccr = new ConnectedChunkRegion(chunks);
        assertEquals(new ChunkBoundingRect(0, 0, 2, 2), ccr.getBounds());
        assertEquals(enclosedChunks, ccr.getChunks());
    }
}
