package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.extern.log4j.Log4j2;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

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

        Player us = new Player(1, 9, 14, Direction.LEFT, 2, true, "1");
        Player op = new Player(2, 5, 17, Direction.RIGHT, 1, true, "2");

        Player[] players = new Player[] {op, us};
        Set<Cell> evaluatedCells = KillAlgorithm.killAlgorithm(cells, players, us);

        assertEquals(9, evaluatedCells.size());
        assertEquals(10, cells[3][17].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[3][16].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[3][15].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[4][15].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[5][15].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[6][15].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[7][15].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[7][16].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells[7][17].getRisks());

        Cell[][] cells2 = new Cell[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                cells2[x][y] = new Cell(x, y);
            }
        }
        cells2[5][17].setId(2);
        cells2[4][17].setId(2);
        cells2[3][17].setId(2);
        cells2[2][17].setId(2);
        cells2[1][17].setId(2);
        cells2[0][17].setId(2);

        cells2[9][14].setId(1);
        cells2[10][14].setId(1);
        cells2[11][14].setId(1);
        cells2[12][14].setId(1);
        cells2[13][14].setId(1);
        cells2[14][14].setId(1);
        cells2[15][14].setId(1);
        cells2[16][14].setId(1);
        cells2[17][14].setId(1);
        cells2[18][14].setId(1);
        cells2[19][14].setId(1);

        //new case
        Player op2 = new Player(1, 9, 14, Direction.LEFT, 2, true, "1");
        Player us2 = new Player(2, 5, 17, Direction.RIGHT, 1, true, "2");

        Player[] players2 = new Player[] {op2, us2};
        Set<Cell> evaluatedCells2 = KillAlgorithm.killAlgorithm(cells2, players2, us2);

        assertEquals(17, evaluatedCells2.size());
        assertEquals(10, cells2[13][14].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[13][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[13][12].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[13][11].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[13][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[12][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[11][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[10][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[9][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[8][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[7][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[6][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[5][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[5][11].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[5][12].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[5][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells2[5][14].getRisks());

        //new case

        Cell[][] cells3 = new Cell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                cells3[x][y] = new Cell(x, y);
            }
        }
        cells3[3][8].setId(2);
        cells3[2][8].setId(2);
        cells3[1][8].setId(2);
        cells3[0][8].setId(2);

        cells3[6][5].setId(1);
        cells3[7][5].setId(1);
        cells3[8][5].setId(1);
        cells3[9][5].setId(1);

        Player op3 = new Player(1, 6, 5, Direction.LEFT, 1, true, "1");
        Player us3 = new Player(2, 3, 8, Direction.RIGHT, 1, true, "2");

        Player[] players3 = new Player[] {op3, us3};
        Set<Cell> evaluatedCells3 = KillAlgorithm.killAlgorithm(cells3, players3, us3);

        assertEquals(9, evaluatedCells3.size());
        assertEquals(10, cells3[8][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[8][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[8][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[7][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[6][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[5][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[4][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[4][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells3[4][5].getRisks());

        //new case

        Cell[][] cells4 = new Cell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                cells4[x][y] = new Cell(x, y);
            }
        }
        cells4[3][3].setId(2);
        cells4[2][3].setId(2);
        cells4[1][3].setId(2);
        cells4[0][3].setId(2);

        cells4[6][5].setId(1);
        cells4[7][5].setId(1);
        cells4[8][5].setId(1);
        cells4[9][5].setId(1);

        Player op4 = new Player(1, 6, 5, Direction.LEFT, 1, true, "1");
        Player us4 = new Player(2, 3, 3, Direction.RIGHT, 1, true, "2");

        Player[] players4 = new Player[] {op4, us4};
        Set<Cell> evaluatedCells4 = KillAlgorithm.killAlgorithm(cells4, players4, us4);

        assertEquals(9, evaluatedCells4.size());
        assertEquals(10, cells4[8][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[8][6].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[8][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[7][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[6][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[5][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[4][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[4][6].getRisks());
        assertEquals(Config.KILL_INCENTIVE, cells4[4][5].getRisks());

        // Swap all cases by changeing x=y and y=x

        Cell[][] swapCells = new Cell[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                swapCells[x][y] = new Cell(x, y);
            }
        }
        swapCells[17][5].setId(2);
        swapCells[17][4].setId(2);
        swapCells[17][3].setId(2);
        swapCells[17][2].setId(2);
        swapCells[17][1].setId(2);
        swapCells[17][0].setId(2);

        swapCells[14][9].setId(1);
        swapCells[14][10].setId(1);
        swapCells[14][11].setId(1);
        swapCells[14][12].setId(1);
        swapCells[14][13].setId(1);
        swapCells[14][14].setId(1);
        swapCells[14][15].setId(1);
        swapCells[14][16].setId(1);
        swapCells[14][17].setId(1);
        swapCells[14][18].setId(1);
        swapCells[14][19].setId(1);

        Player swapUs = new Player(1, 14, 9, Direction.UP, 2, true, "1");
        Player swapOp = new Player(2, 17, 5, Direction.DOWN, 1, true, "2");

        Player[] swapPlayers = new Player[] {swapOp, swapUs};
        Set<Cell> evaluatedSwapCells = KillAlgorithm.killAlgorithm(swapCells, swapPlayers, swapUs);

        assertEquals(9, evaluatedSwapCells.size());
        assertEquals(10, swapCells[17][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[16][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[15][3].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[15][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[15][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[15][6].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[15][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[16][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells[17][7].getRisks());

        //new case

        Cell[][] swapCells2 = new Cell[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                swapCells2[x][y] = new Cell(x, y);
            }
        }
        swapCells2[17][5].setId(2);
        swapCells2[17][4].setId(2);
        swapCells2[17][3].setId(2);
        swapCells2[17][2].setId(2);
        swapCells2[17][1].setId(2);
        swapCells2[17][0].setId(2);

        swapCells2[14][9].setId(1);
        swapCells2[14][10].setId(1);
        swapCells2[14][11].setId(1);
        swapCells2[14][12].setId(1);
        swapCells2[14][13].setId(1);
        swapCells2[14][14].setId(1);
        swapCells2[14][15].setId(1);
        swapCells2[14][16].setId(1);
        swapCells2[14][17].setId(1);
        swapCells2[14][18].setId(1);
        swapCells2[14][19].setId(1);

        Player swapOp2 = new Player(1, 14, 9, Direction.UP, 2, true, "1");
        Player swapUs2 = new Player(2, 17, 5, Direction.DOWN, 1, true, "2");

        Player[] swapPlayers2 = new Player[] {swapOp2, swapUs2};
        Set<Cell> evaluatedSwapCells2 = KillAlgorithm.killAlgorithm(swapCells2, swapPlayers2, swapUs2);

        assertEquals(17, evaluatedSwapCells2.size());
        assertEquals(10, swapCells2[14][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[13][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[12][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[11][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][13].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][12].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][11].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][10].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][9].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][6].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[10][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[11][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[12][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[13][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells2[14][5].getRisks());

        //new case

        Cell[][] swapCells3 = new Cell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                swapCells3[x][y] = new Cell(x, y);
            }
        }
        swapCells3[8][3].setId(2);
        swapCells3[8][2].setId(2);
        swapCells3[8][1].setId(2);
        swapCells3[8][0].setId(2);

        swapCells3[5][6].setId(1);
        swapCells3[5][7].setId(1);
        swapCells3[5][8].setId(1);
        swapCells3[5][9].setId(1);

        Player swapOp3 = new Player(1, 5, 6, Direction.UP, 1, true, "1");
        Player swapUs3 = new Player(2, 8, 3, Direction.DOWN, 1, true, "2");

        Player[] swapPlayers3 = new Player[] {swapOp3, swapUs3};
        Set<Cell> evaluatedSwapCells3 = KillAlgorithm.killAlgorithm(swapCells3, swapPlayers3, swapUs3);

        assertEquals(9, evaluatedSwapCells3.size());
        assertEquals(10, swapCells3[5][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[4][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[3][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[3][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[3][6].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[3][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[3][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[4][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells3[5][4].getRisks());

        //new case

        Cell[][] swapCells4 = new Cell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                swapCells4[x][y] = new Cell(x, y);
            }
        }
        swapCells4[3][3].setId(2);
        swapCells4[3][2].setId(2);
        swapCells4[3][1].setId(2);
        swapCells4[3][0].setId(2);

        swapCells4[5][6].setId(1);
        swapCells4[5][7].setId(1);
        swapCells4[5][8].setId(1);
        swapCells4[5][9].setId(1);

        Player swapOp4 = new Player(1, 5, 6, Direction.UP, 1, true, "1");
        Player swapUs4 = new Player(2, 3, 3, Direction.DOWN, 1, true, "2");

        Player[] swapPlayers4 = new Player[] {swapOp4, swapUs4};
        Set<Cell> evaluatedSwapCells4 = KillAlgorithm.killAlgorithm(swapCells4, swapPlayers4, swapUs4);

        assertEquals(9, evaluatedSwapCells4.size());
        assertEquals(10, swapCells4[5][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[6][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[7][8].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[7][7].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[7][6].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[7][5].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[7][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[6][4].getRisks());
        assertEquals(Config.KILL_INCENTIVE, swapCells4[5][4].getRisks());
    }

}