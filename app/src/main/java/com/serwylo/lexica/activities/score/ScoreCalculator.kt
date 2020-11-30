package com.serwylo.lexica.activities.score

import com.serwylo.lexica.game.Game
import java.util.*

class ScoreCalculator(game: Game) {

    var score = 0
    var maxScore = 0
    var numWords = 0
    var maxWords = 0

    fun getItems(): List<Selected> {
        return items
    }

    private val items: MutableList<Selected>

    data class Selected(val word: String, val score: Int, private val isWord: Boolean) {
        fun isWord(): Boolean {
            return isWord
        }
    }

    init {

        val possible = game.solutions.keys

        maxWords = possible.size

        val uniqueWords = game.uniqueListIterator()

        items = ArrayList()

        while (uniqueWords.hasNext()) {

            val w = uniqueWords.next()

            if (game.isWord(w) && game.getWordScore(w) > 0) {

                val points = game.getWordScore(w)
                items.add(Selected(w, points, true))
                score += points
                numWords++

            } else {

                items.add(Selected(w, 0, false))

            }

            possible.remove(w)

        }

        maxScore = score

        for (w in possible) {
            maxScore += game.getWordScore(w)
        }
    }
}