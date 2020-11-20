package de.uol.snakeinc.ai;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.List;

@CustomLog
public class MoveIteration {

    private int rounds;
    private Board board;

    private List<PlayerOption> enemies;

    private List<PlayerOption> playerOptions;

    private List<PlayerOption> basicOptions;

    public MoveIteration(Board board, List<PlayerOption> enemies, PlayerOption playerOption, int rounds) {
        this.board = board;
        this.enemies = enemies;
        this.rounds = rounds;

        List<PlayerOption> options = new ArrayList<PlayerOption>();
        options.add(playerOption);
        this.playerOptions = options;

        this.run();
    }

    public MoveIteration(Board board, List<PlayerOption> enemies, List<PlayerOption> playerOptions) {
        this.board = board;
        this.enemies = enemies;
        this.playerOptions = playerOptions;
    }

    public Action getBestOption() {
        Action action = Action.TURN_RIGHT;
        int reward = 0;
        for (PlayerOption option : this.basicOptions) {
            if (option.getReward() > reward) {
                reward = option.getReward();
                action = option.getAction();
            }
            log.debug("Action " + option.getAction().toString() + " rewarded: " + option.getReward());
        }
        return action;
    }

    private void run() {
        MoveIteration iteration = this;
        for (int i = 0; i < rounds; i++) {
            boolean first = false;
            if (i == 0) {
                first = true;
            }
            iteration = iteration.generateOptions(first);
            log.debug("Round " + i);
        }
    }

    private MoveIteration generateOptions(boolean first) {
        List<PlayerOption> newEnemies = new ArrayList<PlayerOption>();
        for (PlayerOption option : this.enemies) {
            newEnemies.addAll(option.getNextOptions());
        }
        for (PlayerOption option : newEnemies) {
            board = option.printOptionsToBoard(board);
        }
        List<PlayerOption> newPlayerOptions = new ArrayList<PlayerOption>();
        for (PlayerOption option : this.playerOptions) {
            newPlayerOptions.addAll(option.getPossibleNextOptions(board, first));
        }
        if (first) {
            this.basicOptions = newPlayerOptions;
        }
        for (PlayerOption option : newPlayerOptions) {
            option.reward();
        }

        return new MoveIteration(board, newEnemies, newPlayerOptions);
    }

}
