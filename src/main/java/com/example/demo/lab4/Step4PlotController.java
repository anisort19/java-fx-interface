package com.example.demo.lab4;

import com.example.demo.lab3.FunctionPlotView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class Step4PlotController implements WindowNavigator.StateAware {

    @FXML private FunctionPlotView plotView;
    @FXML private ColorPicker lineColorPicker;
    @FXML private Slider lineWidthSlider;
    @FXML private Label lineWidthLabel;
    @FXML private ChoiceBox<FunctionPlotView.LineStyle> lineStyleChoice;

    @Override public void setState(AppState state) {
        if (plotView != null) plotView.setPoints(state.getPlotPoints());
    }

    @FXML
    public void initialize() {
        lineStyleChoice.setItems(FXCollections.observableArrayList(FunctionPlotView.LineStyle.values()));
        lineStyleChoice.setValue(FunctionPlotView.LineStyle.SOLID);

        lineColorPicker.setValue(Color.DODGERBLUE);
        plotView.lineColorProperty().bind(lineColorPicker.valueProperty());

        plotView.lineWidthProperty().bind(lineWidthSlider.valueProperty());
        lineWidthLabel.textProperty().bind(lineWidthSlider.valueProperty().asString("%.1f"));

        plotView.lineStyleProperty().bind(lineStyleChoice.valueProperty());
    }
}
