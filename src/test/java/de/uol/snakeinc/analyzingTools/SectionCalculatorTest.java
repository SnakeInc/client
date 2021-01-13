package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.tests.WhiteBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SectionCalculatorTest {

    @Test
    public void testRankAreaRiskCellls() {
        SectionCalculator calculator = new SectionCalculator(20, 20);

        Cell[][] cells = new Cell[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        double min = 10.0;
        double max = 90.0;

        double[][] percentages = new double[][] {
            { 10.0, 50.0, 50.0, 70.0, 60.0, 80.0, 30.0, 20.0, 90.0, 10.0 },
            { 70.0, 90.0, 50.0, 30.0, 70.0, 30.0, 20.0, 10.0, 10.0, 40.0 },
            { 10.0, 50.0, 50.0, 70.0, 60.0, 80.0, 30.0, 20.0, 90.0, 10.0 },
            { 10.0, 50.0, 50.0, 70.0, 60.0, 80.0, 30.0, 20.0, 90.0, 10.0 },
            { 10.0, 50.0, 50.0, 70.0, 60.0, 80.0, 30.0, 20.0, 90.0, 10.0 },
            { 70.0, 90.0, 50.0, 30.0, 70.0, 30.0, 20.0, 10.0, 10.0, 40.0 },
            { 10.0, 50.0, 50.0, 70.0, 60.0, 80.0, 30.0, 20.0, 90.0, 10.0 },
            { 70.0, 90.0, 50.0, 30.0, 70.0, 30.0, 20.0, 10.0, 10.0, 40.0 },
            { 70.0, 90.0, 50.0, 30.0, 70.0, 30.0, 20.0, 10.0, 10.0, 40.0 },
            { 10.0, 50.0, 50.0, 70.0, 60.0, 80.0, 30.0, 20.0, 90.0, 10.0 },
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
            for (int x = 0; x < 20; x++) {
                for (int y = 0; y < 20; y++) {
                    int sectionX = (int) Math.floor(x / 2);
                    int sectionY = (int) Math.floor(y / 2);

                    double value = (double) WhiteBox.getInternalState(cells[x][y], "areaRisk");
                    assertEquals(value, values[sectionY][sectionX]);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
