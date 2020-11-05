package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

import java.util.stream.Stream;

@CustomLog
public class PossibleMovesCalculator {

    Board base;

    public PossibleMovesCalculator(Board base) {
        this.base = base;
    }

    public Stream<Board> calculateBoards(int depth) {
        return calculateBoards(base).parallel().flatMap(this::calculateBoards).flatMap(this::calculateBoards);
    }

    private Stream<Board> calculateBoards(Board from) {

        var combinations = new CombinationTree();
        var players = from.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players.containsKey(i)) {
                combinations.add(players.get(i).getPossibleMoves(from));
            }
        }
        return combinations.getCombinations().stream()
            .map(iterator -> new Board(from, iterator, combinations.getDepth()));
    }

    public static void main(String[] Args) {
        var players = new Player[] {null,
            new Player(1, 10, 10, Direction.UP, 5, true, "1"),
            new Player(2, 10, 30, Direction.UP, 5, true, "2"),
            new Player(3, 30, 10, Direction.UP, 5, true, "3"),
            new Player(4, 30, 30, Direction.UP, 5, true, "4"),
        };

        var board = new Board(40, 40, players, 1);
        var calc = new PossibleMovesCalculator(board);

        int i = 0;
        var start = System.currentTimeMillis();
        var elems = calc.calculateBoards(2).count();
        var end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(elems);
    }
}
