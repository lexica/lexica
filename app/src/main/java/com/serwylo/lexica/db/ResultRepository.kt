package com.serwylo.lexica.db

import com.serwylo.lexica.lang.Language

class ResultRepository(private val resultDao: ResultDao) {

    fun findHighScore(gameMode: GameMode, language: Language): Result? {
        return resultDao.findHighScore(gameMode.gameModeId, language.name)
    }

}