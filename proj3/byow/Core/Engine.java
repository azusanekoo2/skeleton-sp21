package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int ROOM_COUNT = 20;
    private Position avatarPosition;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void interactWithKeyboard() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();

        drawMainMenu();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'N') {
                    String seed = readSeedFromKeyboard();
                    TETile[][] world = interactWithInputString("N" + seed + "S");
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(world);
                    playGameWithKeyboard(world, "N" + seed + "S");
                    return;
                } else if (key == 'L') {
                    String inputHistory = loadGame();
                    TETile[][] world = interactWithInputString(inputHistory);

                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(world);

                    playGameWithKeyboard(world, inputHistory);
                    return;
                } else if (key == 'Q') {
                    return;
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        String upper = input.toUpperCase();
        if (upper.startsWith("L")) {
            String savedInput = loadGame();
            String newMoves = upper.substring(1);
            input = savedInput + newMoves;
        }
        String cleanedInput = cleanInputBeforeSave(input);
        long seed = parseSeed(cleanedInput);
        Random random = new Random(seed);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                finalWorldFrame[x][y] = Tileset.NOTHING;
            }
        }
        List<Position> centers = addRandomRooms(finalWorldFrame, random, ROOM_COUNT);
        connectAllRooms(finalWorldFrame, centers);
        addWallsAroundFloors(finalWorldFrame);
        addAvatar(finalWorldFrame, centers);

        String moves = parseMoves(cleanedInput);
        for (int i = 0; i < moves.length(); i++) {
            moveAvatar(finalWorldFrame, moves.charAt(i));
        }
        if (input.toUpperCase().contains(":Q")) {
            saveGame(cleanedInput);
        }
        return finalWorldFrame;
    }

    private long parseSeed(String input) {
        String upper = input.toUpperCase();
        int sIndex = upper.indexOf('S');
        String seedText = upper.substring(1, sIndex);
        long seed = Long.parseLong(seedText);
        return seed;
    }

    private void addRoom(TETile[][] world, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                world[x][y] = Tileset.WALL;
            }
        }
        for (int x = startX + 1; x < startX + width - 1; x++) {
            for (int y = startY + 1; y < startY + height - 1; y++) {
                world[x][y] = Tileset.FLOOR;
            }
        }
    }

    private boolean canPlaceRoom(TETile[][] world, int startX, int startY, int width, int height) {
        boolean emptyPlace = true;
        if (startX <= 0
                || startY <= 0
                || startX + width > world.length - 1
                || startY + height > world[0].length - 1) {
            return false;
        }
        for (int x = startX - 1; x < startX + width + 1; x++) {
            for (int y = startY - 1; y < startY + height + 1; y++) {
                if (world[x][y] != Tileset.NOTHING) {
                    emptyPlace = false;
                    break;
                }
            }
        }
        return emptyPlace;
    }

    private Position addRandomRoom(TETile[][] world, Random random) {
        for (int i = 0; i < 100; i++) {
            int width = random.nextInt(6) + 3;
            int height = random.nextInt(6) + 3;

            int startX = random.nextInt(world.length);
            int startY = random.nextInt(world[0].length);

            if (canPlaceRoom(world, startX, startY, width, height)) {
                addRoom(world, startX, startY, width, height);
                int centerX = startX + width / 2;
                int centerY = startY + height / 2;
                return new Position(centerX, centerY);
            }
        }
        return null;
    }

    private List<Position> addRandomRooms(TETile[][] world, Random random, int roomCount) {
        List<Position> centers = new ArrayList<>();
        for (int i = 0; i < roomCount; i++) {
            Position center = addRandomRoom(world, random);
            if (center != null) {
                centers.add(center);
            }
        }
        return centers;
    }

    private void addHorizontalHallway(TETile[][] world, int x1, int x2, int y) {
        int start = Math.min(x1, x2);
        int end = Math.max(x1, x2);
        for (int x = start; x <= end; x++) {
            world[x][y] = Tileset.FLOOR;
        }
    }

    private void addVerticalHallway(TETile[][] world, int y1, int y2, int x) {
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);
        for (int y = start; y <= end; y++) {
            world[x][y] = Tileset.FLOOR;
        }
    }

    private void connectRoom(TETile[][] world, Position a, Position b) {
        addHorizontalHallway(world, a.x, b.x, a.y);
        addVerticalHallway(world, a.y, b.y, b.x);
    }

    private void connectAllRooms(TETile[][] world, List<Position> centers) {
        for (int i = 0; i < centers.size() - 1; i++) {
            connectRoom(world, centers.get(i), centers.get(i + 1));
        }
    }

    private boolean inBounds(TETile[][] world, int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }

    private void addWallIfNothing(TETile[][] world, int x, int y) {
        if (inBounds(world, x, y) && world[x][y] == Tileset.NOTHING) {
            world[x][y] = Tileset.WALL;
        }
    }

    private void addWallsAroundFloor(TETile[][] world, int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    addWallIfNothing(world, x + dx, y + dy);
                }
            }
        }
    }

    private void addWallsAroundFloors(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    addWallsAroundFloor(world, x, y);
                }
            }
        }
    }

    private void addAvatar(TETile[][] world, List<Position> centers) {
        if (!centers.isEmpty()) {
            avatarPosition = centers.get(0);
            world[avatarPosition.x][avatarPosition.y] = Tileset.AVATAR;
        }
    }

    private static String parseMoves(String input) {
        String upper = input.toUpperCase();
        int sIndex = upper.indexOf('S');
        String move = upper.substring(sIndex + 1);
        return move;
    }

    private void moveAvatar(TETile[][] world, char move) {
        if (move == 'W') {
            moveAvatarBy(world, 0, 1);
        } else if (move == 'A') {
            moveAvatarBy(world, -1, 0);
        } else if (move == 'S') {
            moveAvatarBy(world, 0, -1);
        } else if (move == 'D') {
            moveAvatarBy(world, 1, 0);
        }
    }

    private void moveAvatarBy(TETile[][] world, int dx, int dy) {
        int nextX = avatarPosition.x + dx;
        int nextY = avatarPosition.y + dy;
        if (world[nextX][nextY] == Tileset.FLOOR) {
            world[avatarPosition.x][avatarPosition.y] = Tileset.FLOOR;
            avatarPosition.x = nextX;
            avatarPosition.y = nextY;
            world[avatarPosition.x][avatarPosition.y] = Tileset.AVATAR;
        }
    }

    private void saveGame(String inputHistory) {
        Path savePath = Paths.get("save.txt");
        try {
            Files.writeString(savePath, inputHistory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String cleanInputBeforeSave(String input) {
        String upper = input.toUpperCase();
        int quitIndex = upper.indexOf(":Q");
        if (quitIndex == -1) {
            return input;
        }
        return input.substring(0, quitIndex);
    }

    private String loadGame() {
        Path savePath = Paths.get("save.txt");
        try {
            return Files.readString(savePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawMainMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.7, "CS61B: THE GAME");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.5, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.45, "Load Game (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.4, "Quit (Q)");

        StdDraw.show();
    }

    private String readSeedFromKeyboard() {
        String seed = "";
        while (true) {
            drawSeedScreen(seed);
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'S' && seed.length() > 0) {
                    return seed;
                } else if (Character.isDigit(key)) {
                    seed += key;
                }
            }
        }
    }

    private void drawSeedScreen(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.7, "Enter Seed");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.55, seed);
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.4, "Press S to start");

        StdDraw.show();
    }

    private void playGameWithKeyboard(TETile[][] world, String inputHistory) {
        boolean waitingForQ = false;
        renderWorldWithHUD(world);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (waitingForQ && key == 'Q') {
                    saveGame(inputHistory);
                    return;
                }

                waitingForQ = key == ':';
                if (key == 'W' || key == 'A' || key == 'S' || key == 'D') {
                    moveAvatar(world, key);
                    inputHistory += key;
                    renderWorldWithHUD(world);
                }
            }
            StdDraw.pause(20);
        }
    }

    private void renderWorldWithHUD(TETile[][] world) {
        ter.renderFrame(world);

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();

        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(1, HEIGHT - 1, world[mouseX][mouseY].description());
            StdDraw.show();
        }
    }

    private static boolean sameWorld(TETile[][] a, TETile[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            return false;
        }

        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[0].length; y++) {
                if (a[x][y] != b[x][y]) {
                    return false;
                }
            }
        }

        return true;
    }
}
