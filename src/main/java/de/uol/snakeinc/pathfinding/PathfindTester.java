package de.uol.snakeinc.pathfinding;

import de.uol.snakeinc.pathfinding.astar.AStarSearch;

import java.util.List;
import java.util.Random;

public class PathfindTester {

    public void testPathfinding() {
        int size = 80;
        PathCell[][] field = this.createField(size, 3);
        Pathfinder finder = new AStarSearch(field);

        PathCell start = field[new Random().nextInt(size)][new Random().nextInt(size)];
        PathCell end = field[new Random().nextInt(size)][new Random().nextInt(size)];

        System.out.println("Start: " + start.getY() + " " + start.getX());
        System.out.println("End: " + end.getY() + " " + end.getX());

        long time = System.nanoTime();
        List<PathCell> path = finder.findPath(start, end);
        double totalTime = (System.nanoTime() - time) / 1000000.0D;
        System.out.println("Zeit: " + totalTime + "ms");
        if (path == null) {
            System.out.println("Pfad konnte nicht gefunden werden!");
        }
        char[][] chars = this.getChars(field, path);
        this.printField(chars);
    }

    private PathCell[][] createField(int size, int chance) {
        PathCell[][] cells = new PathCell[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                boolean inUse = false;
                if (new Random().nextInt(chance) == 0) {
                    inUse = true;
                }
                cells[y][x] = new PathCell(inUse, x, y);
            }
        }
        return cells;
    }

    private char[][] getChars(PathCell[][] cells, List<PathCell> path) {
        char[][] chars = new char[cells.length][cells[0].length];
        for (int x = 0; x < cells[0].length; x++) {
            for (int y = 0; y < cells.length; y++) {
                chars[y][x] = ' ';
            }
        }
        if (path != null) {
            for (PathCell cell : path) {
                chars[cell.getY()][cell.getX()] = '*';
            }
        }
        for (int x = 0; x < cells[0].length; x++) {
            for (int y = 0; y < cells.length; y++) {
                if (cells[y][x].isInUse()) {
                    chars[y][x] = '#';
                }
            }
        }
        return chars;
    }

    private void printField(char[][] chars) {
        for (int x = 0; x < chars[0].length; x++) {
            String text = "";
            for (int y = 0; y < chars.length; y++) {
                text = text + chars[y][x];
            }
            System.out.println(text);
        }
    }

}
