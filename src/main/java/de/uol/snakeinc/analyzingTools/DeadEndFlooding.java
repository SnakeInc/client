package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class DeadEndFlooding {

    private int width;
    private int height;

    public DeadEndFlooding(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void calculate(Cell[][] cells, Player us) {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                cells[x][y].setDeadEndFlooding(1.0);
                cells[x][y].setFlooded(false);
            }
        }

        Cell speedUp = this.getCell(cells, us, Action.SPEED_UP);
        Cell slowDown = this.getCell(cells, us, Action.SLOW_DOWN);
        Cell changeNothing = this.getCell(cells, us, Action.CHANGE_NOTHING);
        Cell turnLeft = this.getCell(cells, us, Action.TURN_LEFT);
        Cell turnRight = this.getCell(cells, us, Action.TURN_RIGHT);

        if (speedUp != null) {
            speedUp.setDeadEndFlooding(this.floodRound(cells, speedUp, us.getDirection()));
        }
        if (slowDown != null) {
            slowDown.setDeadEndFlooding(this.floodRound(cells, slowDown, us.getDirection()));
        }
        if (changeNothing != null) {
            changeNothing.setDeadEndFlooding(this.floodRound(cells, changeNothing, us.getDirection()));
        }
        if (turnLeft != null) {
            turnLeft.setDeadEndFlooding(this.floodRound(cells, turnLeft, us.getDirection().getLeft()));
        }
        if (turnRight != null) {
            turnRight.setDeadEndFlooding(this.floodRound(cells, turnRight, us.getDirection().getRight()));
        }
    }

    private Cell getCell(Cell[][] cells, Player us, Action action) {
        int x = us.getX();
        int y = us.getY();
        if (us.getDirection() == Direction.DOWN) {
            switch(action) {
                case SPEED_UP:
                    y = y - (us.getSpeed() + 1);
                case SLOW_DOWN:
                    y = y - (us.getSpeed() - 1);
                case CHANGE_NOTHING:
                    y = y - us.getSpeed();
                case TURN_LEFT:
                    x = x + us.getSpeed();
                case TURN_RIGHT:
                    x = x - us.getSpeed();
            }
        } else if (us.getDirection() == Direction.UP) {
            switch(action) {
                case SPEED_UP:
                    y = y + (us.getSpeed() + 1);
                case SLOW_DOWN:
                    y = y + (us.getSpeed() - 1);
                case CHANGE_NOTHING:
                    y = y + us.getSpeed();
                case TURN_LEFT:
                    x = x - us.getSpeed();
                case TURN_RIGHT:
                    x = x + us.getSpeed();
            }
        } else if (us.getDirection() == Direction.RIGHT) {
            switch(action) {
                case SPEED_UP:
                    x = x + (us.getSpeed() + 1);
                case SLOW_DOWN:
                    x = x + (us.getSpeed() - 1);
                case CHANGE_NOTHING:
                    x = x + us.getSpeed();
                case TURN_LEFT:
                    y = y + us.getSpeed();
                case TURN_RIGHT:
                    y = y - us.getSpeed();
            }
        } else if (us.getDirection() == Direction.LEFT) {
            switch(action) {
                case SPEED_UP:
                    x = x - (us.getSpeed() + 1);
                case SLOW_DOWN:
                    x = x - (us.getSpeed() - 1);
                case CHANGE_NOTHING:
                    x = x - us.getSpeed();
                case TURN_LEFT:
                    y = y - us.getSpeed();
                case TURN_RIGHT:
                    y = y + us.getSpeed();
            }
        }
        if (x >= 0 && x < this.width) {
            if (y >= 0 && y < this.height) {
                Cell cell = cells[x][y];
                if (!cell.isInUse()) {
                    return cell;
                }
            }
        }
        return null;
    }

    private double floodRound(Cell[][] cells, Cell position, Direction direction) {
        position.setHit(true);
        List<Cell> neighbours = this.getPossibleNeighbours(cells, position, direction, false);
        List<Cell> newNeighbours = new ArrayList<Cell>();
        int count = neighbours.size();

        while (count < 80) {
            for (Cell cell : neighbours) {
                newNeighbours.addAll(this.getPossibleNeighbours(cells, cell, direction, true));
            }
            if (newNeighbours.isEmpty()) {
                break;
            }
            count += newNeighbours.size();
            neighbours = newNeighbours;
            newNeighbours = new ArrayList<Cell>();
        }

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                cells[x][y].setHit(false);
            }
        }

        double scale = ((double) count) / 80.0D;

        return new LinearInterpolator(2.0, 1.0).getInterpolation(scale);
    }

    private List<Cell> getPossibleNeighbours(Cell[][] cells, Cell position, Direction direction, boolean all) {
        List<Cell> neighbours = new ArrayList<Cell>();
        if (!all) {
            if (direction == Direction.DOWN) {
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.LEFT));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.DOWN));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.RIGHT));
            } else if (direction == Direction.LEFT) {
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.LEFT));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.DOWN));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.UP));
            } else if (direction == Direction.UP) {
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.LEFT));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.RIGHT));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.UP));
            } else if (direction == Direction.RIGHT) {
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.RIGHT));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.DOWN));
                this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.UP));
            }
        } else {
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.RIGHT));
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.DOWN));
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.UP));
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Direction.LEFT));
        }
        return neighbours;
    }

    private Cell getPossibleNeighbour(Cell[][] cells, Cell position, Direction direction) {
        int x = position.getX();
        int y = position.getY();
        switch(direction) {
            case DOWN:
                y--;
            case UP:
                y++;
            case LEFT:
                x--;
            default:
                x++;
        }
        if (x >= 0 && x < this.width) {
            if (y >= 0 && y < this.height) {
                Cell cell = cells[x][y];
                if (!cell.isInUse() && !cell.hasHit()) {
                    cell.setHit(true);
                    cell.setFlooded(true);
                    return cell;
                }
            }
        }
        return null;
    }

    private void addIfNotNull(List<Cell> cells, Cell cell) {
        if (cell != null) {
            cells.add(cell);
        }
    }
}
