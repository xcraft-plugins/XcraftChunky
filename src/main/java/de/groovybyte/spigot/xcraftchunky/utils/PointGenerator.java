package de.groovybyte.spigot.xcraftchunky.utils;

import com.sk89q.worldedit.math.BlockVector2;
import de.groovybyte.spigot.xcraftchunky.ChunkCoordinate;
import de.groovybyte.spigot.xcraftchunky.ConnectedChunkRegion;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class PointGenerator {

    private final BlockVector2 firstPoint;
    private final Predicate<ChunkCoordinate> containsChunk;
    private final Stream.Builder<BlockVector2> points = Stream.builder();

    private Direction direction = Direction.WEST; // mathematical positive moving to right first
    private ChunkCoordinate cc;
    private BlockVector2 currentPoint;

    public static Stream<BlockVector2> generate(ConnectedChunkRegion region) {
        if (region.getBounds().getHeight() == 1) {
            ChunkBoundingRect bounds = region.getBounds();
            return Stream.of(
                bounds.min.toVec2(Corner.TOP_LEFT),
                bounds.min.toVec2(Corner.BOTTOM_LEFT),
                bounds.max.toVec2(Corner.BOTTOM_RIGHT),
                bounds.max.toVec2(Corner.TOP_RIGHT)
            );
        } else {
            return new PointGenerator(region).points.build();
        }
    }

    private PointGenerator(ConnectedChunkRegion region) {
        containsChunk = region::contains;

        // because we start facing right, we use the north edge first
        cc = region.getBounds()
            .getEdgeChunks(direction.getRightTurned())
            .filter(containsChunk)
            .findFirst().get(); // there has to be a chunk, because we are in our bounds

        firstPoint = cc.toVec2(Corner.getRightCorner(direction));
        currentPoint = firstPoint;
        // turn left once because we are the leftmost chunk
        turnLeft();

        int i = 0;
        do {
            if (canContinueForward()) {
                if (canTurnRight()) {
                    turnRight();
                } else {
                    moveForward();
                }
            } else {
                turnLeft();
            }
            currentPoint = cc.toVec2(Corner.getRightCorner(direction));
        } while (!firstPoint.equals(currentPoint));
    }

    private boolean canContinueForward() {
        return containsChunk.test(
            cc.getNextChunk(direction)
        );
    }

    private boolean canTurnRight() {
        return containsChunk.test(
            cc
                .getNextChunk(direction)
                .getNextChunk(direction.getRightTurned())
        );
    }

    private void turnLeft() {
        pushPoint();
        direction = direction.getLeftTurned();
    }

    private void turnRight() {
        cc = cc.getNextChunk(direction);
        direction = direction.getRightTurned();
        currentPoint = cc.toVec2(Corner.getRightCorner(direction));
        pushPoint();
        cc = cc.getNextChunk(direction);
    }

    private void moveForward() {
        cc = cc.getNextChunk(direction);
    }

    private void pushPoint() {
        points.accept(currentPoint);
    }
}
