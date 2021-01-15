package de.uol.snakeinc.pathfinding;

import de.uol.snakeinc.pathfinding.astar.AStarSearch;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Random;

@Log4j2
public class PathfindTester {

    /**
     * Method to test pathfinding.
     */
    public void testPathfinding() {
        int width = 80;
        int height = 20;
        PathCell[][] field = this.createField(width, height, 5);
        Pathfinder finder = new AStarSearch(field);

        PathCell start = field[new Random().nextInt(width)][new Random().nextInt(height)];
        PathCell end = field[new Random().nextInt(width)][new Random().nextInt(height)];

        log.info("Start: " + start.getY() + " " + start.getX());
        log.info("End: " + end.getY() + " " + end.getX());

        long time = System.nanoTime();
        List<PathCell> path = finder.findPath(start, end);
        double totalTime = (System.nanoTime() - time) / 1000000.0D;
        log.info("Zeit: " + totalTime + "ms");
        if (path == null) {
            log.info("Pfad konnte nicht gefunden werden!");
        }
        char[][] chars = this.getChars(field, path);
        this.printField(chars);
    }

    private PathCell[][] createField(int width, int height, int chance) {
        PathCell[][] cells = new PathCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean inUse = false;
                if (new Random().nextInt(chance) == 0) {
                    inUse = true;
                }
                cells[x][y] = new DefaultPathCell(inUse, x, y);
            }
        }
        return cells;
    }

    private char[][] getChars(PathCell[][] cells, List<PathCell> path) {
        char[][] chars = new char[cells.length][cells[0].length];
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                chars[x][y] = ' ';
            }
        }
        if (path != null) {
            for (PathCell cell : path) {
                chars[cell.getX()][cell.getY()] = '*';
            }
        }
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                if (cells[x][y].isInUse()) {
                    chars[x][y] = '#';
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
            log.info(text);
        }
    }

}
