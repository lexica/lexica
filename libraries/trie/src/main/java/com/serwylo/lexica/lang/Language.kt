package com.serwylo.lexica.lang

import java.util.*

abstract class Language {

    abstract val locale: Locale

    abstract val name: String

    protected abstract val letterPoints: Map<String, Int>

    /**
     * Beta languages are those which have not been properly play tested.
     * When adding a new language, override and return true to show feedback to the user that the
     * dictionary is still in beta.
     */
    open val isBeta: Boolean
        get() = false

    /**
     * Converts a lowercase representation into something for display. For example, in the case
     * of an English "qu", it should probably be displayed with a capitol "Q" but lower case "u":
     * "Qu";
     *
     * @param value The lowercase string, as it is stored in the serialized trie.
     */
    abstract fun toDisplay(value: String?): String?

    /**
     * Same as toDisplay, but for the tried word list.
     * Used in breton to change some letters which are not available as single
     * unicode characters into their representation in two or three characters.
     *
     * @param value The word, as it is stored in the serialized trie.
     */
    abstract fun toRepresentation(value: String?): String?

    /**
     * If some letters just don't make sense without suffixes, then this is where it should be
     * defined. The classic example is in English how "q" is almost always followed by a "u".
     * Although not always the case, it happens so frequently that for the benefit of a game,
     * it doesn't make sense to ever have a "q" by itself.
     */
    abstract fun applyMandatorySuffix(value: String?): String

    /**
     * Each "letter" tile has a score. This score distribution is unique amoung different languages,
     * so even though both German and English both have the letter "e", their score may differ
     * for each language.
     */
    fun getPointsForLetter(letterWithMandatorySuffix: String): Int {
        val lowerCaseLetter = letterWithMandatorySuffix.lowercase()
        return letterPoints[lowerCaseLetter]
                ?: throw IllegalArgumentException("Language $name doesn't have a point value for the $lowerCaseLetter tile")
    }

    /**
     * The name of the trie file, relative to the `assets/` directory.
     * So for example "words.en_US.bin"
     */
    val dictionaryFileName: String
        get() = "dictionary.$name.txt"

    /**
     * The name of the trie file, relative to the `assets/` directory.
     * So for example "words_en_US.bin"
     */
    val trieFileName: String
        get() {
            val suffix = name.replace('-', '_').lowercase(Locale.ENGLISH)
            return "words_$suffix.bin"
        }

    /**
     * The name of the letter distribution file, relative to the `assets/` directory.
     * So for example "letters_en_US.txt"
     */
    val letterDistributionFileName: String
        get() {
            val suffix = name.replace('-', '_').lowercase(Locale.ENGLISH)
            return "letters_$suffix.txt"
        }

    override fun toString(): String {
        return name
    }

    /**
     * A URL which we can send the player to in order to define a word.
     *
     *
     * Must include a single [String.format] "%s" placeholder for the word
     * to be defined.
     *
     *
     * Unless a specific dictionary is required for a certain language, you probably want to use the
     * [Language.getWiktionaryDefinitionUrl] helper method.
     *
     *
     * Note: This is only used if the "Online" dictionary provider is selected from preferences.
     * If "AARD2" or "QuickDic" is selected, they will take precedence.
     */
    abstract val definitionUrl: String

    class NotFound(val name: String) : Exception("Unsupported language: $name")

    companion object {

        @JvmStatic
        val allLanguages = mapOf(
                "br_no_diacritics" to BretonNoDiacritics(),
                "ca" to Catalan(),
                "de_DE" to GermanDe(),
                "de_DE_no_diacritics" to GermanDeNoDiacritics(),
                "en_GB" to EnglishGB(),
                "en_US" to EnglishUS(),
                "es" to Spanish(),
		        "es_solo_enne" to SpanishSoloEnne(),
                "fa" to Persian(),
                "fi" to Finnish(),
                "fr_FR" to French(),
                "fr_FR_no_diacritics" to FrenchNoDiacritics(),
                "hu" to Hungarian(),
                "hr_HR" to Croatian(),
                "it" to Italian(),
                "ja" to Japanese(),
                "nl" to Dutch(),
                "pl" to Polish(),
                "pt_BR" to PortugueseBR(),
                "pt_BR_no_diacritics" to PortugueseBRNoDiacritics(),
                "ru" to Russian(),
                "ru_extended" to RussianExtended(),
                "tr" to Turkish(),
                "uk" to Ukrainian(),
        )

        @JvmStatic
        @Throws(NotFound::class)
        fun from(name: String): Language {
            return fromOrNull(name)
                    ?: throw NotFound(name)
        }

        @JvmStatic
        fun fromOrNull(name: String): Language? {

            // Special cases to deal with legacy code that uses these as language codes in the preference system.
            if ("UK" == name) {
                return EnglishGB()
            } else if ("US" == name) {
                return EnglishUS()
            }

            return allLanguages[name]

        }

        @JvmStatic
        fun getWiktionaryDefinitionUrl(langCode: String): String {
            return "https://$langCode.wiktionary.org/w/index.php?search=%s"
        }

    }
}
