package com.example.demo.lab4;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.stage.Stage;

import java.text.DecimalFormat;

public class Step2ParamsController implements WindowNavigator.StateAware {

    @FXML private Spinner<Double> aSpinner;
    @FXML private Spinner<Double> bSpinner;
    @FXML private Spinner<Integer> nSpinner;

    private final DecimalFormat df = new DecimalFormat("#.###########");
    private AppState state;
    private Stage stage;

    @Override public void setState(AppState state) {
        this.state = state;
        if (aSpinner != null) {
            aSpinner.getValueFactory().setValue(state.getA());
            bSpinner.getValueFactory().setValue(state.getB());
            nSpinner.getValueFactory().setValue(state.getN());
        }
    }
    @Override public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    public void initialize() {
        aSpinner.setValueFactory(doubleFactory(0.0));
        bSpinner.setValueFactory(doubleFactory(1.0));
        aSpinner.setEditable(true);
        bSpinner.setEditable(true);

        nSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 1_000_000, 11, 1));
        nSpinner.setEditable(true);
    }

    private SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory(double initial) {
        SpinnerValueFactory.DoubleSpinnerValueFactory vf =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-1_000_000, 1_000_000, initial, 0.1);

        vf.setConverter(new StringConverter<>() {
            @Override public String toString(Double value) { return value == null ? "" : df.format(value); }
            @Override public Double fromString(String s) {
                if (s == null || s.isBlank()) return 0.0;
                return Double.parseDouble(s.replace(",", "."));
            }
        });
        return vf;
    }

    @FXML
    private void onNext() {
        double a, b; int n;
        try {
            a = aSpinner.getValue();
            b = bSpinner.getValue();
            n = nSpinner.getValue();
        } catch (Exception ex) {
            alert("Помилка вводу", "Перевірте a, b, n.");
            return;
        }

        if (!(a < b)) {
            alert("Некоректні дані", "Потрібно: a < b.");
            return;
        }
        if (n < 2) {
            alert("Некоректні дані", "n має бути ≥ 2.");
            return;
        }

        state.setA(a);
        state.setB(b);
        state.setN(n);
        state.clearResults();

        WindowNavigator.open(stage, "step3-table.fxml", "Крок 3: Таблиця", state, 900, 600);
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
