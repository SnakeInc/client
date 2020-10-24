package de.uol.snakeinc.entities;

import lombok.CustomLog;
import lombok.Getter;

@CustomLog
public class Player {

    @Getter
    private int id;
    @Getter
    private int x;
    @Getter
    private int y;
    @Getter
    private Direction direction;
    @Getter
    private int speed;
    @Getter
    private boolean active;
    @Getter
    private String name;

    public Player(int id, int x, int y, Direction direction, int speed, boolean active, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.active = active;
        this.name = name;
    }
}
