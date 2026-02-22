package com.example.demo.lab4;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TableExportIO {

    public static void saveAsTxt(List<DiagramRow> rows, File file) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            w.write("i\tx\ty\tstatus\n");
            for (DiagramRow r : rows) {
                w.write(r.i() + "\t" + safe(r.x()) + "\t" + safe(r.y()) + "\t" + safe(r.status()) + "\n");
            }
        }
    }

    public static void saveAsCsv(List<DiagramRow> rows, File file) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            w.write("i,x,y,status\n");
            for (DiagramRow r : rows) {
                w.write(r.i() + "," + csv(r.x()) + "," + csv(r.y()) + "," + csv(r.status()) + "\n");
            }
        }
    }

    public static void saveAsJson(List<DiagramRow> rows, File file) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            w.write("{\"rows\":[");
            for (int i = 0; i < rows.size(); i++) {
                DiagramRow r = rows.get(i);
                w.write("{");
                w.write("\"i\":" + r.i() + ",");
                w.write("\"x\":" + json(r.x()) + ",");
                w.write("\"y\":" + json(r.y()) + ",");
                w.write("\"status\":" + json(r.status()));
                w.write("}");
                if (i < rows.size() - 1) w.write(",");
            }
            w.write("]}");
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String csv(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v + "\"";
        }
        return v;
    }

    private static String json(String s) {
        if (s == null) return "null";
        String esc = s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + esc + "\"";
    }
}
