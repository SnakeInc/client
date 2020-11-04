package de.uol.snakeinc.playerTree;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.CoordinateBag;

public class ActiveSubTree extends SubTree {

    ActiveSubTree(PlayerTree player, Action action) {
        super(player);
        switch (action) {
            case CHANGE_NOTHING:
                setSpeed(player.getSpeed());
                setDirection(player.getDirection());
                break;
            case TURN_RIGHT:
            case TURN_LEFT:
                setSpeed(player.getSpeed());
                setDirection(player.getDirection().turn(action));
                break;
            case SPEED_UP:
                if (player.getSpeed() == 10) {
                    this.setActive(false);
                    this.setOccupied(player.getOccupied());
                    this.setX(player.getX());
                    this.setY(player.getY());
                    this.setSpeed(10);
                    this.setDirection(player.getDirection());
                    return;
                } else {
                    this.setDirection(player.getDirection());
                    this.setSpeed(player.getSpeed() + 1);
                }
                break;
            case SLOW_DOWN:
                if (player.getSpeed() == 1) {
                    this.setActive(false);
                    this.setOccupied(player.getOccupied());
                    this.setX(player.getX());
                    this.setY(player.getY());
                    this.setSpeed(1);
                    this.setDirection(player.getDirection());
                    return;
                } else {
                    this.setDirection(player.getDirection());
                    this.setSpeed(player.getSpeed() - 1);
                }
                break;

        }
        setOccupied(new CoordinateBag(player.getX(), player.getY(), this.getSpeed(), this.getDirection(),
            this.getTurn(), this.getId(), player.getOccupied()));
        switch (getDirection()) {
            case UP:
                this.setX(player.getX());
                this.setY(player.getY() + getSpeed());
                break;
            case DOWN:
                this.setX(player.getX());
                this.setY(player.getY() - getSpeed());
                break;
            case RIGHT:
                this.setX(player.getX() + player.getSpeed());
                this.setY(player.getY());
                break;
            case LEFT:
                this.setX(player.getX() - player.getSpeed());
                this.setY(player.getY());
                break;
        }
        this.setActive(this.getOccupied().noneOverlapping() && isOnBoard());
    }
}
