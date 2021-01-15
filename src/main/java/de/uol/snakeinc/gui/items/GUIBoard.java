package de.uol.snakeinc.gui.items;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.Setter;

public class GUIBoard extends GridPane {

    private IntegerProperty actualRoundProperty = new SimpleIntegerProperty();

    private int width ;
    private int height;
    @Setter
    private RiskType riskType;

    public GUIBoard() {
        super();
        actualRoundProperty.setValue(0);
        width = 0;
        height = 0;
        riskType = RiskType.KILLALGORITHM;
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * todo.
     * @param width todo
     * @param height todo
     */
    public void initializeCells(int width, int height) {
        Platform.runLater(() -> {
            this.getChildren().clear();
            this.width = width;
            this.height = height;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    GUICell cell = new GUICell();
                    this.add(cell, i, j);
                }
            }
        });
    }

    public boolean isWidthAndHeight(int width, int height) {
        return this.width == width && this.height == height;
    }


    /**
     * todo.
     * @param cells todo
     * @param us todo
     */
    public void updateBoard(Cell[][] cells, Player us) {
        Platform.runLater(() -> {
            ObservableList<Node> children = this.getChildren();

            actualizeActualRoundProperty();
            for (Node node : children) {
                int j = this.getRowIndex(node);
                int i = this.getColumnIndex(node);

                Cell cell = cells[i][j];

                Color color = Color.YELLOW;
                double globalRisk = cell.getRisks();
                double risks;
                double range = 0.2;
                double defaultValue = 1.0;
                boolean single = false;
                boolean positive = false;
                if (riskType == RiskType.SECTION) {
                    risks = cell.getAreaRisk();
                    single = true;
                } else if (riskType == RiskType.DEADEND) {
                    risks = cell.getDeadEndRisk();
                    single = true;
                } else if (riskType == RiskType.KILLALGORITHM) {
                    risks = cell.getKillIncentive();
                    single = true;
                    positive = true;
                } else if (riskType == RiskType.OPPONENTMOVERISK) {
                    risks = cell.getOpponentMovementRisk();
                    single = true;
                } else if (riskType == RiskType.PATHHIGHLIGHT) {
                    risks = cell.getPathHighlight();
                    single = true;
                    positive = true;
                } else if (riskType == RiskType.OPPONENTMOVERISK) {
                    risks = cell.getOpponentMovementRisk();
                    single = true;
                } else if (riskType == RiskType.DEADENDFLOODING) {
                    risks = cell.getDeadEndFlooding();
                    single = true;
                    range = 2.5;
                } else if (riskType == RiskType.DEADENDJUMPING) {
                    risks = cell.getDeadEndJumping();
                    single = true;
                    positive = true;
                } else {
                    risks = globalRisk;
                }
                ((GUICell) node).setFill(Color.BLACK);
                if (globalRisk >= 10) {
                    ((GUICell) node).setEnemy(cell.getID() == us.getId());
                    ((GUICell) node).setOpacity(1.0D);
                } else {
                    ((GUICell) node).setOption(color, risks, single, positive, range, defaultValue);
                }
                if (riskType == RiskType.DEADENDFLOODING_FLOODED_CELLS) {
                    if (cell.flooded()) {
                        ((GUICell) node).setFill(Color.ORANGE);
                        ((GUICell) node).setOpacity(1.0D);
                    }
                }
            }
        });
    }

    public final int getActualRound() {
        return actualRoundProperty.get();
    }

    public void actualizeActualRoundProperty() {
        actualRoundProperty.setValue(getActualRound() + 1);
    }
}
