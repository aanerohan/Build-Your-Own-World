package byow.Core;

import java.util.*;

public class Graph{
    private PriorityQueue<Room> pq;

    public Graph(int size) {
        Sort comparator = new Sort();
        pq = new PriorityQueue<>(size, comparator);
    }

    public void createNode(Room room) {
        pq.add(room);
        //intToRoom.put(room.botLx(), room);
    }
    public PriorityQueue<Room> nodes() {
        return pq;
    }

    class Sort implements Comparator<Room> {
        @Override
        public int compare(Room o1, Room o2) {
            return o1.botLx() - o2.botLx();
        }
    }

}
