package com.example.demo.lab4;

import com.example.demo.lab3.FunctionPlotView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Step3TableController implements WindowNavigator.StateAware {

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
    }

    @FXML
    private void onNext() {
        WindowNavigator.open(stage, "step4-plot.fxml", "Крок 4: Графік", state, 950, 650);
    }

    // lab 5

    @FXML
    private void onNew() {
        AppState fresh = new AppState();
        WindowNavigator.open(stage, "step1-function.fxml", "Крок 1: Вибір функції", fresh, 600, 300);
    }

    @FXML
    private void onOpen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open table");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All supported (*.txt, *.csv, *.json)", "*.txt", "*.csv", "*.json"),
                new FileChooser.ExtensionFilter("Text (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("JSON (*.json)", "*.json")
        );

        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        try {
            List<DiagramRow> loaded = TableImportIO.openTable(file);

            rows.setAll(loaded);

            state.getRows().clear();
            state.getRows().addAll(loaded);

            state.getPlotPoints().clear();

            state.setCurrentFile(file);
            state.setDirty(false);

        } catch (Exception e) {
            showError("Open error", e.getMessage());
        }
    }

    @FXML
    private void onSave() {
        if (rows.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Таблиця порожня — нічого зберігати.").showAndWait();
            return;
        }

        if (state.getCurrentFile() == null) {
            onSaveAs();
            return;
        }

        File file = state.getCurrentFile();
        String name = file.getName().toLowerCase();

        try {
            if (name.endsWith(".txt")) {
                TableExportIO.saveAsTxt(rows, file);

            } else if (name.endsWith(".json")) {
                TableExportIO.saveAsJson(rows, file);

            } else if (name.endsWith(".csv")) {
                TableExportIO.saveAsCsv(rows, file);

            } else {
                onSaveAs();
                return;
            }

            new Alert(Alert.AlertType.INFORMATION, "Збережено:\n" + file.getAbsolutePath()).showAndWait();

        } catch (Exception e) {
            showError("Save error", e.getMessage());
        }
    }

    @FXML
    private void onSaveAs() {
        if (rows.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Таблиця порожня — нічого зберігати.").showAndWait();
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save As...");

        FileChooser.ExtensionFilter txt = new FileChooser.ExtensionFilter("Text (*.txt)", "*.txt");
        FileChooser.ExtensionFilter json = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");

        fc.getExtensionFilters().addAll(txt, json, csv);
        fc.setSelectedExtensionFilter(txt);
        fc.setInitialFileName("table");

        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        FileChooser.ExtensionFilter chosen = fc.getSelectedExtensionFilter();

        try {
            if (chosen == txt) {
                file = ensureExt(file, ".txt");
                TableExportIO.saveAsTxt(rows, file);
            } else if (chosen == json) {
                file = ensureExt(file, ".json");
                TableExportIO.saveAsJson(rows, file);
            } else if (chosen == csv) {
                file = ensureExt(file, ".csv");
                TableExportIO.saveAsCsv(rows, file);
            } else {
                file = ensureExt(file, ".txt");
                TableExportIO.saveAsTxt(rows, file);
            }

            new Alert(Alert.AlertType.INFORMATION, "Збережено:\n" + file.getAbsolutePath()).showAndWait();

        } catch (Exception e) {
            showError("Save As error", e.getMessage());
        }
    }

    private static File ensureExt(File file, String ext) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(ext)) return file;
        return new File(file.getParentFile(), file.getName() + ext);
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void onPrintPdf() {
        if (table.getItems() == null || table.getItems().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Таблиця порожня — нічого друкувати.");
            a.showAndWait();
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showError("Print error", "PrinterJob недоступний.");
            return;
        }

        boolean proceed = job.showPrintDialog(stage);
        if (!proceed) return;

        boolean success = job.printPage(table);
        if (success) {
            job.endJob();
        } else {
            showError("Print error", "Не вдалося надрукувати сторінку.");
        }
    }
}