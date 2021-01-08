package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;
import de.uol.snakeinc.pathfinding.PathCell;
import de.uol.snakeinc.pathfinding.Pathfinder;
import de.uol.snakeinc.pathfinding.astar.AStarSearch;
import lombok.CustomLog;

import java.util.List;

/**
 * Calculate 10 x 10 grid with sections of free space.
 * @author Sebastian Diers
 */
@CustomLog
public class SectionCalculator {

    // Sektionen handeln(Board in 4 x 4 Sektionen unterteilen).
    // Belegt-Werte Prozentual berechnen.
    // Wenn ein Bereich hochgrading (15%+) nach oben hin(freie Plätze) abweicht
    // ggf. Gegenerpositionen prüfen
    // -> Start async Pathfinding zur freien Sektion

    // Case Path ist aktiv -> Bewerte Züge in die Path-Richtung höher(Gewichtung muss evaluiert werden)
    // bei unbekanntem Hinderniss auf dem Weg -> neue Berechnung

    private double devideWidth;
    private double devideHeight;

    private int height;
    private int width;

    public static int resolution = 10;

    public SectionCalculator(int width, int height) {
        this.devideHeight = height / 10.0D;
        this.devideWidth = width  / 10.0D;

        this.height = height;
        this.width = width;
    }

    /**
     * Calculate values for sections.
     * @param cells cells
     */
    public void calculate(Cell[][] cells, Player us) {
        long time = System.nanoTime();

        int[][] sections = new int[resolution][resolution];
        int[][] options = new int[resolution][resolution];
        // fill options and section. Options are the total optional blocks, sections are the free blocks
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                int sectionX = (int) Math.floor(x / devideWidth);
                int sectionY = (int) Math.floor(y / devideHeight);
                if (cells[x][y].getValue() == 1) {
                    sections[sectionY][sectionX]++;
                }
                options[sectionY][sectionX]++;
            }
        }

        double min = 100.0D;
        double max = 0.0D;
        double[][] percentages = new double[resolution][resolution];

        // Section-Location of best section;
        int bestX = 0;
        int bestY = 0;
        // Calculate percentages of free space in the sections
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; y < resolution; y++) {
                double percentage = (sections[y][x] * 1.0D) / (options[y][x] * 1.0D);
                if (percentage > max) {
                    max = percentage;
                    bestX = x;
                    bestY = y;
                }
                if (percentage < min) {
                    min = percentage;
                }
                percentages[y][x] = percentage;
            }
        }

        this.rankAreaRiskCells(percentages, cells, min, max);

        // Path-Calculation
        int minX = (int) Math.floor(bestX * devideWidth);
        int maxX = (int) Math.floor((bestX+1) * devideWidth);
        int minY = (int) Math.floor(bestY * devideWidth);
        int maxY = (int) Math.floor((bestY+1) * devideWidth);

        int optionX = -1;
        int optionY = -1;
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if(x >= cells.length || y >= cells[0].length) {
                    continue;
                }

                if (!cells[x][y].isInUse()) {
                    optionX = x;
                    optionY = y;
                    break;
                }
            }
            if (optionX != -1 && optionY != -1) {
                break;
            }
        }

        if (optionX != -1 && optionY != -1 && us.getX() != -1 && us.getY() != -1) {
            int sectionX = (int) Math.floor(us.getX() / devideWidth);
            int sectionY = (int) Math.floor(us.getY() / devideHeight);
            double range = percentages[sectionY][sectionX] - min;
            double scale = (range * 1.0D) / (difference * 1.0D);

            double value = new LinearInterpolator(1.0, 1.2).getInterpolation(scale);

            Pathfinder finder = new AStarSearch(cells);
            List<PathCell> pathCells = finder.findPath(cells[us.getX()][us.getY()], cells[optionX][optionY]);

            if (pathCells != null) {
                int count = 8;
                for (PathCell pathCell : pathCells) {
                    if (count <= 0) {
                        break;
                    }
                    Cell cell = (Cell) pathCell;
                    cell.setPathHighlight(value);
                    count--;
                }
            }
        }

        double totalTime = (System.nanoTime() - time) / 1000000.0D;
        log.info("Zeit: " + totalTime + "ms");
    }

    private void rankAreaRiskCells(double[][] percentages, Cell[][] cells, double min, double max) {
        double difference = max - min;

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                //#start
                int sectionX = (int) Math.floor(x / devideWidth);
                int sectionY = (int) Math.floor(y / devideHeight);

                double range = percentages[sectionY][sectionX] - min;
                double scale = (range * 1.0D) / (difference * 1.0D); // scale from min/max-percentage

                // interpolate based on scale. Lower 50 % will get additional risk, upper 50% will lower their risk
                double value = new LinearInterpolator(1.2, 1.0).getInterpolation(scale);
                //System.out.println("Value: " +  value + " Scale: " + scale + " Range: " + range);
                // set value here
                //#end -> In own for-loop for performance
                cells[x][y].setAreaRisk(value);
            }
        }
    }

}
