package com.serwylo.lexica.db;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.List;

public class GameModeRepository {

    private static final String PREF_CURRENT_GAME_MODE_ID = "currentGameModeId";

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

    public GameMode loadCurrentGameMode() {
        long id = preferences.getLong(PREF_CURRENT_GAME_MODE_ID, -1);
        if (id == -1) {
            GameMode gameMode = gameModeDao.getFirst();
            saveCurrentGameMode(gameMode);
            return gameMode;
        }

        return gameModeDao.getById(id);
    }

}
