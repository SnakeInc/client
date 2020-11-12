package de.uol.snakeinc.possibleMoves;

import java.util.Arrays;

public class CoordinateMap {

    private int[] coordinates;
    private int x;
    private int y;

    public CoordinateMap(int x, int y) {
        coordinates = new int[x * y];
    }

    public CoordinateMap(CoordinateMap map) {
        this.coordinates = map.coordinates;
    }

    public void recycle() {
        Arrays.fill(coordinates, 0);
    }

    public void put(int x, int y, int v) {
        if (x < this.x && y < this.y) {
            int key = (this.y * x) + y;
            coordinates[key] = v;
        }
    }

    public void putUnchecked(int x, int y, int v) {
        int key = (this.y * x) + y;
        coordinates[key] = v;
    }

    public int remove(int x, int y) {
        if (x < this.x && y < this.y) {
            int key = (this.y * x) + y;
            int res = coordinates[key];
            coordinates[key] = 0;
            return res;
        }
        return 0;
    }

    public int removeUnchecked(int x, int y) {
        int key = (this.y * x) + y;
        int res = coordinates[key];
        coordinates[key] = 0;
        return res;
    }

    public boolean contains(int x, int y) {
        if (x < this.x && y < this.y) {
            int key = (this.y * x) + y;
            return coordinates[key] != 0;
        }
        return false;
    }

    public boolean containsUnchecked(int x, int y) {
        int key = (this.y * x) + y;
        return coordinates[key] != 0;
    }

}
