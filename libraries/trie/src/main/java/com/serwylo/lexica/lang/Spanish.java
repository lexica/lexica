package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Spanish extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    // These letter values are drawn from the Scrabble point values:
    // https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Spanish
    static {
        letterPoints.put("a", 1);
        letterPoints.put("á", 1);
        letterPoints.put("e", 1);
        letterPoints.put("é", 1);
        letterPoints.put("o", 1);
        letterPoints.put("ó", 1);
        letterPoints.put("i", 1);
        letterPoints.put("í", 1);
        letterPoints.put("n", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("u", 1);
        letterPoints.put("ú", 1);
        letterPoints.put("ü", 1);
        letterPoints.put("l", 1);
        letterPoints.put("t", 1);

        letterPoints.put("d", 2);
        letterPoints.put("g", 2);

        letterPoints.put("c", 3);
        letterPoints.put("b", 3);
        letterPoints.put("m", 3);
        letterPoints.put("p", 3);

        letterPoints.put("h", 4);
        letterPoints.put("f", 4);
        letterPoints.put("v", 4);
        letterPoints.put("y", 4);

        letterPoints.put("q", 5);

        letterPoints.put("j", 8);
        letterPoints.put("ñ", 8);
        letterPoints.put("x", 1);

        letterPoints.put("z", 1);

        // Left out of Scrabble scoring, so look at the probabilities used in this game
        // (i.e. letters_es.txt) and find other letters which have similar frequencies. Base
        // the scores on that.
        letterPoints.put("k", 2);
        letterPoints.put("w", 2);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("es");
    }

    @Override
    public String getName() {
        return "es";
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
