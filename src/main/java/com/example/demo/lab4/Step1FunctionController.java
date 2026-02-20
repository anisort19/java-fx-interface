package com.example.demo.lab4;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class Step1FunctionController implements WindowNavigator.StateAware {

    @FXML private RadioButton f1Radio;
    @FXML private RadioButton f2Radio;
    @FXML private ToggleGroup functionsGroup;

    private AppState state;
    private Stage stage;

    @Override public void setState(AppState state) { this.state = state; }
    @Override public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    private void onNext() {
        state.setFunctionType(f1Radio.isSelected() ? AppState.FunctionType.F1 : AppState.FunctionType.F2);
        WindowNavigator.open(stage, "step2-params.fxml", "Крок 2: Параметри", state, 650, 350);
    }
}
