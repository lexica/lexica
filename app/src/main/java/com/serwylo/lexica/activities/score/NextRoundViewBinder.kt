package com.serwylo.lexica.activities.score

import android.content.Intent
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.serwylo.lexica.R
import com.serwylo.lexica.game.CharProbGenerator.BoardSeed
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.view.QrCodeBinder
import mehdi.sakout.fancybuttons.FancyButton

class NextRoundViewBinder(activity: ScoreActivity, parent: FrameLayout, game: Game) {

    init {
        Log.d("DB", "bindNextRound: calling bindNextRound")
        val nextRoundView = activity.layoutInflater.inflate(R.layout.score_next_round, parent, true)
        val qr = nextRoundView.findViewById<ImageView>(R.id.qr)
        val toggleQr = nextRoundView.findViewById<SwitchCompat>(R.id.toggle_qr)

        val nextGame = Game.generateGame(parent.context, game.gameMode, game.language, BoardSeed.fromPreviousBoard(game.board).seed)

        val qrCodeBinder = QrCodeBinder(parent.context, parent.resources, nextGame)
        qrCodeBinder.bindUI(qr, toggleQr)

        val startNextRoundBtn = nextRoundView.findViewById<FancyButton>(R.id.start_next_round)
        startNextRoundBtn.setOnClickListener {
            Log.d("DB", "bindNextRound: Start Next Round clicked")

            val intent = Intent("com.serwylo.lexica.action.NEW_GAME").apply {
                putExtra("gameMode", nextGame.gameMode)
                putExtra("lang", nextGame.language.name)
                putExtra("board", nextGame.board.letters)

                // todo: find out why these tags are set in the first place normally
                //  and why setting them in this case (with the main difference probably being the absence
                //  of a home activity) on some devices leads to the closing of the app.
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            }

            ContextCompat.startActivity(parent.context, intent, null)
            activity.finish()
        }
    }


}