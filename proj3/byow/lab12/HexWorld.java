package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static int rowWidth(int size, int row) {
        if (row < size) {
            return size + 2 * row;
        } else {
            int mirrorRow = 2 * size - 1 - row;
            return size + mirrorRow * 2;
        }
    }

    private static int rowXOffset(int size, int row) {
        if (row < size) {
            return size - 1 - row;
        } else {
            int mirrorRow = 2 * size - 1 - row;
            return size - 1 - mirrorRow;
        }
    }

    private static void addHexagon(TETile[][] world, int startX, int startY, int size, TETile tile) {
        for (int i = 0; i < 2 * size; i++) {
            int width = rowWidth(size, i);
            int offset = rowXOffset(size, i);
            int y = i + startY;
            for (int j = 0; j < width; j++) {
                int x = j + startX + offset;
                world[x][y] = tile;
            }
        }
    }

    private static void addHexagonColumn(TETile[][] world, int x, int y, int count, int size, TETile tile) {
        for (int i = 0; i < count; i++) {
            addHexagon(world, x, y + 2 * i * size, size, tile);
        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int size = 3;
        int startX = 5;
        int startY = 10;
        int dx = 2 * size - 1;

        addHexagonColumn(world, startX + 0 * dx, startY - 0 * size, 3, size, Tileset.FLOWER);
        addHexagonColumn(world, startX + 1 * dx, startY - 1 * size, 4, size, Tileset.GRASS);
        addHexagonColumn(world, startX + 2 * dx, startY - 2 * size, 5, size, Tileset.FLOWER);
        addHexagonColumn(world, startX + 3 * dx, startY - 1 * size, 4, size, Tileset.GRASS);
        addHexagonColumn(world, startX + 4 * dx, startY - 0 * size, 3, size, Tileset.FLOWER);

        // draws the world to the screen
        ter.renderFrame(world);
    }

}
