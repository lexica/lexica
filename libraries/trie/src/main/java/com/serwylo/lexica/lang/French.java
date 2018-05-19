package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class French extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("à", 1);
        letterPoints.put("a", 1);
        letterPoints.put("â", 1);
        letterPoints.put("e", 1);
        letterPoints.put("è", 1);
        letterPoints.put("i", 1);
        letterPoints.put("é", 1);
        letterPoints.put("ê", 1);
        letterPoints.put("ë", 1);
        letterPoints.put("n", 1);
        letterPoints.put("î", 1);
        letterPoints.put("ï", 1);
        letterPoints.put("o", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("ô", 1);
        letterPoints.put("ö", 1);
        letterPoints.put("l", 1);
        letterPoints.put("t", 1);
        letterPoints.put("u", 1);
        letterPoints.put("û", 1);
        letterPoints.put("ü", 1);

        letterPoints.put("d", 2);
        letterPoints.put("g", 2);
        letterPoints.put("m", 2);

        letterPoints.put("b", 3);
        letterPoints.put("c", 3);
        letterPoints.put("ç", 3);
        letterPoints.put("p", 3);

        letterPoints.put("h", 4);
        letterPoints.put("f", 4);
        letterPoints.put("v", 4);

        letterPoints.put("j", 8);
        letterPoints.put("q", 8);

        letterPoints.put("k", 10);
        letterPoints.put("w", 10);
        letterPoints.put("x", 10);
        letterPoints.put("y", 10);
        letterPoints.put("z", 10);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return Locale.FRENCH;
    }

    @Override
    public String getName() {
        return "fr_FR";
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
