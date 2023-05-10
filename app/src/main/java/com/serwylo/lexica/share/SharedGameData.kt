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
    val type: Type,

    /**
     * If sharing a challenge, include the score to beat and the number of words to beat in the URI
     * so that we can show it to the challenger when they are about to start.
     * Not used for multiplayer.
     */
    val numWordsToBeat: Int = -1,
    val scoreToBeat: Int = -1,
) {

    enum class Type {
        MULTIPLAYER,
        SHARE,
    }

    enum class Platform {
        ANDROID,
        WEB
    }

    constructor(board: List<String>, language: Language, gameMode: GameMode, type: Type, numWordsToBeat: Int = -1, scoreToBeat: Int = -1) : this(
        board,
        language,
        gameMode.scoreType,
        gameMode.timeLimitSeconds,
        gameMode.minWordLength,
        gameMode.hintMode,
        MIN_SUPPORTED_VERSION,
        type,
        numWordsToBeat,
        scoreToBeat,
    )

    fun serialize(platform: Platform = Platform.ANDROID): Uri {
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

        val urlBuilder = getBaseUrl(platform)
            .appendQueryParameter(Keys.board, boardChars)
            .appendQueryParameter(Keys.language, language.name)
            .appendQueryParameter(Keys.time, timeLimitInSeconds.toString())

        // Put these in here in the vain hope that people wont immediately see them in between all
        // the other parameters and change them to make themselves look better before challenging.
        // But really, it isn't a big issue if someone wants to be a silly cheater...
        if (scoreToBeat >= 0 && numWordsToBeat >= 0) {
            urlBuilder
                .appendQueryParameter(Keys.scoreToBeat, scoreToBeat.toString())
                .appendQueryParameter(Keys.numWordsToBeat, numWordsToBeat.toString())
        }

        urlBuilder
            .appendQueryParameter(Keys.minWordLength, minWordLength.toString())
            .appendQueryParameter(Keys.minSupportedLexicaVersion, minSupportedLexicaVersion.toString())
            .appendQueryParameter(Keys.currentLexicaVersion, CURRENT_VERSION.toString())
            .appendQueryParameter(Keys.scoreType, scoreTypeSerialized)

        if (hintModeSerialized.isNotEmpty()) {
            urlBuilder.appendQueryParameter(Keys.hintMode, hintModeSerialized)
        }

        return urlBuilder.build()
    }

    private fun getBaseUrl(platform: Platform): Uri.Builder {
        val androidPath = when (type) {
            Type.MULTIPLAYER -> "m"
            Type.SHARE -> "share"
        }

        val webPath = "web-lexica/multiplayer"

        return when (platform) {
            Platform.WEB -> {
                val path = when (type) {
                    Type.MULTIPLAYER, Type.SHARE -> webPath
                }
                Uri.Builder()
                    .scheme("https")
                    .authority("lexica.github.io")
                    .path("/$path/")
            }
            Platform.ANDROID -> {
                when (type) {
                    Type.MULTIPLAYER -> {
                        Uri.Builder()
                            .scheme("lexica")
                            .authority("multiplayer")
                    }
                    Type.SHARE -> {
                        Uri.Builder()
                            .scheme("https")
                            .authority("lexica.github.io")
                            .path("/$androidPath/")
                    }
                }
            }
        }
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
        const val numWordsToBeat = "w"
        const val scoreToBeat = "sc"
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
            val numWordsToBeat = findKey(uri, Keys.numWordsToBeat, "-1").toInt()
            val scoreToBeat = findKey(uri, Keys.scoreToBeat, "-1").toInt()
            val type = when(uri.pathSegments.firstOrNull()) {
                "m" -> Type.MULTIPLAYER
                else -> Type.SHARE
            }

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
                type,
                numWordsToBeat,
                scoreToBeat,
            )

        }

        private fun findKey(uri: Uri, key: String, default: String? = null): String {
            val value = uri.getQueryParameter(key)
            if (value == null || value.isEmpty()) {
                if (default != null) {
                    return default;
                } else {
                    throw IllegalArgumentException("Expected to find a $key in $uri. Only found these: ${uri.queryParameterNames}.")
                }
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
