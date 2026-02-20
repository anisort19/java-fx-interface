package com.example.demo.lab4;

import java.text.DecimalFormat;

public class DiagramMath {

    private static final DecimalFormat df = new DecimalFormat("#.###########");

    public static double computeF1(double x) {
        double part1 = (Math.pow(2, (x + 1)) + 10) / 4.0;
        double denom = Math.pow(2, ((145 * x) - 2));
        return part1 + (9.0 / denom);
    }

    public static double computeF2(double x) {
        return 2.0 * Math.sin(x) - Math.cos(Math.pow(x, 4));
    }

    public static String format(double v) {
        return df.format(v);
    }
}
