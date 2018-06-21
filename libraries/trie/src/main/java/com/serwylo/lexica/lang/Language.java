package com.serwylo.lexica.lang;

import java.util.Locale;
import java.util.Map;

public abstract class Language {

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
     *
     * @param letter Does NOT contain mandatory suffix.
     */
    public final int getPointsForLetter(String letter) {
        String lowerCaseLetter = letter.toLowerCase();
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
        switch (name) {
            case "ca":
                return new Catalan();

            case "de_DE":
                return new DeGerman();

            case "en_GB":
                return new EnglishGB();

            case "en_US":
                return new EnglishUS();

            case "es":
                return new Spanish();

            case "fa":
                return new Persian();

            case "fr_FR":
                return new French();

            case "hu":
                return new Hungarian();

            case "it":
                return new Italian();

            case "jp":
                return new Japanese();

            case "nl":
                return new Dutch();

            case "ru":
                return new Russian();

            default:
                return null;
        }
    }

    public static class NotFound extends Exception {

        public final String name;

        public NotFound(String name) {
            super("Unsupported language: " + name);
            this.name = name;
        }

    }
}
