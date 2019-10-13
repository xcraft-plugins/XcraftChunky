package de.groovybyte.spigot.xcraftchunky;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.groovybyte.spigot.xcraftchunky.exceptions.NotConnectedException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkBoundingRect;
import de.groovybyte.spigot.xcraftchunky.utils.PointGenerator;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectedChunkRegion {

    private final ChunkBoundingRect bounds;
    private final ImmutableSet<ChunkCoordinate> containedChunks;

    public ConnectedChunkRegion(ProtectedRegion region) {
        bounds = new ChunkBoundingRect(region);
        containedChunks = bounds.getAllChunks()
            // all 4 corners of a chunk have to be inside the region
            .filter(cc -> cc.getCornerCoordinates().allMatch(region::contains))
            .collect(ImmutableSet.toImmutableSet());
    }

    public ConnectedChunkRegion(Set<ChunkCoordinate> containedChunks) throws NotConnectedException {
        if (containedChunks.size() > 1) {
            // check that if there is more than one chunk, each of them is connected to each other
            boolean allChunksConnected = containedChunks.stream()
                .allMatch(cc -> cc
                    .getDirectNeighbors()
                    .anyMatch(containedChunks::contains));
            if (!allChunksConnected) {
                throw new NotConnectedException();
            }
        }
        this.bounds = new ChunkBoundingRect(containedChunks);
        this.containedChunks = ImmutableSet.copyOf(withEnclosedChunks(containedChunks));
    }

    private Set<ChunkCoordinate> withEnclosedChunks(Set<ChunkCoordinate> chunks) {
        Predicate<ChunkCoordinate> inBounds = bounds::contains;
        Predicate<ChunkCoordinate> inRegion = chunks::contains;
        Predicate<ChunkCoordinate> isFreeChunk = inBounds.and(inRegion.negate());

        Set<ChunkCoordinate> pickedFree = bounds
            .getEdgeChunks()
            .filter(isFreeChunk)
            .collect(Collectors.toSet());
        Set<ChunkCoordinate> newFree = pickedFree;

        Predicate<ChunkCoordinate> isPicked = pickedFree::contains;
        Predicate<ChunkCoordinate> isPickableChunk = isFreeChunk.and(isPicked.negate());
        do {
            newFree = newFree.stream()
                .flatMap(ChunkCoordinate::getDirectNeighbors)
                .distinct()
                .filter(isPickableChunk)
                .collect(Collectors.toSet());
            pickedFree.addAll(newFree);
        } while (!newFree.isEmpty());

        return bounds.getAllChunks()
            .filter(isPicked.negate())
            .collect(Collectors.toSet());
    }

    public ChunkBoundingRect getBounds() {
        return bounds;
    }

    public boolean contains(ChunkCoordinate cc) {
        return bounds.contains(cc) && containedChunks.contains(cc);
    }

    public Set<ChunkCoordinate> getChunks() {
        return containedChunks;
    }

    public int getChunkCount() {
        return containedChunks.size();
    }

    public ConnectedChunkRegion withChunk(ChunkCoordinate cc) {
        if (!cc.getDirectNeighbors().anyMatch(this::contains)) {
            throw new NotConnectedException();
        }
        return new ConnectedChunkRegion(Sets.union(
            containedChunks,
            Collections.singleton(cc)
        ));
    }

    protected Stream<BlockVector2> generatePoints() {
        return PointGenerator.generate(this);
    }

    // ------------------------------------------
    public String chunkStringMap() {
        return bounds
            .expandedBy(1)
            .generateMap(cc -> {
                return contains(cc) ? "§2▓" : "§r▓";
            }); // "▓▓" : "▒▒"
    }

//	String blockStringMap() {
//		StringBuilder sb = new StringBuilder("\n");
//		BlockVector2 min = bounds.getMinimumPoint();
//		BlockVector2 max = bounds.getMaximumPoint();
//		int minZ = (int) Math.floor(min.getBlockZ() / 16.) * 16;
//		int maxZ = (int) Math.ceil(max.getBlockZ() / 16.) * 16;
//		int minX = (int) Math.floor(min.getBlockX() / 16.) * 16;
//		int maxX = (int) Math.ceil(max.getBlockX() / 16.) * 16;
//		sb.append(minX).append('@').append(minZ).append(' ').append(maxX).append('@').append(maxZ).append('\n');
//		for (int z = minZ; z < maxZ; z++) {
//			if (z % 16 == 0) {
//				int width = Math.abs(maxX - minX) + (Math.abs(maxZ - minZ) / 16);
//				sb.append(Strings.repeat("-", width)).append('\n');
//			}
//			for (int x = minX; x < maxX; x++) {
//				if (x % 16 == 0) {
//					sb.append('|');
//				}
//				BlockVector2 point = BlockVector2.at(x, z);
//				if (backingProtectedRegion.contains(point)) {
//					sb.append(backingProtectedRegion.getPoints().contains(point) ? 'x' : '#');
//				} else {
//					sb.append('.');
//				}
//			}
//			sb.append('\n');
//		}
//		return sb.toString();
//	}
}
