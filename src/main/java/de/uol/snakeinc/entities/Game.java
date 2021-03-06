package de.uol.snakeinc.entities;

import de.uol.snakeinc.connection.SpeedWebSocketClient;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

public class Game {

    private HashMap<Integer, Player> players;
    @Getter
    private HashMap<Integer, int[][]> boards;
    private EvaluationBoard EvaluationBoard;
    @Getter
    private Player us;

    @Getter
    private int round = 0;

    @Getter
    private String gameId;

    public Game(String serverId) {
        this.gameId = serverId + "_" + generateGameId(System.currentTimeMillis());
        this.players = new HashMap<Integer, Player>();
        this.boards = new HashMap<Integer, int[][]>();
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

    /**
     * Initializes the new Evaluation Board in the game.
     * @param evaluationBoard the new evaluationBoard
     * @param rawBoard        the raw board for logging
     */
    public void informIntelligentBoard(EvaluationBoard evaluationBoard, int[][] rawBoard) {
        EvaluationBoard = evaluationBoard;
        if (evaluationBoard != null) {
            this.boards.put(round, rawBoard);
        }
    }

    /**
     * Updates the EvaluationBoard with the new Player positions etc.
     * @param rawBoard the raw board for logging
     */
    public void informIntelligentBoard(int[][] rawBoard) {
        EvaluationBoard.update(players, us, round);
        boards.put(round, rawBoard);
    }

    /**
     * Run action after messages are parsed to calculate next step.
     * @param socket currently connected socket
     */
    public void runAction(SpeedWebSocketClient socket) {
        if (us.isActive() && socket.isOpen()) {
            socket.sendAction(EvaluationBoard.getAction());
            EvaluationBoard.prepareNextPhase();
        }
        round++;
    }

    static final private int SUBSTRING_MAX = 8;
    private String generateGameId(long time) {
        return DigestUtils.md5Hex("" + time).substring(0, SUBSTRING_MAX);
    }
}
