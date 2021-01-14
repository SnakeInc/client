package de.uol.snakeinc.gui.items;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GUICell extends Rectangle {

    public GUICell () {
        super.setHeight(10);
        super.setWidth(10);
        this.setFill(Paint.valueOf("BLACK"));
    }

    public void setEnemy(boolean us) {
        if (us) {
            this.setFill(Paint.valueOf("GREEN"));
        } else {
            this.setFill(Paint.valueOf("RED"));
        }
        this.setOpacity(1.0);
    }

    public void setOption(Color color, double risk, boolean single, boolean positive) {
        this.setFill(color);
        if (risk <= 0) {
            this.setOpacity(0);
        } else {
            if (single) {
                if (positive) {
                    double opacity = (risk * (-1.0D)) + 1.0D;
                    this.setOpacity(opacity);
                } else {
                    double opacity = (risk - 1.0D) * 5.0D;
                    this.setOpacity(opacity);
                }
            } else {
                double opacity = (risk - 1.0D) / 9.0D;
                this.setOpacity(opacity);
            }
        }
    }
}