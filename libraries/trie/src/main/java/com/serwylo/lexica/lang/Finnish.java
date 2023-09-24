package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Finnish extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("s", 1);
        letterPoints.put("e", 1);
        letterPoints.put("n", 1);
        letterPoints.put("t", 1);
        letterPoints.put("a", 1);
        letterPoints.put("i", 1);

        letterPoints.put("k", 2);
        letterPoints.put("l", 2);
        letterPoints.put("o", 2);
        letterPoints.put("ä", 2);

        letterPoints.put("m", 3);
        letterPoints.put("u", 3);

        letterPoints.put("h", 4);
        letterPoints.put("j", 4);
        letterPoints.put("p", 4);
        letterPoints.put("r", 4);
        letterPoints.put("v", 4);
        letterPoints.put("y", 4);

        letterPoints.put("d", 7);
        letterPoints.put("ö", 7);
        
        letterPoints.put("b", 8);
        letterPoints.put("f", 8);
        letterPoints.put("g", 8);
        letterPoints.put("w", 8);

        letterPoints.put("c", 10);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("fi", "FI");
    }

    @Override
    public String getName() {
        return "fi";
    }

    @Override
    public String toDisplay(String value) {
        return value.toUpperCase(getLocale());
    }

    @Override
    public String toRepresentation(String value) {
        return value;
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
        return "https://www.kielitoimistonsanakirja.fi/%s";
    }
}
