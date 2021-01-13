package de.uol.snakeinc.entities;

import static org.junit.jupiter.api.Assertions.*;
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
        Player us = new Player(1, 0, 0, Direction.DOWN, 1,true, "player1");
        Player op1 = new Player(2, 2, 2, Direction.DOWN, 1,true, "player2");
        Player op2 = new Player(3, 4, 4, Direction.DOWN, 1,true, "player3");
        Player[] players = {us, op1, op2};
        EvaluationBoard evaluationBoard = new EvaluationBoard(10, 10, players, us, 1);

        Player usU = new Player(1, 0, 1, Direction.DOWN, 1,true, "player1");
        Player op1U = new Player(2, 2, 3, Direction.DOWN, 1,true, "player2");
        Player op2U = new Player(3, 4, 8, Direction.DOWN, 4,true, "player3");
        HashMap<Integer, Player> playerHashMap = new HashMap<Integer, Player>(Map.of(1, usU, 2, op1U, 3, op2U));

        assertEquals(1, evaluationBoard.getCells()[0][1].getRisks());
        evaluationBoard.updatePlayersCells(playerHashMap);
        assertEquals(10, evaluationBoard.getCells()[0][1].getRisks());
        assertEquals(1, evaluationBoard.getCells()[4][9].getRisks());
        assertEquals(10, evaluationBoard.getCells()[4][8].getRisks());
        assertEquals(10, evaluationBoard.getCells()[4][7].getRisks());
        assertEquals(10, evaluationBoard.getCells()[4][5].getRisks());


        EvaluationBoard evaluationBoard2 = new EvaluationBoard(10, 10, players, us, 6);
        assertEquals(1, evaluationBoard2.getCells()[0][1].getRisks());
        evaluationBoard2.updatePlayersCells(playerHashMap);
        assertEquals(10, evaluationBoard2.getCells()[0][1].getRisks());
        assertEquals(10, evaluationBoard2.getCells()[4][8].getRisks());
        assertEquals(1, evaluationBoard2.getCells()[4][7].getRisks());
        assertEquals(1, evaluationBoard2.getCells()[4][6].getRisks());
        assertEquals(10, evaluationBoard2.getCells()[4][5].getRisks());


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
    void startingStrategy() {
    }
}