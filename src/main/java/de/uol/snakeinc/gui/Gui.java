package de.uol.snakeinc.gui;

import de.uol.snakeinc.SnakeInc;
import de.uol.snakeinc.gui.items.GUIBoard;
import de.uol.snakeinc.gui.items.RiskType;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class Gui extends Application {

    HBox root = new HBox();
    Boolean isActive = false;
    Pane roundsPane;
    GUIBoard board;
    ObservableList<String> options =
        FXCollections.observableArrayList(
            "Option 1",
            "Option 2",
            "Option 3"
        );
    ComboBox riskType = new ComboBox(FXCollections.observableArrayList(
        RiskType.values()
    ));

    static List<Color> colors = List.of(
        Color.YELLOW,
        Color.RED,
        Color.BLUE,
        Color.ORANGE,
        Color.GREEN,
        Color.LIME,
        Color.PURPLE,
        Color.WHITE
    );

    public void initialize() {
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setSpacing(80);
        ObservableList<String> speedChoices =
            FXCollections.observableArrayList(
                "100",
                "200",
                "1000",
                "2000"
            );
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

    public void setUpBoard(File file) {
        if (isActive) {
            root.getChildren().remove(board);
        }
        board = new GUIBoard();
        root.getChildren().add(board);
    }

    public void initializeRoundsField() {
        if (isActive) {
            roundsPane.getChildren().clear();
        }
        Text rounds = new Text();
        board.actualRoundProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rounds.setText("Runde: " + board.getActualRound());
            }
        });
        roundsPane.getChildren().add(rounds);
    }


    @Override
    public void start(Stage stage) {
        isActive = true;
        initialize();

        riskType.setPadding(new Insets(10,10,10,10));
        riskType.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                board.setRiskType((RiskType) newValue);
            }
        );
        root.getChildren().add(riskType);

        Scene scene = new Scene(root, 1500, 1000, Color.BLACK);
        stage.setScene(scene);
        stage.show();

        SnakeInc.setGui(this);
    }

    public static Color colorBag(int index) {

        if (index <= 0) {
            return Color.BLACK;
        }
        return colors.get(index);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
