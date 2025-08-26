package com.serwylo.lexica.activities.score

import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.serwylo.lexica.game.Game

internal class ScoreTabViewHolder(private val activity: ScoreActivity, private val parent: FrameLayout) : RecyclerView.ViewHolder(parent) {
    fun bindFoundWords(game: Game, sorter: Sorter) {
        FoundWordsViewBinder(activity, parent, game, sorter)
    }

    fun bindMissedWords(game: Game, sorter: Sorter) {
        MissedWordsViewBinder(activity, parent, game, sorter)
    }

    fun bindNextRound(game: Game) {
        NextRoundViewBinder(activity, parent, game)
    }
}
