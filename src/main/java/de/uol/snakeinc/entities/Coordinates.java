package de.uol.snakeinc.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Coordinates implements Comparable<Coordinates> {

    private final int x;
    private final int y;
    private final int turn;
    private final int player;
    @EqualsAndHashCode.Exclude
    private Tuple tuple = new Tuple();

    public Coordinates cross() {
        return new Coordinates(this.x, this.y, this.turn, -1);
    }

    @Override
    public int compareTo(@NonNull Coordinates that) {
        int comp = Integer.compare(this.x, that.x);
        if (comp != 0) {
            return comp;
        }
        comp = Integer.compare(this.y, that.y);
        if (comp != 0) {
            return comp;
        }
        comp = Integer.compare(this.turn, that.turn);
        if (comp != 0) {
            return comp;
        }
        return Integer.compare(this.player, that.player);
    }

    public boolean localEquals(Coordinates that) {
        return this.x == that.x && this.y == that.y;
    }

    public boolean playerEquals(Coordinates that) {
        return this.player == that.player;
    }

    public boolean turnEquals(Coordinates that) {
        return this.turn == that.turn;
    }

    public boolean samePlayerLater(int player, int turn) {
        return this.player == player && this.turn < turn;
    }

    public static Comparator<Coordinates> getTurnComparator() {
        return Comparator
            .comparingInt(Coordinates::getTurn)
            .thenComparingInt(Coordinates::getPlayer)
            .thenComparingInt(Coordinates::getX)
            .thenComparingInt(Coordinates::getY);
    }

    public Tuple getTuple() {
        return tuple;
    }

    @EqualsAndHashCode

    public class Tuple implements Comparable<Tuple> {

        private Tuple() {

        }

        @EqualsAndHashCode.Include
        int getX() {
            return x;
        }

        @EqualsAndHashCode.Include
        int getY() {
            return y;
        }

        int getPlayer() {
            return player;
        }

        int getTurn() {
            return turn;
        }

        Coordinates getCoordinates() {
            return Coordinates.this;
        }

        @Override
        public int compareTo(@NonNull Tuple that) {
            int comp = Integer.compare(x, that.getY());
            if (comp != 0) {
                return comp;
            }
            comp = Integer.compare(y, that.getY());
            return comp;
        }

        public boolean equals(int x, int y) {
            return this.getX() == x && this.getY() == y;
        }
    }

}
