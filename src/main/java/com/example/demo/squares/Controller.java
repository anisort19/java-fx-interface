package com.example.demo.squares;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private Rectangle center;

    @FXML
    private Rectangle tl;

    @FXML
    private Rectangle tr;

    @FXML
    private Rectangle bl;

    @FXML
    private Rectangle br;

    @FXML
    private Rectangle c1, c2, c3, c4, c5, c6, c7;

    @FXML
    public void initialize() {

        center.setRotate(45);
        center.setArcWidth(60);
        center.setArcHeight(60);

        center.widthProperty().bind(tl.widthProperty().multiply(1.4));
        center.heightProperty().bind(center.widthProperty());

        center.xProperty().bind(root.widthProperty().divide(2).subtract(center.widthProperty().divide(2)));
        center.yProperty().bind(root.heightProperty().divide(2).subtract(center.heightProperty().divide(2)));

        tl.widthProperty().bind(root.widthProperty().multiply(0.35));
        tl.heightProperty().bind(tl.widthProperty());

        tr.widthProperty().bind(tl.widthProperty());
        tr.heightProperty().bind(tl.widthProperty());

        bl.widthProperty().bind(tl.widthProperty());
        bl.heightProperty().bind(tl.widthProperty());

        br.widthProperty().bind(tl.widthProperty());
        br.heightProperty().bind(tl.widthProperty());

        tl.setX(0);
        tl.setY(0);

        tr.xProperty().bind(root.widthProperty().subtract(tr.widthProperty()));
        tr.setY(0);

        bl.setX(0);
        bl.yProperty().bind(root.heightProperty().subtract(bl.heightProperty()));

        br.xProperty().bind(root.widthProperty().subtract(br.widthProperty()));
        br.yProperty().bind(root.heightProperty().subtract(br.heightProperty()));

        // палітра
        setupColor(c1);
        setupColor(c2);
        setupColor(c3);
        setupColor(c4);
        setupColor(c5);
        setupColor(c6);
        setupColor(c7);
    }

    private void setupColor(Rectangle rect){

        rect.setOnMouseClicked(e -> {

            Color baseColor = (Color) rect.getFill();

            if(e.getButton() == MouseButton.PRIMARY){
                center.setFill(baseColor);
            }

            if(e.getButton() == MouseButton.SECONDARY){

                double hue = baseColor.getHue();
                double saturation = baseColor.getSaturation();
                double brightness = baseColor.getBrightness();

                saturation = Math.max(0, saturation - 0.4);
                brightness = Math.min(1.0, brightness + 0.1);

                Color lighter = Color.hsb(hue, saturation, brightness);

                center.setFill(lighter);
            }

        });

    }

    @FXML
    private void exitApp(){
        Platform.exit();
    }
}