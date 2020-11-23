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

    private Board board;

    public PlayerOption(Board board, Position position, int speed, Action action) {
        this.board = board;
        this.position = position;
        this.speed = speed;
        this.action = action;
    }

    public PlayerOption(Board board, Position position, int speed, Action action, PlayerOption parentOption) {
        this.board = board;
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

    public boolean hasParent() {
        return this.parentOption != null;
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

    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<PlayerOption> getNextOptions() {
        List<PlayerOption> options = new ArrayList<PlayerOption>();
        // Position left
        Position left = this.position.clone();
        left.turnLeft();
        left.move(speed);
        Board boardLeft = board.clone();
        PlayerOption leftOption = new PlayerOption(boardLeft, left, speed, Action.TURN_LEFT, this);
        this.printOptionToBoard(boardLeft, leftOption);
        options.add(leftOption);

        // Position right
        Position right = this.position.clone();
        right.turnRight();
        right.move(speed);
        Board boardRight = board.clone();
        PlayerOption rightOption = new PlayerOption(boardRight, right, speed, Action.TURN_RIGHT, this);
        this.printOptionToBoard(boardRight, rightOption);
        options.add(rightOption);

        // Position do nothing
        Position doNothing = this.position.clone();
        doNothing.move(speed);
        Board boardDoNothing = board.clone();
        PlayerOption doNothingOption = new PlayerOption(boardDoNothing, doNothing, speed, Action.CHANGE_NOTHING, this);
        this.printOptionToBoard(boardDoNothing, doNothingOption);
        options.add(doNothingOption);

        // Position speed down
        Position speedDown = this.position.clone();
        speedDown.move(speed - 1);
        Board boardSpeedDown = board.clone();
        PlayerOption speedDownOption = new PlayerOption(boardSpeedDown, speedDown, speed - 1, Action.SLOW_DOWN, this);
        this.printOptionToBoard(boardSpeedDown, speedDownOption);
        options.add(speedDownOption);

        // Position speed up
        Position speedUp = this.position.clone();
        speedUp.move(speed + 1);
        Board boardSpeedUp = board.clone();
        PlayerOption speedUpOption = new PlayerOption(boardSpeedUp, speedUp, speed + 1, Action.SPEED_UP, this);
        this.printOptionToBoard(boardSpeedUp, speedUpOption);
        options.add(speedUpOption);

        return options;
    }

    public List<PlayerOption> getPossibleNextOptions(List<PlayerOption> enemieOptions, boolean basic) {
        List<PlayerOption> possibleOptions = new ArrayList<PlayerOption>();
        List<PlayerOption> options = this.getNextOptions();
        for (PlayerOption enemieOption : enemieOptions) {
            for (PlayerOption option : options) {
                if (this.isPossible(enemieOption.getBoard(), this, option)) {
                    if (basic) {
                        possibleOptions.add(new PlayerOption(enemieOption.getBoard(), option.getPosition(), option.getSpeed(), option.getAction()));
                    } else {
                        possibleOptions.add(option);
                    }
                }
            }
        }
        return possibleOptions;
    }

    public Board printOptionToBoard(Board board, PlayerOption option) {
        int[][] cells = board.getCells();
        for (Position position : this.position.getFromCurrentPosition(option.getPosition())) {
            if (!position.collides(board)) {
                cells[position.getZ()][position.getX()] = 1;
            }
        }
        board.setCells(cells);
        return board;
    }

    private boolean isPossible(Board board, PlayerOption oldOption, PlayerOption newOption) {
        // check speed
        if (newOption.getSpeed() <= 0 || newOption.getSpeed() >= 10) {
            if (!newOption.hasParent()) {
                System.out.println(oldOption.getAction() + " speed-problem");
            }
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
