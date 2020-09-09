package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Dutch extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("à", 1);
        letterPoints.put("a", 1);
        letterPoints.put("ä", 1);
        letterPoints.put("e", 1);
        letterPoints.put("è", 1);
        letterPoints.put("i", 1);
        letterPoints.put("é", 1);
        letterPoints.put("ê", 1);
        letterPoints.put("ë", 1);
        letterPoints.put("n", 1);
        letterPoints.put("î", 1);
        letterPoints.put("o", 1);
        letterPoints.put("ï", 1);
        letterPoints.put("ñ", 1);
        letterPoints.put("ô", 1);
        letterPoints.put("ö", 1);

        letterPoints.put("d", 2);
        letterPoints.put("r", 2);
        letterPoints.put("s", 2);
        letterPoints.put("t", 2);

        letterPoints.put("g", 3);
        letterPoints.put("k", 3);
        letterPoints.put("l", 3);
        letterPoints.put("b", 3);
        letterPoints.put("m", 3);
        letterPoints.put("p", 3);

        letterPoints.put("f", 4);
        letterPoints.put("h", 4);
        letterPoints.put("u", 4);
        letterPoints.put("û", 4);
        letterPoints.put("ü", 4);
        letterPoints.put("j", 4);
        letterPoints.put("v", 4);
        letterPoints.put("z", 4);

        letterPoints.put("c", 5);
        letterPoints.put("ç", 5);
        letterPoints.put("w", 5);

        letterPoints.put("x", 8);
        letterPoints.put("y", 8);

        // "q" is normally 10, but we always have a "u" next to the "q", so it isn't as difficult
        // to incorporate into words when present.
        letterPoints.put("qu", 5);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("nl");
    }

    @Override
    public String getName() {
        return "nl";
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
        return getWiktionaryDefinitionUrl("nl");
    }
}
