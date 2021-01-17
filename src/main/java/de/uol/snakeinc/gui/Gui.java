package de.uol.snakeinc.gui;

import de.uol.snakeinc.SnakeInc;
import de.uol.snakeinc.gui.items.GUIBoard;
import de.uol.snakeinc.gui.items.RiskType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui extends Application {

    HBox root = new HBox();
    Boolean isActive = false;
    Pane roundsPane;
    GUIBoard board;
    Button stopUi = new Button();
    ComboBox riskType = new ComboBox(FXCollections.observableArrayList(
        RiskType.values()
    ));
    Stage stage;

    /**
     * Initialize GUI-Board.
     */
    public void initialize() {
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setSpacing(80);
        roundsPane = new Pane();
        roundsPane.setMinHeight(50);
        roundsPane.setMinWidth(100);
        board = new GUIBoard();
        root.getChildren().add(board);
        SnakeInc.setGuiReady(true);
    }

    public GUIBoard getGuiBoard() {
        return this.board;
    }

    @Override
    public void start(Stage stage) {
        isActive = true;
        initialize();

        riskType.setPadding(new Insets(10,10,10,10));
        riskType.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) ->
                board.setRiskType((RiskType) newValue)
        );
        root.getChildren().add(riskType);

        stopUi.setText("Stop UI");

        Scene scene = new Scene(root, 1500, 1000, Color.BLACK);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            SnakeInc.close();
        });
        this.stage = stage;

        SnakeInc.setGui(this);
    }

    public void close() {
        Platform.runLater(() -> {
            this.stage.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
