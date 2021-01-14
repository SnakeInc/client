
        boardAnalyzer.analyze(cells, players, us);
        assertEquals(2, boardAnalyzer.getRound());
    }

    @org.junit.jupiter.api.Test
    void inDistance() {
        Player us = new Player(1, 0, 0, Direction.DOWN, 1, true, "player1");
        Player op = new Player(2, 1, 2, Direction.UP, 1, true, "player2");

        assertTrue(BoardAnalyzer.inDistance(us, op, 3));

        Player us1 = new Player(1, 0, 0, Direction.DOWN, 1, true, "player1");
        Player op1 = new Player(2, 2, 2, Direction.UP, 1, true, "player2");
        assertFalse(BoardAnalyzer.inDistance(us1, op1, 3));

        Player us2 = new Player(1, 0, 0, Direction.DOWN, 1, true, "player1");
        Player op2 = new Player(2, 0, 3, Direction.UP, 1, true, "player2");
        assertTrue(BoardAnalyzer.inDistance(us2, op2, 3));

        Player us3 = new Player(1, 0, 0, Direction.DOWN, 1, true, "player1");
        Player op3 = new Player(2, 0, 1, Direction.UP, 1, true, "player2");
        assertTrue(BoardAnalyzer.inDistance(us2, op2, 5));
    }

    @org.junit.jupiter.api.Test
    void checkForJumping() {
        BoardAnalyzer boardAnalyzer = new BoardAnalyzer(10, 10);
        Player us = new Player(1, 0, 0, Direction.DOWN, 1, true, "player1");
        Player[] players = {new Player(3, 5, 5, Direction.DOWN, 1, true, "player3"),
            new Player(2, 5, 5, Direction.DOWN, 1, true, "player2")};
        Cell[][] cells = new Cell[10][10];

        boardAnalyzer.analyze(cells, players, us);
        assertTrue(boardAnalyzer.checkForJumping(5));
        boardAnalyzer.analyze(cells, players, us);
        boardAnalyzer.analyze(cells, players, us);
        boardAnalyzer.analyze(cells, players, us);
        boardAnalyzer.analyze(cells, players, us);
        boardAnalyzer.analyze(cells, players, us);
        assertTrue(boardAnalyzer.checkForJumping(0));
        assertFalse(boardAnalyzer.checkForJumping(1));
        assertTrue(boardAnalyzer.checkForJumping(6));
    }

    @org.junit.jupiter.api.Test
    void prepareNextPhase() {
        BoardAnalyzer boardAnalyzer = new BoardAnalyzer(10, 10);
        Cell[][] cells = new Cell[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        Player us = new Player(1, 0, 0, Direction.DOWN, 1, true, "player1");
        Player[] players = {new Player(3, 5, 5, Direction.DOWN, 1, true, "player3"),
            new Player(2, 5, 5, Direction.DOWN, 1, true, "player2")};

        assertTrue(boardAnalyzer.getEvaluatedCells().isEmpty());
        boardAnalyzer.analyze(cells, players, us);
        assertFalse(boardAnalyzer.getEvaluatedCells().isEmpty());
        boardAnalyzer.prepareNextPhase();

        assertTrue(boardAnalyzer.getEvaluatedCells().isEmpty());
    }
}