package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.extern.log4j.Log4j2;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
@Log4j2
class KillAlgorithmTest {

    @Test
    void killAlgorithm() {

        Cell[][] cells = new Cell[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }
        cells[5][17].setId(2);
        cells[4][17].setId(2);
        cells[3][17].setId(2);
        cells[2][17].setId(2);
        cells[1][17].setId(2);
        cells[0][17].setId(2);

        cells[9][14].setId(1);
        cells[10][14].setId(1);
        cells[11][14].setId(1);
        cells[12][14].setId(1);
        cells[13][14].setId(1);
        cells[14][14].setId(1);
        cells[15][14].setId(1);
        cells[16][14].setId(1);
        cells[17][14].setId(1);
        cells[18][14].setId(1);
        cells[19][14].setId(1);

        StringBuilder str = new StringBuilder();
        DecimalFormat f = new DecimalFormat("##.00");
        str.append("\n");
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                str.append(f.format(cells[i][j].getRisks())).append("\t");
            }
            str.append("\n");
        }

        log.debug(str.toString());


        Player us = new Player(1, 9, 14, Direction.LEFT, 2, true, "1");
        Player op = new Player(2, 5, 17, Direction.RIGHT, 1, true, "2");

        Player[] players = new Player[] {op,us};
        Set<Cell> evaluatedCells = KillAlgorithm.killAlgorithm(cells, players, us);
        assertFalse(evaluatedCells.isEmpty());
        for (Cell cell : evaluatedCells) {
            System.out.println(cell.getX() + "  " + cell.getY());

            assertEquals(0.8, cells[cell.getX()][cell.getY()].getRisks());
        }
    }
}