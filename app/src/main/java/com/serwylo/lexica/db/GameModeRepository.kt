package com.serwylo.lexica.db

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

class GameModeRepository(
        private val gameModeDao: GameModeDao,
        private val preferences: SharedPreferences,
) {

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

    companion object {
        private const val PREF_CURRENT_GAME_MODE_ID = "currentGameModeId"
        private const val TAG = "GameModeRepository"
    }

}