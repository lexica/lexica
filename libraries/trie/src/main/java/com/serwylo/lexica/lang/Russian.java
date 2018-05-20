package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Russian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put("о", 1);
        letterPoints.put("а", 1);
        letterPoints.put("е", 1);
        letterPoints.put("и", 1);
        letterPoints.put("н", 1);
        letterPoints.put("р", 1);
        letterPoints.put("с", 1);
        letterPoints.put("т", 1);
        letterPoints.put("в", 1);

        letterPoints.put("д", 2);
        letterPoints.put("к", 2);
        letterPoints.put("у", 2);
        letterPoints.put("л", 2);
        letterPoints.put("п", 2);
        letterPoints.put("м", 2);

        letterPoints.put("б", 3);
        letterPoints.put("г", 3);
        letterPoints.put("ь", 3);
        letterPoints.put("я", 3);
        letterPoints.put("ё", 3);

        letterPoints.put("ы", 4);
        letterPoints.put("й", 4);

        letterPoints.put("з", 5);
        letterPoints.put("ж", 5);
        letterPoints.put("х", 5);
        letterPoints.put("ц", 5);
        letterPoints.put("ч", 5);

        letterPoints.put("ш", 8);
        letterPoints.put("э", 8);
        letterPoints.put("ю", 8);

        letterPoints.put("ф", 10);
        letterPoints.put("щ", 10);
        letterPoints.put("ъ", 10);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("ru");
    }

    @Override
    public String getName() {
        return "ru";
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
