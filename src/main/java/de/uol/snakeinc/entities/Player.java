package de.uol.snakeinc.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.Map;
import java.util.stream.Stream;

@Log4j2
public class Player {

    @Getter @Setter(AccessLevel.PRIVATE)
    private int id;
    @Getter @Setter(AccessLevel.PRIVATE)
    private int x;
    @Getter @Setter(AccessLevel.PRIVATE)
    private int y;
    @Getter @Setter(AccessLevel.PRIVATE)
    private Direction direction;
    @Getter @Setter(AccessLevel.PRIVATE)
    private int speed;
    @Getter @Setter
    private boolean active;
    @Getter @Setter(AccessLevel.PRIVATE)
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

    public EntryStream<Player, PlayerMove> getPotentialMovedPlayers(Board board) {
        return StreamEx.of(PlayerMove.CHANGE_NOTHING, PlayerMove.SLOW_DOWN, PlayerMove.SPEED_UP,
            PlayerMove.TURN_LEFT, PlayerMove.TURN_RIGHT)
            .mapToEntry(this::actOnMove)
            .nonNullValues()
            .filterValues(board::tryMove)
            .invert().ifEmpty(
                (Stream<? extends Map.Entry<Player, PlayerMove>>)
                    StreamEx.of(PlayerMove.CHANGE_NOTHING, PlayerMove.SLOW_DOWN, PlayerMove.SPEED_UP,
                        PlayerMove.TURN_LEFT, PlayerMove.TURN_RIGHT)
                        .mapToEntry(this::actOnMove)
                        .nonNullValues()
            );
    }

    public Player actOnMove(PlayerMove move) {
        Player res = null;
        switch (move) {
            case CHANGE_NOTHING:
                res = get();
                res.setDirection(this.getDirection());
                res.setSpeed(this.getSpeed());
                break;
            case SPEED_UP:
                if(this.getSpeed() > 9) {
                    return null;
                }
                res = get();
                res.setSpeed(this.getSpeed() + 1);
                res.setDirection(this.getDirection());
                break;
            case SLOW_DOWN:
                if(this.getSpeed() < 2) {
                    return null;
                }
                res = get();
                res.setDirection(this.getDirection());
                res.setSpeed(this.getSpeed() - 1);
                break;
            case TURN_LEFT:
            case TURN_RIGHT:
                res = get();
                res.setDirection(this.getDirection().change(move));
                res.setSpeed(this.getSpeed());
                break;
        }
        res.setId(this.getId());
        res.setX(this.getX());
        res.setY(this.getY());
        res.setActive(true);
        res.setName(this.getName());
        return res;
    }
}
