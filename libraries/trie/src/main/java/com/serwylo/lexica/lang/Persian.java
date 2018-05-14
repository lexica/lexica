package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Persian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    // This is absolutely the wrong thing to do, because it is taken from the Arabic Scrabble scores.
    // The Wikipedia page on international Scrabble scores is (https://en.wikipedia.org/wiki/Scrabble_letter_distributions).
    // The solution should probably be to implement some form of algorithm to calculate scores
    // based on frequency in the dictionary.
    static {
        letterPoints.put("ا", 1);
        letterPoints.put("ل", 1);
        letterPoints.put("ج", 1);
        letterPoints.put("ح", 1);
        letterPoints.put("خ", 1);
        letterPoints.put("م", 1);
        letterPoints.put("ن", 1);
        letterPoints.put("ه", 1);
        letterPoints.put("و", 1);

        letterPoints.put("ب", 2);
        letterPoints.put("ت", 2);
        letterPoints.put("ر", 2);
        letterPoints.put("د", 2);
        letterPoints.put("س", 2);
        letterPoints.put("ث", 2);

        letterPoints.put("ف", 3);
        letterPoints.put("ق", 3);
        letterPoints.put("ذ", 3);
        letterPoints.put("ش", 3);
        letterPoints.put("ز", 3);

        letterPoints.put("ص", 4);
        letterPoints.put("ض", 4);
        letterPoints.put("ع", 4);
        letterPoints.put("ط", 4);

        letterPoints.put("ظ", 5);

        letterPoints.put("غ", 8);
        letterPoints.put("ء", 8);

        letterPoints.put("أ", 10);
        letterPoints.put("ؤ", 10);

        letterPoints.put("چ", 1);
        letterPoints.put("ى", 1);
        letterPoints.put("ی", 1);
        letterPoints.put("ژ", 1);
        letterPoints.put("آ", 1);
        letterPoints.put("ک", 1);
        letterPoints.put("گ", 1);
        letterPoints.put("پ", 1);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("fa");
    }

    @Override
    public String getName() {
        return "fa";
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
}
