package com.serwylo.lexica.db.migration

import android.content.Context
import android.util.Log
import com.serwylo.lexica.activities.score.ScoreActivity
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.db.GameModeDao
import com.serwylo.lexica.db.Result
import com.serwylo.lexica.db.ResultDao
import com.serwylo.lexica.lang.Language
import java.util.*
import java.util.regex.Pattern
import kotlin.math.sqrt

class MigrateHighScoresFromPreferences(private val context: Context) {

    fun initialiseDb(gameModeDao: GameModeDao, resultDao: ResultDao) {

        Log.i(TAG, "Creating default game modes.")

        val savedGameModes = HashMap<Long, GameMode>()
        defaultGameModes.forEach { defaultGameMode ->
            Log.i(TAG, "Inserting game mode: " + defaultGameMode.label)
            savedGameModes[gameModeDao.insert(defaultGameMode)] = defaultGameMode
        }

        Log.i(TAG, "Looking through legacy preferences to find high scores and migrate to the database.")
        Log.i(TAG, "Cannot record other stats like max possible score for that run because they are not available in the older version of Lexica.")
        migrateHighScoresFromPrefs().forEach { score ->

            Log.i(TAG, "Migrating legacy high score ($score) and ensuring it is linked to appropriate game mode.")
            var gameModeId = findMatchingGameModeId(score.gameMode, savedGameModes)
            if (gameModeId > 0) {
                Log.i(TAG, "Using game mode $gameModeId")
            } else {
                gameModeId = gameModeDao.insert(score.gameMode)
                savedGameModes[gameModeId] = score.gameMode
                Log.i(TAG, "Created custom game mode $gameModeId")
            }

            Log.i(TAG, "Saving high score of " + score.highScore + " against game mode " + gameModeId + " for language " + score.language.name + ".")
            val result = Result(
                    gameModeId = gameModeId,
                    langCode = score.language.name,
                    maxNumWords = 0,
                    numWords = 0,
                    maxScore = score.highScore.toLong(),
                    score = score.highScore.toLong(),
            )
            resultDao.insert(result)

        }
    }

    private fun findMatchingGameModeId(gameMode: GameMode, gameModesToSearch: Map<Long, GameMode>): Long {
        for ((key, existingGameMode) in gameModesToSearch) {
            if (existingGameMode.minWordLength == gameMode.minWordLength && existingGameMode.scoreType == gameMode.scoreType && existingGameMode.timeLimitSeconds == gameMode.timeLimitSeconds && existingGameMode.hintMode == gameMode.hintMode && existingGameMode.boardSize == gameMode.boardSize) {
                return key
            }
        }
        return -1
    }

    private fun migrateHighScoresFromPrefs(): List<LegacyHighScore> {

        val customGameModes: MutableList<LegacyHighScore> = ArrayList()
        val prefs = context.getSharedPreferences(ScoreActivity.SCORE_PREF_FILE, Context.MODE_PRIVATE).all
        for (key in prefs.keys) {
            try {
                val gameMode = maybeGameModeFromPref(key, prefs[key])
                if (gameMode != null) {
                    customGameModes.add(gameMode)
                }
            } catch (e: NullPointerException) {
                // Be extremely defensive here, because we could end up causing a crash loop when upgrading to a
                // new version of lexica, which is much worse than neglecting to port a high score (I think?)
                Log.e(TAG, "Error while checking for high score preference, ignoring this preference.", e)
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Error while checking for high score preference, ignoring this preference.", e)
            }
        }

        return customGameModes

    }

    /**
     * The code which used to save high scores generates preference keys like so:
     *
     *   prefs.getString("dict", "US")
     *    + prefs.getString("boardSize", "16")
     *    + prefs.getString(GameMode.SCORE_TYPE, GameMode.SCORE_WORDS)
     *    + prefs.getString("maxTimeRemaining", "180");
     *
     *
     * We will use the capture groups of the regex to construct a custom [GameMode] with the
     * relevant properties set, and record the [Language] the score was for as well.
     */
    fun maybeGameModeFromPref(key: String?, value: Any?): LegacyHighScore? {

        Log.d(TAG, "Checking existing preference \"$key\" to see if it is a high score.")
        if (key == null) {
            Log.w(TAG, "Key should have been supplied, but got null.")
            return null
        }

        val pattern = Pattern.compile("^(\\w\\w(_\\w\\w)?)(\\d+)([WL])(\\d+)$")
        val matcher = pattern.matcher(key)
        if (!matcher.find()) {
            Log.d(TAG, "Does not seem to be a high score.")
            return null
        }

        Log.i(TAG, "Found existing high score preference \"$key\" with value $value")
        if (value !is Int) {
            Log.w(TAG, "Expected $value to be an integer.")
            return null
        }

        val langCode = matcher.group(1)
        val boardSize = Objects.requireNonNull(matcher.group(3)).toInt()
        val scoreType = matcher.group(4)
        val maxTimeRemaining = Objects.requireNonNull(matcher.group(5)).toInt()
        val language = Language.fromOrNull(langCode)
        if (language == null) {
            Log.e(TAG, "Found legacy high score for \"$key\": $value, but the languge code \"$langCode\" doesn't seem to match a language we know about, so skipping.")
            return null
        }

        val gameMode = GameMode(
                label = "Custom",
                description = "Used in earlier versions of Lexica",
                isCustom = true,
                boardSize = boardSize,
                scoreType = scoreType ?: "W",
                timeLimitSeconds = maxTimeRemaining,
                minWordLength = getMinWordLength(boardSize),
                hintMode = "",
        )

        return LegacyHighScore(language, gameMode, value)
    }

    data class LegacyHighScore(
            val language: Language,
            val gameMode: GameMode,
            val highScore: Int,
    ) {

        override fun toString(): String {
            val size = sqrt(gameMode.boardSize.toDouble()).toInt()
            return "Game Mode [${gameMode.timeLimitSeconds}s / ${size}x$size / ${gameMode.scoreType} / >= ${gameMode.minWordLength}], Lang: ${language.name}, High Score: $highScore"
        }

    }

    companion object {

        private const val TAG = "MigrateScoresFromPrefs"

        private val defaultGameModes:List<GameMode> =
                listOf(
                        GameMode(
                                label = "Sprint",
                                description = "Short game to find as many words as possible",
                                timeLimitSeconds = 180,
                                boardSize = 16,
                                hintMode = "",
                                minWordLength = 3,
                                scoreType = GameMode.SCORE_WORDS,
                                isCustom = false,
                        ),
                        GameMode(
                                label = "Marathon",
                                description = "Try to find all words, without any time pressure",
                                timeLimitSeconds = 1800,
                                boardSize = 36,
                                hintMode = "",
                                minWordLength = 5,
                                scoreType = GameMode.SCORE_WORDS,
                                isCustom = false,
                        ),
                        GameMode(
                                label = "Beginner",
                                description = "Use hints to help find words",
                                timeLimitSeconds = 180,
                                boardSize = 16,
                                hintMode = "hint_both",
                                minWordLength = 3,
                                scoreType = GameMode.SCORE_WORDS,
                                isCustom = false,
                        ),
                        GameMode(
                                label = "Letter Points",
                                description = "Score more points for using less common letters",
                                timeLimitSeconds = 180,
                                boardSize = 25,
                                hintMode = "",
                                minWordLength = 4,
                                scoreType = GameMode.SCORE_LETTERS,
                                isCustom = false,
                        )
                )

        fun getMinWordLength(boardSize: Int): Int {
            when (boardSize) {
                16 -> return 3
                25 -> return 4
                36 -> return 5
            }
            return 3
        }
    }
}