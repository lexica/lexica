package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PortugueseBR extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    // These letter values are drawn from the Scrabble point values:
    // https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Portuguese
    static {
        letterPoints.put("a", 1);
        letterPoints.put("á", 1);
        letterPoints.put("à", 1);
        letterPoints.put("ã", 1);
        letterPoints.put("â", 1);
        letterPoints.put("å", 1);
        letterPoints.put("e", 1);
        letterPoints.put("é", 1);
        letterPoints.put("ê", 1);
        letterPoints.put("è", 1);
        letterPoints.put("i", 1);
        letterPoints.put("í", 1);
        letterPoints.put("î", 1);
        letterPoints.put("ï", 1);
        letterPoints.put("o", 1);
        letterPoints.put("ó", 1);
        letterPoints.put("ô", 1);
        letterPoints.put("õ", 1);
        letterPoints.put("ö", 1);
        letterPoints.put("s", 1);
        letterPoints.put("u", 1);
        letterPoints.put("ú", 1);
        letterPoints.put("m", 1);
        letterPoints.put("r", 1);
        letterPoints.put("t", 1);

        letterPoints.put("d", 2);
        letterPoints.put("l", 2);
        letterPoints.put("c", 2);
        letterPoints.put("p", 2);

        letterPoints.put("n", 3);
        letterPoints.put("b", 3);
        letterPoints.put("ç", 3);

        letterPoints.put("f", 4);
        letterPoints.put("g", 4);
        letterPoints.put("h", 4);
        letterPoints.put("v", 4);

        letterPoints.put("j", 5);

        // "q" is normally 6, but we always have a "u" next to the "q", so
        // it isn't as difficult to incorporate into words when present.
        letterPoints.put("qu", 6);

        letterPoints.put("x", 8);
        letterPoints.put("z", 8);

        // TODO: These are not included in Scrabble.
        // Indeed, they are only used for loanwords.
        // Should probably look at the CrossWords app and see how they deal with these tiles.
        letterPoints.put("k", 2);
        letterPoints.put("w", 2);
        letterPoints.put("y", 2);

    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("pt", "BR");
    }

    @Override
    public String getName() {
        return "pt_BR";
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
