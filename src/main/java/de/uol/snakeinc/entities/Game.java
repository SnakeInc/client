package de.uol.snakeinc.entities;

import de.uol.snakeinc.ai.MoveIteration;
import de.uol.snakeinc.ai.PlayerOption;
import de.uol.snakeinc.ai.Position;
import de.uol.snakeinc.connection.SpeedWebSocketClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        List<PlayerOption> enemies = new ArrayList<PlayerOption>();
        for (int id : this.players.keySet()) {
            Player player = this.players.get(id);
            if (player.isActive()) {
                enemies.add(new PlayerOption(new Position(player.getX(), player.getY(), player.getDirection()), player.getSpeed(), Action.TURN_RIGHT));
            }
        }
        PlayerOption playerOption = new PlayerOption(new Position(us.getX(), us.getY(), us.getDirection()), us.getSpeed(), Action.TURN_RIGHT);

        MoveIteration moveIteration = new MoveIteration(this.currentBoard, enemies, playerOption, 3);


        // Implement logical working here
        socket.sendAction(moveIteration.getBestOption());
    }
}
