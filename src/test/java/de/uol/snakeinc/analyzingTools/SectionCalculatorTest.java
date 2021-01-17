package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.tests.WhiteBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SectionCalculatorTest {

    @Test
    public void testRankAreaRiskCells() {
        double division = 8.0D;
        SectionCalculator calculator = new SectionCalculator(80, 80);
        WhiteBox.setInternalState(calculator, "resolution", 10);
        WhiteBox.setInternalState(calculator, "iterations", 1);
        WhiteBox.setInternalState(calculator, "divideWidth", 8.0D);
        WhiteBox.setInternalState(calculator, "divideHeight", 8.0D);

        Cell[][] cells = new Cell[80][80];
        for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 80; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        double min = 0.1;
        double max = 0.9;

        double[][] percentages = new double[][] {
            { 0.1, 0.5, 0.5, 0.7, 0.6, 0.8, 0.3, 0.2, 0.9, 0.1 },
            { 0.7, 0.9, 0.5, 0.3, 0.7, 0.3, 0.2, 0.1, 0.1, 0.4 },
            { 0.1, 0.5, 0.5, 0.7, 0.6, 0.8, 0.3, 0.2, 0.9, 0.1 },
            { 0.1, 0.5, 0.5, 0.7, 0.6, 0.8, 0.3, 0.2, 0.9, 0.1 },
            { 0.1, 0.5, 0.5, 0.7, 0.6, 0.8, 0.3, 0.2, 0.9, 0.1 },
            { 0.7, 0.9, 0.5, 0.3, 0.7, 0.3, 0.2, 0.1, 0.1, 0.4 },
            { 0.1, 0.5, 0.5, 0.7, 0.6, 0.8, 0.3, 0.2, 0.9, 0.1 },
            { 0.7, 0.9, 0.5, 0.3, 0.7, 0.3, 0.2, 0.1, 0.1, 0.4 },
            { 0.7, 0.9, 0.5, 0.3, 0.7, 0.3, 0.2, 0.1, 0.1, 0.4 },
            { 0.1, 0.5, 0.5, 0.7, 0.6, 0.8, 0.3, 0.2, 0.9, 0.1 },
        };

        double[][] values = new double[][] {
            { 1.2, 1.1, 1.1, 1.05, 1.075, 1.025, 1.15, 1.175, 1.0, 1.2 },
            { 1.05, 1.0, 1.1, 1.15, 1.05, 1.15, 1.175, 1.2, 1.2, 1.125 },
            { 1.2, 1.1, 1.1, 1.05, 1.075, 1.025, 1.15, 1.175, 1.0, 1.2 },
            { 1.2, 1.1, 1.1, 1.05, 1.075, 1.025, 1.15, 1.175, 1.0, 1.2 },
            { 1.2, 1.1, 1.1, 1.05, 1.075, 1.025, 1.15, 1.175, 1.0, 1.2 },
            { 1.05, 1.0, 1.1, 1.15, 1.05, 1.15, 1.175, 1.2, 1.2, 1.125 },
            { 1.2, 1.1, 1.1, 1.05, 1.075, 1.025, 1.15, 1.175, 1.0, 1.2 },
            { 1.05, 1.0, 1.1, 1.15, 1.05, 1.15, 1.175, 1.2, 1.2, 1.125 },
            { 1.05, 1.0, 1.1, 1.15, 1.05, 1.15, 1.175, 1.2, 1.2, 1.125 },
            { 1.2, 1.1, 1.1, 1.05, 1.075, 1.025, 1.15, 1.175, 1.0, 1.2 },
        };


        Method method = WhiteBox.getPrivateMethod(
            calculator,
            "rankAreaRiskCells",
            double[][].class,
            Cell[][].class,
            double.class,
            double.class
        );

        try {
            method.invoke(calculator, percentages, cells, min, max);
            for (int x = 0; x < 80; x++) {
                for (int y = 0; y < 80; y++) {
                    int sectionX = (int) Math.floor(((double) x) / division);
                    int sectionY = (int) Math.floor(((double) y) / division);

                    double value = (double) WhiteBox.getInternalState(cells[x][y], "areaRisk");
                    assertEquals(values[sectionX][sectionY], value);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
