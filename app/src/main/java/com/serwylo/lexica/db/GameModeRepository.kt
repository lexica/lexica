package com.serwylo.lexica.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.serwylo.lexica.share.SharedGameData
import androidx.core.content.edit

class GameModeRepository(
        private val gameModeDao: GameModeDao,
        private val resultDao: ResultDao,
        private val selectedWordDao: SelectedWordDao,
        private val preferences: SharedPreferences,
) {

    /**
     * Convenience constructor. The primary constructor is the one which allows for proper dependency
     * injection and easier unit tests, etc.
     */
    constructor(context: Context): this(
            Database.get(context).gameModeDao(),
            Database.get(context).resultDao(),
            Database.get(context).selectedWordDao(),
            PreferenceManager.getDefaultSharedPreferences(context),
    )

    fun saveCurrentGameMode(gameMode: GameMode) {
        preferences.edit { putLong(PREF_CURRENT_GAME_MODE_ID, gameMode.gameModeId) }
    }

    fun hasGameModes(): Boolean {
        return gameModeDao.getFirst() != null
    }

    fun all(): LiveData<List<GameMode>> {
        return gameModeDao.getAllGameModes()
    }

    fun ensureRulesExist(data: SharedGameData): GameMode {
        Log.d(TAG, "Searching for game mode with the same rules as a shared game.")
        val existing = gameModeDao.getByRules(data.minWordLength, data.scoreType, data.timeLimitInSeconds, data.hints, data.board.size)
        if (existing != null) {
            Log.d(TAG, "Using existing game mode: $existing")
            return existing
        }

        Log.i(TAG, "No matching game mode with appropriate rules, will create a new one.")
        val newGameMode = GameMode(0, GameMode.Type.CUSTOM, "Shared with you", data.board.size, data.timeLimitInSeconds, data.minWordLength, data.scoreType, data.hints)
        val id = gameModeDao.insert(newGameMode)
        return gameModeDao.getById(id) ?: error("Created new game mode $id but then failed to load it from the DB.")
    }

    fun loadCurrentGameMode(): GameMode? {

        val id = preferences.getLong(PREF_CURRENT_GAME_MODE_ID, -1)
        if (id == -1L) {
            Log.i(TAG, "No current game mode stored (perhaps first run?). Choosing the first game mode from the list of modes.")
            val gameMode = gameModeDao.getFirst()

            if (gameMode == null) {
                Log.e(TAG, "No game modes present, expected at least one so we could remember that as the current mode.")
                return null
            }

            Log.i(TAG, "Remembering the current game mode: $gameMode")
            saveCurrentGameMode(gameMode)

            return gameMode
        }

        return gameModeDao.getById(id)
    }

    fun deleteGameMode(mode: GameMode) {
        selectedWordDao.deleteByGameMode(mode.gameModeId)
        resultDao.deleteByGameMode(mode.gameModeId)
        gameModeDao.delete(mode)
    }

    companion object {
        private const val PREF_CURRENT_GAME_MODE_ID = "currentGameModeId"
        private const val TAG = "GameModeRepository"
    }

}