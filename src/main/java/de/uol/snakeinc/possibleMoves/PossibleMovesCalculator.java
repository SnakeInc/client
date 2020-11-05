package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Board;

import java.util.stream.Stream;

public class PossibleMovesCalculator {

    Board base;

    public PossibleMovesCalculator(Board base) {
        this.base = base;
    }

    public Stream<Board> calculateBoards(int depth) {
        var stream = calculateBoards(base);
        for (; depth > 0; depth--) {
            stream = stream.flatMap(this::calculateBoards);
        }
        return stream;
    }

    private Stream<Board> calculateBoards(Board from) {

        var combinations = new CombinationTree();
        for (var player : from.getPlayers().values()) {
            combinations.add(player.getPossibleMoves(from));
        }
        return combinations.getCombinations().stream()
            .map(iterator -> new Board(from, iterator, combinations.getDepth()));
    }
}
