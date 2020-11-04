package de.uol.snakeinc.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Comparator;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Coordinates implements Comparable<Coordinates> {

    private int x;
    private int y;
    private int turn;
    private int player;

    @Override
    public int compareTo(@NonNull Coordinates that) {
        int comp = Integer.compare(this.x, that.y);
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

}
