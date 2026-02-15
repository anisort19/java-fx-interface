package com.example.demo.lab1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.text.DecimalFormat;

public class FunctionController {

    @FXML
    private Spinner<Double> xSpinner;

    @FXML private RadioButton f1Radio;
    @FXML private RadioButton f2Radio;

    @FXML private Label f1Result;
    @FXML private Label f2Result;

    @FXML private ToggleGroup functionsGroup;

    private final DecimalFormat df = new DecimalFormat("#.###########");

    @FXML
    public void initialize() {

        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        -1000000, 1000000, 0.0, 0.1);

        valueFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value == null ? "" : df.format(value);
            }

            @Override
            public Double fromString(String string) {
                if (string == null || string.isBlank()) return 0.0;
                return Double.parseDouble(string.replace(",", "."));
            }
        });

        xSpinner.setValueFactory(valueFactory);
        xSpinner.setEditable(true);
    }

    @FXML
    private void onCalculate() {

        double x = xSpinner.getValue();

        Toggle selected = functionsGroup.getSelectedToggle();

        if (selected == null) {
            showError();
            return;
        }

        if (selected == f1Radio) {

            String func = "y = (2^(x+1) + 10) / 4 + 9 / 2^(145x − 2)";
            double y = computeF1(x);

            f1Result.setText("Результат: y = " +
                    df.format(y) + " (x = " + df.format(x) + ")");

            showInfo(func, x, y);

        } else if (selected == f2Radio) {

            String func = "y = 2·sin(x) − cos(x^4)";
            double y = computeF2(x);

            f2Result.setText("Результат: y = " +
                    df.format(y) + " (x = " + df.format(x) + ")");

            showInfo(func, x, y);
        }
    }

    private double computeF1(double x) {
        double part1 = (Math.pow(2, x + 1) + 10) / 4.0;
        double denom = Math.pow(2, 145 * x - 2);
        return part1 + 9.0 / denom;
    }

    private double computeF2(double x) {
        return 2.0 * Math.sin(x) - Math.cos(Math.pow(x, 4));
    }

    private void showInfo(String func, double x, double y) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Результат");
        alert.setHeaderText("Обчислення виконано");
        alert.setContentText(
                "Функція: " + func +
                        "\nТочка: x = " + df.format(x) +
                        "\nРезультат: y = " + df.format(y));
        alert.showAndWait();
    }

    private void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText("Оберіть функцію.");
        alert.showAndWait();
    }
}
