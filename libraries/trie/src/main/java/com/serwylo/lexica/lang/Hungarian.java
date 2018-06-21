package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Hungarian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("a", 1);
        letterPoints.put("e", 1);
        letterPoints.put("l", 1);
        letterPoints.put("n", 1);
        letterPoints.put("o", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("t", 1);

        letterPoints.put("á", 2);
        letterPoints.put("é", 2);
        letterPoints.put("g", 2);
        letterPoints.put("i", 2);
        letterPoints.put("k", 2);
        letterPoints.put("m", 2);
        letterPoints.put("v", 2);
        letterPoints.put("z", 2);

        letterPoints.put("b", 3);
        letterPoints.put("d", 3);
        letterPoints.put("h", 3);
        letterPoints.put("j", 3);
        letterPoints.put("p", 3);
        letterPoints.put("u", 3);
        letterPoints.put("y", 3);

        letterPoints.put("c", 5);
        letterPoints.put("f", 5);
        letterPoints.put("í", 5);
        letterPoints.put("ó", 5);
        letterPoints.put("ö", 5);
        letterPoints.put("ü", 5);

        letterPoints.put("ő", 8);
        letterPoints.put("ú", 8);
        letterPoints.put("ű", 8);

        letterPoints.put("q", 10);
        letterPoints.put("w", 10);
        letterPoints.put("x", 10);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("hu");
    }

    @Override
    public String getName() {
        return "hu";
    }

    @Override
    public String toDisplay(String value) {
        return value.toUpperCase(getLocale());
    }

    @Override
    public String applyMandatorySuffix(String value) {
        return value;
    }

    @Override
    protected Map<String, Integer> getLetterPoints() {
        return letterPoints;
    }
}
