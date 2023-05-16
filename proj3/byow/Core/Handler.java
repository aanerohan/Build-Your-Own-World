package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.lang.reflect.WildcardType;
import java.util.*;
import edu.princeton.cs.algs4.StdDraw;

public class Handler {

    // tiles
    private TETile wallTile = Tileset.MOUNTAIN;
    private TETile blankTile = Tileset.NOTHING;
    private TETile interiorTile = Tileset.WATER;

    //defines the world
    private TETile[][] world;
    private Hero hero;
    private int width;
    private int height;
    private int numRooms;
    private boolean gameOver;

    Graph graph;
    Random random;


    public Handler(int width, int height, long seed) {
        int minRooms = 15;
        int maxRooms = 25;
        gameOver = false;

        random = new Random(seed);
        numRooms = RandomUtils.uniform(random, minRooms, maxRooms);
        this.graph = new Graph(numRooms);

        this.width = width;
        this.height = height;
        this.world = new TETile[width][height];
        initialize();
        createRooms(numRooms);
        addHallways();
        addWalls();
        addHero();
    }

    private void addHero() {
        Room startingRoom = graph.nodes().peek();
        hero = new Hero(Tileset.AVATAR, this.world, startingRoom.getMidX(), startingRoom.getMidY(), 3);
    }

    private void addWalls() {
        for (int posX = 0; posX < width; posX++) {
            for (int posY = 0; posY < height; posY++) {
                if (world[posX][posY].equals(interiorTile)) {
                    for (int[] coord: neighborTiles(posX, posY)) {
                        if (world[coord[0]][coord[1]].equals(blankTile)) {
                            world[coord[0]][coord[1]] = wallTile;
                        }
                    }
                }
            }
        }
    }


    private void initialize() {
        for (int posX = 0; posX < width; posX++) {
            for (int posY = 0; posY < height; posY++) {
                world[posX][posY] = blankTile;
            }
        }
    }

    private void createRooms(int num) {
        int minRoomWidth = 2;
        int maxRoomWidth = 7;
        int minRoomHeight = 2;
        int maxRoomHeight = 7;
        int numIterations = 0;

        for (int i = 0; i < numRooms*2; i++) {
            int width = RandomUtils.uniform(random, minRoomWidth, maxRoomWidth);
            int height = RandomUtils.uniform(random, minRoomHeight, maxRoomHeight);
            int bottX = RandomUtils.uniform(random, 1, this.width - maxRoomWidth);
            int bottY = RandomUtils.uniform(random, 1, this.height - maxRoomHeight);

            Room room = new Room(width, height, bottX, bottY);
            if (inBounds(room) && validLoc(room)) {
                graph.createNode(room);
                changeTiles(room);
                numIterations+=1;
            }
        }
        this.numRooms = numIterations;
    }

    private boolean inBounds(Room room) {
        return pointBounds(room.botLx(), room.botLy()) && pointBounds(room.topRx(), room.topRy());
    }

    private boolean pointBounds(int x, int y) {
        return x>=0 && x < width && y >=0 && y < height;
    }

    private void changeTiles (Room room) {
        ArrayList<int[]> coords = room.coords();
        for (int[] pos: coords) {
            world[pos[0]][pos[1]] = interiorTile;
        }
    }

    private void changeTiles (int x, int y) {
        world[x][y] = interiorTile;
    }

    public TETile[][] getWorld() {
        return world;
    }
    public boolean validLoc(Room room) {
        int enclosing = 6;
        Room enclosingRoom = new Room(room.getWidth() + enclosing, room.getHeight()+enclosing, room.botLx() - 3, room.botLy() - 3);
        for (int[] coord: enclosingRoom.coords()) {
            if (!inBounds(enclosingRoom) || !world[coord[0]][coord[1]].equals(blankTile)) {
                return false;
            }
        }
        return true;
    }

    public void addHallways () {
        PriorityQueue<Room> nodes = graph.nodes();

        for (int i = 1; i < numRooms; i++) {
            Room r1 = nodes.remove();
            Room r2 = nodes.element();
            createHallway(r1, r2);
        }
    }

    private void createHallway(Room from, Room to) {
        //could create getPos method
        int fromX = RandomUtils.uniform(random, from.botLx(), from.botRx());
        int fromY = RandomUtils.uniform(random, from.botLy(), from.topLy());
        int toX = RandomUtils.uniform(random, to.botLx(), to.botRx());
        int toY = RandomUtils.uniform(random, to.botLy(), to.topLy());
        int dirX = checkDir(fromX, toX);
        int dirY = checkDir(fromY, toY);

        hallsX(fromX, toX, fromY, dirX);
        hallsY(fromY, toY, toX, dirY);

    }

    private int checkDir(int fromCoord, int toCoord) {
        if (toCoord > fromCoord) {
            return 1;
        }
        return -1;
    }


    private void hallsX(int fromX, int toX, int y, int step) {
        int x = fromX;
        while ((x>= fromX && x<=toX) || (x<=fromX) && x>=toX) {
            changeTiles(x, y);
            x += step;
        }
    }

    private void hallsY(int fromY, int toY, int x, int step) {
        int y = fromY;
        while ((y>= fromY && y<=toY) || (y<=fromY) && y>=toY) {
            changeTiles(x, y);
            y += step;
        }
    }

    private HashSet<int[]> neighborTiles (int x, int y) {
        HashSet<int[]> neighbors = new HashSet<>();
        neighbors.add(new int[] {x, y+1});
        neighbors.add(new int[] {x, y-1});
        neighbors.add(new int[] {x+1, y});
        neighbors.add(new int[] {x-1, y});
        neighbors.add(new int[] {x+1, y+1});
        neighbors.add(new int[] {x-1, y+1});
        neighbors.add(new int[] {x+1, y-1});
        neighbors.add(new int[] {x-1, y-1});
        return neighbors;
    }

    public void handleHeroMovement(char input) {
        if (input == 'w' || input == 'W') {
            hero.moveUp();
        } else if (input == 'd' || input == 'D') {
            hero.moveRight();
        } else if (input == 's' || input == 'S') {
            hero.moveDown();
        } else if (input == 'a' || input == 'A') {
            hero.moveLeft();
        }
        if (hero.tileUnder.equals(Tileset.MOUNTAIN)) {
            gameOver = true;
        }
    }


    public void setWorld(TETile[][] newWorld) {
        this.world = newWorld;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
