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

public class GUIBoard extends GridPane {

    private IntegerProperty actualRoundProperty = new SimpleIntegerProperty();

    private int width ;
    private int height;
    private RiskType riskType;

    public GUIBoard() {
        super();
        actualRoundProperty.setValue(0);
        width = 0;
        height = 0;
        riskType = RiskType.KILLALGORITHM;
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
    }

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
                boolean single = false;
                boolean positive = false;
                if (riskType == RiskType.SECTION) {
                    risks = cell.getAreaRisk();
                    single = true;
                } else if (riskType == RiskType.DEADEND) {
                    risks = cell.getDeadEndRisk();
                    single = true;
                } else if (riskType == RiskType.KILLALGORITHM) {
                    risks = cell.getKillAlgorithmRisk();
                    single = true;
                    positive = true;
                } else {
                    risks = globalRisk;
                }
                if (globalRisk >= 10) {
                    ((GUICell) node).setEnemy(cell.getID() == us.getId());
                    ((GUICell) node).setOpacity(1.0D);
                } else {
                    ((GUICell) node).setOption(color, risks, single, positive);
                }
            }
        });
    }

    public void setRiskType(RiskType type) {
        this.riskType = type;
    }

    public final int getActualRound() {
        return actualRoundProperty.get();
    }

    public IntegerProperty actualRoundProperty() {
        return actualRoundProperty;
    }

    public void actualizeActualRoundProperty() {
        actualRoundProperty.setValue(getActualRound() + 1);
    }
}
