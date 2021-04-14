package com.serwylo.lexica.share

import android.content.Context
import android.net.Uri
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.lang.LanguageLabel
import java.lang.IllegalArgumentException
import kotlin.math.sqrt

/**
 * A proof of concept for sharing with a more human-readable format compared to just an obtuse URI.
 *
 */
data class SharedGameDataHumanReadable(
    val board: List<String>,
    val language: Language,
    val scoreType: String,
    val timeLimitInSeconds: Int,
    val minWordLength: Int,
    val hints: String,
) {

    constructor(board: List<String>, gameMode: GameMode, language: Language): this(
        board,
        language,
        gameMode.scoreType,
        gameMode.timeLimitSeconds,
        gameMode.minWordLength,
        gameMode.hintMode,
    )

    constructor(data: SharedGameData): this(
        data.board,
        data.language,
        data.scoreType,
        data.timeLimitInSeconds,
        data.minWordLength,
        data.hints,
    )

    fun serialize(context: Context): String {
        val boardWidth = sqrt(board.size.toDouble()).toInt()
        val board = board
            .chunked(boardWidth)
            .map { it.joinToString(" ") }
            .joinToString("\n")

        return """${board.toUpperCase(language.locale)}

${Keys.language}: ${LanguageLabel.getLabel(context, language)}
${Keys.time}: ${timeLimitInSeconds / 60} mins
${Keys.minWordLength}: $minWordLength"""

    }

    object Keys {
        const val language = "Language"
        const val time = "Time"
        const val scoreType = "Score"
        const val minWordLength = "Min Word Length"
        const val hintMode = "Hints"
    }

    companion object {
        fun parseGameQrCode(uri: Uri): SharedGameDataHumanReadable {
            val path = uri.path?.substring(1)
            if (path == null || path.isEmpty()) {
                throw IllegalArgumentException("Invalid path for shared game: \"${path}\"")
            }

            val lines = removeEmptyLinesAndTrim(path)
            val boardValues = extractBoard(lines)
            val metadataLines = extractMetadata(lines)

            val languageCode = findKey(metadataLines, Keys.language)
            val time = findKey(metadataLines, Keys.time)
            val scoreType = findKey(metadataLines, Keys.scoreType)
            val minWordLength = findKey(metadataLines, Keys.minWordLength)
            val hintMode = findKey(metadataLines, Keys.hintMode)

            return SharedGameDataHumanReadable(
                boardValues,
                Language.from(languageCode),
                parseScoreType(scoreType),
                parseTime(time),
                minWordLength.toInt(),
                parseHintMode(hintMode),
            )

        }

        private fun removeEmptyLinesAndTrim(data: String): List<String> =
            data
                .trim()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

        private fun extractBoard(lines: List<String>): List<String> =
            lines
                .filter { !it.contains(":") }
                .map { it.replace(" ", "") }
                .map { it.toCharArray().toList().map { character -> character.toString() } }
                .flatten()

        private fun extractMetadata(lines: List<String>): Map<String, String> =
            lines
                .filter { it.contains(":") }
                .associate { line ->
                    val parts = line.split(":").map { part -> part.trim() }
                    parts[0] to parts[1]
                }

        private fun findKey(metadata: Map<String, String>, key: String): String {
            val value = metadata[key]
            if (value == null || value.isEmpty()) {
                throw IllegalArgumentException("Expected to find a $key in the shared game. Only found these: $metadata.")
            }

            return value
        }

        private fun parseTime(time: String): Int {
            if (!time.matches(Regex("\\d+m"))) {
                throw IllegalArgumentException("Expected ${Keys.time} to take the format 45m. Actual format received: $time.")
            }

            val timeInMins = time.trimEnd('m').toInt()
            return timeInMins * 60
        }

        private fun parseHintMode(hintMode: String) =
            hintModes[hintMode] ?: throw IllegalArgumentException("Unexpected ${Keys.hintMode}: \"$hintMode\"")

        private val hintModes = mapOf(

            "Colour" to "hint_color",
            "Color" to "hint_color",

            "Number" to "tile_count",

            "Colour + Number" to "hint_both",
            "Color + Number" to "hint_both",
            "Number + Colour" to "hint_both",
            "Number + Color" to "hint_both",

            "None" to "",

        )

        private fun parseScoreType(scoreType: String) =
            scoreTypes[scoreType] ?: throw IllegalArgumentException("Unexpected ${Keys.scoreType}: \"$scoreType\"")

        private val scoreTypes = mapOf(
            "Letter" to GameMode.SCORE_LETTERS,
            "Words" to GameMode.SCORE_WORDS,
        )

    }

}