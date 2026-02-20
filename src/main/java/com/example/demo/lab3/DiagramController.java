package com.example.demo.lab3;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DiagramController {

    @FXML private RadioButton f1Radio;
    @FXML private RadioButton f2Radio;
    @FXML private ToggleGroup functionsGroup;

    @FXML private Spinner<Double> aSpinner;
    @FXML private Spinner<Double> bSpinner;
    @FXML private Spinner<Integer> nSpinner;

    @FXML private Label f1Result;
    @FXML private Label f2Result;

    @FXML private TableView<Row> table;
    @FXML private TableColumn<Row, Integer> colI;
    @FXML private TableColumn<Row, String> colX;
    @FXML private TableColumn<Row, String> colY;
    @FXML private TableColumn<Row, String> colStatus;

    @FXML private FunctionPlotView plotView;
    @FXML private ColorPicker lineColorPicker;
    @FXML private Slider lineWidthSlider;
    @FXML private Label lineWidthLabel;
    @FXML private ChoiceBox<FunctionPlotView.LineStyle> lineStyleChoice;

    private final DecimalFormat df = new DecimalFormat("#.###########");
    private final ObservableList<Row> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colI.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().i()).asObject());
        colX.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().x()));
        colY.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().y()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().status()));
        table.setItems(rows);

        aSpinner.setValueFactory(doubleFactory(0.0));
        bSpinner.setValueFactory(doubleFactory(1.0));
        aSpinner.setEditable(true);
        bSpinner.setEditable(true);

        nSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 1_000_000, 11, 1));
        nSpinner.setEditable(true);

        lineStyleChoice.setItems(FXCollections.observableArrayList(FunctionPlotView.LineStyle.values()));
        lineStyleChoice.setValue(FunctionPlotView.LineStyle.SOLID);

        lineColorPicker.setValue(Color.DODGERBLUE);
        plotView.lineColorProperty().bind(lineColorPicker.valueProperty());

        plotView.lineWidthProperty().bind(lineWidthSlider.valueProperty());
        lineWidthLabel.textProperty().bind(lineWidthSlider.valueProperty().asString("%.1f"));

        plotView.lineStyleProperty().bind(lineStyleChoice.valueProperty());
    }

    private SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory(double initial) {
        SpinnerValueFactory.DoubleSpinnerValueFactory vf =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-1_000_000, 1_000_000, initial, 0.1);

        vf.setConverter(new StringConverter<>() {
            @Override public String toString(Double value) {
                return value == null ? "" : df.format(value);
            }
            @Override public Double fromString(String string) {
                if (string == null || string.isBlank()) return 0.0;
                return Double.parseDouble(string.replace(",", "."));
            }
        });

        return vf;
    }

    @FXML
    private void onCalculate() {
        rows.clear();

        double a, b;
        int n;
        try {
            a = aSpinner.getValue();
            b = bSpinner.getValue();
            n = nSpinner.getValue();
        } catch (Exception ex) {
            showError("Помилка вводу", "Перевірте введені значення a, b, n.");
            return;
        }

        if (!(a < b)) {
            showError("Некоректні дані", "Потрібно: a < b (ліва границя менша за праву).");
            return;
        }
        if (n < 2) {
            showError("Некоректні дані", "Кількість точок n має бути ≥ 2.");
            return;
        }

        Toggle selected = functionsGroup.getSelectedToggle();
        if (selected == null) {
            showError("Помилка", "Оберіть функцію.");
            return;
        }

        double h = (b - a) / (n - 1);

        String funcText = (selected == f1Radio)
                ? "y = (2^(x+1) + 10) / 4 + 9 / 2^(145x − 2)"
                : "y = 2·sin(x) − cos(x^4)";

        List<String> problems = new ArrayList<>();
        List<FunctionPlotView.PlotPoint> plotPoints = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double x = a + i * h;

            try {
                double y = (selected == f1Radio) ? computeF1(x) : computeF2(x);

                if (!Double.isFinite(y)) {
                    rows.add(new Row(i, df.format(x), "—", "Розрив/переповнення"));
                    plotPoints.add(new FunctionPlotView.PlotPoint(x, null));
                } else {
                    rows.add(new Row(i, df.format(x), df.format(y), "OK"));
                    plotPoints.add(new FunctionPlotView.PlotPoint(x, y));
                }

            } catch (Exception ex) {
                problems.add("i=" + i + ", x=" + df.format(x) + " → помилка обчислення");
                rows.add(new Row(i, df.format(x), "—", "Помилка"));
                plotPoints.add(new FunctionPlotView.PlotPoint(x, null));
            }
        }

        String inline = "Інтервал [" + df.format(a) + "; " + df.format(b) + "], n=" + n + ", h=" + df.format(h);
        if (selected == f1Radio) f1Result.setText("Параметри: " + inline);
        else f2Result.setText("Параметри: " + inline);

        plotView.setPoints(plotPoints);

        if (!problems.isEmpty()) {
            showInfo("Попередження",
                    "Функція: " + funcText +
                            "\nІнтервал: [" + df.format(a) + "; " + df.format(b) + "], n=" + n +
                            "\nПроблемні точки:\n- " + String.join("\n- ", problems));
        } else {
            showInfo("Готово",
                    "Функція: " + funcText +
                            "\nІнтервал: [" + df.format(a) + "; " + df.format(b) + "], n=" + n +
                            "\nТаблицю заповнено без помилок, графік побудовано.");
        }
    }

    private double computeF1(double x) {
        double part1 = (Math.pow(2, (x + 1)) + 10) / 4.0;
        double denom = Math.pow(2, ((145 * x) - 2));
        return part1 + (9.0 / denom);
    }

    private double computeF2(double x) {
        return 2.0 * Math.sin(x) - Math.cos(Math.pow(x, 4));
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public record Row(Integer i, String x, String y, String status) {}
}