package com.serwylo.lexica.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.serwylo.lexica.lang.Language

class ResultRepository(private val resultDao: ResultDao) {

    /**
     * Convenience constructor. The primary constructor is the one which allows for proper dependency
     * injection and easier unit tests, etc.
     */
    constructor(context: Context): this(
            Database.get(context).resultDao(),
    )

    fun findHighScore(gameMode: GameMode, language: Language): Result? {
        return resultDao.findHighScore(gameMode.gameModeId, language.name)
    }

    fun top10(gameMode: GameMode, language: Language): LiveData<List<Result>> {
        return resultDao.findTop10(gameMode.gameModeId, language.name)
    }

}