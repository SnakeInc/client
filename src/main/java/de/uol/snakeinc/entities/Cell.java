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

    private double killIncentive;

    //Area-risk
    private double areaRisk;

    @Getter
    private int iD;

    public Cell(int x, int y) {
        super(x, y);
        value = 1;
        opponentMovementRisk = 1;
        tmpMoveCalcValue = 1;
        areaRisk = 1;
        killIncentive = 1;
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
    public void setCell(int id) {
        value = 10;
        this.iD = id;
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
                break;
            case 2:
                opponentMovementRisk = opponentMovementRisk * 1.0625;
                break;
            case 3:
                opponentMovementRisk = opponentMovementRisk * 1.015625;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void setAreaRisk(double risk) {
        this.areaRisk = risk;
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interaction between players.
        return getValue() * opponentMovementRisk * tmpMoveCalcValue * areaRisk * killIncentive;
    }

    public void clearPseudoValue() {
        tmpMoveCalcValue = 1;
    }

    public void setTmpMoveCalcValue() {
        tmpMoveCalcValue = 10;
    }

    /**
     * Prepare the next phase.
     */
    public void prepareNextPhase() {
        this.opponentMovementRisk = 1;
        this.killIncentive = 1;
    }

    public void setKillIncentive() {
        this.killIncentive = 0.8;
    }
}
