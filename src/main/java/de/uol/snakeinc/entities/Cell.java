package de.uol.snakeinc.entities;

import de.uol.snakeinc.pathfinding.PathCell;
import lombok.Getter;

public class Cell extends PathCell {

    //Basic Value.
    @Getter
    private double value;

    //Value that is calculated temporarily
    private double tmpMoveCalcValue;

    //The risk that another agent will move to this cell
    private double opponentMovementRisk;

    //Area-risk
    private double areaRisk;

    @Getter
    private Cell prevCell;
    @Getter
    private Cell nextCell;
    @Getter
    private int iD;

    public Cell(int x, int y) {
        super(x, y);
        value = 1;
        opponentMovementRisk = 1;
        tmpMoveCalcValue = 1;
        areaRisk = 1;
    }

    @Override
    public boolean isInUse() {
        return value != 1;
    }

    public void setId(int id) {
        this.iD = id;
        value = 10;
    }

    //??? Why id?
    public void setNextCell(int id, Cell nextCell) {
        this.nextCell = nextCell;
    }

    //??? Why id?
    public void setPrevCell(int id, Cell prevCell) {
        value = 10;
        this.prevCell = prevCell;
    }

    //??? Why id?
    public void setPrevHoleCell(int id, Cell prevCell) {
        this.prevCell = prevCell;
    }

    public boolean isDeadly() {
        return value >= 10;
    }

    /**
     * Todo this.
     * @param depth Todo this
     */
    public void raiseActionRisk(int depth) {
        switch (depth) {
            case 1:
                opponentMovementRisk = opponentMovementRisk * 1.25;
                //TODO here was fallthrough
                break;
            case 2:
                opponentMovementRisk = opponentMovementRisk * 1.0625;
                //TODO here was fallthrough
                break;
            case 3:
                opponentMovementRisk = opponentMovementRisk * 1.015625;
                //TODO here was fallthrough
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void setAreaRisk(double risk) {
        this.areaRisk = risk;
    }

    public void raiseRisk(double risk) {
        this.opponentMovementRisk *= risk;
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interinteraktion between players.
        return getValue() * opponentMovementRisk * tmpMoveCalcValue * areaRisk;
    }

    public void clearPseudoValue() {
        tmpMoveCalcValue = 1;
    }

    public void setTmpMoveCalcValue() {
        tmpMoveCalcValue = 10;
    }

    public void prepareNextPhase() {
        this.opponentMovementRisk = 1;
    }
}
