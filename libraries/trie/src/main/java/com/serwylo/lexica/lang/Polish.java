package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Polish extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {

        // https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Polish

        letterPoints.put("a", 1);
        letterPoints.put("e", 1);
        letterPoints.put("i", 1);
        letterPoints.put("n", 1);
        letterPoints.put("o", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("w", 1);
        letterPoints.put("z", 1);

        letterPoints.put("c", 2);
        letterPoints.put("d", 2);
        letterPoints.put("k", 2);
        letterPoints.put("l", 2);
        letterPoints.put("m", 2);
        letterPoints.put("p", 2);
        letterPoints.put("t", 2);
        letterPoints.put("y", 1);

        letterPoints.put("b", 3);
        letterPoints.put("g", 3);
        letterPoints.put("h", 3);
        letterPoints.put("j", 3);
        letterPoints.put("ł", 3);
        letterPoints.put("u", 3);

        letterPoints.put("ą", 5);
        letterPoints.put("ę", 5);
        letterPoints.put("f", 5);
        letterPoints.put("ó", 5);
        letterPoints.put("ś", 5);
        letterPoints.put("ź", 5);

        letterPoints.put("ć", 6);

        letterPoints.put("ń", 7);

        letterPoints.put("ż", 9);

    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("pl");
    }

    @Override
    public String getName() {
        return "pl";
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

    /**
     * The dictionary used for Polish is from https://sjp.pl, which also happens to include a
     * definition service. Therefore, it seems appropriate to use this over DuckDuckGo.
     */
    @Override
    public String getDefinitionUrl() {
        return "https://sjp.pl/%s";
    }
}
