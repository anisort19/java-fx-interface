package com.example.demo.lab3;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.List;

public class FunctionPlotView extends Region {

    public enum LineStyle {
        SOLID("Суцільна"),
        DASHED("Штрихова"),
        DOTTED("Пунктир");

        private final String title;
        LineStyle(String title) { this.title = title; }
        @Override public String toString() { return title; }
    }

    public record PlotPoint(double x, Double y) {}

    private final Canvas canvas = new Canvas();

    private final ObjectProperty<Color> lineColor = new SimpleObjectProperty<>(Color.DODGERBLUE);
    private final DoubleProperty lineWidth = new SimpleDoubleProperty(2.0);
    private final ObjectProperty<LineStyle> lineStyle = new SimpleObjectProperty<>(LineStyle.SOLID);

    private final ObservableList<PlotPoint> points = FXCollections.observableArrayList();

    public FunctionPlotView() {
        getChildren().add(canvas);

        widthProperty().addListener((o, a, b) -> redraw());
        heightProperty().addListener((o, a, b) -> redraw());

        lineColor.addListener((o, a, b) -> redraw());
        lineWidth.addListener((o, a, b) -> redraw());
        lineStyle.addListener((o, a, b) -> redraw());
        points.addListener((ListChangeListener<PlotPoint>) change -> redraw());
    }

    public ObjectProperty<Color> lineColorProperty() { return lineColor; }
    public DoubleProperty lineWidthProperty() { return lineWidth; }
    public ObjectProperty<LineStyle> lineStyleProperty() { return lineStyle; }

    public void setPoints(List<PlotPoint> newPoints) {
        points.setAll(newPoints);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        canvas.setWidth(Math.max(0, w));
        canvas.setHeight(Math.max(0, h));
        redraw();
    }

    private void redraw() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 2 || h <= 2) return;

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, w, h);

        g.setStroke(Color.LIGHTGRAY);
        g.setLineWidth(1);
        g.strokeRect(0.5, 0.5, w - 1, h - 1);

        if (points.isEmpty()) {
            drawCenteredText(g, h, "Немає даних для побудови графіка");
            return;
        }

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        int validCount = 0;

        for (PlotPoint p : points) {
            minX = Math.min(minX, p.x());
            maxX = Math.max(maxX, p.x());
            if (p.y() != null && Double.isFinite(p.y())) {
                validCount++;
                minY = Math.min(minY, p.y());
                maxY = Math.max(maxY, p.y());
            }
        }

        if (validCount == 0 || !Double.isFinite(minX) || !Double.isFinite(maxX)) {
            drawCenteredText(g, h, "Усі точки невалідні (розрив/переповнення)");
            return;
        }

        if (Math.abs(maxY - minY) < 1e-12) {
            maxY = minY + 1.0;
        }
        if (Math.abs(maxX - minX) < 1e-12) {
            maxX = minX + 1.0;
        }

        double padL = 50, padR = 20, padT = 20, padB = 40;
        double plotW = w - padL - padR;
        double plotH = h - padT - padB;

        g.setStroke(Color.rgb(220, 220, 220));
        g.setLineWidth(1);
        int grid = 10;
        for (int i = 0; i <= grid; i++) {
            double x = padL + plotW * i / grid;
            double y = padT + plotH * i / grid;
            g.strokeLine(x, padT, x, padT + plotH);
            g.strokeLine(padL, y, padL + plotW, y);
        }

        g.setStroke(Color.GRAY);
        g.setLineWidth(1.5);

        if (minY <= 0 && 0 <= maxY) {
            double y0 = mapY(0, minY, maxY, padT, plotH);
            g.strokeLine(padL, y0, padL + plotW, y0);
        }
        if (minX <= 0 && 0 <= maxX) {
            double x0 = mapX(0, minX, maxX, padL, plotW);
            g.strokeLine(x0, padT, x0, padT + plotH);
        }

        g.setFill(Color.DIMGRAY);
        g.fillText(String.format("x: [%.2f; %.2f]", minX, maxX), padL, h - 12);
        g.fillText(String.format("y: [%.2f; %.2f]", minY, maxY), 10, 20);

        g.setStroke(lineColor.get());
        g.setLineWidth(Math.max(0.5, lineWidth.get()));
        applyDash(g, lineStyle.get());

        boolean hasStarted = false;

        for (PlotPoint p : points) {
            Double yVal = p.y();
            if (yVal == null || !Double.isFinite(yVal)) {

                double sx = mapX(p.x(), minX, maxX, padL, plotW);
                double sy = padT + plotH / 2.0;

                g.setFill(Color.RED);
                g.fillOval(sx - 6, sy - 6, 12, 12);

                hasStarted = false;
                continue;
            }
            double sx = mapX(p.x(), minX, maxX, padL, plotW);
            double sy = mapY(yVal, minY, maxY, padT, plotH);

            if (!hasStarted) {
                g.beginPath();
                g.moveTo(sx, sy);
                hasStarted = true;
            } else {
                g.lineTo(sx, sy);
            }
        }
        if (hasStarted) g.stroke();
        g.setLineDashes((double[]) null);
    }

    private static double mapX(double x, double minX, double maxX, double padL, double plotW) {
        return padL + (x - minX) / (maxX - minX) * plotW;
    }

    private static double mapY(double y, double minY, double maxY, double padT, double plotH) {
        return padT + (maxY - y) / (maxY - minY) * plotH;
    }

    private static void applyDash(GraphicsContext g, LineStyle style) {
        switch (style) {
            case SOLID -> g.setLineDashes((double[]) null);
            case DASHED -> g.setLineDashes(12, 8);
            case DOTTED -> g.setLineDashes(2, 8);
        }
    }

    private static void drawCenteredText(GraphicsContext g, double h, String text) {
        g.setFill(Color.GRAY);
        g.fillText(text, 20, h / 2);
    }
}
