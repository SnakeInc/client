package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;
import de.uol.snakeinc.pathfinding.PathCell;
import de.uol.snakeinc.pathfinding.Pathfinder;
import de.uol.snakeinc.pathfinding.astar.AStarSearch;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Calculate 10 x 10 grid with sections of free space.
 * @author Sebastian Diers
 */
@Log4j2
public class SectionCalculator {

    // Sektionen handeln(Board in 4 x 4 Sektionen unterteilen).
    // Belegt-Werte Prozentual berechnen.
    // Wenn ein Bereich hochgrading (15%+) nach oben hin(freie Plätze) abweicht
    // ggf. Gegenerpositionen prüfen
    // -> Start async Pathfinding zur freien Sektion

    // Case Path ist aktiv -> Bewerte Züge in die Path-Richtung höher(Gewichtung muss evaluiert werden)
    // bei unbekanntem Hinderniss auf dem Weg -> neue Berechnung

    private double divideWidth;
    private double divideHeight;
    public static int resolution = Config.RESOLUTION;

    public SectionCalculator(int width, int height) {
        this.divideHeight = height / Config.DIVISOR;
        this.divideWidth = width  / Config.DIVISOR;
    }

    /**
     * Calculate values for sections.
     * @param cells cells
     * @param us player us
     */
    public void calculate(Cell[][] cells, Player us) {
        int[][] sections = new int[resolution][resolution];
        int[][] options = new int[resolution][resolution];
        // fill options and section. Options are the total optional blocks, sections are the free blocks
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                int sectionX = (int) Math.floor(x / divideWidth);
                int sectionY = (int) Math.floor(y / divideHeight);
                cells[x][y].setPathHighlight(1.0);
                if (!cells[x][y].isInUse()) {
                    sections[sectionX][sectionY]++;
                }
                options[sectionX][sectionY]++;
            }
        }

        double min = Config.CALCULATE_MIN;
        double max = Config.CALCULATE_MAX;
        double[][] percentages = new double[resolution][resolution];

        // Section-Location of best section;
        int bestX = 0;
        int bestY = 0;

        // Calculate percentages of free space in the sections
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; y < resolution; y++) {
                double percentage = ((double) sections[x][y]) / ((double) options[x][y]);
                if (percentage > max) {
                    max = percentage;
                    bestX = x;
                    bestY = y;
                }
                if (percentage < min) {
                    min = percentage;
                }
                percentages[x][y] = percentage;
            }
        }

        this.rankAreaRiskCells(percentages, cells, min, max);

        // Path-Calculation
        int minX = (int) Math.floor(((double) bestX) * divideWidth);
        int maxX = (int) Math.floor(((double) (bestX + 1)) * divideWidth);
        int minY = (int) Math.floor(((double) bestY) * divideHeight);
        int maxY = (int) Math.floor(((double) (bestY + 1)) * divideHeight);

        int optionX = -1;
        int optionY = -1;
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (x >= cells.length || y >= cells[0].length) {
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

        double difference = max - min;

        if (optionX != -1 && optionY != -1 && us.getX() != -1 && us.getY() != -1) {
            int sectionX = (int) Math.floor(us.getX() / divideWidth);
            int sectionY = (int) Math.floor(us.getY() / divideHeight);
            double range = percentages[sectionX][sectionY] - min;
            double scale = range / difference;

            double value = new LinearInterpolator(
                Config.PATH_HIGHLIGHT_INTERPOLATION_MIN,
                Config.PATH_HIGHLIGHT_INTERPOLATION_MAX
            ).getInterpolation(scale);
            Pathfinder finder = new AStarSearch(cells);
            List<PathCell> pathCells = finder.findPath(cells[us.getX()][us.getY()], cells[optionX][optionY], true);

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
    }

    private void rankAreaRiskCells(double[][] percentages, Cell[][] cells, double min, double max) {
        double difference = max - min;

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                //#start
                int sectionX = (int) Math.floor(x / divideWidth);
                int sectionY = (int) Math.floor(y / divideHeight);

                double range = percentages[sectionX][sectionY] - min;
                double scale = range / difference; // scale from min/max-percentage

                // interpolate based on scale. Lower 50 % will get additional risk, upper 50% will lower their risk
                double value = new
                        LinearInterpolator(Config.AREA_RISK_INTERPOLATION_MAX, Config.AREA_RISK_INTERPOLATION_MIN)
                        .getInterpolation(scale);
                //System.out.println("Value: " +  value + " Scale: " + scale + " Range: " + range);
                // set value here
                //#end -> In own for-loop for performance
                cells[x][y].setAreaRisk(value);
            }
        }
    }

}
