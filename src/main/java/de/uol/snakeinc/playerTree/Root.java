package de.uol.snakeinc.playerTree;

import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Player;

public class Root extends PlayerTree {

    public Root(Player player, Board board) {
        super(0, board.getTurn(), board, player.getId());
        this.setOccupied(board.getCoordinateBag());
        this.setX(player.getX());
        this.setY(player.getY());
        this.setSpeed(player.getSpeed());
        this.setDirection(player.getDirection());
        this.setActive(player.isActive());
    }
}
