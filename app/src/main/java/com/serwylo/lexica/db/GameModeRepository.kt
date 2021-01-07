package com.serwylo.lexica.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

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
        preferences.edit().putLong(PREF_CURRENT_GAME_MODE_ID, gameMode.gameModeId).apply()
    }

    fun hasGameModes(): Boolean {
        return gameModeDao.getFirst() != null
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