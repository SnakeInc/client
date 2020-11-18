package de.uol.snakeinc.util;

import de.uol.snakeinc.entities.Coordinates;
import de.uol.snakeinc.possibleMoves.IntSet;

import java.util.ArrayList;

public class CoordinateMap {

    private int[][] map;
    private boolean[] updated;
    private int x;
    private int y;

    public CoordinateMap(int x, int y) {
        this.map = new int[x][];
        this.updated = new boolean[x];

        this.x = x;
        this.y = y;
    }

    public CoordinateMap(CoordinateMap parent) {
        this.x = parent.x;
        this.y = parent.y;

        this.map = parent.map.clone(); // shallow copy intended
        this.updated = new boolean[x];
    }

    /**
     * places the value v at the position (x,y).
     * @param x x coordinate
     * @param y y coordinate
     * @param v the value to be placed
     */
    public void put(int x, int y, int v) {
        if (updated[x]) {
            this.map[x][y] = v;
        } else {
            if (this.map[x] == null) {
                this.map[x] = new int[this.y];
            } else {
                this.map[x] = this.map[x].clone();
            }
            this.map[x][y] = v;
            updated[x] = true;
        }
    }

    /**
     * puts all the coordinates in the map, and reports values that collided with each other.
     * @param coordinates the coordinates to be added
     * @param res         where the collisions are registered
     * @return res
     */
    public IntSet putAll(ArrayList<Coordinates> coordinates, IntSet res) {
        for (int i = 0; i < coordinates.size(); i++) {
            var coordinate = coordinates.get(i);
            int get = this.get(coordinate.getX(), coordinate.getY());
            if (get != 0) {
                res.add(coordinate.getPlayer());
                if (get > 0) {
                    res.add(get);
                }
            } else {
                put(coordinate.getX(), coordinate.getY(), coordinate.getPlayer());
            }
        }
        return res;
    }

    public boolean contains(int x, int y) {
        return this.map[x] != null && this.map[x][y] != 0;
    }

    public boolean contains(Coordinates coordinates) {
        return this.contains(coordinates.getX(), coordinates.getY());
    }

    public int get(int x, int y) {
        return this.map[x] != null ? this.map[x][y] : 0;
    }

    public int get(Coordinates coordinates) {
        return this.get(coordinates.getX(), coordinates.getY());
    }
}
