package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BretonNoDiacritics extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("-", 0);

        letterPoints.put("a", 1);
        letterPoints.put("e", 1);
        letterPoints.put("i", 1);
        letterPoints.put("l", 1);
        letterPoints.put("n", 1);
        letterPoints.put("o", 1);
        letterPoints.put("r", 1);
        letterPoints.put("t", 1);
        letterPoints.put("u", 1);

        letterPoints.put("d", 2);

        letterPoints.put("g", 3);
        letterPoints.put("h", 3);
        letterPoints.put("s", 3);
        letterPoints.put("v", 3);

        letterPoints.put("b", 4);
        letterPoints.put("c", 4);
        letterPoints.put("k", 4);
        letterPoints.put("m", 4);
        letterPoints.put("q", 4);
        letterPoints.put("z", 4);

        letterPoints.put("p", 5);

        letterPoints.put("f", 10);
        letterPoints.put("j", 10);
        letterPoints.put("w", 10);
        letterPoints.put("y", 10);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("br");
    }

    @Override
    public String getName() {
        return "br_no_diacritics";
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

    @Override
    public String getDefinitionUrl() {
        return getWiktionaryDefinitionUrl("br");
    }
}
