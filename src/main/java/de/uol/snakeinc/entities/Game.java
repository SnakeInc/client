package de.uol.snakeinc.entities;

import de.uol.snakeinc.connection.SpeedWebSocketClient;

import java.util.HashMap;

public class Game {

    private HashMap<Integer, Player> players;
    private Board currentBoard;
    private Player us;

    public Game() {
        this.players = new HashMap<Integer, Player>();
    }

    public HashMap<Integer, Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getId(), player);
    }

    public void setUs(int id) {
        this.us = this.players.get(id);
    }

    public void setCurrentBoard(Board board) {
        this.currentBoard = board;
    }

    public Board getCurrentBoard() {
        return this.currentBoard;
    }

    /**
     * Run action after messages are parsed to calculate next step.
     * @param socket currently connected socket
     */
    public void runAction(SpeedWebSocketClient socket) {
        // Implement logical working here
        socket.sendAction(Action.SPEED_UP);
    }
}
