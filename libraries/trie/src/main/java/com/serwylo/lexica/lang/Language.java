package com.serwylo.lexica.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class Language {

    private static Map<String, Language> allLanguages = null;

    public static Map<String, Language> getAllLanguages() {
        if (allLanguages == null) {

            Map<String, Language> langs = new HashMap<>();

            langs.put("ca", new Catalan());
            langs.put("de_DE", new DeGerman());
            langs.put("en_GB", new EnglishGB());
            langs.put("en_US", new EnglishUS());
            langs.put("es", new Spanish());
            langs.put("fa", new Persian());
            langs.put("fr_FR", new French());
            langs.put("hu", new Hungarian());
            langs.put("it", new Italian());
            langs.put("jp", new Japanese());
            langs.put("nl", new Dutch());
            langs.put("pl", new Polish());
            langs.put("pt_BR", new PortugueseBR());
            langs.put("ru", new Russian());
            langs.put("uk", new Ukrainian());

            allLanguages = Collections.unmodifiableMap(langs);

        }

        return allLanguages;
    }

    public abstract Locale getLocale();

    public abstract String getName();

    protected abstract Map<String, Integer> getLetterPoints();

    /**
     * Beta languages are those which have not been properly play tested.
     * When adding a new language, override and return true to show feedback to the user that the
     * dictionary is still in beta.
     */
    public boolean isBeta() {
        return false;
    }

    /**
     * Converts a lowercase representation into something for display. For example, in the case
     * of an English "qu", it should probably be displayed with a capitol "Q" but lower case "u":
     * "Qu";
     * @param value The lowercase string, as it is stored in the serialized trie.
     */
    public abstract String toDisplay(String value);

    /**
     * If some letters just don't make sense without suffixes, then this is where it should be
     * defined. The classic example is in English how "q" is almost always followed by a "u".
     * Although not always the case, it happens so frequently that for the benefit of a game,
     * it doesn't make sense to ever have a "q" by itself.
     */
    public abstract String applyMandatorySuffix(String value);

    /**
     * Each "letter" tile has a score. This score distribution is unique amoung different languages,
     * so even though both German and English both have the letter "e", their score may differ
     * for each language.
     */
    public final int getPointsForLetter(String letterWithMandatorySuffix) {
        String lowerCaseLetter = letterWithMandatorySuffix.toLowerCase();
        Integer points = getLetterPoints().get(lowerCaseLetter);
        if (points == null) {
            throw new IllegalArgumentException("Language " + getName() + " doesn't have a point value for the " + lowerCaseLetter + " tile");
        }

        return points;
    }

    /**
     * The name of the trie file, relative to the `assets/` directory.
     * So for example "words.en_US.bin"
     */
    public final String getDictionaryFileName() {
        return "dictionary." + getName() + ".txt";
    }

    /**
     * The name of the trie file, relative to the `assets/` directory.
     * So for example "words_en_US.bin"
     */
    public final String getTrieFileName() {
        String suffix = getName().replace('-', '_').toLowerCase(Locale.ENGLISH);
        return "words_" + suffix + ".bin";
    }

    /**
     * The name of the letter distribution file, relative to the `assets/` directory.
     * So for example "letters_en_US.txt"
     */
    public final String getLetterDistributionFileName() {
        String suffix = getName().replace('-', '_').toLowerCase(Locale.ENGLISH);
        return "letters_" + suffix + ".txt";
    }

    public static Language from(String name) throws NotFound {
        Language language = fromOrNull(name);
        if (language == null) {
            throw new NotFound(name);
        }

        return language;
    }

    public static Language fromOrNull(String name) {
        return getAllLanguages().get(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * A URL which we can send the player to in order to define a word.
     *
     * Must include a single {@link String#format(String, Object...)} "%s" placeholder.
     *
     * By default, uses DuckDuckGo, however individual languages may have their own more appropriate
     * sources to use.
     *
     * Note: This is only used if the "Online" dictionary provider is selected from preferences.
     *       If "AARD2" or "QuickDic" is selected, they will take precedence.
     */
    public String getDefinitionUrl() {
        return "https://duckduckgo.com/?ia=definition&q=define+%s";
    }

    public static class NotFound extends Exception {

        public final String name;

        public NotFound(String name) {
            super("Unsupported language: " + name);
            this.name = name;
        }

    }
}
