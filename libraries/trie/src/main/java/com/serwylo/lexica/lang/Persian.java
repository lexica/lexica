package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Persian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("ف", 1);
        letterPoints.put("ق", 1);
        letterPoints.put("ل", 1);
        letterPoints.put("م", 1);
        letterPoints.put("ن", 1);
        letterPoints.put("چ", 1);
        letterPoints.put("ه", 1);
        letterPoints.put("و", 1);
        letterPoints.put("ى", 1);
        letterPoints.put("ی", 1);
        letterPoints.put("ژ", 1);
        letterPoints.put("ء", 1);
        letterPoints.put("آ", 1);
        letterPoints.put("أ", 1);
        letterPoints.put("ؤ", 1);
        letterPoints.put("ا", 1);
        letterPoints.put("ب", 1);
        letterPoints.put("ک", 1);
        letterPoints.put("ت", 1);
        letterPoints.put("ث", 1);
        letterPoints.put("ج", 1);
        letterPoints.put("ح", 1);
        letterPoints.put("خ", 1);
        letterPoints.put("د", 1);
        letterPoints.put("گ", 1);
        letterPoints.put("ذ", 1);
        letterPoints.put("ر", 1);
        letterPoints.put("ز", 1);
        letterPoints.put("س", 1);
        letterPoints.put("ش", 1);
        letterPoints.put("ص", 1);
        letterPoints.put("ض", 1);
        letterPoints.put("ط", 1);
        letterPoints.put("ظ", 1);
        letterPoints.put("ع", 1);
        letterPoints.put("غ", 1);
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
