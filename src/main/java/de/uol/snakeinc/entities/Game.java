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
    private Player us;

    private int round = 0;

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

    public void informIntelligentBoard(EvaluationBoard evaluationBoard, int[][] rawBoard) {
        EvaluationBoard = evaluationBoard;
        if (evaluationBoard != null) {
            this.boards.put(round, rawBoard);
        }
    }

    public void informIntelligentBoard(int[][] rawBoard) {
        EvaluationBoard.update(players, us, round);
        boards.put(round, rawBoard);
    }

    public EvaluationBoard getEvaluationBoard() {
        return this.EvaluationBoard;
    }

    /**
     * Run action after messages are parsed to calculate next step.
     * @param socket currently connected socket
     */
    public void runAction(SpeedWebSocketClient socket) {
        if(round < 2) {
            socket.sendAction(EvaluationBoard.startingStrategy());
        } else {
        socket.sendAction(EvaluationBoard.getAction());}
        EvaluationBoard.prepareNextPhase();
        round++;
    }

    public Player getUs() {
        return this.us;
    }

    public int getRounds() {
        return this.round;
    }

    public String getGameId() {
        return this.gameId;
    }

    private String generateGameId(long time) {
        return DigestUtils.md5Hex("" + time).substring(0, 8);
    }
}
