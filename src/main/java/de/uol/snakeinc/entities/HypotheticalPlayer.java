package de.uol.snakeinc.entities;

import de.uol.snakeinc.util.Expandable;
import de.uol.snakeinc.util.MaximinTree;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class HypotheticalPlayer extends Player implements Expandable {

    private Board baseBoard;
    private Action action;
    private CoordinateBag occupied;
    private int turn;

    public HypotheticalPlayer(int id, int x, int y, Direction direction, int speed, boolean active, String name,
                              Board baseBoard, CoordinateBag occupied, Action action, int turn) {
        super(id, x, y, direction, speed, active, name);
        this.baseBoard = baseBoard;
        this.occupied = occupied;
        this.action = action;
        this.turn = turn;
    }

    public HypotheticalPlayer(Player player, Board board) {
        super(player.getId(), player.getX(), player.getY(),
            player.getDirection(), player.getSpeed(), player.isActive(), player.getName());
        this.baseBoard = board;
        this.turn = board.getTurn();
        this.action = null;
        this.occupied = board.getCoordinateBag();
    }

    @Override
    public Expandable[] expand() {
        var res = new ArrayList<HypotheticalPlayer>(5);
        for (Action action : Action.values()) {
            int speed = 0;
            Direction direction = Direction.UP;
            switch (action) {
                case CHANGE_NOTHING:
                    speed = this.getSpeed();
                    direction = this.getDirection();
                    break;
                case TURN_LEFT:
                    speed = this.getSpeed();
                    direction = this.getDirection().turn(Action.TURN_LEFT);
                    break;
                case TURN_RIGHT:
                    speed = this.getSpeed();
                    direction = this.getDirection().turn(Action.TURN_RIGHT);
                    break;
                case SLOW_DOWN:
                    if (this.getSpeed() == 1) {
                        continue;
                    }
                    speed = this.getSpeed() - 1;
                    direction = this.getDirection();
                    break;
                case SPEED_UP:
                    if (this.getSpeed() == 10) {
                        continue;
                    }
                    speed = this.getSpeed() + 1;
                    direction = this.getDirection();
                    break;
            }
            var nextPos = nextPosition(getX(), getY(), speed, direction);
            if (baseBoard.isOnBoard(nextPos)) {
                var coordinateBag = new CoordinateBag(this.getX(), this.getY(), speed, direction,
                    this.turn + 1, this.getId(), this.occupied);
                if (coordinateBag.noneOverlapping()) {
                    res.add(new HypotheticalPlayer(this.getId(), nextPos._1, nextPos._2, direction, speed, true,
                        this.getName(), this.baseBoard, coordinateBag, action, this.turn));
                }
            }
        }
        return (Expandable[]) res.toArray();
    }

    private Tuple2<Integer, Integer> nextPosition(int x, int y, int speed, Direction direction) {
        switch (direction) {
            case DOWN:
                return Tuple.of(x, y - speed);
            case UP:
                return Tuple.of(x, y + speed);
            case LEFT:
                return Tuple.of(x - speed, y);
            case RIGHT:
                return Tuple.of(x + speed, y);
        }
        return Tuple.of(x, y); // this cant happen;
    }

    public List<MaximinTree<HypotheticalPlayer>> createHypotheticalPlayers(Board board, int depth) {
        var res = new ArrayList<MaximinTree<HypotheticalPlayer>>(board.getPlayers().length - 1);
        for (Player player : board.getPlayers()) {
            if (!player.isActive()) {
                continue;
            }
            res.add(new MaximinTree<>(new HypotheticalPlayer(player, board), depth));
        }
        return res;
    }

}
