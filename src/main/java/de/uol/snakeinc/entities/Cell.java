package de.uol.snakeinc.entities;

import de.uol.snakeinc.pathfinding.PathCell;
import lombok.Getter;

public class Cell extends PathCell {


    public static final int DEATH_VALUE = 10;
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

    //
    private double deadEndRisk;

    private boolean hardDeadly;

    private boolean tmpDeadly;

    @Getter
    private int iD;

    public Cell(int x, int y) {
        super(x, y);
        value = 1;
        opponentMovementRisk = 1;
        tmpMoveCalcValue = 1;
        areaRisk = 1;
        killIncentive = 1;
        deadEndRisk = 1;
        hardDeadly = false;
        tmpDeadly = false;
    }

    @Override
    public boolean isInUse() {
        return value != 1;
    }

    public void setId(int id) {
        this.iD = id;
        value = DEATH_VALUE;
        this.hardDeadly = true;
    }

    public boolean isDeadly() {
        return hardDeadly || tmpDeadly;
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

    public void setDeadEndRisk(double riskValue) {
        if((! hardDeadly) && this.deadEndRisk < riskValue) {
            this.deadEndRisk = riskValue;
        }
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interaction between players.
        //Not calculated: Interinteraktion between players.
        if (hardDeadly || tmpDeadly) {
            return DEATH_VALUE;
        }
        return getValue()  * opponentMovementRisk * tmpMoveCalcValue * areaRisk * deadEndRisk * killIncentive;
    }

    public void clearPseudoValue() {
        tmpMoveCalcValue = 1;
        tmpDeadly = false;
    }

    public void setTmpMoveCalcValue() {
        tmpMoveCalcValue = DEATH_VALUE;
        tmpDeadly = true;
    }

    /**
     * Prepare the next phase.
     */
    public void prepareNextPhase() {
        this.opponentMovementRisk = 1;
        this.killIncentive = 1;
    }

    public void setKillIncentive() {
        this.killIncentive = 0.3;
    }
}
