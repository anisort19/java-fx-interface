package com.example.demo.lab4;

import com.example.demo.lab3.FunctionPlotView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppState {

    public enum FunctionType { F1, F2 }

    private FunctionType functionType = FunctionType.F1;

    private double a = 0.0;
    private double b = 1.0;
    private int n = 11;

    private final List<DiagramRow> rows = new ArrayList<>();
    private final List<FunctionPlotView.PlotPoint> plotPoints = new ArrayList<>();

    public FunctionType getFunctionType() { return functionType; }
    public void setFunctionType(FunctionType functionType) { this.functionType = functionType; }

    public double getA() { return a; }
    public void setA(double a) { this.a = a; }

    public double getB() { return b; }
    public void setB(double b) { this.b = b; }

    public int getN() { return n; }
    public void setN(int n) { this.n = n; }

    public List<DiagramRow> getRows() { return rows; }
    public List<FunctionPlotView.PlotPoint> getPlotPoints() { return plotPoints; }

    public void clearResults() {
        rows.clear();
        plotPoints.clear();
    }

    // lab 5
    private File currentFile;
    private boolean dirty = false;

    public File getCurrentFile() { return currentFile; }
    public void setCurrentFile(File f) { this.currentFile = f; }

    public boolean isDirty() { return dirty; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
}
