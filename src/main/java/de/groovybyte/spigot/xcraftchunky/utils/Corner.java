package de.groovybyte.spigot.xcraftchunky.utils;

import de.groovybyte.spigot.xcraftchunky.utils.Direction.*;

import static de.groovybyte.spigot.xcraftchunky.utils.Direction.*;

public enum Corner {
    TOP_RIGHT(NORTH, EAST),
    TOP_LEFT(NORTH, WEST),
    BOTTOM_LEFT(SOUTH, WEST),
    BOTTOM_RIGHT(SOUTH, EAST);

    public static Corner MINIMUM_COORDINATE = TOP_LEFT;
    public static Corner MAXIMUM_COORDINATE = BOTTOM_RIGHT;

    public final Direction vertical, horizontal;

    Corner(Direction v, Direction h) {
        this.vertical = v;
        this.horizontal = h;
    }

    public static Corner forDirections(Direction direction1, Direction direction2) {
        if (direction1.isVertical() == direction2.isVertical()) {
            throw new IllegalArgumentException("direction vectors should span a field");
        }
        int x = direction1.x + direction2.x;
        int z = direction1.z + direction2.z;
        if (z > 0) {
            return x > 0 ? BOTTOM_RIGHT : BOTTOM_LEFT;
        } else {
            return x > 0 ? TOP_RIGHT : TOP_LEFT;
        }
    }

    public static Corner getLeftCorner(Direction direction) {
        return getCorner(direction, Turn.LEFT);
    }

    public static Corner getRightCorner(Direction direction) {
        return getCorner(direction, Turn.RIGHT);
    }

    public static Corner getCorner(Direction direction, Turn turn) {
        return forDirections(direction, direction.getTurned(turn));
    }
}
