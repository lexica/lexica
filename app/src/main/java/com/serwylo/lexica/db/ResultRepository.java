package com.serwylo.lexica.db;

import android.app.Application;
import android.content.SharedPreferences;

import com.serwylo.lexica.lang.Language;

public class ResultRepository {

    private ResultDao resultDao;
    private SharedPreferences preferences;

    public ResultRepository(Application application) {
        Database db = Database.get(application);
        resultDao = db.resultDao();
    }

    public Result findHighScore(GameMode gameMode, Language language) {
        return resultDao.findHighScore(gameMode.getGameModeId(), language.getName());
    }

}
