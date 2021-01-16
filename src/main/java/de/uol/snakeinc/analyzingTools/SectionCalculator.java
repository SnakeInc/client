package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;
import de.uol.snakeinc.pathfinding.PathCell;
import de.uol.snakeinc.pathfinding.Pathfinder;
import de.uol.snakeinc.pathfinding.astar.AStarSearch;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
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

        double min = 1.0D; // @Joost no magic Value
        double max = 0.0D; // @Joost no magic Value
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
                }
                if (percentage < min) {
                    min = percentage;
                }
                percentages[x][y] = percentage;
            }
        }
        double deepMin = 1.0;
        double deepMax = 0.0D;
        double[][] deepPercentages = this.createDeepIterations(percentages);
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; y < resolution; y++) {
                double percentage = deepPercentages[x][y];

                if (percentage > deepMax) {
                    deepMax = percentage;
                    bestX = x;
                    bestY = y;
                }
                if (percentage < deepMin) {
                    deepMin = percentage;
                }
            }
        }
        System.out.println("DeepMin: " + deepMin + " DeepMax: " + deepMax);

        this.rankAreaRiskCells(deepPercentages, cells, deepMin, deepMax);
        this.rankPathHighlightCells(deepPercentages, cells, us, bestX, bestY, min, max);
    }

    private void rankPathHighlightCells(
        double[][] percentages,
        Cell[][] cells,
        Player us,
        double bestX,
        double bestY,
        double min,
        double max
    ) {

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
            List<PathCell> pathCells = finder.findPath(cells[us.getX()][us.getY()], cells[optionX][optionY]);

            if (pathCells != null) {
                int count = Config.PATH_HIGHLIGHT_PATH_RANGE;
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

                double percentage = percentages[sectionX][sectionY];

                double range = percentage - min;
                double scale = range / difference;
                double value = new LinearInterpolator(Config.AREA_RISK_INTERPOLATION_MAX, Config.AREA_RISK_INTERPOLATION_MIN)
                    .getInterpolation(scale);
                // set value here
                //#end -> In own for-loop for performance
                cells[x][y].setAreaRisk(value);
            }
        }
    }

    private double[][] createDeepIterations(double[][] percentages) {
        HashMap<Integer, double[][]> percentageIterations = new HashMap<Integer, double[][]>();
        int divisionAmountSquare = 1;
        int divisionAmount = 1;
        int localResolution = Config.RESOLUTION;

        double[][] deepPercentages = new double[Config.RESOLUTION][Config.RESOLUTION];

        for (int iteration = 0; iteration < Config.ITERATIONS; iteration++) {
            double[][] percentagesGrid = new double[localResolution][localResolution];
            for (int x = 0; x < percentages.length; x++) {
                for (int y = 0; y < percentages[0].length; y++) {
                    int dividedX = (int) Math.floor(((double) x) / ((double) divisionAmount));
                    int dividedY = (int) Math.floor(((double) y) / ((double) divisionAmount));

                    double percentage = percentages[x][y] / ((double) divisionAmountSquare);
                    percentagesGrid[dividedX][dividedY] = percentage;
                }
            }
            percentageIterations.put(iteration, percentagesGrid);
            divisionAmountSquare *= 4;
            divisionAmount *= 2;
            localResolution /= 2;
        }
        for (int x = 0; x < percentages.length; x++) {
            for (int y = 0; y < percentages[0].length; y++) {
                divisionAmount = 1;
                double percentage = 0.0D;
                double division = 0.0D;

                for (int iteration = 0; iteration < Config.ITERATIONS; iteration++) {
                    int dividedX = (int) Math.floor(((double) x) / ((double) divisionAmount));
                    int dividedY = (int) Math.floor(((double) y) / ((double) divisionAmount));

                    percentage += percentageIterations.get(iteration)[dividedX][dividedY] * ((double) Config.MULTIPLICATIONS[iteration]);
                    division += Config.MULTIPLICATIONS[iteration];

                    divisionAmount *= 2;
                }

                percentage = percentage / division;
                deepPercentages[x][y] = percentage;
            }
        }
        return deepPercentages;
    }

}
