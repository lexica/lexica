package com.serwylo.lexica.share

import android.net.Uri
import android.util.Base64
import com.serwylo.lexica.BuildConfig
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language

/**
 * Encode a URI for sharing o Lexica game with other players.
 */
data class SharedGameData(
    val board: List<String>,
    val language: Language,
    val scoreType: String,
    val timeLimitInSeconds: Int,
    val minWordLength: Int,
    val hints: String,
    val minSupportedLexicaVersion: Int,
) {

    constructor(board: List<String>, language: Language, gameMode: GameMode) : this(
        board,
        language,
        gameMode.scoreType,
        gameMode.timeLimitSeconds,
        gameMode.minWordLength,
        gameMode.hintMode,
        MIN_SUPPORTED_VERSION,
    )

    fun serialize(): Uri {
        val boardChars = Base64.encodeToString(board.joinToString(",").encodeToByteArray(),
                Base64.NO_PADDING + Base64.NO_WRAP + Base64.URL_SAFE)
        val scoreTypeSerialized = when (scoreType) {
            GameMode.SCORE_WORDS -> "w"
            GameMode.SCORE_LETTERS -> "l"
            else -> "w"
        }

        val hintModeSerialized = when (hints) {
            "tile_count" -> "n"
            "hint_colour" -> "c"
            "hint_both" -> "nc"
            else -> ""
        }

        val urlBuilder = Uri.Builder()
            .scheme("https")
            .authority("lexica.github.io")
            .path("/m/")
            .appendQueryParameter(Keys.board, boardChars)
            .appendQueryParameter(Keys.language, language.name)
            .appendQueryParameter(Keys.time, timeLimitInSeconds.toString())
            .appendQueryParameter(Keys.scoreType, scoreTypeSerialized)
            .appendQueryParameter(Keys.minWordLength, minWordLength.toString())
            .appendQueryParameter(Keys.minSupportedLexicaVersion, minSupportedLexicaVersion.toString())
            .appendQueryParameter(Keys.currentLexicaVersion, CURRENT_VERSION.toString())

        if (hintModeSerialized.isNotEmpty()) {
            urlBuilder.appendQueryParameter(Keys.hintMode, hintModeSerialized)
        }

        return urlBuilder.build()
    }

    object Keys {
        const val board = "b"
        const val language = "l"
        const val time = "t"
        const val scoreType = "s"
        const val minWordLength = "m"
        const val hintMode = "h"
        const val currentLexicaVersion = "v"
        const val minSupportedLexicaVersion = "mv"
    }

    enum class ShareType(value: String) {
        /**
         * Take a player to the lobby, explaining the game mode which has been shared, but also
         * recommending that they wait for all players to be ready before beginning.
         */
        MULTIPLAYER("multiplayer"),

        /**
         *
         */
        SHARE("share"),

        /**
         * Directly starts a game without any intermediate screen. Useful, for example, to
         * replay games from the score screen (or perhaps the high score list in the future).
         */
        PLAY("play"),
    }

    companion object {

        private const val VERSION_COMMAS_INTRODUCED = 20017
        const val MIN_SUPPORTED_VERSION = 20017
        const val CURRENT_VERSION = BuildConfig.VERSION_CODE

        fun parseGame(uri: Uri): SharedGameData {
            val board = findKey(uri, Keys.board)
            val languageCode = findKey(uri, Keys.language)
            val time = findKey(uri, Keys.time)
            val scoreType = findKey(uri, Keys.scoreType)
            val minWordLength = findKey(uri, Keys.minWordLength)
            val minSupportedLexicaVersion = findKey(uri, Keys.minSupportedLexicaVersion).toInt()

            if (minSupportedLexicaVersion > CURRENT_VERSION) {
                throw IllegalArgumentException("This version of Lexica ($CURRENT_VERSION) is too old, version $minSupportedLexicaVersion is required.")
            }

            val hintMode = if (uri.queryParameterNames.contains(Keys.hintMode)) {
                findKey(uri, Keys.hintMode)
            } else {
                ""
            }

            val language = Language.from(languageCode)

            val decodedBoard = if (minSupportedLexicaVersion < VERSION_COMMAS_INTRODUCED) {
                // Old versions didn't place underscores between letters, so multi-letter cells were
                // ambiguous and can't be reliably parsed. To attempt to deal with this decode
                // each letter in turn and check applyMandatorySuffix to identify how many
                // characters should be present
                var inArray = board.toCharArray().toList()
                val outArray = ArrayList<String>()
                while (inArray.isNotEmpty()) {
                    val firstChar = inArray[0]
                    val withSuffix = language.applyMandatorySuffix(firstChar.toString())
                    outArray.add(withSuffix)
                    inArray = inArray.drop(withSuffix.length)
                }
                outArray
            } else {
                // Board is base64 encoded and has letters split by commas
                val boardString = Base64.decode(board,
                            Base64.NO_PADDING + Base64.NO_WRAP + Base64.URL_SAFE)
                String(boardString).split(",")
            }

            return SharedGameData(
                decodedBoard,
                language,
                parseScoreType(scoreType),
                time.toInt(),
                minWordLength.toInt(),
                parseHintMode(hintMode),
                minSupportedLexicaVersion,
            )

        }

        private fun findKey(uri: Uri, key: String): String {
            val value = uri.getQueryParameter(key)
            if (value == null || value.isEmpty()) {
                throw IllegalArgumentException("Expected to find a $key in $uri. Only found these: ${uri.queryParameterNames}.")
            }

            return value
        }

        private fun parseHintMode(hintMode: String) =
            hintModes[hintMode] ?: throw IllegalArgumentException("Unexpected ${Keys.hintMode}: \"$hintMode\". Expected one of ${hintModes.keys}")

        private val hintModes = mapOf(
            "c" to "hint_color",
            "n" to "tile_count",
            "cn" to "hint_both",
            "nc" to "hint_both",
            "" to "",
        )

        private fun parseScoreType(scoreType: String) =
            scoreTypes[scoreType] ?: throw IllegalArgumentException("Unexpected ${Keys.scoreType}: \"$scoreType\"")

        private val scoreTypes = mapOf(
            "l" to GameMode.SCORE_LETTERS,
            "w" to GameMode.SCORE_WORDS,
        )

    }
}
