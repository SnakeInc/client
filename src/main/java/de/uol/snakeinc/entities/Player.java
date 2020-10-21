package de.uol.snakeinc.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2 @EqualsAndHashCode
public class Player {

    @Getter @Setter
    private int id;
    @Getter @Setter
    private int x;
    @Getter @Setter
    private int y;
    @Getter @Setter
    private Direction direction;
    @Getter @Setter
    private int speed;
    @Getter @Setter
    private boolean active;
    @Getter @Setter @EqualsAndHashCode.Exclude
    private String name;


    static Player get() {
        return new Player();
    }

    private Player()
    { }


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
