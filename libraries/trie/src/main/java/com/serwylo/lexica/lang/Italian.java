package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Italian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("o", 1);
        letterPoints.put("ò", 1);
        letterPoints.put("à", 1);
        letterPoints.put("a", 1);
        letterPoints.put("i", 1);
        letterPoints.put("ì", 1);
        letterPoints.put("e", 1);
        letterPoints.put("è", 1);
        letterPoints.put("é", 1);

        letterPoints.put("c", 2);
        letterPoints.put("r", 2);
        letterPoints.put("s", 2);
        letterPoints.put("t", 2);

        letterPoints.put("l", 3);
        letterPoints.put("m", 3);
        letterPoints.put("n", 3);
        letterPoints.put("u", 3);
        letterPoints.put("ù", 3);

        letterPoints.put("d", 5);
        letterPoints.put("b", 5);
        letterPoints.put("f", 5);
        letterPoints.put("p", 5);
        letterPoints.put("v", 5);

        letterPoints.put("g", 8);
        letterPoints.put("h", 8);
        letterPoints.put("z", 8);

        // "q" is normally 10, but we always have a "u" next to the "q", so it isn't as difficult
        // to incorporate into words when present.
        letterPoints.put("qu", 5);

        // According to https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Italian:
        //   "The letters J, K, W, X, and Y are absent since these letters do not exist in Italian
        //    and are only used in loanwords"
        letterPoints.put("j", 1);
        letterPoints.put("k", 1);
        letterPoints.put("w", 1);
        letterPoints.put("x", 1);
        letterPoints.put("y", 1);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return Locale.ITALIAN;
    }

    @Override
    public String getName() {
        return "it";
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

    @Override
    public String getDefinitionUrl() {
        return getWiktionaryDefinitionUrl("it");
    }
}
