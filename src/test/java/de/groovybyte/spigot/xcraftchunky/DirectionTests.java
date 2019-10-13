package de.groovybyte.spigot.xcraftchunky;

import de.groovybyte.spigot.xcraftchunky.utils.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Maximilian Stiede
 */
public class DirectionTests {

    @Test
    void testGetTurned() {
        assertEquals(Direction.NORTH, Direction.WEST.getTurned(Direction.Turn.RIGHT));
        assertEquals(Direction.NORTH, Direction.EAST.getTurned(Direction.Turn.LEFT));

        assertEquals(Direction.WEST, Direction.SOUTH.getTurned(Direction.Turn.RIGHT));
        assertEquals(Direction.WEST, Direction.NORTH.getTurned(Direction.Turn.LEFT));

        assertEquals(Direction.SOUTH, Direction.EAST.getTurned(Direction.Turn.RIGHT));
        assertEquals(Direction.SOUTH, Direction.WEST.getTurned(Direction.Turn.LEFT));

        assertEquals(Direction.EAST, Direction.NORTH.getTurned(Direction.Turn.RIGHT));
        assertEquals(Direction.EAST, Direction.SOUTH.getTurned(Direction.Turn.LEFT));
    }

    @Test
    void testVerticalityAnHorizontality() {
        assertEquals(false, Direction.NORTH.isHorizontal());
        assertEquals(true, Direction.NORTH.isVertical());

        assertEquals(true, Direction.WEST.isHorizontal());
        assertEquals(false, Direction.WEST.isVertical());

        assertEquals(false, Direction.SOUTH.isHorizontal());
        assertEquals(true, Direction.SOUTH.isVertical());

        assertEquals(true, Direction.EAST.isHorizontal());
        assertEquals(false, Direction.EAST.isVertical());
    }
}
