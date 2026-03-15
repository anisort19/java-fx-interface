package com.example.demo.squares;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
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
    }
}