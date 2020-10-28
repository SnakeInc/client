package de.uol.snakeinc.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinates implements Comparable<Coordinates> {

    private int x;
    private int y;
    private int turn;
    private int player;

    @Override
    public int compareTo(Coordinates that) {
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
}
