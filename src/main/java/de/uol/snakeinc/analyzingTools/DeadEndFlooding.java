package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.math.interpolation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeadEndFlooding {

    private int width;
    private int height;

    public DeadEndFlooding(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Calculate Field-Value for sections and path-highlight.
     * @param cells board-cells
     * @param us player-object for own position-detection
     */
    public void calculate(Cell[][] cells, Player us) {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                cells[x][y].setDeadEndFloodingReset(1.0);
                cells[x][y].setDeadEndJumping(1.0);
                cells[x][y].setFlooded(false);
            }
        }

        if (!checkInDeadEnd(cells, cells[us.getX()][us.getY()], us.getDirection())) {
            Cell speedUp = this.getCell(cells, us, Action.SPEED_UP);
            Cell slowDown = this.getCell(cells, us, Action.SLOW_DOWN);
            Cell changeNothing = this.getCell(cells, us, Action.CHANGE_NOTHING);
            Cell turnLeft = this.getCell(cells, us, Action.TURN_LEFT);
            Cell turnRight = this.getCell(cells, us, Action.TURN_RIGHT);

            if (speedUp != null) {
                this.floodRound(cells, speedUp, us.getDirection(), us, Config.BLOCKS);
            }
            if (slowDown != null) {
                this.floodRound(cells, slowDown, us.getDirection(), us, Config.BLOCKS);
            }
            if (changeNothing != null) {
                this.floodRound(cells, changeNothing, us.getDirection(), us, Config.BLOCKS);
            }
            if (turnLeft != null) {
                this.floodRound(cells, turnLeft, Common.turnLeft(us.getDirection()), us, Config.BLOCKS);
            }
            if (turnRight != null) {
                this.floodRound(cells, turnRight, Common.turnRight(us.getDirection()), us, Config.BLOCKS);
            }
        }
    }

    private Cell getCell(Cell[][] cells, Player us, Action action) {
        int x = us.getX();
        int y = us.getY();
        Direction dir = us.getDirection();
        Common.Tuple xy;
        switch (action) {
            case SPEED_UP:
                xy = Common.generateXY(dir, x, y, us.getSpeed() + 1);
                break;
            case SLOW_DOWN:
                xy = Common.generateXY(dir, x, y, us.getSpeed() - 1);
                break;
            case CHANGE_NOTHING:
                xy = Common.generateXY(dir, x, y, us.getSpeed());
                break;
            case TURN_LEFT:
                xy = Common.generateXY(Common.turnLeft(dir), x, y, us.getSpeed());
                break;
            case TURN_RIGHT:
                xy = Common.generateXY(Common.turnRight(dir), x, y, us.getSpeed());
                break;
            default:
                throw new IllegalStateException();
        }
        x = xy.getX();
        y = xy.getY();

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

    private boolean checkInDeadEnd(Cell[][] cells, Cell position, Direction direction) {
        position.setHit(true);
        List<Cell> checkCells = new ArrayList<Cell>();
        List<Cell> neighbours = this.getPossibleNeighbours(cells, position, direction, true);
        List<Cell> newNeighbours = new ArrayList<Cell>();

        int count = neighbours.size();
        checkCells.add(position);
        checkCells.addAll(neighbours);

        while (count < Config.BLOCKS) {
            for (Cell cell : neighbours) {
                newNeighbours.addAll(this.getPossibleNeighbours(cells, cell, direction, true));
            }
            if (newNeighbours.isEmpty()) {
                break;
            }
            count += newNeighbours.size();
            checkCells.addAll(newNeighbours);
            neighbours = newNeighbours;
            newNeighbours = new ArrayList<Cell>();
        }

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                cells[x][y].setHit(false);
            }
        }

        if (count < Config.BLOCKS) {
            for (Cell cell : checkCells) {
                cell.setDeadEndJumping(2.0D);
            }
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    Cell option = cells[x][y];
                    if (!checkCells.contains(option)) {
                        option.setDeadEndJumping(Config.DEAD_END_INCENTIVE);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void floodRound(Cell[][] cells, Cell position, Direction direction, Player us, int blocks) {
        List<Cell> way = this.getCellsBetween(cells, us.getX(), position.getX(), us.getY(), position.getY());
        for (Cell cell : way) {
            cell.setHit(true);
        }
        position.setHit(true);
        List<Cell> checkCells = new ArrayList<Cell>();
        List<Cell> neighbours = this.getPossibleNeighbours(cells, position, direction, false);
        List<Cell> newNeighbours = new ArrayList<Cell>();

        int count = neighbours.size();
        checkCells.add(position);
        checkCells.addAll(neighbours);

        while (count < blocks) {
            for (Cell cell : neighbours) {
                newNeighbours.addAll(this.getPossibleNeighbours(cells, cell, direction, true));
            }
            if (newNeighbours.isEmpty()) { // Here also possibility to check for 1/2/3 etc. gates
                break;
            }
            count += newNeighbours.size();
            checkCells.addAll(newNeighbours);
            neighbours = newNeighbours;
            newNeighbours = new ArrayList<Cell>();
        }

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                cells[x][y].setHit(false);
            }
        }

        double value = 1.0D;
        if (count < blocks) {
            double scale = ((double) count) / ((double) blocks);

            value = new LinearInterpolator(Config.INTERPOLATION_MAX, Config.INTERPOLATION_MIN).getInterpolation(scale);
            position.setDeadEndFlooding(value + Config.FLOOD_ADD);
        }
        for (Cell cell : checkCells) {
            cell.setDeadEndFlooding(value);
        }
    }

    private List<Cell> getPossibleNeighbours(Cell[][] cells, Cell position, Direction direction, boolean all) {
        List<Cell> neighbours = new ArrayList<Cell>();
        if (!all) {
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, direction));
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Common.turnLeft(direction)));
            this.addIfNotNull(neighbours, this.getPossibleNeighbour(cells, position, Common.turnRight(direction)));
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
        var xy = Common.generateXY(direction,x,y,1);
        x = xy.getX();
        y = xy.getY();
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

    private List<Cell> getCellsBetween(Cell[][] cells, int x1, int x2, int y1, int y2) {
        int xStart = x1;
        if (x2 < x1) {
            xStart = x2;
        }

        int xEnd = x1;
        if (x2 > x1) {
            xEnd = x2;
        }

        int yStart = y1;
        if (y2 < y1) {
            yStart = y2;
        }

        int yEnd = y1;
        if (y2 > y1) {
            yEnd = y2;
        }

        List<Cell> finalCells = new ArrayList<Cell>();
        for (int x = xStart; x < xEnd; x++) {
            finalCells.addAll(Arrays.asList(cells[x]).subList(yStart, yEnd));
        }
        return finalCells;
    }
}
