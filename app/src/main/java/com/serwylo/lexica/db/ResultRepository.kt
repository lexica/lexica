package com.serwylo.lexica.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.serwylo.lexica.activities.score.ScoreCalculator
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.lang.Language

class ResultRepository(private val resultDao: ResultDao, private val selectedWordDao: SelectedWordDao) {

    /**
     * Convenience constructor. The primary constructor is the one which allows for proper dependency
     * injection and easier unit tests, etc.
     */
    constructor(context: Context): this(
            Database.get(context).resultDao(),
            Database.get(context).selectedWordDao(),
    )

    fun findHighScore(gameMode: GameMode, language: Language): Result? {
        return resultDao.findHighScore(gameMode.gameModeId, language.name)
    }

    fun top10(gameMode: GameMode, language: Language): LiveData<List<Result>> {
        return resultDao.findTop10(gameMode.gameModeId, language.name)
    }

    fun recordGameResult(game: Game) {

        val score = ScoreCalculator(game)

        val result = Result(
                0,  // Leaving blank because Room will create an ID for it after insert.
                game.gameMode.gameModeId,
                game.language.name,
                score.score.toLong(),
                score.maxScore.toLong(),
                score.numWords,
                score.maxWords
        )

        val newResultId = resultDao.insert(result)

        val words = score.getItems().map {
            SelectedWord(
                    0,
                    newResultId,
                    it.word,
                    it.score,
                    it.isWord()
            )
        }

        selectedWordDao.insert(words)

    }

}