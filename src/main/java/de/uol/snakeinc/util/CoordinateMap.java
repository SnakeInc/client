package de.uol.snakeinc.util;

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

    public void put(int x, int y, int v) {
        if (updated[x]) {
            this.map[x][y] = v;
        } else {
            if (this.map[x] == null) {
                this.map[x] = new int[y];
            } else {
                this.map[x] = this.map[x].clone();
            }
            this.map[x][y] = v;
            updated[x] = true;
        }
    }

    public boolean contains(int x, int y) {
        return this.map[x] != null && this.map[x][y] != 0;
    }

    public int get(int x, int y) {
        return this.map[x] != null ? this.map[x][y] : 0;
    }
}
