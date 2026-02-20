package com.example.demo.lab4;

import com.example.demo.lab3.FunctionPlotView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Step3TableController implements WindowNavigator.StateAware {

    @FXML private Label infoLabel;

    @FXML private TableView<DiagramRow> table;
    @FXML private TableColumn<DiagramRow, Integer> colI;
    @FXML private TableColumn<DiagramRow, String> colX;
    @FXML private TableColumn<DiagramRow, String> colY;
    @FXML private TableColumn<DiagramRow, String> colStatus;

    private final ObservableList<DiagramRow> rows = FXCollections.observableArrayList();

    private AppState state;
    private Stage stage;

    @Override public void setState(AppState state) {
        this.state = state;
        if (table != null) computeAndFill();
    }
    @Override public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    public void initialize() {
        colI.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().i()).asObject());
        colX.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().x()));
        colY.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().y()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().status()));
        table.setItems(rows);
    }

    private void computeAndFill() {
        rows.clear();
        state.clearResults();

        double a = state.getA();
        double b = state.getB();
        int n = state.getN();
        double h = (b - a) / (n - 1);

        List<String> problems = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double x = a + i * h;

            try {
                double y = (state.getFunctionType() == AppState.FunctionType.F1)
                        ? DiagramMath.computeF1(x)
                        : DiagramMath.computeF2(x);

                if (!Double.isFinite(y)) {
                    rows.add(new DiagramRow(i, DiagramMath.format(x), "—", "Розрив/переповнення"));
                    state.getPlotPoints().add(new FunctionPlotView.PlotPoint(x, null));
                } else {
                    rows.add(new DiagramRow(i, DiagramMath.format(x), DiagramMath.format(y), "OK"));
                    state.getPlotPoints().add(new FunctionPlotView.PlotPoint(x, y));
                }

            } catch (Exception ex) {
                problems.add("i=" + i + ", x=" + DiagramMath.format(x) + " → помилка");
                rows.add(new DiagramRow(i, DiagramMath.format(x), "—", "Помилка"));
                state.getPlotPoints().add(new FunctionPlotView.PlotPoint(x, null));
            }
        }

        state.getRows().addAll(rows);

        String funcText = (state.getFunctionType() == AppState.FunctionType.F1)
                ? "F1: (2^(x+1)+10)/4 + 9/2^(145x−2)"
                : "F2: 2·sin(x) − cos(x^4)";
        infoLabel.setText("Функція: " + funcText + " | Інтервал [" +
                DiagramMath.format(a) + "; " + DiagramMath.format(b) + "], n=" + n +
                ", h=" + DiagramMath.format(h) +
                (problems.isEmpty() ? "" : " | Є проблемні точки"));
    }

    @FXML
    private void onNext() {
        WindowNavigator.open(stage, "step4-plot.fxml", "Крок 4: Графік", state, 950, 650);
    }
}
