package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Turkish extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("a", 1);
        letterPoints.put("e", 1);
        letterPoints.put("i", 1);
        letterPoints.put("k", 1);
        letterPoints.put("l", 1);
        letterPoints.put("r", 1);
        letterPoints.put("n", 1);
        letterPoints.put("t", 1);

        letterPoints.put("ı", 2);
        letterPoints.put("m", 2);
        letterPoints.put("o", 2);
        letterPoints.put("s", 2);
        letterPoints.put("u", 2);

        letterPoints.put("b", 3);
        letterPoints.put("d", 3);
        letterPoints.put("ü", 3);
        letterPoints.put("y", 3);

        letterPoints.put("c", 4);
        letterPoints.put("ç", 4);
        letterPoints.put("ş", 4);
        letterPoints.put("z", 4);

        letterPoints.put("g", 5);
        letterPoints.put("h", 5);
        letterPoints.put("p", 5);

        letterPoints.put("f", 7);
        letterPoints.put("ö", 7);
        letterPoints.put("v", 7);

        letterPoints.put("ğ", 8);

        letterPoints.put("j", 10);
    }

    @Override
    public boolean isBeta() {
        // Although dictionary is from ASpell, which historically has hits-and-misses with its
        // word lists, this was discussed with a native speaker at
        // https://github.com/lexica/lexica/issues/342 prior to merging.
        return false;
    }

    @Override
    public Locale getLocale() {
        return new Locale("tr");
    }

    @Override
    public String getName() {
        return "tr";
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
        return getWiktionaryDefinitionUrl("tr");
    }
}
