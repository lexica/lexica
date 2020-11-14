package com.serwylo.lexica.db;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.List;

public class GameModeRepository {

    private static final String PREF_CURRENT_GAME_MODE_ID = "currentGameModeId";
    private static final String TAG = "GameModeRepository";

    private GameModeDao gameModeDao;
    private SharedPreferences preferences;

    public GameModeRepository(Application application) {
        Database db = Database.get(application);
        gameModeDao = db.gameModeDao();

        preferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public void saveCurrentGameMode(GameMode gameMode) {
        preferences.edit().putLong(PREF_CURRENT_GAME_MODE_ID, gameMode.getGameModeId()).apply();
    }

    public boolean hasGameModes() {
        return gameModeDao.getFirst() != null;
    }

    public GameMode loadCurrentGameMode() {
        long id = preferences.getLong(PREF_CURRENT_GAME_MODE_ID, -1);
        if (id == -1) {
            Log.i(TAG, "No current game mode stored (perhaps first run?). Choosing the first game mode from the list of modes.");
            GameMode gameMode = gameModeDao.getFirst();

            Log.i(TAG, "Remembering the current game mode: " + gameMode);
            saveCurrentGameMode(gameMode);
            return gameMode;
        }

        return gameModeDao.getById(id);
    }

}
