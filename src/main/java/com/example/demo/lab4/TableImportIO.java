package com.example.demo.lab4;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableImportIO {

    public static List<DiagramRow> openTable(File file) throws IOException {
        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".txt")) return readTxt(file);
        if (name.endsWith(".csv")) return readCsv(file);
        if (name.endsWith(".json")) return readJson(file);

        return readTxt(file);
    }

    private static List<DiagramRow> readTxt(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        List<DiagramRow> out = new ArrayList<>();
        for (int idx = 0; idx < lines.size(); idx++) {
            String line = lines.get(idx).trim();
            if (line.isEmpty()) continue;

            if (idx == 0 && line.toLowerCase(Locale.ROOT).startsWith("i")) continue;

            String[] parts = line.split("\t", -1);
            if (parts.length < 4) continue;

            int i = parseIntSafe(parts[0]);
            String x = parts[1];
            String y = parts[2];
            String status = parts[3];

            out.add(new DiagramRow(i, x, y, status));
        }
        return out;
    }

    private static List<DiagramRow> readCsv(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        List<DiagramRow> out = new ArrayList<>();
        for (int idx = 0; idx < lines.size(); idx++) {
            String line = lines.get(idx).trim();
            if (line.isEmpty()) continue;

            if (idx == 0 && line.toLowerCase(Locale.ROOT).startsWith("i,")) continue;

            List<String> parts = parseCsvLine(line);
            if (parts.size() < 4) continue;

            int i = parseIntSafe(parts.get(0));
            String x = parts.get(1);
            String y = parts.get(2);
            String status = parts.get(3);

            out.add(new DiagramRow(i, x, y, status));
        }
        return out;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int k = 0; k < line.length(); k++) {
            char c = line.charAt(k);
            if (inQuotes) {
                if (c == '"') {
                    if (k + 1 < line.length() && line.charAt(k + 1) == '"') {
                        cur.append('"');
                        k++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == ',') {
                    res.add(cur.toString());
                    cur.setLength(0);
                } else if (c == '"') {
                    inQuotes = true;
                } else {
                    cur.append(c);
                }
            }
        }
        res.add(cur.toString());
        return res;
    }

    private static List<DiagramRow> readJson(File file) throws IOException {
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);

        List<DiagramRow> out = new ArrayList<>();

        Pattern rowPattern = Pattern.compile(
                "\\{\\s*\"i\"\\s*:\\s*(\\d+)\\s*,\\s*\"x\"\\s*:\\s*(\"(.*?)\"|null)\\s*,\\s*\"y\"\\s*:\\s*(\"(.*?)\"|null)\\s*,\\s*\"status\"\\s*:\\s*(\"(.*?)\"|null)\\s*}",
                Pattern.DOTALL
        );
        Matcher m = rowPattern.matcher(json);
        while (m.find()) {
            int i = Integer.parseInt(m.group(1));
            String x = unescapeJsonString(m.group(3));
            String y = unescapeJsonString(m.group(5));
            String status = unescapeJsonString(m.group(7));

            if (x == null) x = "";
            if (y == null) y = "";
            if (status == null) status = "";

            out.add(new DiagramRow(i, x, y, status));
        }

        return out;
    }

    private static String unescapeJsonString(String s) {
        if (s == null) return null;
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }
}
