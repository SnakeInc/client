package de.uol.snakeinc.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Coordinates implements Comparable<Coordinates> {

    private final int x;
    private final int y;
    private final int player;

    public Coordinates cross() {
        return new Coordinates(this.x, this.y, -1);
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
        return Integer.compare(this.player, that.player);
    }
}
