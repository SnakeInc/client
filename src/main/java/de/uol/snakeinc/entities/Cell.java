package de.uol.snakeinc.entities;

import lombok.Getter;

public class Cell {

    //Basic Value.
    @Getter
    private double value;

    //Value that is calculated temporarily
    private double tmpMoveCalcValue;

    //The risk that another agent will move to this cell
    private double opponentMovementRisk;

    @Getter
    private Cell prevCell;
    @Getter
    private Cell nextCell;
    @Getter
    private int iD;

    public Cell() {
        value = 1;
        opponentMovementRisk = 1;
        tmpMoveCalcValue = 1;
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

    public void raiseRisk(double risk) {
        this.actionRisk *= risk;
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interinteraktion between players.
        return getValue() * opponentMovementRisk * tmpMoveCalcValue;
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
