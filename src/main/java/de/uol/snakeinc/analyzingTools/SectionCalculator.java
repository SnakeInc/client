package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;
import lombok.extern.log4j.Log4j2;

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

    private double devideWidth;
    private double devideHeight;

    public static int resolution = Config.RESOLUTION;

    public SectionCalculator(int width, int height) {
        this.devideHeight = height / Config.DIVISOR;
        this.devideWidth = width  / Config.DIVISOR;
    }

    /**
     * Calculate values for sections.
     * @param cells cells
     */
    public void calculate(Cell[][] cells) {
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

        double min = Config.CALCULATE_MIN;
        double max = Config.CALCULATE_MAX;
        double[][] percentages = new double[resolution][resolution];
        // Calculate percentages of free space in the sections
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; y < resolution; y++) {
                double percentage = (sections[y][x] * 1.0D) / (options[y][x] * 1.0D);
                if (percentage > max) {
                    max = percentage;
                }
                if (percentage < min) {
                    min = percentage;
                }
                percentages[y][x] = percentage;
            }
        }

        this.rankAreaRiskCells(percentages, cells, min, max);
    }

    private void rankAreaRiskCells(double[][] percentages, Cell[][] cells, double min, double max) {
        double difference = max - min;

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                //#start
                int sectionX = (int) Math.floor(x / devideWidth);
                int sectionY = (int) Math.floor(y / devideHeight);

                double range = percentages[sectionY][sectionX] - min;
                double scale = (range) / (difference); // scale from min/max-percentage

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
