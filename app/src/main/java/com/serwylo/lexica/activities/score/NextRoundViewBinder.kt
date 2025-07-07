package com.serwylo.lexica.activities.score

import android.content.Intent
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.serwylo.lexica.R
import com.serwylo.lexica.game.CharProbGenerator
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.view.QrCodeBinder

class NextRoundViewBinder(activity: ScoreActivity, parent: FrameLayout, game: Game) {

    init {
        Log.d("DB", "bindNextRound: calling bindNextRound")
        val nextRoundView = activity.layoutInflater.inflate(R.layout.score_next_round, parent, true)
        val qr = nextRoundView.findViewById<ImageView>(R.id.qr)
        val toggleQr = nextRoundView.findViewById<SwitchCompat>(R.id.toggle_qr)

        val nextGame = Game.generateGame(parent.context, game.gameMode, game.language, CharProbGenerator.Seed.createRandomFromPreviousBoard(game.board))

        val qrCodeBinder = QrCodeBinder(parent.context, parent.resources, nextGame)
        qrCodeBinder.bindUI(qr, toggleQr)

        val startNextRoundBtn = nextRoundView.findViewById<MaterialButton>(R.id.start_next_round)
        startNextRoundBtn.setOnClickListener {
            Log.d("DB", "bindNextRound: Start Next Round clicked")

            val intent = Intent("com.serwylo.lexica.action.NEW_GAME").apply {
                putExtra("gameMode", nextGame.gameMode)
                putExtra("lang", nextGame.language.name)
                putExtra("board", nextGame.board.letters)
            }

            ContextCompat.startActivity(parent.context, intent, null)
            activity.finish()
        }
    }


}