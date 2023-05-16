package byow.Core;

import java.util.ArrayList;

public class Room {

    private int width;
    private int height;
    private int x;
    private int y;

    public Room(int width, int height, int bottX, int bottY) {
        this.width = width;
        this.height = height;
        this.x = bottX;
        this.y = bottY;
    }
 /*
    public boolean containsTile(int x, int y) {
        return (x >= botLx() && x <= botRx()) && (y >= botLy() && y <= topLy());
    }

    public boolean intersects (Room room) {
        for(int[] coord: room.coords()) {
            if (this.containsTile(coord[0], coord[1])) {
                return true;
            }
        }
        return false;
    }
*/

    public ArrayList<int[]> coords () {
        ArrayList<int[]> interiorCoords = new ArrayList<>();
        for (int posX = x; posX < x + width; posX++) {
            for (int posY = y; posY < y + height; posY++) {
                int[] xy = new int[] {posX, posY};
                interiorCoords.add(xy);
            }
        }
        return interiorCoords;
    }

    public int botRx() {
        return x + width -1;
    }
    public int botRy() {
        return y;
    }
    public int topRx() {
        return botRx();
    }
    public int topRy() {
        return y+height -1;
    }
    public int topLx() {
        return x;
    }
    public int topLy() {
        return topRy();
    }
    public int botLx() {
        return topLx();
    }
    public int botLy() {
        return botRy();
    }
    public int getMidX() {
        return x + (width / 2);
    }

    public int getMidY() {
        return y + (height / 2);
    }

    public int getWidth () {
        return width;
    }
    public int getHeight () {
        return height;
    }

}
