package de.uol.snakeinc.ai;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;

import java.util.ArrayList;
import java.util.List;

public class PlayerOption {

    private Position position;

    private int speed;
    private Action action;

    private int reward = 0;
    private PlayerOption parentOption;

    public PlayerOption(Position position, int speed, Action action) {
        this.position = position;
        this.speed = speed;
        this.action = action;
    }

    public PlayerOption(Position position, int speed, Action action, PlayerOption parentOption) {
        this.position = position;

        this.speed = speed;
        this.action = action;
        this.parentOption = parentOption;
    }

    public void reward() {
        if (this.parentOption != null) {
            this.parentOption.reward();
        } else {
            this.reward++;
        }
    }

    public int getReward() {
        return this.reward;
    }

    public Position getPosition() {
        return this.position;
    }

    public int getSpeed() {
        return this.speed;
    }

    public Action getAction() {
        return this.action;
    }

    public List<PlayerOption> getNextOptions() {
        List<PlayerOption> options = new ArrayList<PlayerOption>();
        // Position left
        Position left = this.position.clone();
        left.turnLeft();
        left.move(speed);
        options.add(new PlayerOption(left, speed, Action.TURN_LEFT, this));

        // Position right
        Position right = this.position.clone();
        right.turnRight();
        right.move(speed);
        options.add(new PlayerOption(right, speed, Action.TURN_RIGHT, this));

        // Position do nothing
        Position doNothing = this.position.clone();
        doNothing.move(speed);
        options.add(new PlayerOption(doNothing, speed, Action.CHANGE_NOTHING, this));

        // Position speed down
        Position speedDown = this.position.clone();
        speedDown.move(speed - 1);
        options.add(new PlayerOption(speedDown, speed - 1, Action.SLOW_DOWN, this));

        // Position speed down
        Position speedUp = this.position.clone();
        speedUp.move(speed + 1);
        options.add(new PlayerOption(speedUp, speed + 1, Action.SPEED_UP, this));

        return options;
    }

    public List<PlayerOption> getPossibleNextOptions(Board board, boolean basic) {
        List<PlayerOption> possibleOptions = new ArrayList<PlayerOption>();
        List<PlayerOption> options = this.getNextOptions();
        for (PlayerOption option : options) {
            if (this.isPossible(board, this, option)) {
                if (basic) {
                    possibleOptions.add(new PlayerOption(option.getPosition(), option.getSpeed(), option.getAction()));
                } else {
                    possibleOptions.add(option);
                }
            }
        }
        return possibleOptions;
    }

    public Board printOptionsToBoard(Board board) {
        List<PlayerOption> options = this.getNextOptions();
        int[][] cells = board.getCells();
        for (PlayerOption option : options) {
            for (Position position : this.position.getFromCurrentPosition(option.getPosition())) {
                if (!position.collides(board)) {
                    cells[position.getZ()][position.getX()] = 1;
                }
            }
        }
        board.setCells(cells);
        return board;
    }

    private boolean isPossible(Board board, PlayerOption oldOption, PlayerOption newOption) {
        // check speed
        if (newOption.getSpeed() <= 0 && newOption.getSpeed() >= 10) {
            return false;
        }

        // Check board
        List<Position> positions = oldOption.getPosition().getFromCurrentPosition(newOption.getPosition());
        for (Position position : positions) {
            if (position.collides(board)) {
                return false;
            }
        }
        return true;
    }

}
