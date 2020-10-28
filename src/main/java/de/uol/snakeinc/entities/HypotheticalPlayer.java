package de.uol.snakeinc.entities;

import static de.uol.snakeinc.entities.Action.SLOW_DOWN;
import static de.uol.snakeinc.entities.Action.SPEED_UP;
import de.uol.snakeinc.util.Expandable;

public class HypotheticalPlayer extends Player implements Expandable {

    private Board baseBoard;
    private Action action;
    private CoordinateBag occupied;

    public HypotheticalPlayer(int id, int x, int y, Direction direction, int speed, boolean active, String name,
                              Board baseBoard, CoordinateBag occupied) {
        super(id, x, y, direction, speed, active, name);
        this.baseBoard = baseBoard;
        this.occupied = occupied;
    }

    @Override
    public Expandable[] expand() {

        for (Action action : Action.values()) {
            if ((action == SPEED_UP && this.getSpeed() == 10) || (action == SLOW_DOWN && this.getSpeed() == 10)) {
                continue;
            }

        }
    }
}
