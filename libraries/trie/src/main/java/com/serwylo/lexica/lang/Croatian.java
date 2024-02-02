package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Croatian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {

        // https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Croatian

        letterPoints.put("a", 1);
        letterPoints.put("i", 1);
        letterPoints.put("e", 1);
        letterPoints.put("o", 1);
        letterPoints.put("n", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("t", 1);
        letterPoints.put("j", 1);
        letterPoints.put("u", 1);

        letterPoints.put("k", 2);
        letterPoints.put("m", 2);
        letterPoints.put("p", 2);
        letterPoints.put("v", 2);

        letterPoints.put("d", 3);
        letterPoints.put("g", 3);
        letterPoints.put("l", 3);
        letterPoints.put("z", 3);
        letterPoints.put("b", 3);
        letterPoints.put("č", 3);

        letterPoints.put("c", 4);
        letterPoints.put("h", 4);
        letterPoints.put("ǉ", 4);
        letterPoints.put("ǌ", 4);
        letterPoints.put("š", 4);
        letterPoints.put("ž", 4);

        letterPoints.put("ć", 5);

        letterPoints.put("f", 8);

        letterPoints.put("đ", 10);
        letterPoints.put("ǆ", 10);

    }

    // TODO: Decide whether to mark as beta or not.
    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("hr");
    }

    @Override
    public String getName() {
        return "hr_HR";
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
        return getWiktionaryDefinitionUrl("hr");
    }
}
