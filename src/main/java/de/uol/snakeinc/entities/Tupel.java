package de.uol.snakeinc.entities;

import lombok.Getter;

public class Tupel {

    @Getter
    private final int x;
    @Getter
    private final int y;

    public Tupel(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
