package com.serwylo.lexica.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.client.android.encode.QRCodeEncoder
import com.serwylo.lexica.R
import com.serwylo.lexica.ThemeManager
import com.serwylo.lexica.Util
import com.serwylo.lexica.databinding.NewMultiplayerBinding
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.db.GameModeRepository
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.share.SharedGameData
import com.serwylo.lexica.share.SharedGameDataHumanReadable
import com.serwylo.lexica.view.QrCodeBinder
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
        binding.toggleQr.isEnabled = false

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

        binding.gameModeDetails.setGameMode(gameMode)
        binding.gameModeDetails.setLanguage(language)

        val qrCodeBinder = QrCodeBinder(this, resources, game)
        qrCodeBinder.bindUI(binding.qr, binding.toggleQr)

        binding.multiplayerGameNumAvailableWords.text = resources.getQuantityString(R.plurals.num_available_words_in_game__tap_to_refresh, game.maxWordCount, game.maxWordCount)
        binding.multiplayerGameNumAvailableWords.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_refresh_title)
                .setMessage(R.string.dialog_refresh_content)
                .setPositiveButton(R.string.dialog_refresh_button) { _, _ ->
                    setup(gameMode)
                    Toast.makeText(this, R.string.dialog_refresh_confirmation_message, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.button_cancel, null)
                .create()
                .show()
        }

        binding.sendInvite.isEnabled = true
        binding.sendInvite.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"

                val humanReadable = SharedGameDataHumanReadable(board, gameMode, language).serialize(applicationContext)
                val text = """
${getString(R.string.invite__multiplayer__description)}

$qrCodeBinder.uri

${getString(R.string.invite__dont_have_lexica_installed)}

${getString(R.string.invite__dont_have_lexica_installed_web)}

$qrCodeBinder.webUri

${getString(R.string.invite__multiplayer__game_offline)}

$humanReadable
""".trim() // trimIndent() doesn't work because the humanReadable board has line breaks at the start of lines.
                putExtra(Intent.EXTRA_TEXT, text)
            }

            startActivity(Intent.createChooser(sendIntent, getString(R.string.send_multiplayer_invite_to)))
        }

        binding.startGame.isEnabled = true
        binding.startGame.setOnClickListener {
            val intent = Intent("com.serwylo.lexica.action.NEW_GAME").apply {
                putExtra("gameMode", gameMode)
                putExtra("lang", language.name)
                putExtra("board", board.toTypedArray())
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            }
            startActivity(intent)
            finish()
        }

    }

    companion object {
        val TAG = NewMultiplayerActivity::class.simpleName
    }

}