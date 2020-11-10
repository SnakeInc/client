package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Board;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class Stats {

    private int overallCounted = 0;
    private int ourDeaths = 0;
    private int otherDeaths = 0;
    private int noDeaths = 0;

    //////////// for profiling purposes only

    private int iterations = 0;
    private int nullIterations = 0;

    ///////////////////////////////////

    public void addUnary(int overallCounted, int ourDeaths, int otherDeaths, int noDeaths) {
        this.overallCounted += overallCounted;
        this.ourDeaths += ourDeaths;
        this.otherDeaths += otherDeaths;
        this.noDeaths += noDeaths;
    }

    public static Stats add(Stats a, Stats b) {
        return new Stats(a.overallCounted + b.overallCounted,
            a.ourDeaths + b.ourDeaths,
            a.otherDeaths + b.otherDeaths,
            a.noDeaths + b.noDeaths,
            a.iterations + b.iterations,
            a.nullIterations + b.nullIterations);
    }

    public Stats(Board board) {
        this.overallCounted = 1;
        for (int i = 1; i < board.getPlayers().length; i++) {
            if (board.getPlayers()[i] == null) {
                if (i == board.getUs()) {
                    ourDeaths++;
                } else {
                    otherDeaths++;
                }
            }
        }
        if (otherDeaths == 0 && ourDeaths == 0) {
            noDeaths = 1;
        }
    }
}
