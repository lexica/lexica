package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Ukrainian extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        // Ukrainian-language Scrabble set from
        // https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Ukrainian
        letterPoints.put("о", 1);
        letterPoints.put("а", 1);
        letterPoints.put("и", 1);
        letterPoints.put("н", 1);
        letterPoints.put("в", 1);
        letterPoints.put("е", 1);
        letterPoints.put("і", 1);
        letterPoints.put("т", 1);
        letterPoints.put("р", 1);

        letterPoints.put("к", 2);
        letterPoints.put("с", 2);
        letterPoints.put("д", 2);
        letterPoints.put("л", 2);
        letterPoints.put("м", 2);
        letterPoints.put("п", 2);

        letterPoints.put("у", 3);

        letterPoints.put("з", 4);
        letterPoints.put("я", 4);
        letterPoints.put("б", 4);
        letterPoints.put("г", 4);

        letterPoints.put("ч", 5);
        letterPoints.put("х", 5);
        letterPoints.put("й", 5);
        letterPoints.put("ь", 5);

        letterPoints.put("ж", 6);
        letterPoints.put("ї", 6);
        letterPoints.put("ц", 6);
        letterPoints.put("ш", 6);

        letterPoints.put("ю", 7);

        letterPoints.put("є", 8);
        letterPoints.put("ф", 8);
        letterPoints.put("щ", 8);

        letterPoints.put("ґ", 10);
        letterPoints.put("'", 10);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("uk");
    }

    @Override
    public String getName() {
        return "uk";
    }

    @Override
    public String toDisplay(String value) {
        return value.toUpperCase(getLocale());
    }

    @Override
    public String toRepresentation(String value) {
        return value;
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
     * At time of writing, the Ukranian wiktionary only has 10,000 definitions, which may be a bit
     * slim.
     */
    @Override
    public String getDefinitionUrl() {
        return getWiktionaryDefinitionUrl("uk");
    }
}
