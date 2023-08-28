package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpanishSoloEnne extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    // These letter values are drawn from the Scrabble point values:
    // https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Spanish
    static {
        letterPoints.put("a", 1);     
        letterPoints.put("e", 1);
        letterPoints.put("o", 1);
        letterPoints.put("i", 1);
        letterPoints.put("n", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("u", 1);
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
        // "q" is normally 10, but we always have a "u" next to the "q", so it isn't as difficult
        // to incorporate into words when present.
        letterPoints.put("qu", 5);
        letterPoints.put("j", 8);
        letterPoints.put("ñ", 8);
        letterPoints.put("x", 8); /* This score was wrong*/
        letterPoints.put("z", 10); /* This score was wrong */

        // TODO: These are not included in Scrabble.
        // Indeed, they are only used for loanwords.
        // Should probably look at the CrossWords app and see how they deal with these tiles.
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
        return "es_solo_enne";
    }

    @Override
    public String toDisplay(String value) {
        if (value.equals("qu")) {
            return "Qu";
        }

        return value.toUpperCase(getLocale());
    }

    @Override
    public String toRepresentation(String value) {
        return value;
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
        return getWiktionaryDefinitionUrl("es");
    }
}
