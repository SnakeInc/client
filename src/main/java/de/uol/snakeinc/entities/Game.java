package de.uol.snakeinc.entities;

import de.uol.snakeinc.connection.SpeedWebSocketClient;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

public class Game {

    private HashMap<Integer, Player> players;
    private HashMap<Integer, Board> boards;
    private Board currentBoard;
    private Player us;

    private int round = 0;

    private String gameId;

    public Game(String serverId) {
        this.gameId = serverId + "_" + generateGameId(System.currentTimeMillis());
        this.players = new HashMap<Integer, Player>();
        this.boards = new HashMap<Integer, Board>();
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
        if (this.currentBoard != null) {
            this.boards.put(round, this.currentBoard);
        }
        this.currentBoard = board;
        this.round++;
    }

    public Board getCurrentBoard() {
        return this.currentBoard;
    }

    public HashMap<Integer, Board> getBoards() {
        return this.boards;
    }

    /**
     * Run action after messages are parsed to calculate next step.
     * @param socket currently connected socket
     */
    public void runAction(SpeedWebSocketClient socket) {
        // Implement logical working here
        socket.sendAction(Action.SPEED_UP);
    }

    public void makeExportReady() {
        this.boards.put(round, this.currentBoard);
    }

    public Player getUs() {
        return this.us;
    }

    public int getRounds() {
        return this.round + 1;
    }

    public String getGameId() {
        return this.gameId;
    }

    private String generateGameId(long time) {
        return DigestUtils.md5Hex("" + time).substring(0, 8);
    }
}
