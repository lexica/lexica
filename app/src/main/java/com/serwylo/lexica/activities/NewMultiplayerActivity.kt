package com.serwylo.lexica.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.client.android.encode.QRCodeEncoder
import com.serwylo.lexica.ThemeManager
import com.serwylo.lexica.Util
import com.serwylo.lexica.databinding.NewMultiplayerBinding
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.db.GameModeRepository
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.share.SharedGameData
import com.serwylo.lexica.share.SharedGameDataHumanReadable
import kotlin.math.min


class NewMultiplayerActivity : AppCompatActivity() {

    private lateinit var binding: NewMultiplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ThemeManager.getInstance().applyTheme(this)

        binding = NewMultiplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.sendInvite.isEnabled = false
        binding.startGame.isEnabled = false

        // DB Query off the main thread to fetch the current game mode details before updating the UI:
        loadCurrentGameMode()

    }

    /**
     * Fetches the current game mode from the database asynchronously. Once loaded, will invoke
     * [setup] on the UI thread again.
     */
    private fun loadCurrentGameMode() {
        val repo = GameModeRepository(applicationContext)

        AsyncTask.execute {
            val current = repo.loadCurrentGameMode()
                ?: error("No game mode present, should have run database migrations prior to navigating to choose game mode activity.")

            runOnUiThread { setup(current) }
        }
    }

    /**
     * Generate an appropriate QR for the game in question, and update the UI to reflect the game mode details (now that we have them).
     */
    private fun setup(gameMode: GameMode) {

        val language = Util().getSelectedLanguageOrDefault(this)
        val game = Game.generateGame(this, gameMode, language)
        val board = mutableListOf<String>()
        for (i in 0 until game.board.size) {
            board.add(game.board.elementAt(i))
        }

        val uri = SharedGameData(board, language, gameMode).serialize()
        val metrics = resources.displayMetrics
        val size = min(metrics.widthPixels, metrics.heightPixels)
        val bitmap = QRCodeEncoder.encodeAsBitmap(uri.toString(), size)

        Log.d(TAG, "Preparing multiplayer game: $uri")

        binding.qr.setImageBitmap(bitmap)
        binding.gameModeDetails.setGameMode(gameMode)
        binding.gameModeDetails.setLanguage(language)

        binding.sendInvite.isEnabled = true
        binding.sendInvite.setOnClickListener {
            val sendIntent = Intent().apply {

                action = Intent.ACTION_SEND

                putExtra(Intent.EXTRA_TEXT,
"""You've been invited to a multiplayer Lexica game:
$uri

Don't have Lexica installed? Get it from:
https://play.google.com/store/apps/details?id=com.serwylo.lexica

Want to play offline? Try playing via pen and paper:

${SharedGameDataHumanReadable(board, gameMode, language).serialize(applicationContext)}
""".trimIndent())

                type = "text/plain"

            }

            startActivity(Intent.createChooser(sendIntent, "Send invite to..."))
        }

        binding.startGame.isEnabled = true
        binding.startGame.setOnClickListener {
            val intent = Intent("com.serwylo.lexica.action.NEW_GAME").apply {
                putExtra("gameMode", gameMode)
                putExtra("lang", language.name)
                putExtra("board", board.joinToString())
            }
            startActivity(intent)
        }

    }

    companion object {
        val TAG = NewMultiplayerActivity::class.simpleName
    }

}