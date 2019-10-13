package de.groovybyte.spigot.xcraftchunky.utils;

import java.util.function.Function;

public enum Direction {
    NORTH(0, -1) {
        @Override
        public Direction getOpposite() {
            return SOUTH;
        }

        @Override
        public Direction getLeftTurned() {
            return WEST;
        }

        @Override
        public Direction getRightTurned() {
            return EAST;
        }
    },
    EAST(1, 0) {
        @Override
        public Direction getOpposite() {
            return WEST;
        }

        @Override
        public Direction getLeftTurned() {
            return NORTH;
        }

        @Override
        public Direction getRightTurned() {
            return SOUTH;
        }
    },
    SOUTH(0, 1) {
        @Override
        public Direction getOpposite() {
            return NORTH;
        }

        @Override
        public Direction getLeftTurned() {
            return EAST;
        }

        @Override
        public Direction getRightTurned() {
            return WEST;
        }
    },
    WEST(-1, 0) {
        @Override
        public Direction getOpposite() {
            return EAST;
        }

        @Override
        public Direction getLeftTurned() {
            return SOUTH;
        }

        @Override
        public Direction getRightTurned() {
            return NORTH;
        }
    };

    public final int x, z;

    Direction(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public boolean isVertical() {
        return z != 0;
    }

    public boolean isHorizontal() {
        return x != 0;
    }

    public abstract Direction getOpposite();

    public abstract Direction getLeftTurned();

    public abstract Direction getRightTurned();

    public Direction getTurned(Turn turn) {
        return turn.apply(this);
    }

    public enum Turn {
        LEFT(Direction::getLeftTurned),
        RIGHT(Direction::getRightTurned);

        private final Function<Direction, Direction> turn;

        Turn(Function<Direction, Direction> turn) {
            this.turn = turn;
        }

        public Direction apply(Direction direction) {
            return turn.apply(direction);
        }
    }
}
