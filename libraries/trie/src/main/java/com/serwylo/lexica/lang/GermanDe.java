package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GermanDe extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("e", 1);
        letterPoints.put("é", 1);
        letterPoints.put("n", 1);
        letterPoints.put("s", 1);
        letterPoints.put("i", 1);
        letterPoints.put("r", 1);
        letterPoints.put("t", 1);
        letterPoints.put("u", 1);
        letterPoints.put("a", 1);
        letterPoints.put("à", 1);
        letterPoints.put("d", 1);

        letterPoints.put("h", 2);
        letterPoints.put("g", 2);
        letterPoints.put("l", 2);
        letterPoints.put("o", 2);

        letterPoints.put("m", 3);
        letterPoints.put("b", 3);
        letterPoints.put("w", 3);
        letterPoints.put("z", 3);

        letterPoints.put("c", 4);
        letterPoints.put("f", 4);
        letterPoints.put("k", 4);
        letterPoints.put("p", 4);

        letterPoints.put("ä", 6);
        letterPoints.put("j", 6);
        letterPoints.put("ü", 6);
        letterPoints.put("v", 1);

        letterPoints.put("ö", 8);
        letterPoints.put("x", 8);

        letterPoints.put("y", 10);
        letterPoints.put("q", 10);

        // ß and œ are not used in Scrabble https://en.wikipedia.org/wiki/Scrabble_letter_distributions#German
        letterPoints.put("ß", 1);
        letterPoints.put("œ", 1);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return Locale.GERMAN;
    }

    @Override
    public String getName() {
        return "de_DE";
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
        return getWiktionaryDefinitionUrl("de");
    }
}
