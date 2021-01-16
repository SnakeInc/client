package de.uol.snakeinc.entities;

import de.uol.snakeinc.tests.WhiteBox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class EvaluationBoardTest {

    @Test
    void update() {
        Player us = new Player(1, 0, 0, Direction.DOWN, 1,true, "player1");
        Player op1 = new Player(2, 2, 2, Direction.DOWN, 1,true, "player2");
        Player op2 = new Player(3, 4, 4, Direction.DOWN, 1,true, "player3");
        Player[] players = {us, op1, op2};
        EvaluationBoard evaluationBoard = new EvaluationBoard(10, 10, players, us, 1);

        Player usU = new Player(1, 0, 1, Direction.DOWN, 1,true, "player1");
        Player op1U = new Player(2, 2, 3, Direction.DOWN, 1,true, "player2");
        Player op2U = new Player(3, 4, 5, Direction.DOWN, 1,true, "player3");
        HashMap<Integer, Player> playerHashMap = new HashMap<Integer, Player>(Map.of(1, usU, 2, op1U, 3, op2U));

        evaluationBoard.update(playerHashMap, usU, 1);

        assertEquals(usU, evaluationBoard.getUs());
        assertEquals(1, evaluationBoard.getRound());
    }

    @Test
    void updatePlayersCells() {
        Player us = new Player(1, 0, 0, Direction.RIGHT, 1,true, "player1");
        Player op1 = new Player(2, 2, 2, Direction.UP, 1,true, "player2");
        Player op2 = new Player(3, 4, 4, Direction.DOWN, 1,true, "player3");
        Player op3 = new Player(4, 7, 7, Direction.LEFT, 1,true, "player4");
        Player[] players = {us, op1, op2, op3};
        EvaluationBoard evaluationBoard = new EvaluationBoard(10, 10, players, us, 1);

        Player usU = new Player(1, 1, 0, Direction.RIGHT, 1,true, "player1");
        Player op1U = new Player(2, 2, 1, Direction.UP, 1,true, "player2");
        Player op2U = new Player(3, 4, 8, Direction.DOWN, 4,true, "player3");
        Player op3U = new Player(4, 6, 7, Direction.LEFT, 1,true, "player4");
        HashMap<Integer, Player> playerHashMap =
            new HashMap<Integer, Player>(Map.of(1, usU, 2, op1U, 3, op2U, 4, op3U));

        assertEquals(1, evaluationBoard.getCells()[1][0].getRisks());
        assertEquals(1, evaluationBoard.getCells()[2][1].getRisks());
        assertEquals(10, evaluationBoard.getCells()[2][2].getRisks());
        assertEquals(10, evaluationBoard.getCells()[7][7].getRisks());

        evaluationBoard.updatePlayersCells(playerHashMap);
        assertEquals(10, evaluationBoard.getCells()[2][1].getRisks());
        assertEquals(1, evaluationBoard.getCells()[2][0].getRisks());
        assertEquals(10, evaluationBoard.getCells()[1][0].getRisks());
        assertEquals(1, evaluationBoard.getCells()[4][9].getRisks());
        assertEquals(10, evaluationBoard.getCells()[4][8].getRisks());
        assertEquals(10, evaluationBoard.getCells()[4][7].getRisks());
        assertEquals(10, evaluationBoard.getCells()[4][5].getRisks());
        assertEquals(10, evaluationBoard.getCells()[6][7].getRisks());

        Player usJ = new Player(1, 3, 0, Direction.RIGHT, 3,true, "player1");
        Player op1J = new Player(2, 2, -1, Direction.UP, 3,true, "player2");
        Player op2J = new Player(3, 4, 8, Direction.DOWN, 4,false, "player3");
        Player op3J = new Player(4, 4, 7, Direction.LEFT, 3,true, "player4");
        HashMap<Integer, Player> playerHashMap2 =
            new HashMap<Integer, Player>(Map.of(1, usJ, 2, op1J, 3, op2J, 4, op3J));

        EvaluationBoard evaluationBoard2 = new EvaluationBoard(10, 10, players, us, 6);
        assertEquals(1, evaluationBoard2.getCells()[1][0].getRisks());
        evaluationBoard2.updatePlayersCells(playerHashMap);
        assertEquals(10, evaluationBoard2.getCells()[1][0].getRisks());
        assertEquals(10, evaluationBoard2.getCells()[4][8].getRisks());
        assertEquals(1, evaluationBoard2.getCells()[4][7].getRisks());
        assertEquals(1, evaluationBoard2.getCells()[4][6].getRisks());
        assertEquals(10, evaluationBoard2.getCells()[4][5].getRisks());

        EvaluationBoard evaluationBoard3 = new EvaluationBoard(10, 10, players, us, 6);
        evaluationBoard3.updatePlayersCells(playerHashMap2);
        assertEquals(10, evaluationBoard3.getCells()[4][7].getRisks());
        assertEquals(1, evaluationBoard3.getCells()[5][7].getRisks());
        assertEquals(10, evaluationBoard3.getCells()[6][7].getRisks());
        assertEquals(10, evaluationBoard3.getCells()[1][0].getRisks());
        assertEquals(10, evaluationBoard3.getCells()[3][0].getRisks());
        assertEquals(1, evaluationBoard3.getCells()[2][0].getRisks());
        assertEquals(10, evaluationBoard3.getCells()[2][1].getRisks());
        assertEquals(1, evaluationBoard3.getCells()[2][0].getRisks());
    }

    @Test
    void prepareNextPhase() {
        Player us = new Player(1, 0, 0, Direction.DOWN, 1,true, "player1");
        Player op1 = new Player(2, 2, 2, Direction.DOWN, 1,true, "player2");
        Player op2 = new Player(3, 4, 4, Direction.DOWN, 1,true, "player3");
        Player[] players = {us, op1, op2};
        EvaluationBoard evaluationBoard = new EvaluationBoard(10, 10, players, us, 0);

        Player usU = new Player(1, 0, 1, Direction.DOWN, 1,true, "player1");
        Player op1U = new Player(2, 2, 3, Direction.DOWN, 1,true, "player2");
        Player op2U = new Player(3, 4, 5, Direction.DOWN, 1,true, "player3");
        HashMap<Integer, Player> playerHashMap = new HashMap<Integer, Player>(Map.of(1, usU, 2, op1U, 3, op2U));

        evaluationBoard.update(playerHashMap, usU, 1);
        assertFalse(evaluationBoard.getBoardAnalyzer().getEvaluatedCells().isEmpty());

        evaluationBoard.prepareNextPhase();
        assertTrue(evaluationBoard.getBoardAnalyzer().getEvaluatedCells().isEmpty());
    }

    @Test
    void getAction() {
        Player us = new Player(1, 0, 0, Direction.DOWN, 1,true, "player1");
        Player op1 = new Player(2, 2, 2, Direction.DOWN, 1,true, "player2");
        Player op2 = new Player(3, 4, 4, Direction.DOWN, 1,true, "player3");
        Player[] players = {us, op1, op2};
        EvaluationBoard evaluationBoard = new EvaluationBoard(10, 10, players, us, 0);

        Player usU = new Player(1, 0, 1, Direction.DOWN, 1,true, "player1");
        Player op1U = new Player(2, 2, 3, Direction.DOWN, 1,true, "player2");
        Player op2U = new Player(3, 4, 5, Direction.DOWN, 1,true, "player3");
        HashMap<Integer, Player> playerHashMap = new HashMap<Integer, Player>(Map.of(1, usU, 2, op1U, 3, op2U));

        evaluationBoard.update(playerHashMap, usU, 1);
        assertEquals(Action.class, evaluationBoard.getAction().getClass());
    }

    @Test
    public void testArrayVariableCellMapping() {
        EvaluationBoard board = new EvaluationBoard(50, 80, new Player[0], null, 1);
        Cell[][] cells = (Cell[][]) WhiteBox.getInternalState(board, "cells");
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                assertEquals(x, cells[x][y].getX());
                assertEquals(y, cells[x][y].getY());
            }
        }
    }
}