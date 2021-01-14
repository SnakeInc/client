package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;
import lombok.CustomLog;

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
    public void calculate(Cell[][] cells) {
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

                double percentage = percentages[sectionY][sectionX];

                // interpolate based on scale. Lower 50 % will get additional risk, upper 50% will lower their risk
                double range = percentage - min;
                double scale = ((double) range) / ((double) difference); // scale from min/max-percentage

                double value = 1.0D;
                if (scale > 0.5) {
                    scale = (scale - 0.5D) * 2.0D;
                    value = new LinearInterpolator(1.125, 1.0).getInterpolation(scale);
                } else {
                    scale = scale * 2.0D;
                    value = new LinearInterpolator(1.2, 1.125).getInterpolation(scale);
                }

                //System.out.println("Value: " +  value + " Scale: " + scale + " Range: " + range);
                // set value here
                //#end -> In own for-loop for performance
                cells[x][y].setAreaRisk(value);
            }
        }
    }

}
