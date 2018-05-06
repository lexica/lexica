package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class English extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("a", 1);
        letterPoints.put("b", 3);
        letterPoints.put("c", 3);
        letterPoints.put("d", 2);
        letterPoints.put("e", 1);
        letterPoints.put("f", 4);
        letterPoints.put("g", 2);
        letterPoints.put("h", 4);
        letterPoints.put("i", 1);
        letterPoints.put("j", 8);
        letterPoints.put("k", 5);
        letterPoints.put("l", 1);
        letterPoints.put("m", 3);
        letterPoints.put("n", 1);
        letterPoints.put("o", 1);
        letterPoints.put("p", 3);
        letterPoints.put("q", 5); // TODO: q or qu?
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("t", 1);
        letterPoints.put("u", 1);
        letterPoints.put("v", 4);
        letterPoints.put("w", 4);
        letterPoints.put("x", 8);
        letterPoints.put("y", 4);
        letterPoints.put("z", 10);
    }

    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public String toDisplay(String value) {
        if (value.equals("qu")) {
            return "Qu";
        }

        return value.toUpperCase(getLocale());
    }

    @Override
    public String applyMandatorySuffix(String value) {
        if (value.equals("q")) {
            return "qu";
        }

        return value;
    }

    @Override
    protected Map<String, Integer> getLetterPoints() {
        return letterPoints;
    }
}
