package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Hero {
    TETile avatar;
    TETile[][] world;
    TETile tileUnder;
    int xPosition;
    int yPosition;
    int numLives;

    public Hero(TETile avatar, TETile[][] world, int initialX, int initialY, int numLives) {
        this.avatar = avatar;
        this.world = world;
        xPosition = initialX;
        yPosition = initialY;
        this.tileUnder = world[initialX][initialY];
        this.numLives = numLives;
        world[initialX][initialY] = this.avatar;
    }

    public int getX() {
        return xPosition;
    }
    public int getY() {
        return yPosition;
    }

    public void changeAvatar(TETile newAvatar) {
        this.avatar = newAvatar;
    }

    public void moveUp() {
        if (canMove(xPosition, yPosition + 1)) {
            world[xPosition][yPosition] = tileUnder;
            yPosition += 1;
            tileUnder = world[xPosition][yPosition];
            world[xPosition][yPosition] = avatar;
        }
    }

    public void moveDown() {
        if (canMove(xPosition, yPosition - 1)) {
            world[xPosition][yPosition] = tileUnder;
            yPosition -= 1;
            tileUnder = world[xPosition][yPosition];
            world[xPosition][yPosition] = avatar;
        }
    }

    public void moveLeft() {
        if (canMove(xPosition - 1, yPosition)) {
            world[xPosition][yPosition] = tileUnder;
            xPosition -= 1;
            tileUnder = world[xPosition][yPosition];
            world[xPosition][yPosition] = avatar;
        }
    }

    public void moveRight() {
        if (canMove(xPosition + 1, yPosition)) {
            world[xPosition][yPosition] = tileUnder;
            xPosition += 1;
            tileUnder = world[xPosition][yPosition];
            world[xPosition][yPosition] = avatar;
        }
    }

    public boolean canMove(int x, int y) {
        if (world[x][y].equals(Tileset.MOUNTAIN)) {
            return false;
        } else {
            return true;
        }
    }
}
