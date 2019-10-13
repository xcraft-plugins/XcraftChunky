package de.groovybyte.spigot.xcraftchunky;

import de.groovybyte.spigot.xcraftchunky.utils.Corner;
import de.groovybyte.spigot.xcraftchunky.utils.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Maximilian Stiede
 */
public class CornerTests {

    @Test
    void testGetCorner() {
        assertEquals(Corner.TOP_LEFT, Corner.getLeftCorner(Direction.NORTH));
        assertEquals(Corner.TOP_LEFT, Corner.getRightCorner(Direction.WEST));

        assertEquals(Corner.BOTTOM_LEFT, Corner.getLeftCorner(Direction.WEST));
        assertEquals(Corner.BOTTOM_LEFT, Corner.getRightCorner(Direction.SOUTH));

        assertEquals(Corner.BOTTOM_RIGHT, Corner.getLeftCorner(Direction.SOUTH));
        assertEquals(Corner.BOTTOM_RIGHT, Corner.getRightCorner(Direction.EAST));

        assertEquals(Corner.TOP_RIGHT, Corner.getLeftCorner(Direction.EAST));
        assertEquals(Corner.TOP_RIGHT, Corner.getRightCorner(Direction.NORTH));
    }
}
