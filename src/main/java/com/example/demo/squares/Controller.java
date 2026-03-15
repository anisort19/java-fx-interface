package com.example.demo.squares;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private Rectangle center, tl, tr, bl, br;

    @FXML
    private Rectangle c1, c2, c3, c4, c5, c6, c7;

    private Shape selected = null;
    private final Circle selectionMarker = new Circle(5, Color.BLACK);

    private RotateTransition rotateTransition;
    private boolean dragStarted = false;

    @FXML
    public void initialize() {

        center.setRotate(45);
        center.setArcWidth(60);
        center.setArcHeight(60);

        center.widthProperty().bind(tl.widthProperty().multiply(1.4));
        center.heightProperty().bind(center.widthProperty());

        tl.widthProperty().bind(root.widthProperty().multiply(0.35));
        tl.heightProperty().bind(tl.widthProperty());

        tr.widthProperty().bind(tl.widthProperty());
        tr.heightProperty().bind(tl.widthProperty());

        bl.widthProperty().bind(tl.widthProperty());
        bl.heightProperty().bind(tl.widthProperty());

        br.widthProperty().bind(tl.widthProperty());
        br.heightProperty().bind(tl.widthProperty());

        root.widthProperty().addListener((obs, oldVal, newVal) -> updateInitialPositions());
        root.heightProperty().addListener((obs, oldVal, newVal) -> updateInitialPositions());

        setupColor(c1);
        setupColor(c2);
        setupColor(c3);
        setupColor(c4);
        setupColor(c5);
        setupColor(c6);
        setupColor(c7);

        selectionMarker.setVisible(false);
        root.getChildren().add(selectionMarker);

        enableDrag(center);
        enableDrag(tl);
        enableDrag(tr);
        enableDrag(bl);
        enableDrag(br);
    }

    private void updateInitialPositions() {
        if (!dragStarted) {
            double rootW = root.getWidth();
            double rootH = root.getHeight();

            center.setLayoutX((rootW - center.getWidth()) / 2);
            center.setLayoutY((rootH - center.getHeight()) / 2);

            tl.setLayoutX(0);
            tl.setLayoutY(0);

            tr.setLayoutX(rootW - tr.getWidth());
            tr.setLayoutY(0);

            bl.setLayoutX(0);
            bl.setLayoutY(rootH - bl.getHeight());

            br.setLayoutX(rootW - br.getWidth());
            br.setLayoutY(rootH - br.getHeight());
        }
    }

    private void setupColor(Rectangle rect) {
        rect.setOnMouseClicked(e -> {
            Color baseColor = (Color) rect.getFill();

            if (e.getButton() == MouseButton.PRIMARY) {
                center.setFill(baseColor);
            }

            if (e.getButton() == MouseButton.SECONDARY) {
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

    private void enableDrag(Shape shape) {
        final Delta dragDelta = new Delta();

        shape.setOnMousePressed(e -> {
            dragStarted = true;
            selectShape(shape);
            dragDelta.x = e.getSceneX() - shape.getLayoutX();
            dragDelta.y = e.getSceneY() - shape.getLayoutY();
            e.consume();
        });

        shape.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - dragDelta.x;
            double newY = e.getSceneY() - dragDelta.y;

            double maxX = root.getWidth() - shape.getBoundsInLocal().getWidth();
            double maxY = root.getHeight() - shape.getBoundsInLocal().getHeight();
            newX = Math.max(0, Math.min(newX, maxX));
            newY = Math.max(0, Math.min(newY, maxY));

            shape.setLayoutX(newX);
            shape.setLayoutY(newY);

            updateSelectionMarker();
            e.consume();
        });
    }

    private void selectShape(Shape shape) {
        selected = shape;
        updateSelectionMarker();
        startRotation(shape);
    }

    private void updateSelectionMarker() {
        if (selected != null) {
            selectionMarker.setVisible(true);
            double centerX = selected.getLayoutX() + selected.getBoundsInLocal().getWidth() / 2;
            double centerY = selected.getLayoutY() + selected.getBoundsInLocal().getHeight() / 2;
            selectionMarker.setLayoutX(centerX);
            selectionMarker.setLayoutY(centerY);
        } else {
            selectionMarker.setVisible(false);
        }
    }

    private void startRotation(Shape shape) {
        if (rotateTransition != null) rotateTransition.stop();

        rotateTransition = new RotateTransition(Duration.seconds(3), shape);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.play();
    }

    @FXML
    private void exitApp() {
        Platform.exit();
    }

    private static class Delta { double x, y; }
}