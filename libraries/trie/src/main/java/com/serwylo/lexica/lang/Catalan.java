package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * TODO: Mandatory _prefix_ for "y", resulting in "ny".
 * Both of "qu" + "ny" are from https://en.wikipedia.org/wiki/Catalan_orthography#Alphabet.
 * However, at this time Lexica only supports suffixes, not prefixes.
 */
public class Catalan extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("e", 1);
        letterPoints.put("è", 1);
        letterPoints.put("é", 1);
        letterPoints.put("à", 1);
        letterPoints.put("a", 1);
        letterPoints.put("i", 1);
        letterPoints.put("í", 1);
        letterPoints.put("ï", 1);
        letterPoints.put("r", 1);
        letterPoints.put("s", 1);
        letterPoints.put("n", 1);
        letterPoints.put("l", 1);
        letterPoints.put("o", 1);
        letterPoints.put("ò", 1);
        letterPoints.put("ó", 1);
        letterPoints.put("ö", 1);
        letterPoints.put("ú", 1);
        letterPoints.put("ü", 1);
        letterPoints.put("u", 1);
        letterPoints.put("t", 1);

        letterPoints.put("c", 2);
        letterPoints.put("d", 2);
        letterPoints.put("m", 2);

        letterPoints.put("b", 3);
        letterPoints.put("g", 3);
        letterPoints.put("p", 3);

        letterPoints.put("f", 4);
        letterPoints.put("v", 41);

        letterPoints.put("h", 8);
        letterPoints.put("j", 8);
        letterPoints.put("z", 8);

        // "q" is normally 8, but we always have a "u" next to the "q", so it isn't as difficult
        // to incorporate into words when present.
        letterPoints.put("qu", 4);

        letterPoints.put("ç", 10);
        letterPoints.put("x", 10);

        // TODO: These are not included in Scrabble.
        // Indeed, they are only used for loanwords.
        // Should probably look at the CrossWords app and see how they deal with these tiles.
        letterPoints.put("k", 5);
        letterPoints.put("w", 5);
        letterPoints.put("y", 5);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("ca");
    }

    @Override
    public String getName() {
        return "ca";
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
