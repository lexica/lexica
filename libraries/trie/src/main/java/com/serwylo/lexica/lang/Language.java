package com.serwylo.lexica.lang;

import java.util.Locale;

public abstract class Language {

    public abstract Locale getLocale();

    public abstract String getName();

    /**
     * Beta languages are thouse which have not been properly play tested.
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
            case "en_US":
                return new UsEnglish();

            case "en_GB":
                return new UkEnglish();

            case "de_DE":
                return new DeGerman();

            case "nl":
                return new Dutch();

            case "fa":
                return new Persian();

            case "it":
                return new Italian();

            case "ca":
                return new Catalan();

            case "es":
                return new Spanish();

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
