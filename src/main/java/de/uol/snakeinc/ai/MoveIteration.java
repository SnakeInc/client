package de.uol.snakeinc.ai;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CustomLog
public class MoveIteration {

    private int rounds;

    private List<PlayerOption> enemies;

    private List<PlayerOption> playerOptions;

    private List<PlayerOption> basicOptions;

    public MoveIteration(List<PlayerOption> enemies, PlayerOption playerOption, int rounds) {
        this.enemies = enemies;
        this.rounds = rounds;

        List<PlayerOption> options = new ArrayList<PlayerOption>();
        options.add(playerOption);
        this.playerOptions = options;

        this.run();
    }

    public MoveIteration(List<PlayerOption> enemies, List<PlayerOption> playerOptions) {
        this.enemies = enemies;
        this.playerOptions = playerOptions;
    }

    public Action getBestOption() {
        HashMap<Action, Integer> rewards = new HashMap<Action, Integer>();
        for (PlayerOption option : this.basicOptions) {
            if (rewards.containsKey(option.getAction())) {
               rewards.put(option.getAction(), rewards.get(option.getAction()) + option.getReward());
            } else {
                rewards.put(option.getAction(), option.getReward());
            }
        }
        Action action = Action.TURN_RIGHT;
        int reward = 0;
        for (Action optionAction : rewards.keySet()) {
            if (rewards.get(optionAction) > reward) {
                reward = rewards.get(optionAction);
                action = optionAction;
            }
            log.debug("Action " + optionAction.toString() + " rewarded: " + rewards.get(optionAction));
        }
        log.debug("Taking action " + action.toString());
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
        }
    }

    private MoveIteration generateOptions(boolean first) {
        List<PlayerOption> newEnemies = new ArrayList<PlayerOption>();
        for (PlayerOption option : this.enemies) {
            newEnemies.addAll(option.getNextOptions());
        }
        List<PlayerOption> newPlayerOptions = new ArrayList<PlayerOption>();
        for (PlayerOption option : this.playerOptions) {
            newPlayerOptions.addAll(option.getPossibleNextOptions(newEnemies, first)); // TODO: give enemies
        }
        if (first) {
            this.basicOptions = newPlayerOptions;
        }
        for (PlayerOption option : newPlayerOptions) {
            option.reward();
        }

        return new MoveIteration(newEnemies, newPlayerOptions);
    }

}
